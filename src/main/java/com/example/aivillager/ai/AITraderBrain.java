package com.example.aivillager.ai;

import com.example.aivillager.entity.AIWanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import java.util.*;

public class AITraderBrain {
    private final AIWanderingTraderEntity trader;
    private final Map<UUID, Float> pr = new HashMap<>();
    private final Random r = new Random();
    private boolean hasAncient = false;
    private static final Map<String, List<String>> D = new HashMap<>();
    static {
        D.put("mysterious_greeting", Arrays.asList("远方的旅人...我有稀有物品...","嘘...别声张。","从下界到末地，我收集了很多。"));
        D.put("shrewd_greeting", Arrays.asList("看看我的货，物超所值。","绿宝石先拿来。","我知道什么是好东西。"));
        D.put("generous_greeting", Arrays.asList("欢迎！今天有好东西！","朋友，价格公道！","来选点装备吧！"));
        D.put("ancient_hint", Arrays.asList("§d这件物品...不对劲...","§5它不应存在...","§d只要1个绿宝石..."));
        D.put("gift", Arrays.asList("慷慨的旅人！","好礼物！","谢谢你！"));
    }

    public AITraderBrain(AIWanderingTraderEntity t) { this.trader = t; }
    public void setHasAncientItem(boolean v) { hasAncient = v; }
    public boolean hasAncientItem() { return hasAncient; }

    public String generateGreeting(PlayerEntity p) {
        if (hasAncient && r.nextFloat() < 0.5f) return new String[]{"§d我发现了一些...不同寻常的东西...","§5今天的货物里有件特别的物品..."}[r.nextInt(2)];
        if (getRel(p) > 70) return "又见面了，老朋友！";
        List<String> g = D.get(trader.getPersonality() + "_greeting");
        return g != null ? g.get(r.nextInt(g.size())) : "你好，旅人。";
    }

    public String generateTradeHint(PlayerEntity p) {
        if (hasAncient && r.nextFloat() < 0.6f) { List<String> ah = D.get("ancient_hint"); if (ah != null) return ah.get(r.nextInt(ah.size())); }
        return "看看我的货物吧。";
    }

    public String getReactionToGift(ItemStack g) {
        if (g.getItem() == Items.EMERALD) return "绿宝石！硬通货！";
        if (g.getItem() == Items.DIAMOND) return "钻石！太珍贵了！";
        if (g.getItem() == Items.NETHERITE_SCRAP) return "下界合金碎片？！";
        if (g.getItem() == Items.ENCHANTED_BOOK) { if (r.nextFloat() < 0.01f) { hasAncient = true; return "§d这本附魔书...让我想起了一件东西..."; } return "附魔书！正好需要。"; }
        List<String> rr = D.get("gift"); return rr != null ? rr.get(r.nextInt(rr.size())) : "谢谢。";
    }

    private float getRel(PlayerEntity p) { return pr.getOrDefault(p.getUuid(), 30.0f); }
    public void updateRelation(PlayerEntity p, float c) { pr.put(p.getUuid(), Math.max(0, Math.min(100, getRel(p) + c))); }
}