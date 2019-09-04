#!/bin/sh

THE_CLASSPATH=
for i in `ls ./lib/*.jar`
do
  THE_CLASSPATH=${THE_CLASSPATH}:${i}
done

THE_CLASSPATH=/tmp/tcpmon-1.2.0.jar:${THE_CLASSPATH}

echo "CLASSPATH = " + ${THE_CLASSPATH}

#---------------------------#
# run the anti-spam program #
#---------------------------#
java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=$1 -cp ".:${THE_CLASSPATH}" org.apache.ws.commons.tcpmon.TcpMonNoAWT $2 $3 $4