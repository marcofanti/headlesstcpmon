/**
 *
 * Copyright 2000-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.ws.commons.tcpmon;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

/**
 * A <code>TcpTunnel</code> object listens on the given port,
 * and once <code>Start</code> is pressed, will forward all bytes
 * to the given host and port.
 *
 * @author Sanjiva Weerawarana (sanjiva@watson.ibm.com)
 */
public class TcpTunnel {
	   private static String targetHost = null;
	   private static int targetPort = 0;
	   private static int listenPort = 0;


    public static void main(String args[]) throws IOException {
        if (args.length != 3 && args.length != 4) {
            System.err.println("Usage: java TcpTunnel listenport tunnelhost tunnelport [encoding]");
            System.exit(1);
        }
        listenPort = Integer.parseInt(args[0]);
        String targetHost = args[1];
        int targetPort = Integer.parseInt(args[2]);
        String enc;
        if (args.length == 4) {
            enc = args[3];
        } else {
            enc = "8859_1";
        }
        System.out.println("TcpTunnel: ready to rock and roll on port " + listenPort);
        ServerSocket ss = new ServerSocket(listenPort);
        while (true) {
            // accept the connection from my client
            Socket sc = ss.accept();

            // connect to the thing I'm tunnelling for
            Socket st = new Socket(targetHost, targetPort);
            System.out.println("TcpTunnel: tunnelling port " + listenPort + " to port " + targetPort + " on host " + targetHost);

            // relay the stuff thru
            new Relay(sc.getInputStream(), st.getOutputStream(), System.out, enc).start();
            new Relay(st.getInputStream(), sc.getOutputStream(), System.out, enc).start();
            // that's it .. they're off; now I go back to my stuff.
        }
    }
    /**
     * Field messages
     */
    public static ResourceBundle messages = null;

    /**
     * Get the message with the given key.  There are no arguments for this message.
     *
     * @param key
     * @param defaultMsg
     * @return string
     */
    public static String getMessage(String key, String defaultMsg) {
        try {
            if (messages == null) {
                initializeMessages();
            }
            return messages.getString(key);
        } catch (Throwable t) {

            // If there is any problem whatsoever getting the internationalized
            // message, return the default.
            return defaultMsg;
        }
    }

    /**
     * Load the resource bundle messages from the properties file.  This is ONLY done when it is
     * needed.  If no messages are printed (for example, only Wsdl2java is being run in non-
     * verbose mode) then there is no need to read the properties file.
     */
    private static void initializeMessages() {
        messages = ResourceBundle.getBundle("org.apache.ws.commons.tcpmon.tcpmon");
    }


}
