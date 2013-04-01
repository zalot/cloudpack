<%@page import="org.apache.hadoop.hive.ql.plan.MapredWork"%>
<%@page import="org.sourceopen.hadoop.hive.ql.http.*"
	import="org.sourceopen.base.*" import="org.apache.hadoop.hive.ql.*"
	import="org.apache.hadoop.hive.ql.exec.*"
	import="org.sourceopen.hadoop.hive.ql.http.*" import="java.util.*"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>QueryPlan of DAGs</title>

<link rel="stylesheet" type="text/css"
	href="http://cdn.sencha.io/ext-4.1.0-gpl/resources/css/ext-all.css" />
<script type="text/javascript" charset="utf-8"
	src="http://cdn.sencha.io/ext-4.1.0-gpl/ext-all.js"></script>

<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/daglayout.js"></script>
<script type="text/javascript" src="js/DagDrawer.js"></script>
</head>
<%!
    Map<String, Task> tkmap = new HashMap<String, Task>();

    public String getID(String stageId) {
        if (stageId != null && stageId.startsWith("Stage-")) {
            return stageId.substring(6);
        }
        return stageId;
    }

    public String generateNode(String node, QueryPlan qp) {
        tkmap.clear();
        StringBuffer sb = new StringBuffer();
        sb.append("var " + node + "  = { '99':{\"label\":\"START\",\"highlighted\":1},");
        sb.append(generateNodeBaseString(qp.getFetchTask(),true));
        System.out.println("==="  + qp.getRootTasks().size());
        for (Task tk : qp.getRootTasks()) {
            generateNode(tk, sb);
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append("};");
        return sb.toString();
    }
    
    public String generateNodeBaseString(Task tk, boolean hl){
        if(tk == null) return "null";
        return "'" + getID(tk.getId()) + "':{\"label\":\""
                  + getID(tk.getId())+ "-" + (tk.getReducer() == null ? "END" : tk.getReducer().getName())
                 +"\"" + (hl ? ",\"highlighted\":1" : "") + "},";
    }

    public void generateNode(Task tk, StringBuffer sb) {
        if (tkmap.get(tk.getId()) != null) return;
        sb.append(generateNodeBaseString(tk,false));
        tkmap.put(tk.getId(), tk);
        List<Task> tks = tk.getChildTasks();
        if (tks == null) return;
        for (Task ctk : tks) {
            generateNode(ctk, sb);
        }
    }

    String rootId = null;
    public String generateNodeRelation(String name, QueryPlan qp) {
        StringBuffer sb = new StringBuffer();
        sb.append("var " + name + " = [ ");
        rootId = getID(qp.getFetchTask().getId());
        if(qp.getRootTasks().size() > 0){
	        for (Task tk : qp.getRootTasks()) {
	            generateNodeRelation(tk,"99", sb);
	        }
        }else{
            generateNodeRelation(qp.getFetchTask(),"99", sb);
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append("];");
        return sb.toString();
    }

    public void generateNodeRelation(Task tk, String ptkId, StringBuffer sb) {
        if (ptkId != null) {
            sb.append("["  + ptkId + "," + getID(tk.getId())  + "],");
        }
        List<Task> tkcs = tk.getChildTasks();
        if (tkcs == null) {
            sb.append("["  + getID(tk.getId()) + "," + rootId  + "],");
            return;
        }
        for (Task tkcf : tkcs) {
            generateNodeRelation(tkcf, getID(tk.getId()),sb);
        }
    }%>
    <%
    QueryPlanHelper.setHook(HiveBase.getCurConf());
    final JspWriter outt = out;
    QueryPlanHelper.setCallBack(new QueryPlanHelper.QueryPlanCallBack() {

        public void call(QueryPlan qp) {
            try {
                outt.println(generateNode("nodes1", qp));
                outt.println(generateNodeRelation("edges1", qp));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    });
    %>
<body>
	<br>
	<h1>Table of DAGs</h1>
	<% String sql = request.getParameter("sql"); %>
	<%
    String dsql =  "select (lrr.lr * rr.rolepecent) jg, lrr.userid " + "           from  " + "          (   "
    + "             select sum((sellmoney - sellamount * 1000)) lr, userid   "
    + "                  from tmp.sell group by userid   " + "           )  lrr  "
    + "       join  " + "           ( " + "             select u.userid,r.roleid, r.rolepecent "
    + "                from tmp.user u "
    + "               join tmp.role r on(u.roleid = r.roleid) " + "           ) rr  "
    + "       on  (lrr.userid = rr.userid) " + "     order by jg desc limit 10";
	if(sql == null || sql.trim().length()<=0) sql = dsql;
    %>
	<form name="sqlanalyze" action="queryplan.jsp" method="post">
		<textarea rows="20" cols="100" id="sql" name="sql"><%=sql %></textarea>
		<button type="submit">analyze</button>
	</form>
	<br>
	<p>You can draw many graphs at the same time, in one container or
		more.</p>
	<p>
		See data used in <a href="data.js">data.js</a>, configuration applied
		in <a href="dag_settings.js">dag_settings.js</a>, source in <a
			href="app.js">app.js</a>.
	</p>
	
	<br>
	<script type="text/javascript">
	<%
            /*
            Stage-1 is a root stage
            Stage-2 depends on stages: Stage-1, Stage-5
            Stage-3 depends on stages: Stage-2
            Stage-5 is a root stage
            Stage-0 is a root stage
            */
            HiveBase.run(sql);
     %>
	var dag1 = {'nodes':nodes1, 'edges':edges1};
	</script>
	<script type="text/javascript" src="js/type1/dag_settings.js"></script>
	<script type="text/javascript">
	Ext.create('Ext.panel.Panel', {
		title : 'Table of Directed Acyclic Graphs',
		x: 0,
		y: 0,
		items : [ {
			xtype: 'dagdrawer',
			settings : dag_settings,
			dag : dag1,
			computeLayout : maxUpOrDownLayerLayout,
			height: 400,
		}],
		autoScroll : true,
		layout : {
			type : 'table',
			columns : 2
		},
		resizable: {
		    constrain: true,
		    constrainTo: Ext.getBody(),
		    dynamic: true,
		    pinned: true,
		    handles: 'all'
		},
		renderTo : Ext.getBody()
	});	
	</script>
</body>
</html>