package com.example.aivillager.world;

import com.example.aivillager.AIVillagerMod;
import com.example.aivillager.AIVillagerConfig;
import com.example.aivillager.entity.AIVillagerEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestType;
import java.util.*;

public class VillageAIVillagerSpawner {
    private static final Map<String,Set<BlockPos>> done=new HashMap<>();
    private static int tick=0;

    public static void register() {
        ServerWorldEvents.LOAD.register((s,w)->{if(w.getRegistryKey().getValue().equals(World.OVERWORLD.getValue()))done.putIfAbsent(w.getRegistryKey().getValue().toString(),new HashSet<>());});
        ServerTickEvents.END_WORLD_TICK.register(w->{if(w.getRegistryKey().getValue().equals(World.OVERWORLD.getValue())&&++tick>=600){tick=0;check(w);}});
    }

    private static void check(ServerWorld w) {
        if(!AIVillagerConfig.config.enableAIVillagers||!AIVillagerConfig.config.spawnInVillages)return;
        String k=w.getRegistryKey().getValue().toString();
        for(BlockPos c:find(w)){if(done.get(k).contains(c))maint(w,c);else{init(w,c);done.get(k).add(c);}}
    }

    private static Set<BlockPos> find(ServerWorld w) {
        Set<BlockPos> cs=new HashSet<>();List<BlockPos> beds=new ArrayList<>(),ws=new ArrayList<>();
        BlockPos pp=w.getPlayers().isEmpty()?BlockPos.ORIGIN:w.getPlayers().get(0).getBlockPos();
        BlockPos.stream(pp.add(-128,-64,-128),pp.add(128,128,128)).forEach(p->{if(w.isChunkLoaded(p.getX()>>4,p.getZ()>>4))w.getPointOfInterestStorage().getType(p).ifPresent(t->{if(t==PointOfInterestType.HOME)beds.add(p.toImmutable());else if(t==PointOfInterestType.FARMER||t==PointOfInterestType.LIBRARIAN)ws.add(p.toImmutable());});});
        if(!beds.isEmpty())cs=cluster(beds,ws);
        return cs;
    }

    private static Set<BlockPos> cluster(List<BlockPos> beds,List<BlockPos> ws){Set<BlockPos> c=new HashSet<>();List<BlockPos> all=new ArrayList<>(beds);all.addAll(ws);Set<BlockPos> un=new HashSet<>(all);while(!un.isEmpty()){BlockPos seed=un.iterator().next();Set<BlockPos> cl=new HashSet<>();Queue<BlockPos> q=new LinkedList<>();q.add(seed);while(!q.isEmpty()){BlockPos cur=q.poll();if(!un.contains(cur))continue;cl.add(cur);un.remove(cur);for(BlockPos o:new ArrayList<>(un))if(cur.getSquaredDistance(o)<=1024)q.add(o);}if(cl.size()>=3)c.add(new BlockPos((int)cl.stream().mapToDouble(BlockPos::getX).average().orElse(0),(int)cl.stream().mapToDouble(BlockPos::getY).average().orElse(0),(int)cl.stream().mapToDouble(BlockPos::getZ).average().orElse(0)));}return c;}

    private static void init(ServerWorld w,BlockPos c){if(w.random.nextFloat()>AIVillagerConfig.config.villageSpawnChance)return;Box b=new Box(c).expand(64);List<VillagerEntity> ve=w.getEntitiesByClass(VillagerEntity.class,b,v->!(v instanceof AIVillagerEntity));int n=Math.min(AIVillagerConfig.config.maxPerVillage,w.random.nextInt(AIVillagerConfig.config.maxPerVillage)+1);for(int i=0;i<n;i++){if(ve.isEmpty())spawnNew(w,c);else replace(w,ve.remove(w.random.nextInt(ve.size())));}}

    private static void maint(ServerWorld w,BlockPos c){Box b=new Box(c).expand(64);List<AIVillagerEntity> ai=w.getEntitiesByClass(AIVillagerEntity.class,b,v->v.isAlive());if(ai.size()<AIVillagerConfig.config.maxPerVillage/2&&w.random.nextFloat()<0.1f){List<VillagerEntity> nv=w.getEntitiesByClass(VillagerEntity.class,b,v->!(v instanceof AIVillagerEntity)&&v.isAlive());if(!nv.isEmpty())replace(w,nv.get(w.random.nextInt(nv.size())));else if(ai.isEmpty())spawnNew(w,c);}}

    private static void replace(ServerWorld w,VillagerEntity ov){BlockPos p=ov.getBlockPos();AIVillagerEntity ai=new AIVillagerEntity(AIVillagerMod.AI_VILLAGER,w);ai.refreshPositionAndAngles(p.getX()+0.5,p.getY(),p.getZ()+0.5,ov.getYaw(),ov.getPitch());if(ov.getVillagerData().getProfession()!=VillagerProfession.NONE)ai.setVillagerData(ai.getVillagerData().withProfession(ov.getVillagerData().getProfession()));if(ov.hasCustomName())ai.setCustomName(ov.getCustomName());ai.setNaturalSpawned(true);ov.discard();w.spawnEntity(ai);}

    private static void spawnNew(ServerWorld w,BlockPos c){for(int i=0;i<20;i++){BlockPos tp=c.add(w.random.nextInt(32)-16,0,w.random.nextInt(32)-16);BlockPos sp=w.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING,tp);if(sp.getY()>=62&&w.getBlockState(sp.down()).isSolid()&&w.getBlockState(sp).isAir()&&w.getBlockState(sp.up()).isAir()){AIVillagerEntity ai=new AIVillagerEntity(AIVillagerMod.AI_VILLAGER,w);ai.refreshPositionAndAngles(sp.getX()+0.5,sp.getY(),sp.getZ()+0.5,w.random.nextFloat()*360,0);ai.setNaturalSpawned(true);w.spawnEntity(ai);return;}}}

    public static void forceSpawnInCurrentVillage(ServerWorld w,BlockPos pp){for(BlockPos c:find(w)){if(c.getSquaredDistance(pp)<=10000){done.get(w.getRegistryKey().getValue().toString()).remove(c);init(w,c);return;}}}
}