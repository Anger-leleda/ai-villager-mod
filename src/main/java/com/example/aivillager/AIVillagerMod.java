package com.example.aivillager;package com.example.aivillager;

import com.example.aivillager.ai.AIVillagerCommands;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AIVillagerMod implements ModInitializer {
    public static final String MOD_ID = "aivillager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("AI Villager Trade Enhancer 初始化中...");
        AIVillagerConfig.loadConfig();
        AIVillagerCommands.register();
        LOGGER.info("AI Villager Trade Enhancer 初始化完成!");
    }
}

import com.example.aivillager.ai.AIVillagerCommands;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AIVillagerMod implements ModInitializer {
    public static final String MOD_ID = "aivillager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("AI Villager Trade Enhancer 初始化中...");
        AIVillagerConfig.loadConfig();
        AIVillagerCommands.register();
        LOGGER.info("AI Villager Trade Enhancer 初始化完成!");
    }
}