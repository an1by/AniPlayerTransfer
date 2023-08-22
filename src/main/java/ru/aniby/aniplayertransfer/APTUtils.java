package ru.aniby.aniplayertransfer;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class APTUtils {
    public static boolean whitelistReplace(String old_name, String new_name) {
        Server server = Bukkit.getServer();
        switch (APTConfig.whitelist) {
            case "easywhitelist" -> {
                Plugin plugin = server.getPluginManager().getPlugin("EasyWhitelist");
                assert plugin != null;

                if (!plugin.getConfig().getStringList("whitelisted").contains(old_name.toLowerCase(Locale.ROOT)))
                    return true;

                AniPlayerTransfer.debug(
                        String.format("[Transfer] [WhiteList] %s -> %s", old_name, new_name)
                );
                for (String command : List.of(
                        "easywl remove " + old_name,
                        "easywl add " + new_name
                )) server.dispatchCommand(server.getConsoleSender(), command);
            }
        }
        return false;
    }

    public static UUID getOfflineUUID(String player_name) {
        String requestString = "OfflinePlayer:" + player_name;
        return UUID.nameUUIDFromBytes(requestString.getBytes(StandardCharsets.UTF_8));
    }

    public static String getRootPath() {
        String path = new File(".").getAbsolutePath();
        return path;
//        return path.substring(0, path.length() - 2);
    }
}
