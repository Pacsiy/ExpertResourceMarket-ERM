package edu.buaa.server.util;

import java.util.Random;

class QuickRandomTool {
    static String quickCheckCode() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

}
