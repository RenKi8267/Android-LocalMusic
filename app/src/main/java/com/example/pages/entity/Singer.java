package com.example.pages.entity;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;

public class Singer {
    //歌手名字
    private String name;
    //歌手名字的首字母
    private String firstCharacter;
    //有几张专辑
    private int AlbumNum;
    //总共有几首歌
    private int totalMusic;
    //music list
    private ArrayList<Music> musicArrayList;

    public ArrayList<Music> getMusicArrayList() {
        return musicArrayList;
    }

    public void setMusicArrayList(ArrayList<Music> musicArrayList) {
        this.musicArrayList = musicArrayList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        //同时把firstCharacter也设置了
        String first = transformPinYin(name).substring(0,1);
        if(first.matches("[A-Z]") || first.matches("[a-z]")) {
            this.firstCharacter = first.toUpperCase();
        }
        else
            this.firstCharacter = "#";
    }

    public String getFirstCharacter() {
        return firstCharacter;
    }

    public int getAlbumNum() {
        return AlbumNum;
    }

    public void setAlbumNum(int albumNum) {
        AlbumNum = albumNum;
    }

    public int getTotalMusic() {
        return totalMusic;
    }

    public void setTotalMusic(int totalMusic) {
        this.totalMusic = totalMusic;
    }

    //转为拼音
    public String transformPinYin(String character) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < character.length(); i++) {
            buffer.append(Pinyin.toPinyin(character.charAt(i)));
        }
        return buffer.toString();
    }
}
