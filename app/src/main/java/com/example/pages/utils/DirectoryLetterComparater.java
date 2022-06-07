package com.example.pages.utils;

import com.example.pages.entity.Album;
import com.example.pages.entity.Directory;

import java.util.Comparator;

public class DirectoryLetterComparater implements Comparator<Directory> {

    public int compare(Directory o1, Directory o2) {
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
