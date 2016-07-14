package vezzoni.cassandra.data.loader.dataset.xml;

import java.io.InputStream;
import static org.junit.Assert.*;
import org.junit.Test;
import vezzoni.cassandra.data.loader.dataset.DataSet;

public class XmlDataSetTest {

    @Test
    public void testStatements() throws Exception {

        InputStream is = this.getClass().getClassLoader().getResourceAsStream("xml/employee.xml");
        DataSet dataSet = new XmlDataSet(is);
        
        assertNotNull(dataSet);
        
        InputStream source = dataSet.getSource();
        assertEquals(is, source);
    }

}
