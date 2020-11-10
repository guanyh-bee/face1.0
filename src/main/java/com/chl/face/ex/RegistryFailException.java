package com.chl.face.ex;

/**
 * @author gyh
 * @create 2020-11-02 14:58
 */
public class RegistryFailException extends BaseException{
    public RegistryFailException() {
        super();
    }

    public RegistryFailException(String message) {
        super(message);
    }

    public RegistryFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistryFailException(Throwable cause) {
        super(cause);
    }

    protected RegistryFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
