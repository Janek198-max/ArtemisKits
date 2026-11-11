package pl.artemis.kits;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.*;

public final class KitManager {

    public static final long COOLDOWN_MS = 60_000L; // 1 minuta

    private final ArtemisPlugin plugin;
    private final File file;
    private final Map<String, ItemStack[]> kits = new LinkedHashMap<>();
    private final Map<String, Long> lastUse = new HashMap<>();

    public KitManager(ArtemisPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "kits.yml");
        load();
    }

    private void load() {
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String name : cfg.getKeys(false)) {
            try {
                kits.put(name, decode(cfg.getString(name, "")));
            } catch (Exception ex) {
                plugin.getLogger().warning("Nie udało się wczytać kita " + name + ": " + ex.getMessage());
            }
        }
    }

    private void save() {
        YamlConfiguration cfg = new YamlConfiguration();
        try {
            for (Map.Entry<String, ItemStack[]> entry : kits.entrySet()) {
                cfg.set(entry.getKey(), encode(entry.getValue()));
            }
            plugin.getDataFolder().mkdirs();
            cfg.save(file);
        } catch (IOException ex) {
            plugin.getLogger().severe("Nie udało się zapisać kits.yml: " + ex.getMessage());
        }
    }

    private static String encode(ItemStack[] items) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (BukkitObjectOutputStream out = new BukkitObjectOutputStream(bytes)) {
            out.writeObject(items);
        }
        return Base64.getEncoder().encodeToString(bytes.toByteArray());
    }

    private static ItemStack[] decode(String data) throws IOException, ClassNotFoundException {
        byte[] raw = Base64.getDecoder().decode(data);
        try (BukkitObjectInputStream in = new BukkitObjectInputStream(new ByteArrayInputStream(raw))) {
            return (ItemStack[]) in.readObject();
        }
    }

    public List<String> names() { return new ArrayList<>(kits.keySet()); }

    public ItemStack[] get(String name) { return kits.get(name); }

    public void put(String name, ItemStack[] items) {
        kits.put(name, items.clone());
        save();
    }

    private static String key(Player p, String kit) {
        return p.getUniqueId() + ":" + kit;
    }

    public long remainingMs(Player p, String kit) {
        Long last = lastUse.get(key(p, kit));
        if (last == null) return 0;
        return Math.max(0, COOLDOWN_MS - (System.currentTimeMillis() - last));
    }

    public void markUsed(Player p, String kit) {
        lastUse.put(key(p, kit), System.currentTimeMillis());
    }
}
