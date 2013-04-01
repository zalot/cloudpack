package org.sourceopen.analyze.concurrent;

import java.util.concurrent.ConcurrentHashMap;

public class TestConcurrentHashMap {

    protected final static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();

    public static class MapThread extends Thread {

        public void run() {
        }
    }

    public void testCon() {

    }
}
