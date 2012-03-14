package com.alibaba.hbase.replication.utility;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValue.Type;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.hadoop.hbase.util.Bytes;

import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.protocol.Body;
import com.alibaba.hbase.replication.protocol.Body.Edit;
import com.alibaba.hbase.replication.protocol.Head;

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
        UUID clusterId = entry.getKey().getClusterId();

        // 目前只支持单向同步，所以ClusterId 只要不是默认的就判定该 Entry 为同步的数据
        if (clusterId != HConstants.DEFAULT_CLUSTER_ID) {
            return;
        }
        // 不同步 META 和 ROOT 表
        if (isCusTable(tableName)) {
            String strTableName = Bytes.toString(tableName);
            List<KeyValue> kvs = entry.getEdit().getKeyValues();
            for (KeyValue kv : kvs) {
                // 不同步 METAFAMILY
                if (kv.matchingFamily(HLog.METAFAMILY)) continue;
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
                edit.setValue(kv.getValue());
                edit.setTimeStamp(kv.getTimestamp());
                body.addEdit(strTableName, edit);
            }
        }
    }

    public static Path getFileStatusByHLogEntry(FileSystem fs, Path rootPath, HLogEntry entry) throws IOException {
        String filePath = null;
        if (entry.getType() == HLogEntry.Type.LIFE) {
            String dir = URLDecoder.decode(entry.getGroupName());
            filePath = rootPath.toString() + "/.logs/" + dir + "/" + entry.getName();
        } else if (entry.getType() == HLogEntry.Type.OLD) {
            filePath = rootPath.toString() + "/.oldlogs/" + entry.getName();
        }
        Path path = new Path(filePath);
        if(fs.exists(path))
            return path;
        return null;
    }
    
    public static HLogEntry getHLogEntryByHead(Head head) {
        HLogEntry entry = new HLogEntry();
        entry.setGroupName(head.getGroupName());
        entry.setTimestamp(head.getFileTimestamp());
        entry.setPos(head.getStartOffset());
        entry.setType(HLogEntry.Type.OLD);
        return entry;
    }
}
