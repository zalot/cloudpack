#!/bin/bash
if [ `id -u` = 0 ]
then
    echo "********************************************************************"
    echo "** Error: root (the superuser) can't run this script!"
    echo "********************************************************************"
    exit 1
fi

cd `dirname $0`
BIN_DIR=`pwd`

#import home var env
. $BIN_DIR/env.sh

cd ..
DEPLOY_HOME=`pwd`

HOST_NAME=`hostname`
LOG_DIR=$OUTPUT_HOME/logs

if [ ! -r $BIN_DIR/env.sh ]; then
    echo "********************************************************************"
    echo "** Error: $BIN_DIR/env.sh not exist! "
    echo "** Please execute: "
    echo "**   cd $DEPLOY_HOME "
    echo "**   antxconfig ."
    echo "** or: "
    echo "**   mvn autoconf:autoconf"
    echo "********************************************************************"    
    exit 1
fi

if [ ! -r $JAVA_HOME/bin/java ]; then
    echo "********************************************************************"
    echo "** Error: PC2.javahome=$JAVA_HOME not exist!"
    echo "********************************************************************"    
    exit 1
fi

if [ $PRODUCTION = "run" ]; then
	#after the environment of online is all 64-bit,the below if and else judgement can be delete
    str=`file $JAVA_HOME/bin/java | grep 64-bit`
    if [ -n "$str" ]; then
        #let memTotal=`cat /proc/meminfo |grep MemTotal|awk '{printf "%d", $2/1024 }'`
        #if [ $memTotal -gt 2500 ];then
            JAVA_MEM_OPTS=" -server -Xmx2g -Xms2g -Xmn256m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
        #else 
        #    JAVA_MEM_OPTS=" -server -Xmx1g -Xms1g -Xmn256m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
        #fi
    else
		JAVA_MEM_OPTS=" -server -Xms1024m -Xmx1024m -XX:PermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
    fi
            
    JAVA_OPTS=" $JAVA_MEM_OPTS "
    
elif [ $PRODUCTION = "test" ]; then
	JAVA_MEM_OPTS=" -server -Xms1024m -Xmx1024m -XX:MaxPermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
	JAVA_OPTS=" $JAVA_MEM_OPTS "
	
elif [ $PRODUCTION = "dev" ]; then
    #we shuold reduce resource usage on developing mode
    JAVA_MEM_OPTS=" -server -Xms64m -Xmx1024m -XX:MaxPermSize=128m "
    JAVA_OPTS=" $JAVA_MEM_OPTS $JAVA_DEBUG_OPT "
    
else
    echo "********************************************************************"
    echo "** Error: production=$PRODUCTION should be only: run, test, dev!"
    echo "********************************************************************"    
    exit 1
fi

if [ "$1" = "debug" ]; then
	DEBUG_SUSPEND="n"
	if [ "$2" = "suspend" ]; then
		DEBUG_SUSPEND="y"
	fi
	JAVA_OPTS="$JAVA_OPTS -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=$DEBUG_PORT,server=y,suspend=$DEBUG_SUSPEND"
fi
if [ "$1" = "jmx" ]; then
	JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
fi

export JAVA_OPTS=" $JAVA_OPTS -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true"

EXIST_PIDS=`ps  --no-heading -C java -f --width 1000 | grep "$DEPLOY_HOME" |awk '{print $2}'`
if [ ! -z "$EXIST_PIDS" ]; then
    echo "PC2 service server $HOST_NAME already started!"
    echo "PID: $EXIST_PIDS"
    exit;
fi

if [ ! -d $OUTPUT_HOME ]; then
	mkdir $OUTPUT_HOME
fi
if [ ! -d $LOG_DIR ]; then
	mkdir $LOG_DIR
fi

CONFIG_DIR=$DEPLOY_HOME/conf
LIB_JARS=$DEPLOY_HOME/lib/*
echo -e "Starting hbase.replication.producer @ $HOST_NAME ...\c"

STDOUT_LOG=$LOG_DIR/stdout.log
nohup $JAVA_HOME/bin/java $JAVA_OPTS -classpath $CONFIG_DIR:$LIB_JARS com.alibaba.hbase.replication.server.Producer >> $STDOUT_LOG 2>&1 &

echo "OK!"
START_PID=`ps  --no-heading -C java -f --width 1000 | grep "$DEPLOY_HOME" |awk '{print $2}'`
echo "PID: $START_PID"
