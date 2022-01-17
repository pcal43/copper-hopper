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

    public static enum SpawnAction {
        ALLOW,
        DISALLOW
    }

    public static class Rule {
        public String name;
        public SpawnAction spawn;
        public When when;
    }

    public static class When {
        public String[] world;
        public String[] dimension;
        public String[] entityId;
        public String[] biome;
        public SpawnGroup[] spawnGroup;
        public String[] blockX;
        public String[] blockY;
        public String[] blockZ;
        public String[] timeOfDay;
        public String[] lightLevel;
    }
}

