package com.ys.springboot.demo.exception;

public class ServiceMyException  extends MyException{

    private static final long serialVersionUID = 1L;

    public ServiceMyException() {
        super();
    }

    public ServiceMyException(String message) {
        super(message);
    }

    public ServiceMyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceMyException(Throwable cause) {
        super(cause);
    }

}
