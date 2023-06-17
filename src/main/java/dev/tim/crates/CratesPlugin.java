package dev.tim.crates;

import dev.tim.crates.command.CrateCommand;
import dev.tim.crates.listener.InteractListener;
import dev.tim.crates.listener.MenuListener;
import dev.tim.crates.manager.CrateManager;
import dev.tim.crates.manager.SpinningManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class CratesPlugin extends JavaPlugin {

    private CrateManager crateManager;
    private SpinningManager spinningManager;

    @Override
    public void onEnable() {
        crateManager = new CrateManager(this);
        crateManager.loadCrateFile();
        spinningManager = new SpinningManager(this);

        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);
        getCommand("crate").setExecutor(new CrateCommand(this));

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Crates plugin aangezet");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Crates plugin uitgezet");
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }
    public SpinningManager getSpinningManager() {
        return spinningManager;
    }
}
