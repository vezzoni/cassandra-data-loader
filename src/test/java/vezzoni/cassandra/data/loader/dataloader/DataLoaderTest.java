package vezzoni.cassandra.data.loader.dataloader;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.InputStream;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import vezzoni.cassandra.data.loader.dataset.DataSet;
import vezzoni.cassandra.data.loader.dataset.xml.XmlDataSet;
import vezzoni.cassandra.data.loader.exception.DataSetParseException;

public class DataLoaderTest {

    private Cluster cluster;
    private Session session;
    private DataLoader dataLoader;
    
    @Before
    public void setUp() {
        this.cluster = Cluster.builder()
            .addContactPoints("localhost")
            .withPort(9042)
            .withCredentials("cassandra", "cassandra")
            .build();
        
        this.session = this.cluster.connect();
        this.session.execute("CREATE KEYSPACE cloader WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 }");
        
        this.session = this.cluster.connect("cloader");
        this.session.execute("CREATE TABLE employee (id int, first_name text, last_name text, active Boolean, PRIMARY KEY(id))");
        
        this.dataLoader = new DataLoader(this.session);
    }

    @After
    public void tearDown() {
        this.session.execute("DROP TABLE employee");
        this.session.execute("DROP KEYSPACE cloader");
        this.session.close();
        this.cluster.close();
    }
    
    @Test
    public void testLoaderTruncatingExistingData() throws DataSetParseException {

        InputStream is = this.getClass().getClassLoader().getResourceAsStream("xml/employee.xml");
        DataSet dataSet = new XmlDataSet(is);
        this.dataLoader.load(dataSet, LoadActionEnum.TRUNCATE_AND_CREATE);
        
        ResultSet result = this.session.execute("select * from employee");
        
        assertNotNull(result);
        
        List<Row> rows = result.all();
        
        assertTrue(rows.size() == 3);
        
        Row row = rows.get(0);
        assertNotNull(row);
        
        int id = row.getInt("id");
        assertTrue(id == 1);
        String firstName = row.get("first_name", String.class);
        assertEquals("Chuck", firstName);
        String lastName = row.getString("last_name");
        assertEquals("Norris", lastName);
        boolean active = row.getBool("active");
        assertTrue(active);
    }
    
}
