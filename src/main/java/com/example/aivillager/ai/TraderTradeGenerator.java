package com.example.aivillager.ai;

import com.example.aivillager.AIVillagerConfig;
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

    public static void generateVillagerTrades(TradeOfferList offers, Random r) {
        if (r.nextFloat() < 0.3f) {
            addEnchantedTrade(offers, r, "generous");
        }
        if (r.nextFloat() < 0.001f) {
            addAncientItem(offers, r);
        }
    }

    public static void generateWanderingTraderTrades(TradeOfferList offers, Random r) {
        String personality;
        double roll = r.nextDouble();
        if (roll < AIVillagerConfig.config.generousTraderChance) personality = "generous";
        else if (roll < AIVillagerConfig.config.generousTraderChance + AIVillagerConfig.config.mysteriousTraderChance) personality = "mysterious";
        else personality = "shrewd";

        int count = switch (personality) {
            case "generous" -> r.nextInt(3) + 2;
            case "mysterious" -> r.nextInt(2) + 1;
            default -> r.nextFloat() < 0.5f ? 1 : 0;
        };
        for (int i = 0; i < count; i++) {
            addEnchantedTrade(offers, r, personality);
        }

        double ancientChance = "mysterious".equals(personality) ? AIVillagerConfig.config.ancientBugChanceMysterious : AIVillagerConfig.config.ancientBugChanceNormal;
        if (r.nextFloat() < ancientChance) {
            addAncientItem(offers, r);
        }
    }

    private static void addEnchantedTrade(TradeOfferList offers, Random r, String personality) {
        ItemStack item = genEnchantedItem(r, personality);
        if (item == null) return;
        int cost = calcCost(item, personality);
        if ("generous".equals(personality)) cost = (int) (cost * 0.6);
        cost = Math.max(2, cost);
        offers.add(new TradeOffer(new ItemStack(Items.EMERALD, cost), item, 1, cost / 2, 0.3f));
    }

    private static ItemStack genEnchantedItem(Random r, String personality) {
        ItemStack item;
        int type = r.nextInt(100);
        if (type < 30) item = genWeapon(r, personality);
        else if (type < 60) item = genTool(r, personality);
        else if (type < 85) item = genArmor(r, personality);
        else item = genBook(r, personality);
        applyEnchants(item, r, personality);
        return item;
    }

    private static ItemStack genWeapon(Random r, String p) {
        Item[] items = "generous".equals(p) ? new Item[]{Items.DIAMOND_SWORD, Items.NETHERITE_SWORD} : new Item[]{Items.DIAMOND_SWORD, Items.IRON_SWORD};
        return new ItemStack(items[r.nextInt(items.length)]);
    }

    private static ItemStack genTool(Random r, String p) {
        Item[] items = "generous".equals(p) ? new Item[]{Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE} : new Item[]{Items.DIAMOND_PICKAXE};
        return new ItemStack(items[r.nextInt(items.length)]);
    }

    private static ItemStack genArmor(Random r, String p) {
        Item[] items = "generous".equals(p) ? new Item[]{Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE} : new Item[]{Items.DIAMOND_CHESTPLATE};
        return new ItemStack(items[r.nextInt(items.length)]);
    }

    private static ItemStack genBook(Random r, String p) {
        return new ItemStack(Items.ENCHANTED_BOOK);
    }

    private static void applyEnchants(ItemStack stack, Random r, String personality) {
        List<Enchantment> avail = getAvailable(stack);
        if (avail.isEmpty()) return;
        int count = "generous".equals(personality) ? r.nextInt(3) + 2 : r.nextInt(2) + 1;
        boolean maxLevel = "generous".equals(personality) && r.nextFloat() < 0.4f;
        Collections.shuffle(avail, r);
        int applied = 0;
        for (Enchantment e : avail) {
            if (applied >= count) break;
            int lv = (maxLevel && r.nextFloat() < 0.3f) ? e.getMaxLevel() : r.nextInt(e.getMaxLevel()) + 1;
            boolean conflict = false;
            for (Enchantment ex : EnchantmentHelper.get(stack).keySet()) {
                if (!e.canCombine(ex)) { conflict = true; break; }
            }
            if (!conflict) {
                if (stack.getItem() == Items.ENCHANTED_BOOK) {
                    EnchantedBookItem.addEnchantment(stack, new EnchantmentLevelEntry(e, lv));
                } else {
                    stack.addEnchantment(e, lv);
                }
                applied++;
            }
        }
    }

    private static List<Enchantment> getAvailable(ItemStack stack) {
        List<Enchantment> list = new ArrayList<>();
        boolean isBook = stack.getItem() == Items.ENCHANTED_BOOK;
        for (Enchantment e : net.minecraft.registry.Registries.ENCHANTMENT) {
            if (!BLACK.contains(e) && !e.isCursed() && (isBook || e.isAcceptableItem(stack))) list.add(e);
        }
        return list;
    }

    private static int calcCost(ItemStack stack, String p) {
        int c = 5;
        Item i = stack.getItem();
        if (i == Items.NETHERITE_SWORD || i == Items.NETHERITE_PICKAXE || i == Items.NETHERITE_CHESTPLATE || i == Items.NETHERITE_BOOTS) c += 25;
        else if (i == Items.DIAMOND_SWORD || i == Items.DIAMOND_PICKAXE || i == Items.DIAMOND_CHESTPLATE) c += 15;
        for (var e : EnchantmentHelper.get(stack).entrySet())
            c += e.getValue() >= e.getKey().getMaxLevel() ? e.getValue() * 10 : e.getValue() * 5;
        if (stack.getItem() == Items.ENCHANTED_BOOK) c += 10;
        if ("generous".equals(p)) c = (int) (c * 0.7);
        return Math.max(3, c);
    }

    private static void addAncientItem(TradeOfferList offers, Random r) {
        ItemStack item = switch (r.nextInt(6)) {
            case 0 -> allProtArmor(r);
            case 1 -> infMendBow(r);
            case 2 -> allSharpSword(r);
            case 3 -> silkFortuneTool(r);
            case 4 -> allBoots(r);
            default -> ancientBook(r);
        };
        if (item != null) {
            NbtCompound nbt = item.getOrCreateNbt();
            nbt.putBoolean("AncientBug", true);
            NbtCompound display = nbt.getCompound("display");
            if (display.isEmpty()) display = new NbtCompound();
            NbtList lore = new NbtList();
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal("§c⚠ 远古遗物 ⚠").formatted(Formatting.BOLD))));
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal("§7本不应存在于这个世界..."))));
            display.put("Lore", lore);
            nbt.put("display", display);
            offers.add(new TradeOffer(new ItemStack(Items.EMERALD, 1), item, 1, 50, 1f));
        }
    }

    private static ItemStack allProtArmor(Random r) {
        ItemStack a = new ItemStack(r.nextBoolean() ? Items.NETHERITE_CHESTPLATE : Items.DIAMOND_CHESTPLATE);
        a.addEnchantment(Enchantments.PROTECTION, 4);
        a.addEnchantment(Enchantments.BLAST_PROTECTION, 4);
        a.addEnchantment(Enchantments.FIRE_PROTECTION, 4);
        a.addEnchantment(Enchantments.PROJECTILE_PROTECTION, 4);
        a.addEnchantment(Enchantments.UNBREAKING, 3);
        return a;
    }

    private static ItemStack infMendBow(Random r) {
        ItemStack b = new ItemStack(Items.BOW);
        b.addEnchantment(Enchantments.INFINITY, 1);
        b.addEnchantment(Enchantments.MENDING, 1);
        b.addEnchantment(Enchantments.POWER, 5);
        b.addEnchantment(Enchantments.FLAME, 1);
        b.addEnchantment(Enchantments.PUNCH, 2);
        b.addEnchantment(Enchantments.UNBREAKING, 3);
        return b;
    }

    private static ItemStack allSharpSword(Random r) {
        ItemStack s = new ItemStack(r.nextBoolean() ? Items.NETHERITE_SWORD : Items.DIAMOND_SWORD);
        s.addEnchantment(Enchantments.SHARPNESS, 5);
        s.addEnchantment(Enchantments.SMITE, 5);
        s.addEnchantment(Enchantments.BANE_OF_ARTHROPODS, 5);
        s.addEnchantment(Enchantments.LOOTING, 3);
        s.addEnchantment(Enchantments.UNBREAKING, 3);
        s.addEnchantment(Enchantments.MENDING, 1);
        return s;
    }

    private static ItemStack silkFortuneTool(Random r) {
        ItemStack t = new ItemStack(r.nextBoolean() ? Items.NETHERITE_PICKAXE : Items.DIAMOND_PICKAXE);
        t.addEnchantment(Enchantments.SILK_TOUCH, 1);
        t.addEnchantment(Enchantments.FORTUNE, 3);
        t.addEnchantment(Enchantments.EFFICIENCY, 5);
        t.addEnchantment(Enchantments.UNBREAKING, 3);
        t.addEnchantment(Enchantments.MENDING, 1);
        return t;
    }

    private static ItemStack allBoots(Random r) {
        ItemStack b = new ItemStack(r.nextBoolean() ? Items.NETHERITE_BOOTS : Items.DIAMOND_BOOTS);
        b.addEnchantment(Enchantments.PROTECTION, 4);
        b.addEnchantment(Enchantments.FEATHER_FALLING, 4);
        b.addEnchantment(Enchantments.THORNS, 3);
        b.addEnchantment(Enchantments.DEPTH_STRIDER, 3);
        b.addEnchantment(Enchantments.FROST_WALKER, 2);
        b.addEnchantment(Enchantments.SOUL_SPEED, 3);
        b.addEnchantment(Enchantments.UNBREAKING, 3);
        b.addEnchantment(Enchantments.MENDING, 1);
        return b;
    }

    private static ItemStack ancientBook(Random r) {
        ItemStack b = new ItemStack(Items.ENCHANTED_BOOK);
        List<EnchantmentLevelEntry> list = Arrays.asList(
            new EnchantmentLevelEntry(Enchantments.PROTECTION, 4),
            new EnchantmentLevelEntry(Enchantments.BLAST_PROTECTION, 4),
            new EnchantmentLevelEntry(Enchantments.SHARPNESS, 5),
            new EnchantmentLevelEntry(Enchantments.SMITE, 5),
            new EnchantmentLevelEntry(Enchantments.SILK_TOUCH, 1),
            new EnchantmentLevelEntry(Enchantments.FORTUNE, 3),
            new EnchantmentLevelEntry(Enchantments.INFINITY, 1),
            new EnchantmentLevelEntry(Enchantments.MENDING, 1)
        );
        Collections.shuffle(list, r);
        for (int i = 0; i < Math.min(r.nextInt(3) + 3, list.size()); i++) {
            EnchantedBookItem.addEnchantment(b, list.get(i));
        }
        return b;
    }
}