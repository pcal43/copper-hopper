package net.pcal.mobfilter;


import net.minecraft.entity.SpawnGroup;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("ALL")
public class MFConfig {

    @FunctionalInterface
    public static interface InputStreamSupplier {
        InputStream get() throws IOException;
    }

    private final InputStreamSupplier inputStreamSupplier;

    MFConfig(InputStreamSupplier supplier) {
        this.inputStreamSupplier = requireNonNull(supplier);
    }

    public ConfigurationFile reload() throws IOException {
        Yaml yaml = new Yaml(new Constructor(ConfigurationFile.class));
        try(InputStream inputStream = this.inputStreamSupplier.get()) {
            ConfigurationFile config = yaml.load(inputStream);
            return config;
        }
    }

    public static class ConfigurationFile {
        public boolean debugEnabled;
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
        public int[] time;
        public String[] blockX;
        public String[] blockY;
        public String[] blockZ;
        public String[] chunkX;
        public String[] chunkY;
    }
}

