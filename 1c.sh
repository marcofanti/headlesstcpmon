#!/bin/bash

mvn clean install
jar uf /Users/marcofanti/Documents/Development/fr2/headlesstcpmon/target/tcpmon-1.2.0.jar -C bin/org/apache/ws/commons/tcpmon collector.min.js
cd bin
jar uf /Users/marcofanti/Documents/Development/fr2/headlesstcpmon/target/tcpmon-1.2.0.jar org/apache/ws/commons/tcpmon/tcpmon.properties
cd ..
cp /Users/marcofanti/Documents/Development/fr2/headlesstcpmon/target/tcpmon-1.2.0.jar /tmp
scp /Users/marcofanti/Documents/Development/fr2/headlesstcpmon/target/tcpmon-1.2.0.jar  tomcat@openam2:/tmp
scp /Users/marcofanti/Documents/Development/fr2/headlesstcpmon/lib/*  tomcat@openam2:lib/


