package org.sourceopen.hadoop.zookeeper.core;

public class ZNodeProxyException extends RuntimeException {

    private static final long serialVersionUID = -7911291284669899673L;

    public ZNodeProxyException(){
        super();
    }

    public ZNodeProxyException(String message, Throwable cause){
        super(message, cause);
        // Thread.setDefaultUncaughtExceptionHandler(eh)
    }

    public ZNodeProxyException(String message){
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ZNodeProxyException(Throwable cause){
        super(cause);
    }

}
