package com.example.pages.utils;

import com.example.pages.service.MusicService;
import com.example.pages.entity.Music;

import java.util.ArrayList;

public interface MyBinderInterface {
    //暂停
    void pause();
    //恢复
    void resume();
    //播放
    void play();
    //播放下一首
    void playNext();
    //播放上一首
    void playPrev();
    //释放
    void release();
    //是否正在播
    boolean isPlaying();
    //获取时长
    int getDuration();
    //当前位置
    int getCurrentPosition();
    //拖动位置
    void seekTo(int length);
    //获取当前索引
    int getCurrentIndex();
    //设置当前索引
    void setCurrentIndex(int currentIdx);

    void reset();

    void setPlayMode();

    void setMusicList(ArrayList<Music> musicList);

    ArrayList<Music> getMusicList();

    MusicService getService();
}
