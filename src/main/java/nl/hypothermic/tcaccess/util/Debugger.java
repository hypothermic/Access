package nl.hypothermic.tcaccess.util;

import java.util.UUID;

import nl.hypothermic.tcaccess.net.AccessServer;

public class Debugger {

	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.keyStore", "src/main/resources/ssl/tca.keystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "MySecurePassword");
		AccessServer as = new AccessServer(9097);
		String uuid = UUID.randomUUID().toString();
        AccessServer.allowUUID(uuid);
        System.out.println("Allowed UUID: " + uuid);
		as.run();
	}
}
