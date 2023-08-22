package ru.aniby.aniplayertransfer;

import org.bukkit.ChatColor;

public class APTConfig {
    public static void init() {
        AniPlayerTransfer plugin = AniPlayerTransfer.instance;

        APTConfig.replace_if_empty = plugin.getConfig().getBoolean("replace_if_empty", true);
        APTConfig.default_world = plugin.getConfig().getString("default_world", "world");
        APTConfig.debug = plugin.getConfig().getBoolean("debug");

        // Messages
        String successMessage = plugin.getConfig().getString("messages.transfer.success");
        if (successMessage != null)
            Messsages.Transfer.success = ChatColor.translateAlternateColorCodes('&', successMessage);

        String wrongArguments = plugin.getConfig().getString("messages.wrong_arguments");
        if (wrongArguments != null)
            Messsages.wrong_arguments = ChatColor.translateAlternateColorCodes('&', wrongArguments);

        String fromMessage = plugin.getConfig().getString("messages.transfer.from");
        if (fromMessage != null)
            Messsages.Transfer.from = ChatColor.translateAlternateColorCodes('&', fromMessage);

        String toMessage = plugin.getConfig().getString("messages.transfer.to");
        if (toMessage != null)
            Messsages.Transfer.to = ChatColor.translateAlternateColorCodes('&', toMessage);

        if (plugin.getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
            plugin.getLogger().info("DiscordSRV detected! Discord transfer enabled!");
            APTConfig.discord = true;
        }
        if (plugin.getServer().getPluginManager().getPlugin("EasyWhitelist") != null) {
            plugin.getLogger().info("EasyWhitelist detected! Discord transfer enabled!");
            APTConfig.whitelist = "easywhitelist";
        }
    }

    public static boolean replace_if_empty = true;
    public static String default_world = "world";
    public static boolean debug = false;
    public static boolean discord = false;
    public static String whitelist = null;

    public static class Messsages {
        public static String wrong_arguments = "§cWrong arguments!";
        public static class Transfer {
            public static String success = "§aSuccess tranfer data from <old_name> to <new_name>";
            public static String from = "§eTransferring player data from <old_name>";
            public static String to = "§eTransferring player data to <new_name>";
        }
    }
}
