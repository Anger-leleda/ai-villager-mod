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
        ItemStack item; int type = r.nextInt(100);
        if (type<30) item=genW(r,trust,p); else if (type<60) item=genT(r,trust,p); else if (type<85) item=genA(r,trust,p); else item=genB(r,trust,p);
        if (item==null) return null;
        int cost = calc(item, p); if ("generous".equals(p)) cost=(int)(cost*0.6); cost=Math.max(2,cost);
        return new TradeOffer(new ItemStack(Items.EMERALD, cost), item, "generous".equals(p)?2:1, cost/2, 0.3f);
    }

    private static ItemStack genW(Random r, float trust, String p) {
        Item[] w = ("generous".equals(p)&&(trust>60||r.nextFloat()<0.3f))?new Item[]{Items.DIAMOND_SWORD,Items.NETHERITE_SWORD}:new Item[]{Items.DIAMOND_SWORD,Items.IRON_SWORD};
        ItemStack s = new ItemStack(w[r.nextInt(w.length)]); applyE(s,r,trust,p); return s;
    }
    private static ItemStack genT(Random r, float trust, String p) {
        Item[] t = ("generous".equals(p)&&(trust>60||r.nextFloat()<0.3f))?new Item[]{Items.DIAMOND_PICKAXE,Items.NETHERITE_PICKAXE}:new Item[]{Items.DIAMOND_PICKAXE};
        ItemStack s = new ItemStack(t[r.nextInt(t.length)]); applyE(s,r,trust,p); return s;
    }
    private static ItemStack genA(Random r, float trust, String p) {
        Item[] a = ("generous".equals(p)&&(trust>60||r.nextFloat()<0.3f))?new Item[]{Items.DIAMOND_CHESTPLATE,Items.NETHERITE_CHESTPLATE}:new Item[]{Items.DIAMOND_CHESTPLATE};
        ItemStack s = new ItemStack(a[r.nextInt(a.length)]); applyE(s,r,trust,p); return s;
    }
    private static ItemStack genB(Random r, float trust, String p) { ItemStack b = new ItemStack(Items.ENCHANTED_BOOK); applyE(b,r,trust,p); return b; }

    private static void applyE(ItemStack s, Random r, float trust, String p) {
        List<Enchantment> avail = getAvail(s); if (avail.isEmpty()) return;
        int count; boolean max;
        if ("generous".equals(p)) { if(trust>70){count=r.nextInt(4)+3;max=r.nextFloat()<0.5f;}else if(trust>40){count=r.nextInt(3)+2;max=r.nextFloat()<0.35f;}else{count=r.nextInt(2)+1;max=r.nextFloat()<0.2f;} }
        else if ("mysterious".equals(p)) { if(trust>80){count=r.nextInt(3)+2;max=r.nextFloat()<0.4f;}else{count=r.nextInt(2)+1;max=r.nextFloat()<0.25f;} }
        else { count=r.nextInt(2)+1;max=r.nextFloat()<0.2f; }
        Collections.shuffle(avail, r); int app=0;
        for (Enchantment e : avail) {
            if (app>=count) break;
            int lv = (max&&r.nextFloat()<0.3f)?e.getMaxLevel():r.nextInt(e.getMaxLevel())+1;
            boolean conf = false;
            for (Enchantment ex : EnchantmentHelper.get(s).keySet()) if (!e.canCombine(ex)) { conf=true; break; }
            if (!conf) { if (s.getItem()==Items.ENCHANTED_BOOK) EnchantedBookItem.addEnchantment(s,new EnchantmentLevelEntry(e,lv)); else s.addEnchantment(e,lv); app++; }
        }
    }

    private static List<Enchantment> getAvail(ItemStack s) {
        List<Enchantment> l = new ArrayList<>(); boolean isB = s.getItem()==Items.ENCHANTED_BOOK;
        for (Enchantment e : net.minecraft.registry.Registries.ENCHANTMENT) { if (!BLACK.contains(e)&&!e.isCursed()&&(isB||e.isAcceptableItem(s))) l.add(e); }
        return l;
    }

    private static int calc(ItemStack s, String p) {
        int c=5; Item i=s.getItem();
        if (i==Items.NETHERITE_SWORD||i==Items.NETHERITE_PICKAXE||i==Items.NETHERITE_CHESTPLATE||i==Items.NETHERITE_BOOTS) c+=25;
        else if (i==Items.DIAMOND_SWORD||i==Items.DIAMOND_PICKAXE||i==Items.DIAMOND_CHESTPLATE) c+=15;
        for (var e : EnchantmentHelper.get(s).entrySet()) c+=e.getValue()>=e.getKey().getMaxLevel()?e.getValue()*10:e.getValue()*5;
        if (s.getItem()==Items.ENCHANTED_BOOK) c+=10;
        if ("generous".equals(p)) c=(int)(c*0.7);
        return Math.max(3,c);
    }

    private static void addAncient(TradeOfferList o, Random r, AIWanderingTraderEntity t) {
        ItemStack item; switch(r.nextInt(6)) {
            case 0: item=allProt(r); break; case 1: item=infBow(r); break; case 2: item=allSharp(r); break;
            case 3: item=silkFort(r); break; case 4: item=allBoots(r); break; default: item=ancBook(r); break;
        }
        if (item!=null) {
            NbtCompound nbt=item.getOrCreateNbt(); nbt.putBoolean("AncientBug",true);
            o.add(new TradeOffer(new ItemStack(Items.EMERALD,1),item,1,50,1f));
            if(t!=null)t.getAiBrain().setHasAncientItem(true);
        }
    }

    private static ItemStack allProt(Random r){ItemStack a=new ItemStack(r.nextBoolean()?Items.NETHERITE_CHESTPLATE:Items.DIAMOND_CHESTPLATE);a.addEnchantment(Enchantments.PROTECTION,4);a.addEnchantment(Enchantments.BLAST_PROTECTION,4);a.addEnchantment(Enchantments.FIRE_PROTECTION,4);a.addEnchantment(Enchantments.PROJECTILE_PROTECTION,4);a.addEnchantment(Enchantments.UNBREAKING,3);return a;}
    private static ItemStack infBow(Random r){ItemStack b=new ItemStack(Items.BOW);b.addEnchantment(Enchantments.INFINITY,1);b.addEnchantment(Enchantments.MENDING,1);b.addEnchantment(Enchantments.POWER,5);b.addEnchantment(Enchantments.FLAME,1);b.addEnchantment(Enchantments.PUNCH,2);b.addEnchantment(Enchantments.UNBREAKING,3);return b;}
    private static ItemStack allSharp(Random r){ItemStack s=new ItemStack(r.nextBoolean()?Items.NETHERITE_SWORD:Items.DIAMOND_SWORD);s.addEnchantment(Enchantments.SHARPNESS,5);s.addEnchantment(Enchantments.SMITE,5);s.addEnchantment(Enchantments.BANE_OF_ARTHROPODS,5);s.addEnchantment(Enchantments.LOOTING,3);s.addEnchantment(Enchantments.UNBREAKING,3);s.addEnchantment(Enchantments.MENDING,1);return s;}
    private static ItemStack silkFort(Random r){ItemStack t=new ItemStack(r.nextBoolean()?Items.NETHERITE_PICKAXE:Items.DIAMOND_PICKAXE);t.addEnchantment(Enchantments.SILK_TOUCH,1);t.addEnchantment(Enchantments.FORTUNE,3);t.addEnchantment(Enchantments.EFFICIENCY,5);t.addEnchantment(Enchantments.UNBREAKING,3);t.addEnchantment(Enchantments.MENDING,1);return t;}
    private static ItemStack allBoots(Random r){ItemStack b=new ItemStack(r.nextBoolean()?Items.NETHERITE_BOOTS:Items.DIAMOND_BOOTS);b.addEnchantment(Enchantments.PROTECTION,4);b.addEnchantment(Enchantments.FEATHER_FALLING,4);b.addEnchantment(Enchantments.THORNS,3);b.addEnchantment(Enchantments.DEPTH_STRIDER,3);b.addEnchantment(Enchantments.FROST_WALKER,2);b.addEnchantment(Enchantments.SOUL_SPEED,3);b.addEnchantment(Enchantments.UNBREAKING,3);b.addEnchantment(Enchantments.MENDING,1);return b;}
    private static ItemStack ancBook(Random r){ItemStack b=new ItemStack(Items.ENCHANTED_BOOK);List<EnchantmentLevelEntry> list=Arrays.asList(new EnchantmentLevelEntry(Enchantments.PROTECTION,4),new EnchantmentLevelEntry(Enchantments.BLAST_PROTECTION,4),new EnchantmentLevelEntry(Enchantments.SHARPNESS,5),new EnchantmentLevelEntry(Enchantments.SMITE,5),new EnchantmentLevelEntry(Enchantments.SILK_TOUCH,1),new EnchantmentLevelEntry(Enchantments.FORTUNE,3),new EnchantmentLevelEntry(Enchantments.INFINITY,1),new EnchantmentLevelEntry(Enchantments.MENDING,1));Collections.shuffle(list,r);for(int i=0;i<Math.min(r.nextInt(3)+3,list.size());i++)EnchantedBookItem.addEnchantment(b,list.get(i));return b;}

    public static void addSpecialTrade(TradeOfferList o, Random r, AIWanderingTraderEntity t) {
        ItemStack item=genET(r,100,t.getPersonality()).getSellItem();
        int cost="generous".equals(t.getPersonality())?r.nextInt(15)+15:r.nextInt(20)+30;
        o.add(new TradeOffer(new ItemStack(Items.EMERALD,cost),item,"generous".equals(t.getPersonality())?2:1,20,0.5f));
    }

    public static void refreshTrades(TradeOfferList o, Random r, AIWanderingTraderEntity t) {
        o.removeIf(of->of.getSellItem().hasEnchantments()||of.getSellItem().getItem()==Items.ENCHANTED_BOOK);
        addEnchantedItemTrades(o,r,t);
    }
}