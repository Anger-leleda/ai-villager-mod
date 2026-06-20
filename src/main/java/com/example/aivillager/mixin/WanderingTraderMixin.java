package com.example.aivillager.mixin;

import com.example.aivillager.ai.TraderTradeGenerator;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTraderEntity.class)
public class WanderingTraderMixin {

    @Inject(method = "fillRecipes", at = @At("TAIL"))
    private void addCustomTrades(CallbackInfo ci) {
        WanderingTraderEntity self = (WanderingTraderEntity) (Object) this;
        TradeOfferList offers = self.getOffers();
        java.util.Random r = new java.util.Random();
        TraderTradeGenerator.generateWanderingTraderTrades(offers, r);
    }
}