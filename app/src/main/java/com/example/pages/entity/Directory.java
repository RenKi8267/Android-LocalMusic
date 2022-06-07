package com.example.pages.entity;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;

public class Directory {
    //文件夹名称
    private String name;
    //文件夹的首字母
    private String firstCharacter;
    //该文件夹下有几首歌
    private int totalMusic;
    //文件夹的路径
    private String url;
    //存放于该文件夹下的music list
    private ArrayList<Music> musicArrayList;

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

    public int getTotalMusic() {
        return totalMusic;
    }

    public void setTotalMusic(int totalMusic) {
        this.totalMusic = totalMusic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<Music> getMusicArrayList() {
        return musicArrayList;
    }

    public void setMusicArrayList(ArrayList<Music> musicArrayList) {
        this.musicArrayList = musicArrayList;
        this.totalMusic = musicArrayList.size();
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
