package org.sourceopen.analyze.hadoop.hdfs.client;

import java.util.LinkedList;

public class AnalyzeDataStreamer {

    private LinkedList<Object> dataQueue = new LinkedList<Object>();

    public class TestDataStreamer implements Runnable {

        @Override
        public void run() {
            synchronized (dataQueue) {
                while (true) {

                }
            }
        }
    }

}
