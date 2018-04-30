package nl.hypothermic.tcaccess;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import nl.hypothermic.tcaccess.net.AccessServer;

public class Main extends JavaPlugin {
	
	// keytool -genkey -v -keystore tca.keystore -alias tcassl -keyalg RSA -keysize 4096 -validity 200
	
	private AccessServer as;
	
	@Override public void onEnable() {
		getLogger().info("Loading configuration...");
        this.getConfig().addDefaults(new HashMap<String, Object>() {{ put("server.port", 9097);
        															  put("keystore.location", "TCAccess/tca.keystore");
        															  put("keystore.password", "MySecurePassword"); }});
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
        	this.saveDefaultConfig();
        }
        getLogger().info("Starting AccessServer... [ks=" + this.getConfig().getString("keystore.location") + ", pwd=" + this.getConfig().getString("keystore.password") + "]"); 
        System.setProperty("javax.net.ssl.keyStore", this.getConfig().getString("keystore.location"));
        System.setProperty("javax.net.ssl.keyStorePassword", this.getConfig().getString("keystore.password"));
        as = new AccessServer(this.getConfig().getInt("server.port"));
        as.start();
        getLogger().info("Starting cmd listeners...");
        CommandListener rcl = new CommandListener(this);
        Bukkit.getPluginManager().registerEvents(rcl, this);
		getLogger().info("Load and start complete.");
	}

	@Override public void onDisable() {
		as.stop();
		getLogger().info("Termination complete.");
	}
	
	public AccessServer getAccessServer() {
		return as;
	}
	
	public static boolean execCommand(String cmd) {
		// TODO: create custom sender (implement org.bukkit.command.CommandSender)
		return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
	}
}
