package com.example.aivillager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.WanderingTraderEntityRenderer;

public class AIVillagerModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(AIVillagerMod.AI_VILLAGER,
            ctx -> new VillagerEntityRenderer(ctx));
        EntityRendererRegistry.register(AIVillagerMod.AI_WANDERING_TRADER,
            ctx -> new WanderingTraderEntityRenderer(ctx));
    }
}