package fr.alice.tetrisbot.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * File <b>RandomArray</b> located on fr.alice.tetrisbot.utils
 * RandomArray is a part of tetris-bot.
 * <p>
 * Copyright (c) 2023 tetris-bot.
 * <p>
 *
 * @author Alois. B. (IDarKay),
 * Created the 27/02/2023 at 19:02
 */
public class RandomArray<T>
{

    private final T[] keys;
    private final Random rdm;

    private int[] indexes;
    private int currentIndex;

    public RandomArray(T[] keys) {
        this(keys, 0);
    }

    public RandomArray(T[] keys, long seed) {
        if (seed == 0 ) {
            this.rdm = new Random();
        } else {
            this.rdm = new Random(seed);
        }
        this.keys = keys;
        this.currentIndex = -1;
        this.indexes = new int[this.keys.length];
        for (int i = 0; i < this.keys.length; i++)
        {
            this.indexes[i] = i;
        }
    }

    private void update() {
        this.currentIndex = keys.length;
        for (int i = 0; i < currentIndex; i++) {
            int next = rdm.nextInt(keys.length);
            int tmp = this.indexes[next];
            this.indexes[next] = this.indexes[i];
            this.indexes[i] = tmp;
        }
        this.currentIndex--;
    }


    public T get() {
        if (this.currentIndex < 0) {
            this.update();
        }
        return this.keys[this.indexes[this.currentIndex]];
    }

    public T pop() {
        if (this.currentIndex < 0) {
            this.update();
        }
        return this.keys[this.indexes[this.currentIndex--]];
    }



}
