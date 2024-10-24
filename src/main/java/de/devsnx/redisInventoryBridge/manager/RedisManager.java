package de.devsnx.redisInventoryBridge.manager;

import de.devsnx.redisInventoryBridge.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.logging.Logger;

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
            System.out.println("Verbindung zu Redis hergestellt: " + host + ":" + port);
        } catch (JedisConnectionException e) {
            System.err.println("Fehler bei der Verbindung zu Redis: " + e.getMessage());
        }
    }

    /**
     * Speichert das Inventar eines Spielers in Redis.
     *
     * @param playerUUID Die UUID des Spielers.
     * @param inventoryData Die serialisierten Inventardaten.
     */
    public void saveInventory(String playerUUID, String inventoryData) {
        try {
            jedis.hset("player:" + playerUUID + ":inventory", "data", inventoryData);
            System.out.println("Inventar für Spieler " + playerUUID + " in Redis gespeichert.");
        } catch (JedisConnectionException e) {
            System.err.println("Fehler beim Speichern des Inventars in Redis: " + e.getMessage());
            reconnect();
        }
    }

    /**
     * Ruft das gespeicherte Inventar eines Spielers aus Redis ab.
     *
     * @param playerUUID Die UUID des Spielers.
     * @return Die serialisierten Inventardaten.
     */
    public String getInventory(String playerUUID) {
        try {
            return jedis.hget("player:" + playerUUID + ":inventory", "data");
        } catch (JedisConnectionException e) {
            System.err.println("Fehler beim Abrufen des Inventars von Redis: " + e.getMessage());
            reconnect();
            return null;
        }
    }

    /**
     * Schließt die Redis-Verbindung.
     */
    public void close() {
        if (jedis != null) {
            jedis.close();
            System.out.println("Redis-Verbindung geschlossen.");
        }
    }

    /**
     * Versucht, die Verbindung zu Redis neu herzustellen.
     */
    private void reconnect() {
        System.out.println("Versuche, die Verbindung zu Redis neu herzustellen...");
        connect();
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
        logger.info("Inventar für Spieler " + player.getName() + " erfolgreich in Redis gespeichert.");
    }

}