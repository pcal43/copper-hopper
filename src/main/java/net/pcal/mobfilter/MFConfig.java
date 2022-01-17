package net.pcal.mobfilter;


import net.minecraft.entity.SpawnGroup;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;

@SuppressWarnings("ALL")
public class MFConfig {

    static ConfigurationFile load(final InputStream inputStream) {
        Yaml yaml = new Yaml(new Constructor(ConfigurationFile.class));
        return yaml.load(inputStream);
    }

    public static class ConfigurationFile {
        public String logLevel;
        public Rule[] rules;
    }

    public static enum What {
        ALLOW_SPAWN,
        DISALLOW_SPAWN
    }

    public static class Rule {
        public String name;
        public What what;
        public When when;
    }

    public static class When {
        public String[] worldName;
        public String[] dimensionId;
        public String[] entityId;
        public String[] biomeId;
        public SpawnGroup[] spawnGroup;
        public String[] blockX;
        public String[] blockY;
        public String[] blockZ;
        public String[] blockId;
        public String[] timeOfDay;
        public String[] lightLevel;
    }
}

