#!/bin/bash
if [ `id -u` = 0 ]
then
    echo "********************************************************************"
    echo "ERROR! root (the superuser) can't run this script."
    echo "********************************************************************"
    exit 1
fi

cd `dirname $0`
BIN_DIR=`pwd`

cd ..
DEPLOY_HOME=`pwd`

HOST_NAME=`hostname`

KILL_PIDS=`ps  --no-heading -C java -f --width 1000 | grep "$DEPLOY_HOME" |awk '{print $2}'`
if [ -z "$KILL_PIDS" ]; then
    echo "Replication server $HOST_NAME is not started!"
    exit 1;
fi

# dump java stack
$BIN_DIR/dump.sh

echo -e "Stopping replication server $HOST_NAME ...\c"
for PID in $KILL_PIDS ; do
	kill $PID > /dev/null 2>&1
done

COUNT=0
while [ $COUNT -lt 1 ]; do    
    echo -e ".\c"
    sleep 1
    COUNT=1
    for PID in $KILL_PIDS ; do
		PID_INST=`ps --no-heading -p $PID`
		if [ -n "$PID_INST" ]; then
			COUNT=0
			break
		fi
	done
done
echo "OK!"
echo "PID: $KILL_PIDS"
