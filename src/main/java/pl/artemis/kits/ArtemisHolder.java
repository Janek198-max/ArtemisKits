package pl.artemis.kits;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ArtemisHolder implements InventoryHolder {

    public enum Type { MENU, MENU_EDIT, KIT_VIEW, KIT_EDIT }

    private final Type type;
    private final String kit;
    private Inventory inventory;

    public ArtemisHolder(Type type, String kit) {
        this.type = type;
        this.kit = kit;
    }

    public Type type() { return type; }
    public String kit() { return kit; }
    public void setInventory(Inventory inventory) { this.inventory = inventory; }

    @Override
    public Inventory getInventory() { return inventory; }
}
