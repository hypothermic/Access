package nl.hypothermic.tcaccess;

import static org.bukkit.ChatColor.*; // static

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import nl.hypothermic.tcaccess.net.AccessServer;

public class CommandListener implements Listener {

	public CommandListener(Main main) {
		this.cl = main;
	}
	
	private final Main cl;
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
	    if (event.getMessage().trim().toLowerCase().equals("/tca")) {
	        event.setCancelled(true);
	        event.getPlayer().sendMessage(RED + "- " +        "Access v" + cl.getDescription().getVersion() + " by hypothermic");
	        event.getPlayer().sendMessage(RED + "- " + GRAY + "Status: " + WHITE + (cl.getAccessServer().isAlive() ? "Running" : "Stopped"));
	        event.getPlayer().sendMessage(RED + "- " + GRAY + "Connected clients:   " + WHITE + cl.getAccessServer().getConnectedUUIDs());
	        event.getPlayer().sendMessage(RED + "- " + GRAY + "Activated keys:       " + WHITE + cl.getAccessServer().getAllowedUUIDs());
	        event.getPlayer().sendMessage(RED + "- " + GRAY + "To generate key: " + RED + "/tca generate ");
	    }
	    if (event.getMessage().trim().toLowerCase().equals("/tca generate")) {
	        event.setCancelled(true);
	        String uuid = UUID.randomUUID().toString();
	        AccessServer.allowUUID(uuid);
	        event.getPlayer().sendMessage(RED + "- " + GRAY + "New key activated: " + RED + uuid);
	        cl.getLogger().info(event.getPlayer().getName() + " activated new key " + uuid);
	    }
	}
}
