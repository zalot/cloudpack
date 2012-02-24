package com.alibaba.hbase.replication.utility;

import java.util.regex.Pattern;

import com.alibaba.hbase.replication.domain.HLogInfo;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog;

/**
 * 日志工具
 * 
 * @author zalot.zhaoh
 *
 */
public class HLogUtil {
	
	// copy from HLog
	private static final Pattern pattern = Pattern.compile(".*\\.\\d*");
	
	public static HLogInfo getHLogInfo(Path path){
		if(HLog.validateHLogFilename(path.getName())){
			String[] str = path.getName().split("\\.");
			return new HLogInfo(str[0], Long.parseLong(str[1]), path);
		}
		return null;
	}
}
