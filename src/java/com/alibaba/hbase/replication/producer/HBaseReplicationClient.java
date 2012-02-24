package com.alibaba.hbase.replication.producer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.hbase.replication.hlog.HLogOperator;
import com.alibaba.hbase.replication.hlog.HLogOperator.EntryInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValue.Type;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.KeeperException;

/**
 * @author zalot.zhaoh
 */
public class HBaseReplicationClient implements HBaseReplication {
	public static final Log LOG = LogFactory
			.getLog(HBaseReplicationClient.class);

	protected Configuration conf;
	protected HTablePool pool;
	protected HLogOperator hlogOperator;
	protected boolean isStoped = true;

	public HBaseReplicationClient(Configuration conf) throws IOException,
			KeeperException, InterruptedException {
		pool = new HTablePool(conf, 100);
	}

	public boolean puts(List<EntryInfo> entryInfos) {
		try {
			Map<byte[], List<Put>> puts = new TreeMap<byte[], List<Put>>(Bytes.BYTES_COMPARATOR);
			for (EntryInfo entryInfo : entryInfos) {
				byte[] tableName = entryInfo.getEntry().getKey().getTablename();
				if (isCusTable(tableName)) {
					// copy from ReplicationSink.put()
					// =======================================================
					put(entryInfo);
					// copy from ReplicationSink.put()
					// =======================================================
				}

			}
			 for(byte [] table : puts.keySet()) {
				 put(table, puts.get(table));
			 }
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean put(EntryInfo entryInfo) {
		try {
			byte[] tableName = entryInfo.getEntry().getKey().getTablename();
			Map<byte[], List<Put>> puts = new TreeMap<byte[], List<Put>>(Bytes.BYTES_COMPARATOR);
			if (true) {
				// copy from ReplicationSink.put()
				// =======================================================
				Entry entry = entryInfo.getEntry();
				WALEdit edit = entry.getEdit();
				List<KeyValue> kvs = edit.getKeyValues();
				if (kvs.get(0).isDelete()) {
					Delete delete = new Delete(kvs.get(0).getRow(), kvs
							.get(0).getTimestamp(), null);
					delete.setClusterId(entry.getKey().getClusterId());
					for (KeyValue kv : kvs) {
						switch (Type.codeToType(kv.getType())) {
						case DeleteFamily:
							// family marker
							delete.deleteFamily(kv.getFamily(),
									kv.getTimestamp());
							break;
						case DeleteColumn:
							// column marker
							delete.deleteColumns(kv.getFamily(),
									kv.getQualifier(), kv.getTimestamp());
							break;
						case Delete:
							// version marker
							delete.deleteColumn(kv.getFamily(),
									kv.getQualifier(), kv.getTimestamp());
							break;
						}
					}
					 delete(entry.getKey().getTablename(), delete);
				} else {
					byte[] table = entry.getKey().getTablename();
					List<Put> tableList = puts.get(table);
					if (tableList == null) {
						tableList = new ArrayList<Put>();
						puts.put(table, tableList);
					}
					// With mini-batching, we need to expect multiple rows
					// per edit
					byte[] lastKey = kvs.get(0).getRow();
					Put put = new Put(kvs.get(0).getRow(), kvs.get(0)
							.getTimestamp());
					put.setClusterId(entry.getKey().getClusterId());
					for (KeyValue kv : kvs) {
						if (!Bytes.equals(lastKey, kv.getRow())) {
							tableList.add(put);
							put = new Put(kv.getRow(), kv.getTimestamp());
							put.setClusterId(entry.getKey().getClusterId());
						}
						put.add(kv.getFamily(), kv.getQualifier(),
								kv.getValue());
						lastKey = kv.getRow();
					}
					tableList.add(put);
				}
				
				 for(byte [] table : puts.keySet()) {
					 put(table, puts.get(table));
				 }
				// copy from ReplicationSink.put()
				// =======================================================
				return true;
			}
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isRootRegion(byte[] tableName) {
		return Bytes.equals(tableName,
				HRegionInfo.ROOT_REGIONINFO.getTableName());
	}

	public static boolean isMetaRegion(byte[] tableName) {
		return Bytes.equals(tableName,
				HRegionInfo.FIRST_META_REGIONINFO.getTableName());
	}

	public static boolean isCusTable(byte[] tableName) {
		return !isRootRegion(tableName) && !isMetaRegion(tableName);
	}

	public HTableInterface getHTable(byte[] tableName) {
		return getHTable(Bytes.toString(tableName));
	}

	public HTableInterface getHTable(String tableName) {
		HTableInterface htable = null;
		try {
			htable = pool.getTable(tableName);
			return htable;
		} catch (Exception e) {
			LOG.error("No Fount Table [" + tableName + "]");
			return null;
		}
	}

	
	private void put(byte[] tableName, List<Put> puts) throws IOException {
	    if (puts.isEmpty()) {
	      return;
	    }
	    HTableInterface table = null;
	    try {
	      table = pool.getTable(tableName);
	      table.put(puts);
	    }catch(Exception e){
	    	e.printStackTrace();
	    } finally {
	      
	    }
	  }

	  private void delete(byte[] tableName, Delete delete) throws IOException {
	    HTableInterface table = null;
	    try {
	      table = this.pool.getTable(tableName);
	      table.delete(delete);
	    } finally {
	      
	    }
	  }
}
