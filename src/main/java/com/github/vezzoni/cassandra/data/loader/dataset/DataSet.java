package com.github.vezzoni.cassandra.data.loader.dataset;

import java.io.InputStream;
import com.github.vezzoni.cassandra.data.loader.dataset.xml.XmlDataSet;

/**
 * Interface responsible for generic data sets operations.
 * 
 * @see XmlDataSet
 * 
 * @author vezzoni
 */
public interface DataSet {
    
    /**
     * Returns the source data set in order to work with.
     * 
     * @return {@link InputStream}
     */
    InputStream getSource();
    
}
