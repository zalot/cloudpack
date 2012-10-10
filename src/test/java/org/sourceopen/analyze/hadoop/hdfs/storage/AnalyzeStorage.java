package org.sourceopen.analyze.hadoop.hdfs.storage;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.hdfs.server.common.HdfsConstants.NodeType;
import org.apache.hadoop.hdfs.server.common.Storage;
import org.apache.hadoop.hdfs.server.common.StorageInfo;

public class AnalyzeStorage extends Storage {

    protected AnalyzeStorage(NodeType type){
        super(type);
    }

    @Override
    public boolean isConversionNeeded(StorageDirectory sd) throws IOException {
        return true;
    }

    @Override
    protected void corruptPreUpgradeStorage(File rootDir) throws IOException {
        System.out.println(rootDir);
    }
    
    
    public static void main(String[] args){
        AnalyzeStorage storage = new AnalyzeStorage(NodeType.DATA_NODE);
        StorageInfo info = new StorageInfo();
        storage.setStorageInfo(info);
        storage.addStorageDir(storage.new StorageDirectory(new File("/tmp/sotreage/")));
    }
}
