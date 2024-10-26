package de.devsnx.redisInventoryBridge.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.util.Base64;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

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

    /**
     * Serialisiert die aktiven Potion-Effekte des Spielers in einen String.
     *
     * @param effects Die Liste der aktiven Potion-Effekte des Spielers.
     * @return Ein String, der die Potion-Effekte repräsentiert.
     */
    public static String serializePotionEffects(List<PotionEffect> effects) {
        return effects.stream()
                .map(effect -> effect.getType().getName() + ":" + effect.getDuration() + ":" + effect.getAmplifier())
                .collect(Collectors.joining(","));
    }

    /**
     * Deserialisiert einen String und wendet die Potion-Effekte auf den Spieler an.
     *
     * @param player Der Spieler, auf den die Potion-Effekte angewendet werden sollen.
     * @param data Der String, der die Potion-Effekte repräsentiert.
     */
    public static void applyPotionEffects(Player player, String data) {
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType())); // Entfernt alle aktuellen Effekte
        String[] effectData = data.split(",");
        for (String effectStr : effectData) {
            String[] parts = effectStr.split(":");
            if (parts.length == 3) {
                try {
                    PotionEffect effect = new PotionEffect(
                            PotionEffectType.getByName(parts[0]),
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2])
                    );
                    player.addPotionEffect(effect);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}