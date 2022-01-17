package net.pcal.mobfilter;


import com.google.common.collect.ImmutableList;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.pcal.mobfilter.MFConfig.ConfigurationFile;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import static net.pcal.mobfilter.MFRules.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;


/**
 *
 */
public class MFService {


    private static final class SingletonHolder {
        private static final MFService INSTANCE;

        static {
            INSTANCE = new MFService();
        }
    }

    public static MFService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final Logger logger = LogManager.getLogger(MFService.class);
    private FilterRuleList ruleList;

    public boolean disallowSpawn(ServerWorld sw,
                                 SpawnGroup sg,
                                 StructureAccessor sa,
                                 ChunkGenerator cg,
                                 SpawnSettings.SpawnEntry se,
                                 BlockPos.Mutable pos,
                                 double sd) {
        final MFRules.SpawnRequest req = new SpawnRequest(sw, sg, sa, cg, se, pos, sd, this.logger);
        final boolean disallowSpawn = ruleList.isSpawnDisallowed(req);
        if (disallowSpawn) {
            logger.debug(() -> "[MobFilter] DISALLOW " + req);
        } else {
            logger.trace(() -> "[MobFilter] ALLOW " + req);
        }
        return disallowSpawn;
    }

    public void loadConfig() throws IOException {
        final File configFile = Paths.get(".", "config", "mobfilter.yaml").toFile();
        final ConfigurationFile config;
        try (final InputStream in = new FileInputStream(configFile)) {
            config = MFConfig.load(in);
        }
        this.ruleList = buildRules(config);
        final Level logLevel;
        if (config.logLevel != null) {
            Level configuredLevel = Level.getLevel(config.logLevel);
            if (configuredLevel == null) {
                logger.error("[MobFilter] Invalid logLevel " + config.logLevel + " in mobfilter.yaml, using INFO");
                logLevel = Level.INFO;
            } else {
                logLevel = configuredLevel;
            }
        } else {
            logLevel = Level.INFO;
        }
        Configurator.setLevel(MFService.class.getName(), logLevel);
        logger.info("[MobFilter] configuration loaded, debug level is "+logger.getLevel());
    }


    private static FilterRuleList buildRules(ConfigurationFile fromConfig) {
        final ImmutableList.Builder<FilterRule> rulesBuilder = ImmutableList.builder();
        int i = 0;
        for (final MFConfig.Rule configRule : fromConfig.rules) {
            final ImmutableList.Builder<FilterCheck> checks = ImmutableList.builder();
            final MFConfig.When when = configRule.when;
            if (when.spawnGroup != null && when.spawnGroup.length > 0) {
                final EnumSet<SpawnGroup> enumSet = EnumSet.copyOf(Arrays.asList(when.spawnGroup));
                checks.add(new SpawnGroupCheck(enumSet));
            }
            if (when.entityId != null) checks.add(new EntityIdCheck(StringSet.of(when.entityId)));
            if (when.world != null) checks.add(new WorldNameCheck(StringSet.of(when.world)));
            if (when.dimension != null) checks.add(new DimensionCheck(StringSet.of(when.dimension)));
            if (when.biome != null) checks.add(new BiomeCheck(StringSet.of(when.biome)));

            if (when.blockX != null) {
                int[] range =  parseRange(when.blockX);
                checks.add(new BlockPosCheck(Direction.Axis.X, range[0], range[1]));
            }
            if (when.blockY != null) {
                int[] range =  parseRange(when.blockY);
                checks.add(new BlockPosCheck(Direction.Axis.Y, range[0], range[1]));
            }
            if (when.blockZ != null) {
                int[] range =  parseRange(when.blockZ);
                checks.add(new BlockPosCheck(Direction.Axis.Z, range[0], range[1]));
            }
            if (when.timeOfDay != null) {
                int[] range =  parseRange(when.timeOfDay);
                checks.add(new TimeOfDayCheck(range[0], range[1]));
            }
            if (when.lightLevel != null) {
                int[] range =  parseRange(when.lightLevel);
                checks.add(new LightLevelCheck(range[0], range[1]));
            }
            String ruleName = configRule.name != null ? configRule.name : "rule" + i;
            rulesBuilder.add(new FilterRule(ruleName, checks.build(), configRule.spawn == MFConfig.SpawnAction.DISALLOW));
            i++;
        }
        return new FilterRuleList(rulesBuilder.build());
    }

    private static int[] parseRange(String[] configValues) {
        if (configValues.length != 2) {
            throw new IllegalArgumentException("Invalid number of values in int range: "+Arrays.toString(configValues));
        }
        int[] out = new int[2];
        out[0] = "MIN".equals(configValues[0]) ? Integer.MIN_VALUE : Integer.parseInt(configValues[0]);
        out[1] = "MAX".equals(configValues[1]) ? Integer.MAX_VALUE : Integer.parseInt(configValues[1]);
        return out;
    }


}
