package de.devsnx.redisInventoryBridge.manager;

import de.devsnx.redisInventoryBridge.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Marvin Hänel (DevSnx)
 * @since 24.10.2024 19:43
 */

public class RedisManager {

    private Jedis jedis;
    private String host;
    private int port;
    private String username;
    private String password;

    private Logger logger = Bukkit.getLogger();

    public RedisManager(@NotNull FileConfiguration config) {
        // Lade die Redis-Einstellungen aus der Konfigurationsdatei
        this.host = config.getString("redis.host");
        this.port = config.getInt("redis.port");
        this.username = config.getString("redis.username", "");
        this.password = config.getString("redis.password", "");

        connect();
    }

    /**
     * Stellt die Verbindung zu Redis her.
     */
    private void connect() {
        try {
            this.jedis = new Jedis(host, port);

            // Überprüfen, ob Benutzername und Passwort gesetzt sind
            if (username != null && !username.isEmpty()) {
                jedis.auth(username, password); // Falls Redis ein Benutzername-Passwort-Paar erwartet
            } else if (password != null && !password.isEmpty()) {
                jedis.auth(password); // Nur Passwort verwenden, falls kein Benutzername benötigt wird
            }

            jedis.connect();
            System.out.println("Connected to Redis: " + host + ":" + port);
        } catch (JedisConnectionException e) {
            System.err.println("Error connecting to Redis: " + e.getMessage());
        }
    }

    /**
     * Schließt die Redis-Verbindung.
     */
    public void close() {
        if (jedis != null) {
            jedis.close();
            System.out.println("Redis connection closed.");
        }
    }

    /**
     * Versucht, die Verbindung zu Redis neu herzustellen.
     */
    private void reconnect() {
        System.out.println("Trying to reconnect to Redis...");
        connect();
    }

    /**
     * Speichert das Inventar eines Spielers in Redis.
     */
    public void saveInventory(String playerUUID, String inventoryData) {
        try {
            jedis.hset("player:" + playerUUID + ":inventory", "data", inventoryData);
            System.out.println("Inventory saved from " + playerUUID);
        } catch (JedisConnectionException e) {
            System.err.println("Error saving inventory from " + playerUUID + " in Redis: " + e.getMessage());
            reconnect();
        }
    }


    /**
     * Ruft das gespeicherte Inventar eines Spielers aus Redis ab.
     */
    public String getInventory(String playerUUID) {
        try {
            return jedis.hget("player:" + playerUUID + ":inventory", "data");
        } catch (JedisConnectionException e) {
            System.err.println("Error getting inventory from " + playerUUID + " in Redis: " + e.getMessage());
            reconnect();
            return null;
        }
    }

    /**
     * Hilfsmethode, um das Inventar des Spielers zu speichern.
     */
    public void savePlayerInventory(Player player) {
        String playerUUID = player.getUniqueId().toString();

        // Serialisiere das Inventar des Spielers
        String serializedInventory = InventoryUtils.serializeInventory(player.getInventory().getContents());

        // Speichern des serialisierten Inventars in Redis
        saveInventory(playerUUID, serializedInventory);
        logger.info("Inventory from " + player.getName() + " successfully saved in Redis.");
    }

    /**
     * Speichert das Herzlevel des Spielers in Redis.
     */
    public void savePlayerHearts(String playerUUID, double health) {
        jedis.hset("player:" + playerUUID + ":attributes", "hearts", String.valueOf(health));
    }

    /**
     * Ruft das Herzlevel des Spielers aus Redis ab.
     */
    public String getPlayerHearts(String playerUUID) {
        return jedis.hget("player:" + playerUUID + ":attributes", "hearts");
    }

    /**
     * Speichert das Essenlevel des Spielers in Redis.
     */
    public void savePlayerFoodLevel(String playerUUID, int foodLevel) {
        jedis.hset("player:" + playerUUID + ":attributes", "foodLevel", String.valueOf(foodLevel));
    }

    /**
     * Ruft das Essenlevel des Spielers aus Redis ab.
     */
    public String getPlayerFoodLevel(String playerUUID) {
        return jedis.hget("player:" + playerUUID + ":attributes", "foodLevel");
    }

    /**
     * Speichert das Erfahrungslevel des Spielers in Redis.
     */
    public void savePlayerExpLevel(String playerUUID, int expLevel) {
        jedis.hset("player:" + playerUUID + ":attributes", "expLevel", String.valueOf(expLevel));
    }

    /**
     * Speichert das Erfahrung des Spielers in Redis.
     */
    public void savePlayerExp(String playerUUID, float exp) {
        jedis.hset("player:" + playerUUID + ":attributes", "exp", String.valueOf(exp));
    }

    /**
     * Ruft das Erfahrung des Spielers aus Redis ab.
     */
    public String getPlayerExp(String playerUUID) {
        return jedis.hget("player:" + playerUUID + ":attributes", "exp");
    }


    /**
     * Ruft das Erfahrungslevel des Spielers aus Redis ab.
     */
    public String getPlayerExpLevel(String playerUUID) {
        return jedis.hget("player:" + playerUUID + ":attributes", "expLevel");
    }

    /**
     * Speichert die aktiven Potion-Effekte des Spielers in Redis.
     */
    public void savePlayerPotions(Player player) {
        String serializedPotions = player.getActivePotionEffects().stream()
                .map(p -> p.getType().getName() + ":" + p.getDuration() + ":" + p.getAmplifier())
                .collect(Collectors.joining(","));
        jedis.hset("player:" + player.getUniqueId().toString() + ":attributes", "potions", serializedPotions);
    }

    /**
     * Ruft die gespeicherten Potion-Effekte des Spielers aus Redis ab.
     */
    public String getPlayerPotions(String playerUUID) {
        return jedis.hget("player:" + playerUUID + ":attributes", "potions");
    }

}