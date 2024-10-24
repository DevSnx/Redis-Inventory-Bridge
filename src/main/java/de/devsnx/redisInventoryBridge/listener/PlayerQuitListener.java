package de.devsnx.redisInventoryBridge.listener;

import de.devsnx.redisInventoryBridge.manager.RedisManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

/**
 * @author Marvin Hänel (DevSnx)
 * @since 24.10.2024 19:40
 */

public class PlayerQuitListener implements Listener {

    private RedisManager redisManager;
    private Logger logger;

    public PlayerQuitListener(RedisManager redisManager) {
        this.redisManager = redisManager;
        this.logger = Bukkit.getLogger();
    }

    /**
     * Event-Handler für das Verlassen eines Spielers.
     * Speichert das Inventar des Spielers in Redis.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        redisManager.savePlayerInventory(event.getPlayer());
    }

}
