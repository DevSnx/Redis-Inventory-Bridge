package de.devsnx.redisInventoryBridge;

import de.devsnx.redisInventoryBridge.listener.PlayerJoinListener;
import de.devsnx.redisInventoryBridge.listener.PlayerKickListener;
import de.devsnx.redisInventoryBridge.listener.PlayerQuitListener;
import de.devsnx.redisInventoryBridge.manager.RedisManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RedisInventoryBridge extends JavaPlugin {

    public static RedisInventoryBridge instance;
    private RedisManager redisManager;

    @Override
    public void onEnable() {
        instance = this;

        // Konfigurationsdatei laden/generieren
        saveDefaultConfig();

        String redisHost = getConfig().getString("redis.host", "localhost");
        int redisPort = getConfig().getInt("redis.port", 6379);
        String redisUsername = getConfig().getString("redis.host", "redisuser");
        String redisPassword = getConfig().getString("redis.host", "redispassword");

        redisManager = new RedisManager(getConfig());

        // Event-Listener registrieren
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(redisManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(redisManager), this);
        getServer().getPluginManager().registerEvents(new PlayerKickListener(redisManager), this);

        getLogger().info("Redis Inventory Bridge aktiviert!");

    }

    @Override
    public void onDisable() {
        // Plugin wird deaktiviert - schließe Redis-Verbindung
        if (redisManager != null) {
            redisManager.close();
            getLogger().info("Redis-Verbindung geschlossen.");
        }

        getLogger().info("Redis Inventory Bridge deaktiviert!");

        instance = null;
    }

    public static RedisInventoryBridge getInstance() {
        return instance;
    }
}
