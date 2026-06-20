package com.example.aivillager.mixin;

import com.example.aivillager.ai.TraderTradeGenerator;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public class VillagerTradeMixin {

    @Inject(method = "fillRecipes", at = @At("TAIL"), require = 0)
    private void addCustomTrades(CallbackInfo ci) {
        try {
            VillagerEntity self = (VillagerEntity) (Object) this;
            TradeOfferList offers = self.getOffers();
            java.util.Random r = new java.util.Random();
            TraderTradeGenerator.generateVillagerTrades(offers, r);
        } catch (Exception ignored) {}
    }
}