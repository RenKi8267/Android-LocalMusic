package com.example.pages.utils;

import com.example.pages.entity.Album;
import com.example.pages.entity.Singer;

import java.util.Comparator;

public class SingerLetterComparater implements Comparator<Singer> {

    public int compare(Singer o1, Singer o2) {
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
