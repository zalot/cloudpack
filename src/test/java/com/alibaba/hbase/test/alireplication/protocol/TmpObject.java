package com.alibaba.hbase.test.alireplication.protocol;

import java.io.Serializable;
import java.util.UUID;


public class TmpObject implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 9156196077199494430L;

    String                    k1               = UUID.randomUUID().toString().substring(0, 10);
    String                    k2               = UUID.randomUUID().toString().substring(0, 10);
    long                      v1               = TestProtocolBody.rnd.nextLong();
    long                      v2               = TestProtocolBody.rnd.nextLong();

    public String getK1() {
        return k1;
    }

    public void setK1(String k1) {
        this.k1 = k1;
    }

    public String getK2() {
        return k2;
    }

    public void setK2(String k2) {
        this.k2 = k2;
    }

}
