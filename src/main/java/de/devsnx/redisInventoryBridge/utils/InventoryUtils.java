package de.devsnx.redisInventoryBridge.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @author Marvin Hänel (DevSnx)
 * @since 24.10.2024 19:47
 */

public class InventoryUtils {

    /**
     * Serialisiert ein ItemStack-Array zu einem Base64-String.
     *
     * @param inventory Das zu serialisierende Inventar.
     * @return Der Base64-String des Inventars.
     */
    public static String serializeInventory(ItemStack[] inventory) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);

            bukkitObjectOutputStream.writeObject(inventory);
            bukkitObjectOutputStream.close();

            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deserialisiert einen Base64-String zu einem ItemStack-Array.
     *
     * @param data Der Base64-String des Inventars.
     * @return Das deserialisierte Inventar.
     */
    public static ItemStack[] deserializeInventory(String data) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

            ItemStack[] inventory = (ItemStack[]) bukkitObjectInputStream.readObject();
            bukkitObjectInputStream.close();

            return inventory;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}