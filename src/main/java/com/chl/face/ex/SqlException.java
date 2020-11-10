package com.chl.face.ex;

/**
 * @author gyh
 * @create 2020-11-02 15:02
 */
public class SqlException extends BaseException{
    public SqlException() {
        super();
    }

    public SqlException(String message) {
        super(message);
    }

    public SqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlException(Throwable cause) {
        super(cause);
    }

    protected SqlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
