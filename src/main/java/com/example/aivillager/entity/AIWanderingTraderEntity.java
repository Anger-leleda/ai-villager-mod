package com.example.aivillager.entity;

import com.example.aivillager.AIVillagerConfig;
import com.example.aivillager.ai.AITraderBrain;
import com.example.aivillager.ai.TraderTradeGenerator;
import com.example.aivillager.world.AINameGenerator;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;

public class AIWanderingTraderEntity extends WanderingTraderEntity {

    private final AITraderBrain aiBrain;
    private String personality = "generous";
    private float trustLevel = 30.0f;
    private int knowledgeLevel = 3;
    private int interactionCooldown = 0;
    private int tradesRemaining = 6;
    private long spawnTime;

    public AIWanderingTraderEntity(EntityType<? extends WanderingTraderEntity> entityType, World world) {
        super(entityType, world);
        this.aiBrain = new AITraderBrain(this);
        this.spawnTime = world.getTime();
        assignRandomPersonality();
        if (!hasCustomName()) setCustomName(AINameGenerator.generateNameByPersonality(personality));
        if (!world.isClient) initializeTrades();
    }

    private void assignRandomPersonality() {
        double gc = AIVillagerConfig.config.generousTraderChance;
        double mc = AIVillagerConfig.config.mysteriousTraderChance;
        double roll = random.nextDouble();
        if (roll < gc) { personality = "generous"; trustLevel = 40.0f; knowledgeLevel = 4; }
        else if (roll < gc + mc) { personality = "mysterious"; trustLevel = 25.0f; knowledgeLevel = 5; }
        else { personality = "shrewd"; trustLevel = 30.0f; knowledgeLevel = 3; }
    }

    private void initializeTrades() {
        TradeOfferList o = getOffers(); o.clear();
        java.util.Random r = new java.util.Random();
        switch (personality) {
            case "generous" -> TraderTradeGenerator.generateGenerousTrades(o, r, this);
            case "mysterious" -> TraderTradeGenerator.generateMysteriousTrades(o, r, this);
            case "shrewd" -> TraderTradeGenerator.generateShrewdTrades(o, r, this);
        }
        TraderTradeGenerator.addEnchantedItemTrades(o, r, this);
    }

    @Override
    public void tick() {
        super.tick();
        if (interactionCooldown > 0) interactionCooldown--;
        long maxTime = "generous".equals(personality) ? 12000 : 6000;
        if (!getWorld().isClient && getWorld().getTime() - spawnTime > maxTime) {
            if (random.nextFloat() < ("generous".equals(personality) ? 0.05f : 0.1f)) discard();
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (getWorld().isClient) return ActionResult.SUCCESS;
        if (interactionCooldown > 0) {
            player.sendMessage(Text.literal("<" + getName().getString() + "> 稍等..."), true);
            return ActionResult.CONSUME;
        }
        if (player.isSneaking() && player.getStackInHand(hand).isEmpty()) { startConversation(player); return ActionResult.SUCCESS; }
        if (!player.getStackInHand(hand).isEmpty() && player.isSneaking()) { handleGift(player, player.getStackInHand(hand)); return ActionResult.SUCCESS; }
        if (!player.isSneaking()) {
            String title = switch (personality) {
                case "generous" -> "§a" + getName().getString() + " §f- 慷慨的商人";
                case "mysterious" -> "§5" + getName().getString() + " §f- 神秘的商人";
                default -> "§6" + getName().getString() + " §f- 精明的商人";
            };
            setCustomer(player);
            sendOffers(player, Text.literal(title), 1);
            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }

    private void startConversation(PlayerEntity player) {
        player.sendMessage(Text.literal("<" + getName().getString() + "> " + aiBrain.generateGreeting(player)), false);
        if (aiBrain.hasAncientItem()) player.sendMessage(Text.literal("§d⚠ " + getName().getString() + "的交易中有异常物品...").formatted(Formatting.BOLD), false);
        float hintChance = switch (personality) { case "generous" -> 0.6f; case "mysterious" -> 0.2f; default -> 0.3f; };
        if (aiBrain.hasAncientItem()) hintChance += 0.3f;
        if (random.nextFloat() < hintChance && trustLevel > 40) player.sendMessage(Text.literal("<" + getName().getString() + "> " + aiBrain.generateTradeHint(player)).formatted(Formatting.ITALIC), false);
        interactionCooldown = 30;
    }

    private void handleGift(PlayerEntity player, ItemStack gift) {
        float ti = (float) AIVillagerConfig.config.giftTrustIncrease;
        if ("generous".equals(personality)) ti *= 1.5f;
        if (gift.getItem() == Items.EMERALD) ti *= 3;
        else if (gift.getItem() == Items.DIAMOND) ti *= 5;
        else if (gift.getItem() == Items.GOLD_INGOT) ti *= 2;
        adjustTrust(ti);
        player.sendMessage(Text.literal("<" + getName().getString() + "> " + aiBrain.getReactionToGift(gift)), false);
        if (!player.isCreative()) gift.decrement(1);
        if (trustLevel > ("generous".equals(personality) ? 50 : 70) && tradesRemaining > 0) {
            TraderTradeGenerator.addSpecialTrade(getOffers(), new java.util.Random(), this);
            tradesRemaining--;
            player.sendMessage(Text.literal("§a" + getName().getString() + " 解锁了特殊交易！"), false);
        }
    }

    public void afterUsing(TradeOfferList offer) {
        adjustTrust("generous".equals(personality) ? 3.0f : 2.0f);
        if (random.nextFloat() < ("generous".equals(personality) ? 0.4f : 0.2f) && trustLevel > 50)
            TraderTradeGenerator.refreshTrades(getOffers(), new java.util.Random(), this);
    }

    public String getPersonality() { return personality; }
    public float getTrustLevel() { return trustLevel; }
    public void adjustTrust(float a) { trustLevel = Math.max(0, Math.min(100, trustLevel + a)); }
    public int getKnowledgeLevel() { return knowledgeLevel; }
    public AITraderBrain getAiBrain() { return aiBrain; }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("Personality", personality);
        nbt.putFloat("TrustLevel", trustLevel);
        nbt.putInt("KnowledgeLevel", knowledgeLevel);
        nbt.putInt("TradesRemaining", tradesRemaining);
        nbt.putLong("SpawnTime", spawnTime);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        personality = nbt.getString("Personality");
        trustLevel = nbt.getFloat("TrustLevel");
        knowledgeLevel = nbt.getInt("KnowledgeLevel");
        tradesRemaining = nbt.getInt("TradesRemaining");
        spawnTime = nbt.getLong("SpawnTime");
    }
}