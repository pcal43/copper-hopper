package net.pcal.mobfilter;


import com.google.common.collect.ImmutableList;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.pcal.mobfilter.MFConfig.ConfigurationFile;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import static net.pcal.mobfilter.MFRules.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 *
 */
public class MFService {

    private static final Logger LOGGER = LogManager.getLogger(MFRules.class);

    private static final class SingletonHolder {
        private static final MFService INSTANCE;

        static {
            INSTANCE = new MFService();
        }
    }

    public static MFService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private FilterRuleList ruleList;
    private boolean debugEnabled = false;

    public boolean disallowSpawn(ServerWorld sw,
                                 SpawnGroup sg,
                                 StructureAccessor sa,
                                 ChunkGenerator cg,
                                 SpawnSettings.SpawnEntry se,
                                 BlockPos.Mutable pos,
                                 double sd) {
        final MFRules.SpawnRequest req = new SpawnRequest(sw, sg, sa, cg, se, pos, sd);
        final boolean disallowSpawn = ruleList.disallowSpawn(req);
        if (this.debugEnabled) {
            LOGGER.debug("[MobFilter] " + (disallowSpawn ? "DISALLOW" : "ALLOW") + " " + req);
        }
        return disallowSpawn;
    }

    public void loadConfig() {
        LOGGER.info("[MobFilter] loading configuration");
        /**
        List<FilterRule> rules = new ArrayList<FilterRule>();
        List<FilterCheck> checks = List.of(new DimensionCheck(new Identifier("minecraft:overworld")), new SpawnGroupCheck(SpawnGroup.MONSTER), new WorldNameCheck("New World"), new DimensionCheck(new Identifier("minecraft:overworld")));
        FilterRule worldRule = new FilterRule("no-overworld", checks, true);
        this.ruleList = new FilterRuleList(List.of(worldRule));
        this.debugEnabled = true;
        if (this.debugEnabled) {
            Configurator.setLevel(MFRules.class.getName(), Level.DEBUG);
        }
         **/
    }


    private static FilterRuleList buildRules(ConfigurationFile fromConfig) {
        final ImmutableList.Builder<FilterRule> rulesBuilder = ImmutableList.builder();
        for(final MFConfig.Rule configRule : fromConfig.rules) {
            final ImmutableList.Builder<FilterCheck> checks = ImmutableList.builder();
            final MFConfig.When when = configRule.when;
            if (when.spawnGroup != null && when.spawnGroup.length > 0) {
                final EnumSet<SpawnGroup> enumSet = EnumSet.copyOf(Arrays.asList(when.spawnGroup));
                checks.add(new SpawnGroupCheck(enumSet));
            }
            if (when.world != null) {
                checks.add(new WorldNameCheck(StringSet.of(when.world)));
            }
            if (when.dimension != null) {
                checks.add(new DimensionCheck(StringSet.of(when.dimension)));
            }
            if (when.biome != null) {
                checks.add(new BiomeCheck(StringSet.of(when.biome)));
            }

            rulesBuilder.add();

        }


        return null;
    }


}
