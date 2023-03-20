package me.hsgamer.gamesinthebox.game.simple.feature;

import me.hsgamer.gamesinthebox.game.feature.GameConfigFeature;
import me.hsgamer.gamesinthebox.game.feature.HologramFeature;
import me.hsgamer.gamesinthebox.game.simple.SimpleGameArena;
import me.hsgamer.gamesinthebox.planner.feature.ReplacementFeature;
import me.hsgamer.gamesinthebox.util.LocationUtil;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.variable.InstanceVariableManager;
import me.hsgamer.minigamecore.base.Feature;
import me.hsgamer.unihologram.common.api.Hologram;
import me.hsgamer.unihologram.common.api.HologramLine;
import me.hsgamer.unihologram.common.line.TextHologramLine;
import org.bukkit.Location;

import java.util.*;
import java.util.stream.Collectors;

public class DescriptiveHologramFeature implements Feature {
    private static final UUID DUMMY_UUID = UUID.randomUUID();
    private final InstanceVariableManager instanceVariableManager;
    private final SimpleGameArena arena;
    private final List<HologramUpdater> hologramUpdaters = new ArrayList<>();

    public DescriptiveHologramFeature(SimpleGameArena arena) {
        this.arena = arena;
        instanceVariableManager = new InstanceVariableManager();
        instanceVariableManager.register("", (original, uuid) -> Optional.ofNullable(arena.getPlanner().getFeature(ReplacementFeature.class))
                .map(replacementFeature -> replacementFeature.replace(original))
                .orElse(null));
        instanceVariableManager.setReplaceAll(true);
    }

    @Override
    public void postInit() {
        GameConfigFeature gameConfigFeature = arena.getFeature(GameConfigFeature.class);
        Object hologramSection = gameConfigFeature.get("hologram");
        List<Map<String, Object>> hologramList = new ArrayList<>();
        if (hologramSection instanceof List) {
            for (Object hologramObject : (List<?>) hologramSection) {
                MapUtils.castOptionalStringObjectMap(hologramObject).ifPresent(hologramList::add);
            }
        } else {
            MapUtils.castOptionalStringObjectMap(hologramSection).ifPresent(hologramList::add);
        }

        for (Map<String, Object> hologramMap : hologramList) {
            Location location = LocationUtil.getLocation(Objects.toString(hologramMap.get("location")));
            List<String> lines = CollectionUtils.createStringListFromObject(hologramMap.get("lines"));
            if (location == null || lines.isEmpty()) {
                continue;
            }
            Hologram<Location> hologram = arena.getFeature(HologramFeature.class).createHologram(location);
            hologramUpdaters.add(new HologramUpdater(hologram, lines));
        }
    }

    @Override
    public void clear() {
        clearHologram();
        hologramUpdaters.clear();
    }

    public void initHologram() {
        hologramUpdaters.forEach(hologramUpdater -> HologramFeature.reInit(hologramUpdater.hologram));
    }

    public void updateHologram() {
        hologramUpdaters.forEach(HologramUpdater::update);
    }

    public void clearHologram() {
        hologramUpdaters.forEach(hologramUpdater -> HologramFeature.clearIfInitialized(hologramUpdater.hologram));
    }

    private class HologramUpdater {
        private final Hologram<Location> hologram;
        private final List<String> lines;

        private HologramUpdater(Hologram<Location> hologram, List<String> lines) {
            this.hologram = hologram;
            this.lines = lines;
        }

        public void update() {
            if (!hologram.isInitialized()) {
                return;
            }
            List<HologramLine> replacedLines = lines.stream()
                    .map(line -> instanceVariableManager.setVariables(line, DUMMY_UUID))
                    .map(ColorUtils::colorize)
                    .map(TextHologramLine::new)
                    .collect(Collectors.toList());
            hologram.setLines(replacedLines);
        }
    }
}