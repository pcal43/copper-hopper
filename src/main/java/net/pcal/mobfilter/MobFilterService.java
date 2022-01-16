package net.pcal.mobfilter;


import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import static net.pcal.mobfilter.MobFilterRules.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 *
 */
public class MobFilterService {

    private static final Logger LOGGER = LogManager.getLogger(MobFilterRules.class);

    private static final class SingletonHolder {
        private static final MobFilterService INSTANCE;

        static {
            INSTANCE = new MobFilterService();
        }
    }

    public static MobFilterService getInstance() {
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
        final MobFilterRules.SpawnRequest req = new SpawnRequest(sw, sg, sa, cg, se, pos, sd);
        final boolean disallowSpawn = ruleList.disallowSpawn(req);
        if (this.debugEnabled) {
            LOGGER.debug("[MobFilter] " + (disallowSpawn ? "DISALLOW" : "ALLOW") + " " + req);
        }
        return disallowSpawn;
    }

    public void loadConfig() {
        LOGGER.info("[MobFilter] loading configuration");
        List<FilterRule> rules = new ArrayList<FilterRule>();
        List<FilterCheck> checks = List.of(new DimensionCheck(new Identifier("minecraft:overworld")), new SpawnGroupCheck(SpawnGroup.MONSTER), new WorldNameCheck("New World"), new DimensionCheck(new Identifier("minecraft:overworld")));
        FilterRule worldRule = new FilterRule("no-overworld", checks, true);
        this.ruleList = new FilterRuleList(List.of(worldRule));
        this.debugEnabled = true;
        if (this.debugEnabled) {
            Configurator.setLevel(MobFilterRules.class.getName(), Level.DEBUG);
        }
    }


}
