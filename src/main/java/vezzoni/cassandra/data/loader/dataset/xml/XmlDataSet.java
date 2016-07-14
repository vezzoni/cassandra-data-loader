package vezzoni.cassandra.data.loader.dataset.xml;

import java.io.InputStream;
import vezzoni.cassandra.data.loader.dataset.DataSet;

/**
 * Specialized class for <code>XML</code> formated source.
 * 
 * @see DataSet
 * 
 * @author vezzoni
 */
public class XmlDataSet implements DataSet {
    
    private final InputStream source;
    
    /**
     * Constructor responsible to receive the <code>XML</code> formated source.
     * 
     * @param source <code>XML</code> formated source
     */
    public XmlDataSet(InputStream source) {
        this.source = source;
    }

    @Override
    public InputStream getSource() {
        return this.source;
    }
    
}
