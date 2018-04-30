package nl.hypothermic.tcaccess.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class DebugClient {

	public static void main(String[] args) {
		String authkey = "137d29a8-cc45-4c7d-b2cc-fd7863a87909";
		SSLSocketFactory f = (SSLSocketFactory) SSLSocketFactory.getDefault();
		//
		TrustManager[] trustAllCerts = new TrustManager[] { 
				    new X509TrustManager() {     
				        public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
				            return new X509Certificate[0];
				        } 
				        public void checkClientTrusted( 
				            java.security.cert.X509Certificate[] certs, String authType) {
				        } 
				        public void checkServerTrusted( 
				            java.security.cert.X509Certificate[] certs, String authType) {
				        }
				    } 
		};
		try {
			SSLContext sc = SSLContext.getInstance("SSL"); 
		    sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
			SSLSocket c = (SSLSocket) sc.getSocketFactory().createSocket("0.0.0.0", 9097);

		 	c.startHandshake();
		 	DataOutputStream dos = new DataOutputStream(c.getOutputStream());
		 	DataInputStream dis = new DataInputStream(c.getInputStream());
		 	while (true) {
		 		String input = dis.readUTF();
		 		System.out.println("CLIENT: RECEIVED " + input);
		 		if (input.startsWith("ANCE|")) {
		 			if (input.endsWith("v1")) {
		 				dos.writeUTF("AUTH|" + authkey);
		 			} else {
		 				c.close();
		 				throw new RuntimeException("Unsupported protocol");
		 			}
		 		}
		 		if (input.startsWith("AUCP|")) {
		 			if (input.endsWith("DONE")) {
		 				dos.writeUTF("CMD|time set 0"); // yay it works!
		 				break;
		 			} else if (input.endsWith("ERR")){
		 				throw new RuntimeException("Authentication error");
		 			}
		 		}
		 		if (input.startsWith("FAIL|PARAM")) {
		 			throw new RuntimeException("Server reported param error");
		 		}
		 		if (input.startsWith("CMD|DONE")) {
		 			throw new RuntimeException("Command successfull!");
		 		}
		 		if (input.startsWith("CMD|ERR")) {
		 			throw new RuntimeException("Command error!");
		 		}
		 	}
		 	System.out.println("--- Auth success ---");
		 	while (true) {
		 		String input = dis.readUTF();
		 	}
		} catch (Exception x) {
		    x.printStackTrace();
		}
	}
}
