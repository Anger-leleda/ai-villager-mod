package com.example.aivillager.world;

import net.minecraft.text.Text;
import java.util.Random;

public class AINameGenerator {
    private static final Random R = new Random();
    private static final String[] PRE = {"聪明的","智慧的","友好的","神秘的","古老的","年轻的","博学的","善良的"};
    private static final String[] NAME = {"艾达","比特","赛博","迪吉","电子","弗拉什","千兆","赫兹","奇洛","逻辑","梅加","尼欧","像素","量子","西格玛"};
    private static final String[] SUF = {"","博士","教授","大师","学者"};

    public static Text generateName() {
        StringBuilder n = new StringBuilder();
        if (R.nextBoolean()) n.append(PRE[R.nextInt(PRE.length)]);
        n.append(NAME[R.nextInt(NAME.length)]);
        if (R.nextFloat() < 0.3f) n.append(SUF[R.nextInt(SUF.length)]);
        return Text.literal(n.toString());
    }

    public static Text generateNameByPersonality(String p) {
        String[] arr = switch (p) {
            case "generous" -> new String[]{"阳光的","温暖的","欢乐的","蜜糖"};
            case "mysterious" -> new String[]{"暗影","迷雾","幽魂","虚空"};
            default -> new String[]{"精算师","老狐狸","金手指","银舌头"};
        };
        return Text.literal(arr[R.nextInt(arr.length)] + NAME[R.nextInt(NAME.length)]);
    }
}