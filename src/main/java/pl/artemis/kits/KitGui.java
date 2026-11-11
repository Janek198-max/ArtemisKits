package pl.artemis.kits;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class KitGui {

    private final ArtemisPlugin plugin;

    public KitGui(ArtemisPlugin plugin) {
        this.plugin = plugin;
    }

    /** Główne GUI /kits. Na górze "Odbierz swojego kita!", kity w środku, dla OP przycisk Kit edycja. */
    public void openMenu(Player p) {
        ArtemisHolder h = new ArtemisHolder(ArtemisHolder.Type.MENU, null);
        Inventory inv = Bukkit.createInventory(h, 27, ArtemisPlugin.c("&8Kity"));
        h.setInventory(inv);

        inv.setItem(4, item(Material.PAPER, "&6&lOdbierz swojego kita!"));

        List<String> names = plugin.kits().names();
        for (int i = 0; i < names.size() && i < 7; i++) {
            inv.setItem(10 + i, item(Material.CHEST, "&a" + names.get(i), "&7Kliknij, aby zobaczyć"));
        }
        if (p.hasPermission("artemis.admin")) {
            inv.setItem(22, item(Material.ANVIL, "&e&lKit edycja", "&7Kliknij, aby edytować kity"));
        }
        p.openInventory(inv);
    }

    /** Podgląd kita z zielonym barwnikiem "Odbierz kita" na dole. */
    public void openKitView(Player p, String kit) {
        ItemStack[] contents = plugin.kits().get(kit);
        if (contents == null) {
            p.sendMessage(ArtemisPlugin.c("&cTaki kit nie istnieje."));
            return;
        }
        ArtemisHolder h = new ArtemisHolder(ArtemisHolder.Type.KIT_VIEW, kit);
        Inventory inv = Bukkit.createInventory(h, 54, ArtemisPlugin.c("&8Kit: " + kit));
        h.setInventory(inv);

        for (int i = 0; i < 45 && i < contents.length; i++) {
            if (contents[i] != null) inv.setItem(i, contents[i].clone());
        }
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, item(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
        inv.setItem(49, item(Material.LIME_DYE, "&a&lOdbierz kita!", "&7Kliknij, aby odebrać zawartość"));
        p.openInventory(inv);
    }

    public void openEditList(Player p) {
        ArtemisHolder h = new ArtemisHolder(ArtemisHolder.Type.MENU_EDIT, null);
        Inventory inv = Bukkit.createInventory(h, 27, ArtemisPlugin.c("&8Wybierz kit do edycji"));
        h.setInventory(inv);

        List<String> names = plugin.kits().names();
        for (int i = 0; i < names.size() && i < 7; i++) {
            inv.setItem(10 + i, item(Material.WRITABLE_BOOK, "&e" + names.get(i), "&7Kliknij, aby edytować"));
        }
        p.openInventory(inv);
    }

    /** Edycja kita: 45 slotów. Zawartość zapisuje się po zamknięciu GUI. */
    public void openEdit(Player p, String kit) {
        if (!p.hasPermission("artemis.admin")) {
            p.sendMessage(ArtemisPlugin.c("&cNie masz uprawnień."));
            return;
        }
        ItemStack[] existing = plugin.kits().get(kit);
        ArtemisHolder h = new ArtemisHolder(ArtemisHolder.Type.KIT_EDIT, kit);
        Inventory inv = Bukkit.createInventory(h, 45, ArtemisPlugin.c("&8Edycja: " + kit));
        h.setInventory(inv);
        if (existing != null) {
            for (int i = 0; i < 45 && i < existing.length; i++) {
                if (existing[i] != null) inv.setItem(i, existing[i].clone());
            }
        }
        p.openInventory(inv);
    }

    public static void give(Player p, Collection<ItemStack> items) {
        for (ItemStack it : items) {
            if (it == null) continue;
            for (ItemStack left : p.getInventory().addItem(it.clone()).values()) {
                p.getWorld().dropItemNaturally(p.getLocation(), left);
            }
        }
    }

    private static ItemStack item(Material mat, String name, String... lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(ArtemisPlugin.c(name));
        if (lore.length > 0) {
            meta.lore(Arrays.stream(lore).map(ArtemisPlugin::c).toList());
        }
        it.setItemMeta(meta);
        return it;
    }
}
