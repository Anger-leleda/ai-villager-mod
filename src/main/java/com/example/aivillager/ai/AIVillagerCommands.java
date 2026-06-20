package com.example.aivillager.ai;

import com.example.aivillager.AIVillagerConfig;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class AIVillagerCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((d, ra, e) -> d.register(
            CommandManager.literal("aivillager")
                .requires(s -> s.hasPermissionLevel(2))
                .then(CommandManager.literal("reload").executes(ctx -> {
                    AIVillagerConfig.reloadConfig();
                    ctx.getSource().sendFeedback(() -> Text.literal("§a配置已重载！"), true);
                    return 1;
                }))
                .then(CommandManager.literal("info").executes(ctx -> {
                    var c = AIVillagerConfig.config;
                    ctx.getSource().sendFeedback(() -> Text.literal(String.format(
                        "§6=== AI交易增强 ===\n§e慷慨型:%.0f%% | 远古物品(普通):%.2f%% | 远古物品(神秘):%.2f%%",
                        c.generousTraderChance * 100, c.ancientBugChanceNormal * 100, c.ancientBugChanceMysterious * 100)), false);
                    return 1;
                }))
                .then(CommandManager.literal("setChance").then(CommandManager.argument("chance", DoubleArgumentType.doubleArg(0, 1)).executes(ctx -> {
                    AIVillagerConfig.config.generousTraderChance = DoubleArgumentType.getDouble(ctx, "chance");
                    AIVillagerConfig.saveConfig();
                    ctx.getSource().sendFeedback(() -> Text.literal("§a慷慨型概率已设为:" + (AIVillagerConfig.config.generousTraderChance * 100) + "%"), true);
                    return 1;
                })))
        ));
    }
}