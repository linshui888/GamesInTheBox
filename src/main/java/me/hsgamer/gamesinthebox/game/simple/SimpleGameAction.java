package me.hsgamer.gamesinthebox.game.simple;

import me.hsgamer.gamesinthebox.game.GameAction;
import me.hsgamer.minigamecore.implementation.feature.TimerFeature;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The simple {@link GameAction}.
 * Provided actions:
 * <ul>
 *     <li>{@code skip-time}: Skip the time</li>
 * </ul>
 */
public class SimpleGameAction implements GameAction {
    private final SimpleGameArena arena;

    public SimpleGameAction(SimpleGameArena arena) {
        this.arena = arena;
    }

    @Override
    public List<String> getActions() {
        List<String> actions = new ArrayList<>();
        actions.add("skip-time");
        return actions;
    }

    @Override
    public List<String> getActionArgs(CommandSender sender, String action, String... args) {
        return Collections.emptyList();
    }

    @Override
    public boolean performAction(CommandSender sender, String action, String... args) {
        if (action.equalsIgnoreCase("skip-time")) {
            arena.getFeature(TimerFeature.class).setDuration(0);
            return true;
        }
        return false;
    }
}