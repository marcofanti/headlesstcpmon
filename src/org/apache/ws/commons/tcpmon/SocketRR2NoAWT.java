package org.apache.ws.commons.tcpmon;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this class handles the pumping of data from the incoming socket to the
 * outgoing socket
 */
class SocketRRNoAWT extends Thread {
	private static final String TAG = SocketRRNoAWT.class.getName();
    private final Logger logger = LoggerFactory.getLogger(TAG);
	/**
     * Field inSocket
     */
    Socket inSocket = null;

    /**
     * Field outSocket
     */
    Socket outSocket = null;

    /**
     * Field in
     */
    InputStream in = null;

    /**
     * Field out
     */
    OutputStream out = null;

    /**
     * Field done
     */
    volatile boolean done = false;

    /**
     * Field tmodel
     */
    volatile long elapsed = 0;
    
    /**
     * Field type
     */
    String type = null;

    /**
     * Field myConnection
     */
    ConnectionNoAWT myConnection = null;


    /**
     * Constructor SocketRR
     *
     * @param c
     * @param inputSocket
     * @param inputStream
     * @param outputSocket
     * @param outputStream
     * @param _textArea
     * @param format
     * @param tModel
     * @param index
     * @param type
     * @param slowLink
     */
    public SocketRRNoAWT(ConnectionNoAWT c, Socket inputSocket,
                    InputStream inputStream, Socket outputSocket,
                    OutputStream outputStream, int index,
                    final String type) {
        inSocket = inputSocket;
        in = inputStream;
        outSocket = outputSocket;
        out = outputStream;
        this.type = type;
        myConnection = c;
        
        start();
    }

    /**
     * Method isDone
     *
     * @return boolean
     */
    public boolean isDone() {
        return done;
    }

    public String getElapsed() {
    		return String.valueOf(elapsed);
    }
    
    /**
     * Method run
     */
    public void run() {
        try {
            byte[] buffer = new byte[100000];
            byte[] tmpbuffer = new byte[200000];
            int saved = 0;
            int len;
            int i1, i2;
            int i;
            int reqSaved = 0;
            int tabWidth = 3;
            boolean atMargin = true;
            int thisIndent = -1, nextIndent = -1, previousIndent = -1;
            long start = System.currentTimeMillis();
            a:
            for (; ;) {
            	int index = 0;
                elapsed = System.currentTimeMillis() - start;
            	
                if (done) {
                    break;
                }
                
                // try{
                // len = in.available();
                // }catch(Exception e){len=0;}
                len = buffer.length;

                // Used to be 1, but if we block it doesn't matter
                // however 1 will break with some servers, including apache
                if (len == 0) {
                    len = buffer.length;
                }
                if (saved + len > buffer.length) {
                    len = buffer.length - saved;
                }
                int len1 = 0;
                while (len1 == 0) {
                    try {
                    	logger.info("available" + in.available());
                        len1 = in.read(buffer, saved, len);
                        logger.info("buffer:" + new String(buffer) + ":");
//                        System.out.println("saved:" + new String(saved) + ":");
                    } catch (Exception ex) {
                        if (done && (saved == 0)) {
                            break a;
                        }
                        len1 = -1;
                        break;
                    }
                }
                len = len1;
                if ((len == -1) && (saved == 0)) {
                    break;
                }
                if (len == -1) {
                    done = true;
                }
                
                // No matter how we may (or may not) format it, send it
                // on unformatted - we don't want to mess with how its
                // sent to the other side, just how its displayed
                
                if (type.equals("specialResponse")) {
                	if ((out != null) && (len > 0)) {
                    	byte[] newBuffer = com.behaviosec.proxy.ByteUtils.subbytes(buffer, saved, saved + len);
                    	String bufferString = new String(newBuffer);
                    	int indexBreak = bufferString.indexOf("\r\n\r\n");
                    	int indexContentLength = bufferString.indexOf("Content-Length: ") + "Content-Length: ".length();
                    	int sizeContentLength = bufferString.substring(indexContentLength).indexOf("\r\n");
                    	String contentLengthString = bufferString.substring(indexContentLength, indexContentLength + sizeContentLength);
                    	int contentLengthInt = Integer.parseInt(contentLengthString);
                    	String beforeContentLength = bufferString.substring(0, indexContentLength); 
                    	String afterContentLength = bufferString.substring(indexContentLength + sizeContentLength, indexBreak); 
                    	String whatToSearch1 = "</body>";
                    	int index1 = bufferString.toLowerCase().indexOf(whatToSearch1);
                    	if (index1 > 10) {
                    		//out.write()
                    		String bufferString1 = bufferString.substring(indexBreak, index1 + whatToSearch1.length());
                        	String whatToSearch2 = "</form>";
                        	int index2 = bufferString1.toLowerCase().indexOf(whatToSearch2);
                    		String bufferString1a = bufferString1.substring(0, index2);
                    		String bufferString1b = bufferString1.substring(index2);

                    		String bufferString2 = bufferString.substring(index1 + whatToSearch1.length() + 1);
                    		String hidden = "<input type=\"hidden\" class=\"form-hidden\" name=\"fgdatawata\">";
                    		int contentLength = contentLengthInt + +hidden.length() + TcpMonNoAWT.scriptLength - 2;
                    				
                    				//731;//bufferString1.getBytes().length+ bufferString2.getBytes().length + TcpMonNoAWT.scriptLength;
                    		
                    		System.out.println("Sizes " + bufferString1.getBytes().length + " " + bufferString2.getBytes().length +
                    		" " + (bufferString1.getBytes().length+ bufferString2.getBytes().length));
                    		
                    		String a = TcpMonNoAWT.script;
                    		String bufferString0 = beforeContentLength + contentLength + afterContentLength;
                    		
                    		out.write(bufferString0.getBytes(), 0, bufferString0.length());
                     		out.write(bufferString1a.getBytes(), 0, bufferString1a.length());
                     		out.write(hidden.getBytes(), 0, hidden.length());
                     		out.write(bufferString1b.getBytes(), 0, bufferString1b.length());
                       		out.write(a.getBytes(), 0, TcpMonNoAWT.scriptLength);
                    		out.write(bufferString2.getBytes(), 0, bufferString2.length());
                 		
                    		System.out.println("\n\n\n\n\n" + bufferString0 + bufferString1a + hidden + bufferString1b + a + bufferString2);
                    		
                    //		+ "<script>" + TcpMonNoAWT.script +
                    		//		"</script>" + bufferString.substring(index1 + 7);    				
                    	}
	                    out.flush();
//	                    System.out.println(type + index + " " + myConnection.targetHost + " "  + myConnection.targetPort  + "->\n" + new String(bufferString));
	                    break;
	                }  
                } else {                
	                if ((out != null) && (len > 0)) {
//	                	System.out.println(type + index + " " + myConnection.targetHost + " "  + myConnection.targetPort  + "->\n" + new String(buffer, 0, len));	      
	                    out.write(buffer, saved, len);
	                }
                }
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            done = true;
            try {
                if (out != null) {
                    out.flush();
                    if (null != outSocket) {
                        outSocket.shutdownOutput();
                    } else {
                        out.close();
                    }
                    out = null;
                }
            } catch (Exception e) {
            }
            try {
                if (in != null) {
                    if (inSocket != null) {
                        inSocket.shutdownInput();
                    } else {
                        in.close();
                    }
                    in = null;
                }
            } catch (Exception e) {
            }
            myConnection.wakeUp();
        }
    }

    /**
     * Method halt
     */
    public void halt() {
        try {
            if (inSocket != null) {
                inSocket.close();
            }
            if (outSocket != null) {
                outSocket.close();
            }
            inSocket = null;
            outSocket = null;
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            in = null;
            out = null;
            done = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
