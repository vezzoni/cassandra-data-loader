package com.github.vezzoni.cassandra.data.loader.exception;

public class DataSetParseException extends Exception {

    public DataSetParseException() {
    }

    public DataSetParseException(String message) {
        super(message);
    }

    public DataSetParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSetParseException(Throwable cause) {
        super(cause);
    }

    public DataSetParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
