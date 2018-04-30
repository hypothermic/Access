package nl.hypothermic.tcaccess.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import org.bukkit.Bukkit;

public class AccessServer extends Thread {
	
	SSLServerSocketFactory fac = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
	private ServerSocket listener;
	private String addr;
	private int port;
	
	// Connected UUID's
	private static HashSet<String> uuidList = new HashSet<String>();
	// Allowed UUID's
	private static HashSet<String> uuidAllowed = new HashSet<String>();
	
	//private boolean isRunning;

	public AccessServer(/*String addr,*/ int port) {
		//this.addr = addr;
		this.port = port;
		try {
			listener = fac.createServerSocket(port/*, 1, (SocketAddress) InetSocketAddress.createUnresolved(addr, port)*/);
		} catch (IOException e) {
			// should not happen in any case
			e.printStackTrace();
			throw new RuntimeException("[Access] Failed to create server socket! " + e.getCause());
		}
	}
	
	public void run() {
		try {
			//listener.bind(new InetSocketAddress(addr, port));
        	while (true) { //while(this.isRunning)
        		System.out.println(listener.toString());
            	new AccessHandler(listener.accept()).start();
            }
		} catch (IOException iox) {
			//Bukkit.getLogger().log(Level.SEVERE, "IOException in AccessServer: " + iox.getCause());
			iox.printStackTrace();
		} finally {
			if (listener != null) {
				if (!listener.isClosed()) {
					try {
						listener.close();
					} catch (IOException iox) {
						//Bukkit.getLogger().log(Level.SEVERE, "Failed to close listener socket");
					}
				}
			}
		}
	}
	
	public Integer getPort() {
		if (listener != null) {
			return listener.getLocalPort();
		} else {
			return null;
		}
	}
	
	public String getAddr() {
		if (listener != null) {
			return listener.getLocalSocketAddress().toString();
		} else {
			return "Socket not initialized";
		}
	}
	
	public static void allowUUID(String uuid) {
		uuidAllowed.add(uuid);
	}
	
	public static void disallowUUID(String uuid) {
		uuidAllowed.remove(uuid);
	}
	
	public static void addUUID(String uuid) {
		uuidList.add(uuid);
	}
	
	public static void removeUUID(String uuid) {
		uuidList.remove(uuid);
	}
	
	public static HashSet<String> getConnectedUUIDs() {
		return uuidList;
	}
	
	public static HashSet<String> getAllowedUUIDs() {
		return uuidAllowed;
	}
	
	public static boolean verifyUUID(String uuid) {
		return uuidAllowed.contains(uuid) && !uuidList.contains(uuid);
	}
}
