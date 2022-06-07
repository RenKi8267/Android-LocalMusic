package com.example.pages.entity;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;

public class Album {
    //
    private String albumID;
    //专辑名
    private String albumName;
    //专辑名的首字母
    private String firstCharacter;
    //歌手
    private String singer;
    //有几首歌
    private int num;
    //包含的歌曲list
    private ArrayList<Music> musicArrayList;

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
        //同时把firstCharacter也设置了
        String first = transformPinYin(albumName).substring(0,1);
        if(first.matches("[A-Z]") || first.matches("[a-z]")) {
            this.firstCharacter = first.toUpperCase();
        }
        else
            this.firstCharacter = "#";
    }

    public String getFirstCharacter() {
        return firstCharacter;
    }


    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public ArrayList<Music> getMusicArrayList() {
        return musicArrayList;
    }

    public void setMusicArrayList(ArrayList<Music> musicArrayList) {
        this.musicArrayList = musicArrayList;
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
