package net.pcal.mobfilter;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;


import static java.util.Objects.requireNonNull;

abstract class MobFilterRules {

    private static final Logger LOGGER = LogManager.getLogger(MobFilterRules.class);

    public static class FilterRuleList {
        private final List<FilterRule> rules;

        FilterRuleList(List<FilterRule> rules) {
            this.rules = requireNonNull(rules);
        }

        public boolean disallowSpawn(SpawnRequest request) {
            for (FilterRule rule : rules) {
                Boolean disallowSpawn = rule.disallowSpawn(request);
                if (disallowSpawn != null) return disallowSpawn;
            }
            return false;
        }
    }

    record FilterRule(String ruleName,
                      Collection<FilterCheck> checks,
                      boolean disallowSpawnWhenMatched) {

        FilterRule {
            requireNonNull(ruleName);
            requireNonNull(disallowSpawnWhenMatched);
            requireNonNull(checks);
        }

        public Boolean disallowSpawn(SpawnRequest request) {
            for (final FilterCheck check : checks) {
                if (!check.isMatch(request)) return null;
            }
            return this.disallowSpawnWhenMatched;
        }
    }

    record SpawnRequest(ServerWorld serverWorld,
                        SpawnGroup spawnGroup,
                        StructureAccessor structureAccessor,
                        ChunkGenerator chunkGenerator,
                        SpawnSettings.SpawnEntry spawnEntry,
                        BlockPos blockPos,
                        double squaredDistance) {
    }

    interface FilterCheck {
        boolean isMatch(SpawnRequest spawn);
    }


    record CoordinateCheck(Direction.Axis axis, int value) implements FilterCheck {
        CoordinateCheck { requireNonNull(axis); }
        @Override
        public boolean isMatch(SpawnRequest spawn) {

            return false;
        }
    }

    record DimensionCheck(Identifier dimensionId) implements FilterCheck {
        DimensionCheck { requireNonNull(dimensionId); }
        @Override
        public boolean isMatch(SpawnRequest spawn) {
            // FIXME how do you get the Identifier for a DimensionType?  Don't understand what 'effects' is
            // but it seems to work.  May break custom dimensions.
            return this.dimensionId.equals(spawn.serverWorld.getDimension().getEffects());
        }
    }

    record WorldNameCheck(String worldName) implements FilterCheck {
        WorldNameCheck { requireNonNull(worldName); }
        @Override
        public boolean isMatch(SpawnRequest req) {
            final ServerWorldProperties swp;
            try {
                swp = (ServerWorldProperties)req.serverWorld.getLevelProperties();
            } catch(ClassCastException cce) {
                LOGGER.warn("[MobFilter] serverWorld.getLevelProperties() is unexpected class: " +
                        req.serverWorld.getLevelProperties().getClass().getName());
                return false;
            }
            return this.worldName.equals(swp.getLevelName());
        }
    }

    static class EntityNameCheck implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest req) {
            return false;
        }
    }

    record SpawnGroupCheck(SpawnGroup req) implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest spawn) {
            return spawn.spawnGroup == this.req;
        }
    }

}


