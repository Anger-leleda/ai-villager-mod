package com.example.aivillager.ai;

import com.example.aivillager.entity.AIWanderingTraderEntity;
import net.minecraft.enchantment.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import java.util.*;

public class TraderTradeGenerator {
    private static final Random R = new Random();
    private static final Set<Enchantment> BLACK = Set.of(Enchantments.BINDING_CURSE, Enchantments.VANISHING_CURSE);

    public static void generateGenerousTrades(TradeOfferList o, Random r, AIWanderingTraderEntity t) {
        o.add(new TradeOffer(new ItemStack(Items.EMERALD, r.nextInt(2)+1), new ItemStack(Items.DIAMOND, r.nextInt(3)+1), 6, 2, 0f));
        o.add(new TradeOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.GOLDEN_APPLE, r.nextInt(2)+1), 8, 1, 0f));
        o.add(new TradeOffer(new ItemStack(Items.EMERALD, r.nextInt(2)+1), new ItemStack(Items.EXPERIENCE_BOTTLE, r.nextInt(12)+4), 4, 3, 0f));
        o.add(new TradeOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.IRON_INGOT, r.nextInt(8)+4), 10, 2, 0f));
        if (r.nextFloat() < 0.001) addAncient(o, r, t);
    }
    public static void generateMysteriousTrades(TradeOfferList o, Random r, AIWanderingTraderEntity t) {
        o.add(new TradeOffer(new ItemStack(Items.EMERALD, r.nextInt(10)+15), new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), 3, 10, 0.2f));
        o.add(new TradeOffer(new ItemStack(Items.EMERALD, r.nextInt(5)+10), new ItemStack(Items.NETHERITE_SCRAP), 2, 8, 0.15f));
        o.add(new TradeOffer(new ItemStack(Items.EMERALD, r.nextInt(8)+20), new ItemStack(Items.DRAGON_BREATH), 2, 12, 0.25f));
        if (r.nextFloat() < 0.003) addAncient(o, r, t);
    }
    public static void generateShrewdTrades(TradeOfferList o, Random r, AIWanderingTraderEntity t) {
        o.add(new TradeOffer(new ItemStack(Items.EMERALD, r.nextInt(5)+5), new ItemStack(Items.DIAMOND, r.nextInt(2)+1), 5, 5, 0.1f));
        o.add(new TradeOffer(new ItemStack(Items.EMERALD, r.nextInt(3)+8), new ItemStack(Items.EXPERIENCE_BOTTLE, r.nextInt(8)+4), 4, 6, 0.15f));
        o.add(new TradeOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.SLIME_BALL, r.nextInt(4)+2), 8, 2, 0.05f));
        if (r.nextFloat() < 0.001) addAncient(o, r, t);
    }

    public static void addEnchantedItemTrades(TradeOfferList o, Random r, AIWanderingTraderEntity t) {
        String p = t.getPersonality(); float trust = t.getTrustLevel(); int c = 0;
        if ("generous".equals(p)) { if (trust>60) c=r.nextInt(4)+3; else if (trust>30) c=r.nextInt(3)+2; else c=r.nextInt(2)+1; }
        else if ("mysterious".equals(p)) { if (trust>80) c=r.nextInt(3)+2; else if (trust>50) c=r.nextInt(2)+1; else c=r.nextFloat()<0.3f?1:0; }
        else { if (trust>70) c=r.nextInt(2)+1; else c=r.nextFloat()<0.4f?1:0; }
        for (int i=0;i<c;i++) { TradeOffer et = genET(r, trust, p); if (et!=null) o.add(et); }
    }

    private static TradeOffer genET(Random r, float trust, String p) {