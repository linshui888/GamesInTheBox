/*
   Copyright 2023-2023 Huynh Tien

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package me.hsgamer.gamesinthebox.game.simple.feature;

import me.hsgamer.gamesinthebox.game.feature.GameConfigFeature;
import me.hsgamer.gamesinthebox.game.feature.PointFeature;
import me.hsgamer.gamesinthebox.game.simple.SimpleGameArena;
import me.hsgamer.hscore.common.Validate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SimplePointFeature extends PointFeature {
    private final SimpleGameArena arena;
    private int pointPlus = 1;
    private int pointMinus = 0;
    private int maxPlayersToAdd = -1;

    public SimplePointFeature(SimpleGameArena arena, PointConsumer pointConsumer) {
        super(pointConsumer);
        this.arena = arena;
    }

    @Override
    public void postInit() {
        super.postInit();

        GameConfigFeature gameConfigFeature = arena.getFeature(GameConfigFeature.class);
        pointPlus = Optional.ofNullable(gameConfigFeature.get("point.plus"))
                .map(Objects::toString)
                .flatMap(Validate::getNumber)
                .map(Number::intValue)
                .orElse(pointPlus);
        pointMinus = Optional.ofNullable(gameConfigFeature.get("point.minus"))
                .map(Objects::toString)
                .flatMap(Validate::getNumber)
                .map(Number::intValue)
                .orElse(pointMinus);
        maxPlayersToAdd = Optional.ofNullable(gameConfigFeature.get("point.max-players-to-add"))
                .map(Objects::toString)
                .flatMap(Validate::getNumber)
                .map(Number::intValue)
                .orElse(maxPlayersToAdd);
    }

    public void addPoint(UUID uuid) {
        applyPoint(uuid, pointPlus);
    }

    public void removePoint(UUID uuid) {
        applyPoint(uuid, -pointMinus);
    }

    public void tryAddPoint(List<UUID> uuids) {
        if (maxPlayersToAdd >= 0 && uuids.size() > maxPlayersToAdd) {
            return;
        }
        uuids.forEach(this::addPoint);
    }

    public int getPointPlus() {
        return pointPlus;
    }

    public int getPointMinus() {
        return pointMinus;
    }
}