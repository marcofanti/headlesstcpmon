package org.apache.ws.commons.tcpmon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a connection listens to a single current connection
 */
class ConnectionNoAWT extends Thread {

	/**
	 * Field active
	 */
	boolean active;

	/**
	 * Field fromHost
	 */
	String fromHost;

	/**
	 * Field time
	 */
	String time;

	/**
	 * Field elapsed time
	 */
	long elapsedTime;

	/**
	 * Field inSocket
	 */
	Socket inSocket = null;

	/**
	 * Field outSocket
	 */
	Socket outSocket = null;

	/**
	 * Field clientThread
	 */
	Thread clientThread = null;

	/**
	 * Field serverThread
	 */
	Thread serverThread = null;

	/**
	 * Field rr1
	 */
	SocketRRNoAWT rr1 = null;

	/**
	 * Field rr2
	 */
	SocketRRNoAWT rr2 = null;

	/**
	 * Field inputStream
	 */
	InputStream inputStream = null;

	String targetHost = null;
	int targetPort = 0;
	int listenPort = 0;
	private static final String TAG = ConnectionNoAWT.class.getName();
	
    private static final Logger logger = LoggerFactory.getLogger(TAG);

	private static String lastURL = "";
	/**
	 * Constructor Connection
	 *
	 * @param l
	 * @param s
	 */
	public ConnectionNoAWT(Socket s, String targetHost, int targetPort, int listenPort) {
		this.targetHost = targetHost;
		this.targetPort = targetPort;
		this.listenPort = listenPort;
		inSocket = s;
		start();
	}

	/**
	 * Constructor Connection
	 *
	 * @param l
	 * @param in
	 */
	public ConnectionNoAWT(InputStream in, String targetHost, int targetPort, int listenPort) {
		this.targetHost = targetHost;
		this.targetPort = targetPort;
		this.listenPort = listenPort;
		inputStream = in;
		start();
	}

	/**
	 * Method run
	 */
	public void run() {
		try {
			active = true;
			if (inSocket != null) {
				fromHost = (inSocket.getInetAddress()).getHostName();
			} else {
				fromHost = "resend";
			}
			String dateformat = TCPMon.getMessage("dateformat00", "yyyy-MM-dd HH:mm:ss");
			DateFormat df = new SimpleDateFormat(dateformat);
			time = df.format(new Date());

			InputStream tmpIn1 = inputStream;
			OutputStream tmpOut1 = null;
			InputStream tmpIn2 = null;
			OutputStream tmpOut2 = null;
			if (tmpIn1 == null) {
				tmpIn1 = inSocket.getInputStream();
			}
			
			String getHost = "";

			if (inSocket != null) {
				tmpOut1 = inSocket.getOutputStream();
				getHost = inSocket.getInetAddress() + "";
			}
			String bufferedData = null;
			StringBuffer buf = null;

			//
			// Change Host: header to point to correct host
			//
			byte[] b1 = new byte[1];
			buf = new StringBuffer();
			String s1;
			String lastLine = null;
			boolean hasCookieHeader = false;
			String getHeader = "";
			String postHeader = null;
			String contentLengthString = null;
			String getOrPost = null;
			String newHost =  null;
			String connectionString =  null;
			
			for (;;) {
				int len;
				len = tmpIn1.read(b1, 0, 1);
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

				String upperLine = line.toUpperCase();
				
				// check to see if we have found GET: header
				if (upperLine.startsWith("GET ")) {
					getOrPost = line;
					getHeader = line.substring(4).trim();
					continue;
				}

				// check to see if we have found POST: header
				else if (upperLine.startsWith("POST ")) {
					getOrPost = line;
					postHeader = line.substring(5).trim();
					continue;
				}

				// check to see if we have found Content Length: header
				else if (upperLine.startsWith("CONNECTION: ")) {
					connectionString = line.substring("CONNECTION: ".length()).trim();
					continue;
				}

				// check to see if we have found Connection: header
				else if (upperLine.startsWith("CONTENT-LENGTH: ")) {
					contentLengthString = line.substring("Content-Length ".length()).trim();
					continue;
				}

				// check to see if we have found Host: header
				else if (upperLine.startsWith("HOST: ")) {
					// we need to update the hostname to target host
					getHost = line.substring(6).trim();
					newHost = "Host: " + targetHost + ":" + targetPort + "\r\n";
					//bufferedData = bufferedData.concat(newHost);
					continue;
				}
				
				// check to see if we have found Cookie: header
				else if (line.startsWith("Cookie: ")) {
					if (!line.startsWith("Cookie: JSESSIONID")) {
						hasCookieHeader = true;
					}
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

			if (targetPort == -1) {
				targetPort = 80;
			}
		
			String checkForURL = TcpMonNoAWT.getMessage("checkForURL", "/login HTTP/1.1");
			int checkForURLLength = checkForURL.length() - " HTTP/1.1".length();
			if (!hasCookieHeader) {
				if (getHeader != null && getHeader.endsWith(checkForURL) ) {
					String forgeRockHost = TcpMonNoAWT.getMessage("forgeRockHost", "");
					String forgeRockPort = TcpMonNoAWT.getMessage("forgeRockPort", "");
//					String redirectURL = TcpMonNoAWT.getMessage("redirectURL", "");
					
						int blank = getHeader.indexOf(" ");
						if (blank > checkForURLLength) {
							getHeader = getHeader.substring(0, blank - checkForURLLength);
						}
					System.out.println("lastURL " + lastURL);
//					if (lastURL != getHeader && lastURL.trim().length() > 0 && getHeader.trim().length() > 0) {
//						lastURL = getHeader;
	
						System.out.println("getHeader " + getHeader);
						String page = "HTTP/1.1 302 Found\r\n" + 
								"Location: " + forgeRockHost + ":" + forgeRockPort + "/openam/XUI/?goto=http://" + getHost + getHeader + "&realm=/#login/\r\n\r\n";
						//"Location: " + forgeRockHost + ":" + forgeRockPort + "/openam/XUI/?goto=" + redirectURL + "&realm=/#login/\r\n\r\n";
						System.out.println("page " + page);
						tmpOut1.write(page.getBytes());
						tmpOut1.flush();
						halt();
						return;
					//}
				} 
			}
			System.out.println("Buffered Data:\n" + bufferedData + "End\n");
			
			String responseType = "response";
			if (getHeader.equals("/examples/servlets/servlet/CookieExample HTTP/1.1")) {
				responseType = "specialResponse";
			} else if (getHeader.endsWith(".jsp HTTP/1.1")) {
				responseType = "specialResponse";
			}

			ByteBuffer byteBuffer = ByteBuffer.allocate(1000000);
			ByteBuffer byteBuffer2 = null;
			String requestType = "request";
			InputStream newTmpIn1 = null; 
			String data2 = null;
			if (postHeader != null) {
				if (postHeader.endsWith("HTTP/1.1")) {
					requestType = "specialRequest";

					int index = 0;
					System.out.println(tmpIn1.available());
				    while (tmpIn1.available() > 0) {
//				    	System.out.println(index++);
				        byteBuffer.put((byte) tmpIn1.read());
				    }
				    				    
					String requestData = new String(byteBuffer.array());
					int indexData = requestData.indexOf("fgdatawata=");
					Utils utils = new Utils();
					if (indexData > -1) {
						String data = utils.decode(requestData.substring(indexData + "fgdatawata=".length()));
						boolean b = utils.checkData(data);
//						System.out.println("\n\n\n\n" + data);
						System.out.println("CheckData" + b);
						data2 = requestData.substring(0, indexData - 1) + "\r\n\r\n";
						contentLengthString = "" + data2.length();
					}
				}
			}

			System.out.println("Connecting to " + targetHost + " " + targetPort);
			outSocket = new Socket(targetHost, targetPort);
			tmpIn2 = outSocket.getInputStream();
			tmpOut2 = outSocket.getOutputStream();
			
			if (data2 != null) {
				contentLengthString = "Content-Length: " + data2.length() + 1 + "\r\n";
			} else {
				if (contentLengthString == null) {
					contentLengthString = "";
				} else {
					contentLengthString = "Content-Length: " + contentLengthString + "\r\n";
				}
			}
			if (bufferedData != null) {
				bufferedData = getOrPost + newHost + "Connection: " + connectionString + "\r\n" +  contentLengthString + bufferedData;
				byte[] b = bufferedData.getBytes();
				tmpOut2.write(b);
			}

			if (requestType.contentEquals("specialRequest")) {
				byteBuffer2 = ByteBuffer.allocate(data2.length() + 10);
				// this is the channel to the endpoint
				rr1 = new SocketRRNoAWT(this, inSocket, new ByteBufferBackedInputStream(byteBuffer2), outSocket, tmpOut2, 1, requestType);
	
				// this is the channel from the endpoint
				rr2 = new SocketRRNoAWT(this, outSocket, tmpIn2, inSocket, tmpOut1, 0, responseType);
				
			} else {
				// this is the channel to the endpoint
				rr1 = new SocketRRNoAWT(this, inSocket, tmpIn1, outSocket, tmpOut2, 1, requestType);
	
				// this is the channel from the endpoint
				rr2 = new SocketRRNoAWT(this, outSocket, tmpIn2, inSocket, tmpOut1, 0, responseType);
			}

			while ((rr1 != null) || (rr2 != null)) {
				
				// Only loop as long as the connection to the target
				// machine is available - once that's gone we can stop.
				// The old way, loop until both are closed, left us
				// looping forever since no one closed the 1st one.

				if ((null != rr1) && rr1.isDone()) {
					logger.warn("rr1 done     rr1 = null " + (rr1 == null) + " rr2 = null" + (rr2 == null));
					rr1 = null;
				}

				if ((null != rr2) && rr2.isDone()) {
					logger.warn("rr2 done     rr1 = null " + (rr1 == null) + " rr2 = null" + (rr2 == null));
					rr2 = null;
				}

				synchronized (this) {
					this.wait(100); // Safety just incase we're not told to wake up.
				}
			}

			active = false;

		} catch (Exception e) {
			StringWriter st = new StringWriter();
			PrintWriter wr = new PrintWriter(st);
			e.printStackTrace(wr);
			wr.close();
			// something went wrong before we had the output area
			System.out.println(st.toString());
			halt();
		}
	}

	/**
	 * Method wakeUp
	 */
	synchronized void wakeUp() {
		this.notifyAll();
	}

	/**
	 * Method halt
	 */
	public void halt() {
		try {
			if (rr1 != null) {
				rr1.halt();
			}
			if (rr2 != null) {
				rr2.halt();
			}
			if (inSocket != null) {
				inSocket.close();
			}
			inSocket = null;
			if (outSocket != null) {
				outSocket.close();
			}
			outSocket = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method remove
	 */
	public void remove() {
		int index = -1;
		try {
			halt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class ByteBufferBackedInputStream extends InputStream {

		ByteBuffer buf;

		ByteBufferBackedInputStream(ByteBuffer buf) {
			this.buf = buf;
		}

		public synchronized int read() throws IOException {
			if (!buf.hasRemaining()) {
				return -1;
			}
			return buf.get();
		}

		public synchronized int read(byte[] bytes, int off, int len) throws IOException {
			if (!buf.hasRemaining()) {
				return -1;
			}

			len = Math.min(len, buf.remaining());
			buf.get(bytes, off, len);
			return len;
		}
	}
}
