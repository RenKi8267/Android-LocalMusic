package com.example.pages;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.pages.entity.Album;
import com.example.pages.entity.Music;
import com.example.pages.utils.MusicResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGetAlbum() throws Exception{
        Music musi = new Music();
        musi.setAlbumName("song");
        musi.setName("fox");
        musi.setAuthor("xjq");
        ArrayList<Music> musicArrayList = new ArrayList<>();
        musicArrayList.add(musi);

        ArrayList<Album> albumArrayList = new ArrayList<>();
        HashMap<String, LinkedList<Music>> map = new HashMap<>(musicArrayList.size());

        Album album;

        for (Music music : musicArrayList) {
            String albumName = music.getAlbumName();
            //若包含这个album,说明已经放过了，直接追加到后面
            if(map.containsKey(albumName))
                map.get(albumName).add(music);
                //若没有包含，则添加进去
            else {
                LinkedList<Music> temp = new LinkedList<>();
                temp.add(music);
                map.put(albumName,temp);
            }
        }

        ArrayList<Music> musics;

        //遍历map，将内容放到albumlist
        for (String album_name : map.keySet()) {
            album = new Album();
            musics = new ArrayList<Music>(map.get(album_name));
            album.setAlbumID(musics.get(0).getAlbumID());
            album.setAlbumName(album_name);
            album.setMusicArrayList(musics);
            album.setNum(musics.size());
            album.setSinger(musics.get(0).getAuthor());
            albumArrayList.add(album);
        }


        assertEquals(1,albumArrayList.size());
    }
}