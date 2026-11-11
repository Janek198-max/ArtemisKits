package pl.artemis.kits;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class NameTagManager {

    private final ArtemisPlugin plugin;

    public NameTagManager(ArtemisPlugin plugin) {
        this.plugin = plugin;
    }

    /** Dopisuje po nicku ⚔️ (poziom miecza) i 🛡️ (poziom seta). */
    public void update(Player p) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam(p.getName());
        if (team == null) team = board.registerNewTeam(p.getName());

        int sword = plugin.items().swordLevel(p);
        int set = plugin.items().setLevel(p);

        team.suffix(ArtemisPlugin.c(" &b⚔️" + sword + " &a🛡️" + set));
        if (!team.hasEntry(p.getName())) team.addEntry(p.getName());
    }
}
