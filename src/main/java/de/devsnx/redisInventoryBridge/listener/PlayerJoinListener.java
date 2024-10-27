package de.devsnx.redisInventoryBridge.listener;

import de.devsnx.redisInventoryBridge.RedisInventoryBridge;
import de.devsnx.redisInventoryBridge.manager.RedisManager;
import de.devsnx.redisInventoryBridge.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

/**
 * @author Marvin Hänel (DevSnx)
 * @since 24.10.2024 19:40
 */

public class PlayerJoinListener implements Listener {


    private RedisManager redisManager;
    private Logger logger;

    public PlayerJoinListener(RedisManager redisManager) {
        this.redisManager = redisManager;
        this.logger = Bukkit.getLogger();
    }

    /**
     * Event-Handler für das Joinen eines Spielers.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();
        String serializedInventory = redisManager.getInventory(playerUUID);

        if (serializedInventory != null) {
            ItemStack[] inventory = InventoryUtils.deserializeInventory(serializedInventory);
            player.getInventory().setContents(inventory);

            if(RedisInventoryBridge.getInstance().getConfig().getBoolean("joinmessage.enable") == true) {
                player.sendMessage(RedisInventoryBridge.getInstance().getConfig().getString("joinmessage.message").replace("&", "§"));
            }
        }

        if(RedisInventoryBridge.getInstance().getConfig().getBoolean("saves.hearts") == true) {
            String hearts = redisManager.getPlayerHearts(playerUUID);
            if (hearts != null) {
                player.setHealth(Double.parseDouble(hearts));
            }
        }

        if(RedisInventoryBridge.getInstance().getConfig().getBoolean("saves.foodLevel") == true) {
            String foodLevel = redisManager.getPlayerFoodLevel(playerUUID);
            if (foodLevel != null) {
                player.setFoodLevel(Integer.parseInt(foodLevel));
            }
        }

        if(RedisInventoryBridge.getInstance().getConfig().getBoolean("saves.expLevel") == true) {
            String expLevel = redisManager.getPlayerExpLevel(playerUUID);
            if (expLevel != null) {
                player.setLevel(Integer.parseInt(expLevel));
            }
        }

        if(RedisInventoryBridge.getInstance().getConfig().getBoolean("saves.potions") == true) {
            String potions = redisManager.getPlayerPotions(playerUUID);
            if (potions != null) {
                InventoryUtils.applyPotionEffects(player, potions);
            }
        }
    }
}