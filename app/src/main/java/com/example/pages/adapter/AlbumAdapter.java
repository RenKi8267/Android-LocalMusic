package com.example.pages.adapter;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pages.R;
import com.example.pages.activity.DetailActivity;
import com.example.pages.entity.Album;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder>{
    static ArrayList<Album> albumArrayList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // 每个item
        public Toolbar toolbar;

        public MyViewHolder(View v) {
            super(v);
            toolbar = v.findViewById(R.id.album_item);
            toolbar.inflateMenu(R.menu.album_item_menu);
            //设置整个toolbar点击事件
            toolbar.setOnClickListener(new View.OnClickListener() {
                //点击可跳转到歌曲播放页面
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    intent.putExtra("title",toolbar.getTitle());
                    intent.putExtra("type","album");
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    //通过contentResolver获取本地音乐，得到需要的内容传到这里来
    public void setData(ArrayList<Album> myAlbum) {
        this.albumArrayList = myAlbum;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AlbumAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_item, parent, false);
        AlbumAdapter.MyViewHolder vh = new AlbumAdapter.MyViewHolder(v);
        return vh;
    }

    public ArrayList<Album> getMusicArrayList() {
        return albumArrayList;
    }

    public String getSortLetters(int position) {
        if (albumArrayList == null || albumArrayList.isEmpty()) {
            return null;
        }
        return albumArrayList.get(position).getFirstCharacter();
    }

    public int getSortLettersFirstPosition(String letters) {
        if (albumArrayList == null || albumArrayList.isEmpty()) {
            return -1;
        }
        int position = -1;
        for (int index = 0; index < albumArrayList.size(); index++) {
            if (albumArrayList.get(index).getFirstCharacter().equals(letters)) {
                position = index;
                break;
            }
        }
        return position;
    }

    public int getNextSortLetterPosition(int position) {
        if (albumArrayList == null || albumArrayList.isEmpty() || albumArrayList.size() <= position + 1) {
            return -1;
        }
        int resultPosition = -1;
        for (int index = position + 1; index < albumArrayList.size(); index++) {
            if (!albumArrayList.get(position).getFirstCharacter().equals(albumArrayList.get(index).getFirstCharacter())) {
                resultPosition = index;
                break;
            }
        }
        return resultPosition;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AlbumAdapter.MyViewHolder holder, int position) {
        //replace每个item中的内容，即每一行歌曲是一个toolbar，给toolbar中内容赋值
        holder.toolbar.setTitle(albumArrayList.get(position).getAlbumName());
        holder.toolbar.setSubtitle(albumArrayList.get(position).getNum() + " 首  " +
                albumArrayList.get(position).getSinger());
        holder.toolbar.setNavigationIcon(new BitmapDrawable(holder.itemView.getContext().getResources()
                ,albumArrayList.get(position).getMusicArrayList().get(0).getAlbumBitmap()));
    }

    @Override
    public int getItemCount() {
        return albumArrayList == null? 0 : albumArrayList.size();
    }
}
