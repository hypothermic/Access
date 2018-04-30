package nl.hypothermic.tcaccess.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import nl.hypothermic.tcaccess.Main;

public class AccessHandler extends Thread {
	
	// [S] ANCE[ASv*        = announce
	// [C] AUTH[<uuid>      = client sends uuid key
	// [S] AUCP[(DONE|ERR)  = authentication successfull

	private Socket s;
	private String uuid;
	
	public AccessHandler(Socket s) {
		this.s = s;
	}
	
	public void run() {
		try {
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			DataInputStream in = new DataInputStream(s.getInputStream());
			
			// AUTH stage
			while (true) {
				out.writeUTF("ANCE|ASv1");
				uuid = in.readUTF();
				if (uuid == null || uuid.length() < 6 || uuid.length() > 80 || !uuid.startsWith("AUTH|")) {
					out.writeUTF("AUCP|ERR");
					return;
				}
				uuid = uuid.substring(5);
                if (AccessServer.verifyUUID(uuid)) {
                	AccessServer.addUUID(uuid);
                	out.writeUTF("AUCP|DONE");
                	break;
                } else {
                	out.writeUTF("AUCP|ERR");
                }
			}
			
			// SECURE stage
			while (true) {
				String cmd = in.readUTF();
				// Only allow MC commands for right now.
				if (cmd == null || cmd.length() < 5 || cmd.length() > 60) {
					out.writeUTF("FAIL|PARAM");
					return;
				}
				if (cmd.startsWith("CMD|")) {
					cmd = cmd.substring(4);
					Bukkit.getLogger().info(uuid + " is executing command: " + cmd);
					if (Main.execCommand(cmd)) {
						out.writeUTF("CMD|DONE");
					} else {
						out.writeUTF("CMD|ERR");
					}
					return;
				}/* else if (cmd.startsWith("LS|PLAYERS|RQ")) {
					StringBuffer names = new StringBuffer();
					for (Player p : Bukkit.getOnlinePlayers()) {
						names.append(p.getName());
					}
					out.writeUTF("LS|PLAYERS|RS");
				}*/
				out.writeUTF("FAIL|NOCMD");
			}
		} catch (EOFException eofx) {
			;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Client disconnect
			if (uuid != null) {
				AccessServer.removeUUID(uuid);
			}
			try {
				s.close();
			} catch (IOException e) {
				;
			}
		}
	}
}
