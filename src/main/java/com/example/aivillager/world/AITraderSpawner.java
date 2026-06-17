package com.example.aivillager.world;

import com.example.aivillager.AIVillagerMod;
import com.example.aivillager.AIVillagerConfig;
import com.example.aivillager.entity.AIWanderingTraderEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.server.world.ServerWorld;

public class AITraderSpawner {
    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((e, w) -> {
            if (!AIVillagerConfig.config.enableAIVillagers) return;
            if (e instanceof WanderingTraderEntity && !(e instanceof AIWanderingTraderEntity) && e.getType() == EntityType.WANDERING_TRADER) {
                ServerWorld sw = (ServerWorld) w;
                if (sw.random.nextFloat() < AIVillagerConfig.config.traderReplaceChance) {
                    AIWanderingTraderEntity ai = new AIWanderingTraderEntity(AIVillagerMod.AI_WANDERING_TRADER, sw);
                    ai.refreshPositionAndAngles(e.getX(), e.getY(), e.getZ(), e.getYaw(), e.getPitch());
                    e.discard();
                    sw.spawnEntity(ai);
                }
            }
        });
    }
}