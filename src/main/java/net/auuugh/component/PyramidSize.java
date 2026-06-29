package net.auuugh.component;

import com.mojang.serialization.Codec;

public record PyramidSize(int size) {
    public PyramidSize {
        if (size < 1 || size > 4) {
            System.out.println("Invalid size: " + size);
        }
    }

    public static Codec<PyramidSize> CODEC;
}
