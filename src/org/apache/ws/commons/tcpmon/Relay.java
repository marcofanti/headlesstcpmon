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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * A <code>Relay</code> object is used by <code>TcpTunnel</code>
 * and <code>TcpTunnelGui</code> to relay bytes from an
 * <code>InputStream</code> to a <code>OutputStream</code>.
 *
 * @author Sanjiva Weerawarana (sanjiva@watson.ibm.com)
 * @author Scott Nichol (snichol@computer.org)
 */
public class Relay extends Thread {
    /**
     * Field rr1
     */
    SocketRRNoAWT rr1 = null;

    /**
     * Field rr2
     */
    SocketRRNoAWT rr2 = null;

    final static int BUFSIZ = 1000;
    InputStream in;
    OutputStream out;
//    byte buf[] = new byte[BUFSIZ];
    OutputStream os;
    String enc = "8859_1";
    Relay(InputStream in, OutputStream out, OutputStream os, String enc) {
        this.in = in;
        this.out = out;
        this.os = os;
        this.enc = enc;
    }
    public String getEncoding() {
        return enc;
    }
    public void run() {
        int n;
        try {
        	
            String targetHost = "localhost";
			String listenPort = "8080";

            String dateformat = TCPMon.getMessage("dateformat00", "yyyy-MM-dd HH:mm:ss");
            DateFormat df = new SimpleDateFormat(dateformat);
            StringBuffer buf = new StringBuffer();
            String bufferedData = null;

            // 
            // Change Host: header to point to correct host
            // 
            byte[] b1 = new byte[1];
            String s1;
            String lastLine = null;
            for (; ;) {
                int len;
                len = in.read(b1, 0, 1);
                if (len == -1) {
                    break;
                }
                s1 = new String(b1);
                buf.append(s1);
                if (b1[0] != '\n') {
                    continue;
                }

                // we have a complete line
                String line = buf.toString();
                buf.setLength(0);
                System.out.println(line);
                // check to see if we have found Host: header
                if (line.startsWith("Host: ")) {

					// we need to update the hostname to target host
                    String newHost = "Host: " + targetHost + ":"
                            + listenPort + "\r\n";
                    bufferedData = bufferedData.concat(newHost);
                    break;
                }

                // add it to our headers so far
                if (bufferedData == null) {
                    bufferedData = line;
                } else {
                    bufferedData = bufferedData.concat(line);
                }

                // failsafe
                if (line.equals("\r\n")) {
                    break;
                }
                if ("\n".equals(lastLine) && line.equals("\n")) {
                    break;
                }
                lastLine = line;
            }
            if (bufferedData != null) {
//                inputText.append(bufferedData);
                int idx = (bufferedData.length() < 50)
                        ? bufferedData.length()
                        : 50;
                s1 = bufferedData.substring(0, idx);
                int i = s1.indexOf('\n');
                if (i > 0) {
                    s1 = s1.substring(0, i - 1);
                }
                s1 = s1 + "                           "
                        + "                       ";
                s1 = s1.substring(0, 51);
//                listener.tableModel.setValueAt(s1, index + 1,
//                		TCPMon.REQ_COLUMN);
            }
        	
        	System.out.println(bufferedData);
        	
        	byte[] byteArray = bufferedData.toString().getBytes();
        	out.write(byteArray, 0, byteArray.length);
        	
        	if (os != null) {
                os.write(byteArray, 0, byteArray.length);
                os.flush();
            }
  /*      	
            // this is the channel to the endpoint
            rr1 = new SocketRRNoAWT(this, inSocket, tmpIn1, outSocket, tmpOut2, 1, "request:");

            // this is the channel from the endpoint
            rr2 = new SocketRRNoAWT(this, outSocket, tmpIn2, inSocket, tmpOut1, 0, "response:");

            while ((rr1 != null) || (rr2 != null)) {

            		if (rr2 != null) {
            			listener.tableModel.setValueAt(rr2.getElapsed(), 1 + index, TCPMon.ELAPSED_COLUMN);
            		}
            		
                // Only loop as long as the connection to the target
                // machine is available - once that's gone we can stop.
                // The old way, loop until both are closed, left us
                // looping forever since no one closed the 1st one.
            	
                if ((null != rr1) && rr1.isDone()) {
                    if ((index >= 0) && (rr2 != null)) {
                        listener.tableModel.setValueAt(
                                TCPMon.getMessage("resp00", "Resp"), 1 + index,
                                TCPMon.STATE_COLUMN);
                    }
                    rr1 = null;
                }

                if ((null != rr2) && rr2.isDone()) {
                    if ((index >= 0) && (rr1 != null)) {
                        listener.tableModel.setValueAt(
                                TCPMon.getMessage("req00", "Req"), 1 + index,
                                TCPMon.STATE_COLUMN);
                    }
                    rr2 = null;
                }

                synchronized (this) {
                    this.wait(100);    // Safety just incase we're not told to wake up.
                }
            }

            active = false;

            if (index >= 0) {
                listener.tableModel.setValueAt(
                        TCPMon.getMessage("done00", "Done"),
                        1 + index, TCPMon.STATE_COLUMN);
            }

        } catch (Exception e) {
            StringWriter st = new StringWriter();
            PrintWriter wr = new PrintWriter(st);
            int index = listener.connections.indexOf(this);
            if (index >= 0) {
                listener.tableModel.setValueAt(
                        TCPMon.getMessage("error00", "Error"), 1 + index,
                        TCPMon.STATE_COLUMN);
            }
            e.printStackTrace(wr);
            wr.close();
            if (outputText != null) {
                outputText.append(st.toString());
            } else {
                // something went wrong before we had the output area
                System.out.println(st.toString());
            }
            halt();
        }
*/
        	
 /*       	
        	
        	
        	
        	
            while ((n = in.read(buf)) > 0) {
                out.write(buf, 0, n);
                out.flush();
                if (os != null) {
                    os.write(buf, 0, n);
                    os.flush();
                }
            } */
        } catch (IOException e) {
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
            }
        }
    }
    public void setEncoding(String enc) {
        this.enc = enc;
    }
}
