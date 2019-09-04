#!/bin/bash

mvn clean install
scp /Users/marcofanti/Documents/Development/fr2/headlesstcpmon/target/tcpmon-1.2.0.jar  tomcat@openam2:/tmp

ssh tomcat@openam2 '/tmp/5.sh'

