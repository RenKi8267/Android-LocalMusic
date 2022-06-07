package com.example.pages.entity;

import android.graphics.Bitmap;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.Formatter;
import java.util.Locale;

public class Music {
    //
    private String musicID;
    //歌名
    private String name;

    //首字母
    private String firstCharacter;

    //歌手名字
    private String author;

    //歌曲时长
    private String duration;
    private int length;

    //专辑封面ID
    private String albumID;

    //封面图片
    private Bitmap albumBitmap;

    //属于哪个专辑
    private String albumName;

    //存放路径(完整路径)
    private String url;

    //所在文件夹的名称
    private String directoryName;

    //是否正在播放
    private boolean isPlaying = false;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Bitmap getAlbumBitmap() {
        return albumBitmap;
    }

    public void setAlbumBitmap(Bitmap albumBitmap) {
        this.albumBitmap = albumBitmap;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getMusicID() {
        return musicID;
    }

    public void setMusicID(String musicID) {
        this.musicID = musicID;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
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


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        //获取的是毫秒，转换为秒
        long time = Integer.parseInt(duration);
        this.duration = stringForTime(time);
    }

    //转为拼音
    public String transformPinYin(String character) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < character.length(); i++) {
            buffer.append(Pinyin.toPinyin(character.charAt(i)));
        }
        return buffer.toString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        /**同时设置所在文件夹的名称*/
        String[] str = url.split("/");
        this.directoryName = str[str.length - 2];
    }

    public String stringForTime(long timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}
