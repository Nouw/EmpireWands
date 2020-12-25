package net.nouw.empirewand;

import de.slikey.effectlib.EffectManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class EmpireWand extends JavaPlugin {

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        Objects.requireNonNull(this.getCommand("wand")).setExecutor(new GetWand());
        getServer().getPluginManager().registerEvents(new WandActionListener(Objects.requireNonNull(getServer().getPluginManager().getPlugin("EmpireWand"))), this);

        System.out.println("EmpireWand: " + "Finished enabling EmpireWand");
    }


}
