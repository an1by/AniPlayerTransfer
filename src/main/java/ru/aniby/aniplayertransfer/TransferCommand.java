package ru.aniby.aniplayertransfer;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransferCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(APTConfig.Messsages.wrong_arguments);
            return true;
        }
        String oldName = args[0];
        String newName = args[1];
        Player oldPlayer = Bukkit.getPlayer(oldName);
        if (oldPlayer != null)
            oldPlayer.kick(Component.text(
                    APTConfig.Messsages.Transfer.from
                            .replaceAll("<new_name>", newName)
                            .replaceAll("<old_name>", oldName)
            ), PlayerKickEvent.Cause.PLUGIN);
        Player newPlayer = Bukkit.getPlayer(newName);
        if (newPlayer != null)
            newPlayer.kick(Component.text(
                    APTConfig.Messsages.Transfer.to
                            .replaceAll("<new_name>", newName)
                            .replaceAll("<old_name>", oldName)
            ), PlayerKickEvent.Cause.PLUGIN);
        sender.sendMessage(Component.text(
                APTConfig.Messsages.Transfer.success
                        .replaceAll("<new_name>", newName)
                        .replaceAll("<old_name>", oldName)
        ));
        transferPlayerData(oldName, newName);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int len = args.length;
        if (len == 0 || len > 2)
            return new ArrayList<>();

        String argument = args[len - 1];
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.startsWith(argument))
                .toList();
    }

    public static void transferPlayerData(@NotNull String old_name, @NotNull String new_name) {
        AniPlayerTransfer.instance.getLogger().info(
                String.format("[Transfer] %s -> %s", old_name, new_name)
        );

        // Init basic
        String rootPath = APTUtils.getRootPath();
        UUID oldUUID = APTUtils.getOfflineUUID(old_name);
        UUID newUUID = APTUtils.getOfflineUUID(new_name);

        // Asynchronous task
        Bukkit.getScheduler().runTaskAsynchronously(AniPlayerTransfer.instance, () -> {
            try {
                // Files
                // advancements
                Path oldAdvancements = Paths.get(rootPath, APTConfig.default_world, "advancements", oldUUID + ".json");
                Path newAdvancements = Paths.get(rootPath, APTConfig.default_world, "advancements", newUUID + ".json");
                File oldAdvancementsFile = new File(oldAdvancements.toUri());
                if (!oldAdvancementsFile.exists() && new File(newAdvancements.toUri()).exists() && APTConfig.replace_if_empty) {
                    Files.delete(newAdvancements);
                } else if (oldAdvancementsFile.exists()) {
                    Files.move(
                            oldAdvancements,
                            newAdvancements,
                            StandardCopyOption.REPLACE_EXISTING
                    );
                    AniPlayerTransfer.debug(
                            String.format("[Transfer] [Advancements] %s -> %s", old_name, new_name)
                    );
                }

                // playerdata
                Path oldPlayerdata = Paths.get(rootPath, APTConfig.default_world, "playerdata", oldUUID + ".dat");
                Path newPlayerdata = Paths.get(rootPath, APTConfig.default_world, "playerdata", newUUID + ".dat");
                File oldPlayerdataFile = new File(oldPlayerdata.toUri());
                if (!oldPlayerdataFile.exists() && new File(newPlayerdata.toUri()).exists() && APTConfig.replace_if_empty) {
                    Files.delete(newPlayerdata);
                } else if (oldPlayerdataFile.exists()) {
                    Files.move(
                            oldPlayerdata,
                            newPlayerdata,
                            StandardCopyOption.REPLACE_EXISTING
                    );
                    AniPlayerTransfer.debug(
                            String.format("[Transfer] [Player Data] %s -> %s", old_name, new_name)
                    );
                }

                // stats
                Path oldStats = Paths.get(rootPath, APTConfig.default_world, "stats", oldUUID + ".json");
                Path newStats = Paths.get(rootPath, APTConfig.default_world, "stats", newUUID + ".json");
                File oldStatsFile = new File(oldPlayerdata.toUri());
                if (!oldStatsFile.exists() && new File(newStats.toUri()).exists() && APTConfig.replace_if_empty) {
                    Files.delete(newStats);
                } else if (oldStatsFile.exists()) {
                    Files.move(
                            oldStats,
                            newStats,
                            StandardCopyOption.REPLACE_EXISTING
                    );
                    AniPlayerTransfer.debug(
                            String.format("[Transfer] [Statistics] %s -> %s", old_name, new_name)
                    );
                }

                // Discord
                if (APTConfig.discord) {
                    AniPlayerTransfer.debug(
                            String.format("[Transfer] [DiscordSRV] %s -> %s", old_name, new_name)
                    );
                    AccountLinkManager linkManager = DiscordSRV.getPlugin().getAccountLinkManager();
                    String discordId = linkManager.getDiscordId(oldUUID);
                    if (discordId != null) {
                        linkManager.unlink(oldUUID);
                        linkManager.link(discordId, newUUID);
                    }
                }
            } catch (IOException e) {
                AniPlayerTransfer.disable();
                throw new RuntimeException(e);
            }
        });

        APTUtils.whitelistReplace(old_name, new_name);
    }
}
