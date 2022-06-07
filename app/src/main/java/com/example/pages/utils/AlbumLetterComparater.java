package com.example.pages.utils;

import com.example.pages.entity.Album;
import com.example.pages.entity.Music;

import java.util.Comparator;

public class AlbumLetterComparater implements Comparator<Album> {

    public int compare(Album o1, Album o2) {
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
