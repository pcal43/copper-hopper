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

import java.util.*;


import static java.util.Objects.requireNonNull;

@SuppressWarnings("SpellCheckingInspection")
abstract class MFRules {

    private static final Logger LOGGER = LogManager.getLogger(MFRules.class);

    public static class FilterRuleList {
        private final List<FilterRule> rules;

        FilterRuleList(List<FilterRule> rules) {
            this.rules = requireNonNull(rules);
        }

        public boolean isSpawnDisallowed(SpawnRequest request) {
            for (FilterRule rule : rules) {
                Boolean disallowSpawn = rule.disallowSpawn(request);
                if (disallowSpawn != null) {
                    request.logger().trace(()->"[MobFilter] "+rule.ruleName+" matched");
                    return disallowSpawn;
                }
            }
            request.logger().trace(()->"[MobFilter] no rules matched");
            return false;
        }
    }

    record FilterRule(String ruleName,
                      Collection<FilterCheck> checks,
                      boolean disallowSpawnWhenMatched) {

        FilterRule {
            requireNonNull(ruleName);
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
                        double squaredDistance,
                        Logger logger) {
    }

    interface FilterCheck {
        boolean isMatch(SpawnRequest spawn);
    }

    record DimensionCheck(StringSet dimensionNames) implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest req) {
            // FIXME how do you get the Identifier for a DimensionType?  Don't understand what 'effects' is
            // but it seems to work.  May break custom dimensions.
            final String dimensionName = req.serverWorld.getDimension().getEffects().toString();
            req.logger().trace(()->"[MobFilter] DimensionCheck: "+dimensionName+ " in "+dimensionNames);
            return this.dimensionNames.contains(dimensionName);
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
            final String worldName = swp.getLevelName();
            req.logger().trace(()->"[MobFilter] WorldNameCheck: "+worldName+ " in "+worldNames);
            return worldNames.contains(worldName);
        }
    }

    record SpawnGroupCheck(EnumSet<SpawnGroup> groups) implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest req) {
            req.logger().trace(()->"[MobFilter] SpawnGroupCheck: "+this.groups+ " "+req.spawnGroup+" "+this.groups.contains(req.spawnGroup));
            return this.groups.contains(req.spawnGroup);
        }
    }

    record BiomeCheck(StringSet biomeIds) implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest req) {
            final Biome biome = req.serverWorld().getBiome(req.blockPos());
            String biomeId = String.valueOf(BuiltinRegistries.BIOME.getId(biome)); // FIXME?
            req.logger().trace(()->"[MobFilter] BiomeCheck "+biomeId+" in "+biomeIds);
            return this.biomeIds.contains(biomeId);
        }
    }
    record EntityIdCheck(StringSet entityIds) implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest req) {
            String entityId = String.valueOf(Registry.ENTITY_TYPE.getId(req.spawnEntry.type)); // FIXME?
            req.logger().trace(()->"[MobFilter] EntityNameCheck "+entityId+" in "+entityIds);
            return this.entityIds.contains(entityId);
        }
    }

    record BlockPosCheck(Direction.Axis axis, int min, int max) implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest req) {
            int val = req.blockPos.getComponentAlongAxis(this.axis);
            req.logger().trace(()->"[MobFilter] BlockPosCheck "+axis+" "+min+" <= "+val+" <= "+max);
            return min <= val && val <= max;
        }
    }

    record LightLevelCheck(int min, int max) implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest req) {
            int val = req.serverWorld().getLightLevel(req.blockPos);
            req.logger().trace(()->"[MobFilter] LightLevelCheck "+min+" <= "+val+" <= "+max);
            return min <= val && val <= max;
        }
    }

    record TimeOfDayCheck(long min, long max) implements FilterCheck {
        @Override
        public boolean isMatch(SpawnRequest req) {
            long val = req.serverWorld.getTimeOfDay();
            req.logger().trace(()->"[MobFilter] TimeOfDayCheck "+min+" <= "+val+" <= "+max);
            return min <= val && val <= max;
        }
    }

    /**
     * An immutable set of strings with membership testing.  This gets used a lot and may be
     * in need of optimization.
     */
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

        public String toString() {
            return Arrays.toString(this.strings);
        }
    }
}


