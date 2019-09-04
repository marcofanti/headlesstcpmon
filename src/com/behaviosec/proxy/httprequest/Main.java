package com.behaviosec.proxy.httprequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
//from   j  a v a 2 s .  co m
public class Main {
  public static void main(String[] args) throws Exception {
	  
	  String url = "/openam/XUI/?goto=http://openam.example.com:8890/home/pep-sso&realm=/#login/";
	  url = "/examples/servlets/servlet/CookieExample";
	  String host = "openam2.example.com";
	  //host = "gmail.com";
	  int port = 80;
	  port = 8080;
	  
    InetAddress addr = InetAddress.getByName(host);
    Socket socket = new Socket(addr, port);
    boolean autoflush = true;
    PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
    StringBuffer sb1 = new StringBuffer();
    port=8090;
    // send an HTTP request to the web server
    sb1.append("GET " + url + " HTTP/1.1\r\n");
    sb1.append("Host: " + host+":"+port + "\r\n");
    sb1.append("Connection: Keep-Alive" + "\r\n");
    sb1.append("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36");
    sb1.append("Sec-Fetch-Mode: navigate" + "\r\n");
    sb1.append("Sec-Fetch-User: ?1" + "\r\n");
    sb1.append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\r\n");
    sb1.append("Sec-Fetch-Site: none\r\n");
    sb1.append("Accept-Encoding: gzip, deflate, br\r\n");
    sb1.append("Accept-Language: en-US,en;q=0.9,pt;q=0.8\r\n");
    sb1.append("\r\n");

    out.print(sb1.toString());
    System.out.println(sb1.toString());
    out.flush();
    //out.close();
    System.out.println("Finished sending");
    // read the response
    boolean loop = true;
    StringBuilder sb = new StringBuilder(200000);
    while (loop) {
    	System.out.println("in ready" + in.ready());
      if (in.ready()) {
        int i = 0;
        while (i != -1) {
          i = in.read();
          sb.append((char) i);
//          System.out.println("sb" + sb);
        }
        loop = false;
      }
    }
    System.out.println(sb.toString());
    socket.close();
  }
}