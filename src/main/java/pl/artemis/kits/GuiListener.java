package pl.artemis.kits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public final class GuiListener implements Listener {

    private final ArtemisPlugin plugin;

    public GuiListener(ArtemisPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof ArtemisHolder h)) return;
        if (h.type() == ArtemisHolder.Type.KIT_EDIT) return; // w edycji wolno wkładać przedmioty

        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getClickedInventory() != e.getInventory()) return;

        int slot = e.getSlot();
        List<String> names = plugin.kits().names();

        switch (h.type()) {
            case MENU -> {
                if (slot >= 10 && slot <= 16 && slot - 10 < names.size()) {
                    plugin.gui().openKitView(p, names.get(slot - 10));
                } else if (slot == 22 && p.hasPermission("artemis.admin")) {
                    plugin.gui().openEditList(p);
                }
            }
            case MENU_EDIT -> {
                if (slot >= 10 && slot <= 16 && slot - 10 < names.size()) {
                    plugin.gui().openEdit(p, names.get(slot - 10));
                }
            }
            case KIT_VIEW -> {
                if (slot == 49) claim(p, h.kit());
            }
            default -> { }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getInventory().getHolder() instanceof ArtemisHolder h)) return;
        if (h.type() != ArtemisHolder.Type.KIT_EDIT) e.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof ArtemisHolder h)) return;
        if (h.type() != ArtemisHolder.Type.KIT_EDIT) return;

        ItemStack[] contents = new ItemStack[45];
        for (int i = 0; i < 45; i++) contents[i] = e.getInventory().getItem(i);
        plugin.kits().put(h.kit(), contents);

        if (e.getPlayer() instanceof Player p) {
            p.sendMessage(ArtemisPlugin.c("&aZapisano kit &e" + h.kit() + "&a."));
        }
    }

    private void claim(Player p, String kit) {
        ItemStack[] contents = plugin.kits().get(kit);
        if (contents == null) {
            p.sendMessage(ArtemisPlugin.c("&cTaki kit nie istnieje."));
            return;
        }
        long left = plugin.kits().remainingMs(p, kit);
        if (left > 0) {
            p.sendMessage(ArtemisPlugin.c("&cPoczekaj jeszcze &e" + (left / 1000) + "s &cprzed kolejnym odbiorem."));
            return;
        }
        KitGui.give(p, Arrays.asList(contents));
        plugin.kits().markUsed(p, kit);
        p.sendMessage(ArtemisPlugin.c("&aOdebrałeś kita &e" + kit + "&a!"));
        p.closeInventory();
    }
}
