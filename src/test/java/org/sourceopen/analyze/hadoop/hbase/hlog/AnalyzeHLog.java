package org.sourceopen.analyze.hbase.hlog;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * HLog 分析 <BR>
 * 直接运行即可 <BR>
 * 用于观察 /root/hlog 和 /root/hlogold 目录的运行状态 <BR>
 * 来发现 HLOG 目录的一些细节 <BR>
 * 1。 hbase.regionserver.maxlogs 参数的作用
 * 
 * @author zalot.zhaoh Mar 5, 2012 6:18:23 PM
 */
public class AnalyzeHLog {

    static FileSystem    fs;
    static Path          pathHlog     = new Path("/root/hlog");
    static Path          ppathOldhlog = new Path("/root/hlogold");
    static Configuration conf;

    @BeforeClass
    public static void init() throws IOException {
        conf = new Configuration();
        conf.set(LocalFileSystem.FS_DEFAULT_NAME_KEY, "file:///");
        fs = LocalFileSystem.get(conf);
        fs.deleteOnExit(pathHlog);
        fs.deleteOnExit(ppathOldhlog);
    }

    @AfterClass
    public static void des() throws IOException {
        fs.deleteOnExit(pathHlog);
        fs.deleteOnExit(ppathOldhlog);
    }

    public Configuration createConfig() {
        Configuration conf = HBaseConfiguration.create();
        conf.setInt(HConstants.HREGION_MAX_FILESIZE, 5);
        // 查看 HLog 源码
        conf.setLong("hbase.regionserver.hlog.blocksize", 1 * 1024);
        return conf;
    }

    @Test
    public void testLocalHLogWrite() throws Exception {
        HLog hlog = new HLog(fs, pathHlog, ppathOldhlog, createConfig());
        byte[] TABLEA = "testTableA".getBytes();
        byte[] TABLEB = "testTableB".getBytes();
        HRegionInfo hriA = new HRegionInfo(TABLEA, HConstants.EMPTY_START_ROW, HConstants.EMPTY_END_ROW);
        HRegionInfo hriB = new HRegionInfo(TABLEB, HConstants.EMPTY_START_ROW, HConstants.EMPTY_END_ROW);
        HTableDescriptor htd = new HTableDescriptor();
        htd.addFamily(new HColumnDescriptor("column"));

        WALEdit cols = new WALEdit();
        final byte[] row = Bytes.toBytes("row");
        long timestamp = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            cols.add(new KeyValue(row, Bytes.toBytes("column"), Bytes.toBytes(Integer.toString(i)), timestamp,
                                  new byte[] { (byte) (i + '0') }));
        }

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 1000; y++) {
                hlog.append(hriA, TABLEA, cols, System.currentTimeMillis(), htd);
                hlog.append(hriB, TABLEB, cols, System.currentTimeMillis(), htd);
            }
            if (x % 2 == 0) {
                long complateId = -1;
                try {
                    complateId = hlog.startCacheFlush(hriA.getEncodedNameAsBytes());
                } catch (Exception ec) {
                    hlog.abortCacheFlush(hriA.getEncodedNameAsBytes());
                    complateId = -1;
                }
                if (complateId > 0) {
                    hlog.completeCacheFlush(hriA.getEncodedNameAsBytes(), hriA.getTableName(), complateId,
                                            hriA.isMetaRegion());
                    hlog.rollWriter();
                }
            } else {
                long complateId = -1;
                try {
                    complateId = hlog.startCacheFlush(hriB.getEncodedNameAsBytes());
                } catch (Exception ec) {
                    hlog.abortCacheFlush(hriB.getEncodedNameAsBytes());
                    complateId = -1;
                }
                if (complateId > 0) {
                    hlog.completeCacheFlush(hriB.getEncodedNameAsBytes(), hriB.getTableName(), complateId,
                                            hriB.isMetaRegion());
                    hlog.rollWriter();
                }
            }
        }
    }
}
