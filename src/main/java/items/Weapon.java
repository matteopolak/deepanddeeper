package items;
import org.bukkit.Material;

public class Weapon extends Item {

    private int damage = 1;
    private int swingCooldown = 1000; //Delay between the current swing and next swing in milliseconds.

    public Weapon(String name, Material material, String[] lore) {
        super(name, material, 1, lore);
    }

    public void damage(int damage) {
        this.damage = damage;
    }

    public void swingCooldown(int swingCooldown) {
        this.swingCooldown = swingCooldown;
    }

    public void onHit() {
        //Do something
    }
}
