package ru.aniby.aniplayertransfer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class AniPlayerTransfer extends JavaPlugin {
    public static AniPlayerTransfer instance;

    @Override
    public void onEnable() {
        instance = this;

        // Config Init
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists())
            saveDefaultConfig();
        APTConfig.init();

        // Online-mode check
        if (Bukkit.getServer().getOnlineMode()) {
            getLogger().warning("Server is in online-mode! Disabling plugin...");
            disable();
            return;
        }

        // Command register
        this.getCommand("transfer").setExecutor(new TransferCommand());
    }

    public static void debug(String text) {
        if (APTConfig.debug)
            instance.getLogger().info(text);
    }

    public static void disable() {
        instance.getServer().getPluginManager().disablePlugin(instance);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
