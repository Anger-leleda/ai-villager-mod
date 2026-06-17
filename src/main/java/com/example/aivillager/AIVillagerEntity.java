package com.example.aivillager.entity;

import com.example.aivillager.AIVillagerMod;
import com.example.aivillager.ai.AIVillagerBrain;
import com.example.aivillager.world.AINameGenerator;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class AIVillagerEntity extends VillagerEntity {

    private final AIVillagerBrain aiBrain;
    private String personality = "friendly";
    private float trustLevel = 50.0f;
    private int knowledgeLevel = 1;
    private int interactionCooldown = 0;
    private boolean isNaturalSpawned = false;

    public AIVillagerEntity(EntityType<? extends VillagerEntity> entityType, World world) {
        super(entityType, world);
        this.aiBrain = new AIVillagerBrain(this);
        assignRandomPersonality();
        this.knowledgeLevel = this.random.nextInt(5) + 1;
        if (!this.hasCustomName()) this.setCustomName(AINameGenerator.generateName());
    }

    private void assignRandomPersonality() {
        double[] chances = AIVillagerMod.AIVillagerConfig.config.personalityChances;
        String[] types = AIVillagerMod.AIVillagerConfig.config.personalityTypes;
        double roll = this.random.nextDouble(), cum = 0.0;
        for (int i = 0; i < types.length; i++) {
            cum += chances[i];
            if (roll <= cum) { this.personality = types[i]; return; }
        }
        this.personality = types[types.length - 1];
    }

    @Override
    public void tick() {
        super.tick();
        if (interactionCooldown > 0) interactionCooldown--;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient) return ActionResult.SUCCESS;
        if (interactionCooldown > 0) {
            player.sendMessage(Text.literal(this.getName().getString() + " 正在思考中..."), true);
            return ActionResult.CONSUME;
        }
        if (!player.getStackInHand(hand).isEmpty()) {
            handleGift(player, player.getStackInHand(hand));
            return ActionResult.SUCCESS;
        }
        startConversation(player);
        return ActionResult.SUCCESS;
    }

    private void handleGift(PlayerEntity player, net.minecraft.item.ItemStack gift) {
        float ti = (float) AIVillagerMod.AIVillagerConfig.config.giftTrustIncrease;
        if (gift.getItem() == Items.EMERALD) ti *= 2;
        else if (gift.getItem() == Items.DIAMOND) ti *= 3;
        adjustTrust(ti);
        String reaction = aiBrain.getReactionToGift(gift);
        if (reaction != null) player.sendMessage(Text.literal("<" + getName().getString() + "> " + reaction), false);
        if (!player.isCreative()) gift.decrement(1);
    }

    private void startConversation(PlayerEntity player) {
        player.sendMessage(Text.literal("<" + getName().getString() + "> " + aiBrain.generateGreeting(player)), false);
        interactionCooldown = AIVillagerMod.AIVillagerConfig.config.interactionCooldown;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() instanceof PlayerEntity p) {
            adjustTrust(-(float) AIVillagerMod.AIVillagerConfig.config.hurtTrustDecrease);
            p.sendMessage(Text.literal("<" + getName().getString() + "> 为什么伤害我？！"), false);
        }
        return super.damage(source, amount);
    }

    public String getPersonality() { return personality; }
    public float getTrustLevel() { return trustLevel; }
    public void adjustTrust(float a) { trustLevel = Math.max(0, Math.min(100, trustLevel + a)); }
    public int getKnowledgeLevel() { return knowledgeLevel; }
    public void learnNewThings(int a) { knowledgeLevel = Math.min(AIVillagerMod.AIVillagerConfig.config.maxKnowledgeLevel, knowledgeLevel + a); }
    public AIVillagerBrain getAiBrain() { return aiBrain; }
    public boolean isNaturalSpawned() { return isNaturalSpawned; }
    public void setNaturalSpawned(boolean v) { isNaturalSpawned = v; }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("Personality", personality);
        nbt.putFloat("TrustLevel", trustLevel);
        nbt.putInt("KnowledgeLevel", knowledgeLevel);
        nbt.putInt("InteractionCooldown", interactionCooldown);
        nbt.putBoolean("IsNaturalSpawned", isNaturalSpawned);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        personality = nbt.getString("Personality");
        trustLevel = nbt.getFloat("TrustLevel");
        knowledgeLevel = nbt.getInt("KnowledgeLevel");
        interactionCooldown = nbt.getInt("InteractionCooldown");
        isNaturalSpawned = nbt.getBoolean("IsNaturalSpawned");
    }
}