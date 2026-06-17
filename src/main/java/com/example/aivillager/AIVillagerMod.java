package com.example.aivillager;

import com.example.aivillager.entity.AIVillagerEntity;
import com.example.aivillager.entity.AIWanderingTraderEntity;
import com.example.aivillager.world.VillageAIVillagerSpawner;
import com.example.aivillager.world.AITraderSpawner;
import com.example.aivillager.ai.AIVillagerCommands;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AIVillagerMod implements ModInitializer {

    public static final String MOD_ID = "aivillager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final EntityType<AIVillagerEntity> AI_VILLAGER = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MOD_ID, "ai_villager"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, AIVillagerEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
            .trackRangeBlocks(10)
            .build()
    );

    public static final EntityType<AIWanderingTraderEntity> AI_WANDERING_TRADER = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MOD_ID, "ai_wandering_trader"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, AIWanderingTraderEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
            .trackRangeBlocks(10)
            .build()
    );

    public static final SpawnEggItem AI_VILLAGER_SPAWN_EGG = Registry.register(
        Registries.ITEM,
        new Identifier(MOD_ID, "ai_villager_spawn_egg"),
        new SpawnEggItem(AI_VILLAGER, 0x6B3D22, 0xE8D5A3, new net.minecraft.item.Item.Settings())
    );

    public static final SpawnEggItem AI_WANDERING_TRADER_SPAWN_EGG = Registry.register(
        Registries.ITEM,
        new Identifier(MOD_ID, "ai_wandering_trader_spawn_egg"),
        new SpawnEggItem(AI_WANDERING_TRADER, 0x4B6E8E, 0xD4C43A, new net.minecraft.item.Item.Settings())
    );

    @Override
    public void onInitialize() {
        LOGGER.info("AI Villager & Trader Mod 初始化中...");

        FabricDefaultAttributeRegistry.register(AI_VILLAGER, AIVillagerEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AI_WANDERING_TRADER, AIWanderingTraderEntity.createMobAttributes());

        AIVillagerConfig.loadConfig();
        AIVillagerCommands.register();
        VillageAIVillagerSpawner.register();
        AITraderSpawner.register();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(AI_VILLAGER_SPAWN_EGG);
            entries.add(AI_WANDERING_TRADER_SPAWN_EGG);
        });

        LOGGER.info("AI Villager & Trader Mod 初始化完成!");
    }
}
