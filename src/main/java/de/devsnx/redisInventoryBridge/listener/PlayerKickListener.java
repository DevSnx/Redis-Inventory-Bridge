package de.devsnx.redisInventoryBridge.listener;

import de.devsnx.redisInventoryBridge.manager.RedisManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.logging.Logger;

/**
 * @author Marvin Hänel (DevSnx)
 * @since 24.10.2024 19:52
 */

public class PlayerKickListener implements Listener {

    private RedisManager redisManager;
    private Logger logger;

    public PlayerKickListener(RedisManager redisManager) {
        this.redisManager = redisManager;
        this.logger = Bukkit.getLogger();
    }

    /**
     * Event-Handler, wenn ein Spieler gekickt wird.
     * Speichert das Inventar des Spielers in Redis.
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        redisManager.savePlayerInventory(event.getPlayer());
    }

}