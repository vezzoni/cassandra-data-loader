package vezzoni.cassandra.data.loader.dataloader;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.TableMetadata;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import vezzoni.cassandra.data.loader.dataset.DataSet;
import vezzoni.cassandra.data.loader.exception.DataSetParseException;

/**
 * Class responsible to parse the {@link DataSet} and perform persistent operations against {@code Cassandra} 
 * through the {@link Session} which already have existing tables (data set source entries).
 * 
 * <p>
 * The default {@link LoadActionEnum} is {@code LoadActionEnum.CREATE}.
 * 
 * <pre>
 * <code>DataType.Name.TINYINT</code>, <code>DataType.Name.TINYINT</code> and <code>DataType.Name.INT</code> will be converted into <code>java.lang.Integer</code>.
 * <code>DataType.Name.BIGINT</code>  will be converted into <code>java.lang.Long</code>.
 * <code>DataType.Name.FLOAT</code>, <code>DataType.Name.DOUBLE</code> and <code>DataType.Name.DECIMAL</code> will be converted into <code>java.lang.Double</code>.
 * <code>DataType.Name.FLOAT</code>, <code>DataType.Name.DOUBLE</code> and <code>DataType.Name.DECIMAL</code> will be converted into <code>java.lang.Boolean</code>.
 * <code>DataType.Name.TIMESTAMP</code> will be converted into <code>java.util.Date</code> (the data set source file has to have the pattern <code>yyyy-MM-ddTHH:mm:ss</code>).
 * The remaining data types will be converted into <code>java.lang.String</code>.
 * </pre>
 * 
 * @see DataSet
 * @see LoadActionEnum
 * @see Session
 * @see DataType.Name
 * 
 * @author vezzoni
 */
public class DataLoader {

    private final Session session;
    
    private final Map<String, Statement> truncateStatements;
    private final List<Statement> createStatements;
    private final Map<String, ColumnMetadata> columnMetadataByName;
    
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    /**
     * Constructor responsible to receive the available and active {@code Cassandra session}.
     * 
     * @param session {@link Session}
     */
    public DataLoader(final Session session) {
        this.session = session;

        this.truncateStatements = new HashMap<>();
        this.createStatements = new LinkedList<>();
        this.columnMetadataByName = new HashMap<>();
    }
    
    /**
     * Loads the {@code dataSet} into the {@code Cassandra} with the default {@code LoadActionEnum.CREATE} action.
     * 
     * @param dataSet {@link DataSet} which contains the data set source data.
     * 
     * @throws DataSetParseException 
     */
    public void load(final DataSet dataSet) throws DataSetParseException {
        this.load(dataSet, LoadActionEnum.CREATE);
    }

    /**
     * Loads the {@code dataSet} into the {@code Cassandra}.
     * 
     * @param dataSet {@link DataSet} which contains the data set source data.
     * @param loadAction {@link LoadActionEnum} value.
     * 
     * @throws DataSetParseException 
     */
    public void load(final DataSet dataSet, final LoadActionEnum loadAction) throws DataSetParseException {

        try {
            this.buildStatements(dataSet.getSource());
        } catch (DataSetParseException e) {
            throw e;
        }
        
        if (loadAction.equals(LoadActionEnum.TRUNCATE_AND_CREATE)) {
            for (Statement entry : this.truncateStatements.values()) {
                this.session.execute(entry.toString());
            }
        }

        BatchStatement batch = new BatchStatement(BatchStatement.Type.UNLOGGED);

        for (Statement entry : this.createStatements) {
            batch.add(entry);
        }
        
        if (batch.size() > 0) {
            this.session.execute(batch);
        }
    }

    private void buildStatements(InputStream source) throws DataSetParseException {

        KeyspaceMetadata keyspaceMetadata = this
            .session
            .getCluster()
            .getMetadata()
            .getKeyspace(this.session.getLoggedKeyspace());
        TableMetadata tableMetadata;
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(source);

            document.getDocumentElement().normalize();

            Node rootNode = document.getChildNodes().item(0);
            NodeList tableNodes = rootNode.getChildNodes();
            Node tableNode;
            NamedNodeMap columns;
            Node attrNode;
            Object value;
            Insert insert;

            for (int i = 0; i < tableNodes.getLength(); i++) {
                tableNode = tableNodes.item(i);
                if (tableNode.getNodeType() == Node.ELEMENT_NODE) {

                    if (!this.truncateStatements.containsKey(tableNode.getNodeName())) {
                        this.truncateStatements.put(tableNode.getNodeName(), QueryBuilder.truncate(tableNode.getNodeName()));
                        
                        tableMetadata = keyspaceMetadata.getTable(tableNode.getNodeName());
                        for (ColumnMetadata c : tableMetadata.getColumns()) {
                            this.columnMetadataByName.put(c.getName().toLowerCase(), c);
                        }
                    }
                    
                    insert = QueryBuilder.insertInto(tableNode.getNodeName());
                    columns = tableNode.getAttributes();
                    for (int j = 0; j < columns.getLength(); j++) {
                        attrNode = columns.item(j);
                        value = this.getValue(attrNode);
                        insert.value(attrNode.getNodeName(), value);
                    }

                    this.createStatements.add(insert);
                }
            }

        } catch (NumberFormatException | DOMException | ParseException | ParserConfigurationException | SAXException | IOException e) {
            throw new DataSetParseException(e);
        }
    }

    private Object getValue(Node columnNode) throws NumberFormatException, DOMException, ParseException {

        ColumnMetadata columnMetadata = this.columnMetadataByName.get(columnNode.getNodeName().toLowerCase());
        DataType.Name dataType = columnMetadata.getType().getName();

        Object value;

        switch (dataType) {
            case INT:
            case SMALLINT:
            case TINYINT:
                value = Integer.valueOf(columnNode.getTextContent());
                break;
            case BIGINT:
                value = Long.valueOf(columnNode.getTextContent());
                break;
            case DECIMAL:
            case DOUBLE:
            case FLOAT:
                value = Double.valueOf(columnNode.getTextContent());
                break;
            case BOOLEAN:
                value = Boolean.valueOf(columnNode.getTextContent());
                break;
            case TIMESTAMP: {
                try {
                    value = this.dateTimeFormat.parse(columnNode.getTextContent());
                } catch (ParseException e) {
                    throw e;
                }
            }
            break;

            default: value = columnNode.getTextContent();
        }

        return value;
    }
}
