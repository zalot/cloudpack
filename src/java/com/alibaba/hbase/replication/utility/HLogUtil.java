package com.alibaba.hbase.replication.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValue.Type;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.hadoop.hbase.util.Bytes;

import com.alibaba.hbase.replication.hlog.domain.HLogInfo;
import com.alibaba.hbase.replication.protocol.Body;
import com.alibaba.hbase.replication.protocol.Body.Edit;

/**
 * 日志工具
 * 
 * @author zalot.zhaoh
 */
public class HLogUtil {

    public static boolean isRootRegion(byte[] tableName) {
        return Bytes.equals(tableName, HRegionInfo.ROOT_REGIONINFO.getTableName());
    }

    public static boolean isMetaRegion(byte[] tableName) {
        return Bytes.equals(tableName, HRegionInfo.FIRST_META_REGIONINFO.getTableName());
    }

    public static boolean isCusTable(byte[] tableName) {
        return !isRootRegion(tableName) && !isMetaRegion(tableName);
    }

    // copy from HLog
    // private static final Pattern pattern = Pattern.compile(".*\\.\\d*");

    public static HLogInfo getHLogInfo(Path path) {
        if (HLog.validateHLogFilename(path.getName())) {
            String[] str = path.getName().split("\\.");
            return new HLogInfo(str[0], Long.parseLong(str[1]), path);
        }
        return null;
    }

    public static List<Path> getHLogsByHDFS(FileSystem fs, Path path) throws IOException {
        List<Path> hlogs = new ArrayList<Path>();
        if (!fs.exists(path)) return hlogs;
        if (fs.isFile(path)) {
            if (HLog.validateHLogFilename(path.getName())) hlogs.add(path);
        } else {
            for (FileStatus fst : fs.listStatus(path)) {
                List<Path> childHlogs = getHLogsByHDFS(fs, fst.getPath());
                for (Path cdPath : childHlogs)
                    hlogs.add(cdPath);
            }
        }
        return hlogs;
    }

    public static void put2Body(Entry entry, Body body) {
        Edit edit = null;
        byte[] tableName = entry.getKey().getTablename();
        if (isCusTable(tableName)) {
            String strTableName = Bytes.toString(tableName);
            List<KeyValue> kvs = entry.getEdit().getKeyValues();
            for (KeyValue kv : kvs) {
                if(kv.matchingFamily(HLog.METAFAMILY)) continue;
                edit = new Edit();
                switch (Type.codeToType(kv.getType())) {
                    case DeleteFamily:
                        edit.setType(Body.Type.DeleteFamily);
                        break;
                    case DeleteColumn:
                        edit.setType(Body.Type.DeleteColumn);
                        break;
                    case Delete:
                        edit.setType(Body.Type.Delete);
                        break;
                    case Put:
                        edit.setType(Body.Type.Put);
                        break;
                }
                edit.setFamily(kv.getFamily());
                edit.setQualifier(kv.getQualifier());
                edit.setRowKey(kv.getRow());
                edit.setTimeStamp(kv.getTimestamp());
                body.addEdit(strTableName, edit);
            }
        }
    }
}
