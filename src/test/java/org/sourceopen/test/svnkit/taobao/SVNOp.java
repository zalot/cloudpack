package org.sourceopen.test.svnkit.taobao;

import java.io.File;

import org.junit.Test;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class SVNOp {

    @Test
    public void testUrl() throws SVNException {
        // 默认 SVN 操作
        DefaultSVNOptions op = new DefaultSVNOptions(new File("/work/workspace/taobao/scheduleragent"), false);
        
        // 打开一个SVN 管理器
        SVNClientManager manager = SVNClientManager.newInstance(op, "username", "password");

        File svnFile = new File("/work/workspace/taobao/scheduleragent/assembly.xml");
        
        // 回复一个文件
        manager.getWCClient().doRevert(new File[] { svnFile }, SVNDepth.FILES, null);
        
        // 更新这个文件至最新版
        manager.getUpdateClient().doUpdate(svnFile, SVNRevision.HEAD, SVNDepth.FILES, true, true);
    }
}
