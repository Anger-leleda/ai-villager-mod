package com.example.aivillager;

import net.fabricmc.api.ClientModInitializer;

public class AIVillagerModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // AI村民和流浪商人使用原版渲染，无需额外注册
    }
}