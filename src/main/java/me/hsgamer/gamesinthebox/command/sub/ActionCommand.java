package me.hsgamer.gamesinthebox.command.sub;

import me.hsgamer.gamesinthebox.GamesInTheBox;
import me.hsgamer.gamesinthebox.Permissions;
import me.hsgamer.gamesinthebox.game.GameAction;
import me.hsgamer.gamesinthebox.planner.feature.GameFeature;
import me.hsgamer.hscore.bukkit.command.sub.SubCommand;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.minigamecore.base.Arena;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionCommand extends SubCommand {
    private final GamesInTheBox plugin;

    public ActionCommand(GamesInTheBox plugin) {
        super("action", "Perform actions in the current game of the planner", "/<label> action <planner> <action> [args]", Permissions.ACTION.getName(), true);
        this.plugin = plugin;
    }

    @Override
    public void onSubCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        Optional<Arena> plannerOptional = plugin.getPlannerManager().getArenaByName(args[0]);
        if (!plannerOptional.isPresent()) {
            MessageUtils.sendMessage(sender, plugin.getMessageConfig().getPlannerNotFound());
            return;
        }
        Arena planner = plannerOptional.get();
        GameFeature gameFeature = planner.getFeature(GameFeature.class);
        GameAction gameAction = gameFeature.getGameAction();
        if (gameAction.performAction(sender, args[1], Arrays.copyOfRange(args, 2, args.length))) {
            MessageUtils.sendMessage(sender, plugin.getMessageConfig().getSuccess());
        } else {
            MessageUtils.sendMessage(sender, plugin.getMessageConfig().getGameCannotPerformAction());
        }
    }

    @Override
    public boolean isProperUsage(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        return args.length >= 2;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String... args) {
        if (args.length == 1) {
            return plugin.getPlannerManager().getAllArenas().stream().map(Arena::getName).collect(Collectors.toList());
        } else if (args.length == 2) {
            return plugin.getPlannerManager().getArenaByName(args[0])
                    .map(planner -> planner.getFeature(GameFeature.class))
                    .map(GameFeature::getGameAction)
                    .map(GameAction::getActions)
                    .orElse(Collections.emptyList());
        } else if (args.length >= 3) {
            return plugin.getPlannerManager().getArenaByName(args[0])
                    .map(planner -> planner.getFeature(GameFeature.class))
                    .map(GameFeature::getGameAction)
                    .map(gameAction -> gameAction.getActionArgs(sender, args[1], Arrays.copyOfRange(args, 2, args.length)))
                    .orElse(Collections.emptyList());
        }
        return Collections.emptyList();
    }
}