package com.example.aivillager.ai;

import com.example.aivillager.entity.AIVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import java.util.*;

public class AIVillagerBrain {
    private final AIVillagerEntity villager;
    private final Map<String, List<String>> kb = new HashMap<>();
    private final Random r = new Random();

    public AIVillagerBrain(AIVillagerEntity v) {
        this.villager = v;
        kb.put("greetings", Arrays.asList("你好，旅行者！","今天天气不错！","嗨！新面孔！"));
        kb.put("friendly", Arrays.asList("有什么可以帮你？","需要建议吗？","村庄很安全。"));
        kb.put("neutral", Arrays.asList("嗯...让我看看。","每个人都有自己的路。"));
        kb.put("grumpy", Arrays.asList("别打扰我！","烦人的冒险者。","没事请离开。"));
    }

    public String generateGreeting(PlayerEntity p) {
        float rel = villager.getTrustLevel();
        if (rel > 70) return "老朋友！又见面了！";
        if (rel < 30) return "哼，又来了。";
        return getR("greetings");
    }

    public String getReactionToGift(ItemStack g) {
        if (g.getItem() == Items.EMERALD) return "绿宝石！太感谢了！";
        if (g.getItem() == Items.DIAMOND) return "钻石！太珍贵了！";
        if (g.isFood()) return "食物！真贴心！";
        return "谢谢你的礼物！";
    }

    private String getR(String cat) {
        List<String> l = kb.get(cat);
        return l != null && !l.isEmpty() ? l.get(r.nextInt(l.size())) : "嗯...";
    }
}