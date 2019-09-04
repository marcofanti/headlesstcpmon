/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ws.commons.tcpmon;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * wait for incoming connections, spawn a connection thread when
 * stuff comes in.
 */
class SocketWaiterNoAWT extends Thread {

   /**
    * Field sSocket
    */
   ServerSocket sSocket = null;

   /**
    * Field pleaseStop
    */
   boolean pleaseStop = false;

   String targetHost = null;
   int targetPort = 0;
   int listenPort = 0;

   

   /**
    * Constructor SocketWaiter
    *
    * @param l
    * @param p
    */
   public SocketWaiterNoAWT(String targetHost, int targetPort, int listenPort) {
		this.targetHost = targetHost;
		this.targetPort = targetPort;
		this.listenPort = listenPort;
       start();
   }

   /**
    * Method run
    */
   public void run() {
       try {
           sSocket = new ServerSocket(listenPort);
           for (; ;) {
               Socket inSocket = sSocket.accept();
               if (pleaseStop) {
                   break;
               }
               new ConnectionNoAWT(inSocket, targetHost, targetPort, listenPort);
               inSocket = null;
           }
       } catch (Exception exp) {
           exp.printStackTrace();
       }
   }

   /**
    * force a halt by connecting to self and then closing the server socket
    */
   public void halt() {
       try {
           pleaseStop = true;
           new Socket("127.0.0.1", listenPort);
           if (sSocket != null) {
               sSocket.close();
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}
