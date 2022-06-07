package com.example.pages.utils;

import com.example.pages.entity.Music;

import java.util.Comparator;

public class LetterComparater implements Comparator<Music> {

    public int compare(Music o1, Music o2) {
        if (o1.getFirstCharacter().equals("@")
                || o2.getFirstCharacter().equals("#")) {
            return 1;
        } else if (o1.getFirstCharacter().equals("#")
                || o2.getFirstCharacter().equals("@")) {
            return -1;
        } else {
            return o1.getFirstCharacter().compareTo(o2.getFirstCharacter());
        }
    }
}
