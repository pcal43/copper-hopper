package net.pcal.mobfilter;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;


import static java.util.Objects.requireNonNull;

@SuppressWarnings("SpellCheckingInspection")
abstract class MFRules {

    private static final Logger LOGGER = LogManager.getLogger(MFRules.class);

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

    record DimensionCheck(StringSet dimensionNames) implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest spawn) {
            // FIXME how do you get the Identifier for a DimensionType?  Don't understand what 'effects' is
            // but it seems to work.  May break custom dimensions.
            return this.dimensionNames.contains(spawn.serverWorld.getDimension().getEffects().toString());
        }
    }

    record WorldNameCheck(StringSet worldNames) implements FilterCheck {
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
            return worldNames.contains(swp.getLevelName());
        }
    }

    record BiomeCheck(StringSet biomeIds) implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest req) {
            final Biome biome = req.serverWorld().getBiome(req.blockPos());
            String biomeId = String.valueOf(BuiltinRegistries.BIOME.getId(biome)); // FIXME?
            return this.biomeIds.contains(biomeId);
        }
    }
    record EntityNameCheck(StringSet entityIds) implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest req) {
            String entityId = String.valueOf(Registry.ENTITY_TYPE.getId(req.spawnEntry.type)); // FIXME?
            return this.entityIds.contains(entityId);
        }
    }

    record SpawnGroupCheck(EnumSet<SpawnGroup> groups) implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest spawn) {
            return this.groups.contains(spawn.spawnGroup);
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class StringSet {
        private final String[] strings;

        public static StringSet of(String[] strings) {
            return new StringSet(strings);
        }

        private StringSet(String[] strings) {
            this.strings = requireNonNull(strings);
        }

        public boolean contains(String value) {
            for (String a : this.strings) if (Objects.equals(a, value)) return true;
            return false;
        }
    }
}


