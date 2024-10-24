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
     * Lädt das Inventar des Spielers aus Redis und stellt es wieder her.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();

        // Abrufen des gespeicherten Inventars aus Redis
        String serializedInventory = redisManager.getInventory(playerUUID);

        if (serializedInventory != null) {
            // Deserialisiere und setze das Inventar des Spielers
            ItemStack[] inventory = InventoryUtils.deserializeInventory(serializedInventory);
            player.getInventory().setContents(inventory);
            logger.info("Inventar für Spieler " + player.getName() + " erfolgreich aus Redis geladen.");

            if(RedisInventoryBridge.getInstance().getConfig().getBoolean("joinmessage.enable") == true) {
                player.sendMessage(RedisInventoryBridge.getInstance().getConfig().getString("joinmessage.message").replace("&", "§"));
            }

            player.sendMessage("Inventar erfolgreich aus Redis geladen.");
        } else {
            logger.info("Kein gespeichertes Inventar für Spieler " + player.getName() + " gefunden.");
            player.sendMessage("Kein gespeichertes Inventar gefunden.");
        }
    }
}