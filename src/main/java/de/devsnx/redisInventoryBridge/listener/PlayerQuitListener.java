package de.devsnx.redisInventoryBridge.listener;

import de.devsnx.redisInventoryBridge.RedisInventoryBridge;
import de.devsnx.redisInventoryBridge.manager.RedisManager;
import de.devsnx.redisInventoryBridge.utils.InventoryUtils;
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

        if(RedisInventoryBridge.getInstance().getConfig().getBoolean("saves.hearts") == true) {
            redisManager.savePlayerHearts(event.getPlayer().getUniqueId().toString(), event.getPlayer().getHealth());
        }

        if(RedisInventoryBridge.getInstance().getConfig().getBoolean("saves.foodLevel") == true) {
           redisManager.savePlayerFoodLevel(event.getPlayer().getUniqueId().toString(), event.getPlayer().getFoodLevel());
        }

        if(RedisInventoryBridge.getInstance().getConfig().getBoolean("saves.expLevel") == true) {
            redisManager.savePlayerExpLevel(event.getPlayer().getUniqueId().toString(), event.getPlayer().getLevel());
        }

        if(RedisInventoryBridge.getInstance().getConfig().getBoolean("saves.exp") == true) {
            redisManager.savePlayerExp(event.getPlayer().getUniqueId().toString(), event.getPlayer().getExp());
        }

        if(RedisInventoryBridge.getInstance().getConfig().getBoolean("saves.potions") == true) {
            redisManager.savePlayerPotions(event.getPlayer());
        }

    }

}
