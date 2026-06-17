package com.example.aivillager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.*;

public class AIVillagerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("aivillager.json");
    public static ConfigData config = new ConfigData();

    public static class ConfigData {
        public boolean enableAIVillagers = true;
        public int maxKnowledgeLevel = 10;
        public double trustGainRate = 1.0;
        public double trustLossRate = 1.0;
        public boolean enableChatHistory = true;
        public int maxChatHistory = 50;
        public String[] personalityTypes = {"friendly", "neutral", "grumpy"};
        public double[] personalityChances = {0.4, 0.35, 0.25};
        public boolean enableTrading = true;
        public int interactionCooldown = 20;
        public double giftTrustIncrease = 5.0;
        public double hurtTrustDecrease = 10.0;
        public double tradeTrustIncrease = 2.0;
        public boolean spawnInVillages = true;
        public double villageSpawnChance = 0.8;
        public int maxPerVillage = 3;
        public double traderReplaceChance = 0.6;
        public double generousTraderChance = 0.6;
        public double mysteriousTraderChance = 0.25;
        public double shrewdTraderChance = 0.15;
        public double ancientBugChanceNormal = 0.001;
        public double ancientBugChanceMysterious = 0.003;
    }

    public static void loadConfig() {
        if (!Files.exists(CONFIG_PATH)) { saveConfig(); return; }
        try (Reader r = Files.newBufferedReader(CONFIG_PATH)) {
            ConfigData loaded = GSON.fromJson(r, ConfigData.class);
            if (loaded != null) config = loaded;
        } catch (IOException e) { saveConfig(); }
    }

    public static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer w = Files.newBufferedWriter(CONFIG_PATH)) { GSON.toJson(config, w); }
        } catch (IOException ignored) {}
    }

    public static void reloadConfig() { loadConfig(); }
}