package pl.artemis.kits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class DamageListener implements Listener {

    private final ArtemisPlugin plugin;

    public DamageListener(ArtemisPlugin plugin) {
        this.plugin = plugin;
    }

    /** Miecz Artemidy zadaje 2 serca (4 punkty) za cios. */
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        if (plugin.items().swordLevel(p) > 0) {
            e.setDamage(4.0);
        }
    }
}
