package org.sourceopen.hadoop.zookeeper.core;

public class ZNodeProxyRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -7911291284669899673L;

    public ZNodeProxyRuntimeException(){
        super();
    }

    public ZNodeProxyRuntimeException(String message, Throwable cause){
        super(message, cause);
        // Thread.setDefaultUncaughtExceptionHandler(eh)
    }

    public ZNodeProxyRuntimeException(String message){
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ZNodeProxyRuntimeException(Throwable cause){
        super(cause);
    }

}
