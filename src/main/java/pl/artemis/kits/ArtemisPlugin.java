package pl.artemis.kits;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArtemisPlugin extends JavaPlugin implements CommandExecutor {

    private KitManager kits;
    private KitGui gui;
    private ArtemisItems items;
    private NameTagManager tags;

    public static Component c(String legacy) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(legacy)
                .decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public void onEnable() {
        kits = new KitManager(this);
        items = new ArtemisItems(this);
        gui = new KitGui(this);
        tags = new NameTagManager(this);

        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent e) {
                tags.update(e.getPlayer());
            }
        }, this);

        getCommand("kits").setExecutor(this);
        getCommand("edytujkit").setExecutor(this);
        getCommand("odbierz").setExecutor(this);

        // Emotki przy nickach odświeżane co minutę
        getServer().getScheduler().runTaskTimer(this,
                () -> getServer().getOnlinePlayers().forEach(tags::update), 1200L, 1200L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Tylko dla graczy.");
            return true;
        }

        switch (cmd.getName().toLowerCase()) {
            case "kits" -> gui.openMenu(p);
            case "edytujkit" -> {
                if (args.length < 1) {
                    p.sendMessage(c("&cUżycie: /edytujkit <nazwa>"));
                } else {
                    gui.openEdit(p, args[0].toLowerCase());
                }
            }
            case "odbierz" -> {
                if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
                    KitGui.give(p, items.createSet());
                    p.sendMessage(c("&aOdebrałeś set Artemidy!"));
                } else {
                    p.sendMessage(c("&cUżycie: /odbierz set"));
                }
            }
            default -> { }
        }
        return true;
    }

    public KitManager kits() { return kits; }
    public KitGui gui() { return gui; }
    public ArtemisItems items() { return items; }
}
