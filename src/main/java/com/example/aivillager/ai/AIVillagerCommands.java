package com.example.aivillager.ai;

import com.example.aivillager.AIVillagerConfig;
import com.example.aivillager.world.VillageAIVillagerSpawner;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class AIVillagerCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((d, ra, e) -> d.register(CommandManager.literal("aivillager")
            .requires(s -> s.hasPermissionLevel(2))
            .then(CommandManager.literal("reload").executes(ctx -> { AIVillagerConfig.reloadConfig(); ctx.getSource().sendFeedback(()->Text.literal("§a配置已重载！"),true); return 1; }))
            .then(CommandManager.literal("info").executes(ctx -> { var c=AIVillagerConfig.config; ctx.getSource().sendFeedback(()->Text.literal(String.format("§6=== AI村民模组 ===\n§e村庄生成:%.0f%% | 每村上限:%d | 慷慨型:%.0f%% | 远古物品:%.2f%%",c.villageSpawnChance*100,c.maxPerVillage,c.generousTraderChance*100,c.ancientBugChanceNormal*100)),false); return 1; }))
            .then(CommandManager.literal("spawn").executes(ctx -> { VillageAIVillagerSpawner.forceSpawnInCurrentVillage(ctx.getSource().getWorld(),ctx.getSource().getPlayer().getBlockPos()); ctx.getSource().sendFeedback(()->Text.literal("§a已尝试生成AI村民"),true); return 1; }))
            .then(CommandManager.literal("setChance").then(CommandManager.argument("chance",DoubleArgumentType.doubleArg(0,1)).executes(ctx -> { AIVillagerConfig.config.villageSpawnChance=DoubleArgumentType.getDouble(ctx,"chance"); AIVillagerConfig.saveConfig(); ctx.getSource().sendFeedback(()->Text.literal("§a村庄生成概率已设为:"+(AIVillagerConfig.config.villageSpawnChance*100)+"%"),true); return 1; })))
            .then(CommandManager.literal("setMax").then(CommandManager.argument("count",IntegerArgumentType.integer(1,10)).executes(ctx -> { AIVillagerConfig.config.maxPerVillage=IntegerArgumentType.getInteger(ctx,"count"); AIVillagerConfig.saveConfig(); ctx.getSource().sendFeedback(()->Text.literal("§a每村上限已设为:"+AIVillagerConfig.config.maxPerVillage),true); return 1; })))
        ));
    }
}