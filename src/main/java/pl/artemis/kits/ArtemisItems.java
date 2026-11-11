package pl.artemis.kits;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ArtemisItems {

    public static final int LEVEL = 16;
    private static final Color SET_COLOR = Color.fromRGB(0x1A237E); // ciemny niebieski
    private static final String HEAD_TEXTURE =
            "http://textures.minecraft.net/texture/e7ecd11dd7e0aef46ae431175a48d32edbe7fd393d1693757529086273f2f";

    private final ArtemisPlugin plugin;
    private final NamespacedKey pieceKey;
    private final NamespacedKey levelKey;

    public ArtemisItems(ArtemisPlugin plugin) {
        this.plugin = plugin;
        this.pieceKey = new NamespacedKey(plugin, "piece");
        this.levelKey = new NamespacedKey(plugin, "level");
    }

    public List<ItemStack> createSet() {
        List<ItemStack> set = new ArrayList<>();
        set.add(armor(Material.LEATHER_HELMET, "helmet", "Hełm Artemidy", EquipmentSlotGroup.HEAD));
        set.add(armor(Material.LEATHER_CHESTPLATE, "chestplate", "Napierśnik Artemidy", EquipmentSlotGroup.CHEST));
        set.add(armor(Material.LEATHER_LEGGINGS, "leggings", "Nogawice Artemidy", EquipmentSlotGroup.LEGS));
        set.add(armor(Material.LEATHER_BOOTS, "boots", "Buty Artemidy", EquipmentSlotGroup.FEET));
        set.add(sword());
        set.add(head());
        return set;
    }

    private ItemStack armor(Material mat, String piece, String name, EquipmentSlotGroup slot) {
        ItemStack item = new ItemStack(mat);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(SET_COLOR);
        meta.displayName(ArtemisPlugin.c("&5&l" + name + " &6★" + LEVEL));
        meta.lore(lore("&9⛨ Ochrona: &b32", "&a♥ Niezniszczalność: &a32"));
        meta.setUnbreakable(true);
        meta.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(
                new NamespacedKey(plugin, "armor_" + piece), 32.0,
                AttributeModifier.Operation.ADD_NUMBER, slot));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        stamp(meta, piece);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack sword() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ArtemisPlugin.c("&5&lMiecz Artemidy &6★" + LEVEL));
        meta.lore(lore("&c⚔ Ostrość: &c33", "&a♥ Niezniszczalność: &a32"));
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        stamp(meta, "sword");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack head() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        try {
            PlayerProfile profile = Bukkit.createProfile(
                    UUID.nameUUIDFromBytes("artemis-head".getBytes(StandardCharsets.UTF_8)), "Artemis");
            profile.getTextures().setSkin(URI.create(HEAD_TEXTURE).toURL());
            meta.setPlayerProfile(profile);
        } catch (Exception ex) {
            plugin.getLogger().warning("Nie udało się ustawić tekstury głowy: " + ex.getMessage());
        }
        meta.displayName(ArtemisPlugin.c("&5&lGłowa Artemidy &6★" + LEVEL));
        meta.lore(lore("&a♥ Niezniszczalność: &a32"));
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        stamp(meta, "head");
        item.setItemMeta(meta);
        return item;
    }

    private static List<Component> lore(String... stats) {
        List<Component> out = new ArrayList<>();
        out.add(ArtemisPlugin.c("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        for (String s : stats) out.add(ArtemisPlugin.c(s));
        out.add(ArtemisPlugin.c("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        return out;
    }

    private void stamp(ItemMeta meta, String piece) {
        meta.getPersistentDataContainer().set(pieceKey, PersistentDataType.STRING, piece);
        meta.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, LEVEL);
    }

    public String pieceOf(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(pieceKey, PersistentDataType.STRING);
    }

    public int levelOf(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        Integer lvl = item.getItemMeta().getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
        return lvl == null ? 0 : lvl;
    }

    /** Poziom miecza z artefaktu w ręce głównej (0 gdy brak). */
    public int swordLevel(Player p) {
        ItemStack hand = p.getInventory().getItemInMainHand();
        return "sword".equals(pieceOf(hand)) ? levelOf(hand) : 0;
    }

    /** Najwyższy poziom elementu seta założonego na zbroję (0 gdy brak). */
    public int setLevel(Player p) {
        int best = 0;
        for (ItemStack piece : p.getInventory().getArmorContents()) {
            if (pieceOf(piece) != null) best = Math.max(best, levelOf(piece));
        }
        return best;
    }
}
