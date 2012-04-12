package com.alibaba.hbase.replication.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.hadoop.hbase.util.Bytes;

import com.alibaba.hbase.replication.hlog.domain.HLogEntry;
import com.alibaba.hbase.replication.protocol.ProtocolHead;
import com.alibaba.hbase.replication.protocol.ProtocolBody;
import com.alibaba.hbase.replication.protocol.protobuf.SerBody.Edit;

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

    public static int put2Body(Entry entry, ProtocolBody body, UUID localUUID) {
        byte[] tableName = entry.getKey().getTablename();
        UUID clusterId = entry.getKey().getClusterId();

        // 目前只支持单向同步，所以ClusterId 只要不是默认的就判定该 Entry 为同步的数据
        if (clusterId != localUUID) {
            return 0;
        }
        int count = 0;
        // 不同步 META 和 ROOT 表
        if (isCusTable(tableName)) {
            List<KeyValue> kvs = entry.getEdit().getKeyValues();
            for (KeyValue kv : kvs) {
                // 不同步 METAFAMILY
                if (kv.matchingFamily(HLog.METAFAMILY)) continue;
                body.putKeyValue(Bytes.toString(tableName), kv);
                count++;
            }
        }
        return count;
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
        if (fs.exists(path)) return path;
        return null;
    }

    public static HLogEntry getHLogEntryByHead(ProtocolHead head) {
        HLogEntry entry = new HLogEntry();
        entry.setGroupName(head.getGroupName());
        entry.setTimestamp(head.getFileTimestamp());
        entry.setPos(head.getStartOffset());
        entry.setType(HLogEntry.Type.OLD);
        return entry;
    }

    public static byte[] gzip(byte[] data) throws Exception {
        byte[] rs;
        GZIPOutputStream gzipOut = null;
        ByteArrayOutputStream byteOut = null;
        try {
            byteOut = new ByteArrayOutputStream();
            gzipOut = new GZIPOutputStream(byteOut);
            gzipOut.write(data);
            gzipOut.finish();
            gzipOut.close();
            rs = byteOut.toByteArray();
            byteOut.close();
            return rs;
        } finally {
            if (gzipOut != null) {
                gzipOut.close();
            }
            if (byteOut != null) {
                byteOut.close();
            }
            gzipOut = null;
            byteOut = null;
        }
    }

    public static byte[] ungzip(byte[] data) throws Exception {
        byte[] b = null;
        ByteArrayInputStream byteIn = null;
        GZIPInputStream gzipIn = null;
        ByteArrayOutputStream byteOut = null;
        try {
            byteIn = new ByteArrayInputStream(data);
            gzipIn = new GZIPInputStream(byteIn);
            byte[] buf = new byte[1024];
            int num = -1;
            byteOut = new ByteArrayOutputStream();
            while ((num = gzipIn.read(buf, 0, buf.length)) != -1) {
                byteOut.write(buf, 0, num);
            }
            b = byteOut.toByteArray();
            byteOut.flush();
            byteOut.close();
            gzipIn.close();
            byteIn.close();
        } finally {
            if (gzipIn != null) {
                gzipIn.close();
            }
            if (byteIn != null) {
                byteIn.close();
            }
            if (byteOut != null) {
                byteOut.close();
            }
            gzipIn = null;
            byteOut = null;
            byteIn = null;
        }
        return b;
    }

    public static String getBaseInfo(Object obj) {
        String mName = ManagementFactory.getRuntimeMXBean().getName();
        if (obj != null) return "[host]" + mName + " [thread]" + Thread.currentThread().getName() + " [class]"
                                + obj.getClass().getName();
        else return "[host]" + mName + " [thread]" + Thread.currentThread().getName();
    }
}
