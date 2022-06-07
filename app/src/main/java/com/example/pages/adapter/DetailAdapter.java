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
import com.example.pages.entity.Music;

import java.util.ArrayList;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.MyViewHolder>{
    private ArrayList<Music> musicArrayList;

    public interface MyOnItemClickListener {
        void OnItemClickListener(int postion);
    }

    static DetailAdapter.MyOnItemClickListener myOnItemClickListener;

    public void setMyOnItemClickListener(DetailAdapter.MyOnItemClickListener myOnItemClickListener) {
        this.myOnItemClickListener =  myOnItemClickListener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // 每个item
        public Toolbar toolbar;

        public MyViewHolder(View v) {
            super(v);
            toolbar = v.findViewById(R.id.detail_item);
            toolbar.inflateMenu(R.menu.detail_item_menu);
            //设置整个toolbar点击事件
            toolbar.setOnClickListener(new View.OnClickListener() {
                //点击播放该歌曲
                @Override
                public void onClick(View view) {
                    myOnItemClickListener.OnItemClickListener(getPosition());
                }
            });
        }
    }

    //通过contentResolver获取本地音乐，得到需要的内容传到这里来
    public void setData(ArrayList<Music> music) {
        this.musicArrayList = music;
    }

    @Override
    public DetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_item, parent, false);
        DetailAdapter.MyViewHolder vh = new DetailAdapter.MyViewHolder(v);
        return vh;
    }

    public ArrayList<Music> getMusicArrayList() {
        return musicArrayList;
    }

    public String getSortLetters(int position) {
        if (musicArrayList == null || musicArrayList.isEmpty()) {
            return null;
        }
        return musicArrayList.get(position).getFirstCharacter();
    }

    public int getSortLettersFirstPosition(String letters) {
        if (musicArrayList == null || musicArrayList.isEmpty()) {
            return -1;
        }
        int position = -1;
        for (int index = 0; index < musicArrayList.size(); index++) {
            if (musicArrayList.get(index).getFirstCharacter().equals(letters)) {
                position = index;
                break;
            }
        }
        return position;
    }

    public int getNextSortLetterPosition(int position) {
        if (musicArrayList == null || musicArrayList.isEmpty() || musicArrayList.size() <= position + 1) {
            return -1;
        }
        int resultPosition = -1;
        for (int index = position + 1; index < musicArrayList.size(); index++) {
            if (!musicArrayList.get(position).getFirstCharacter().equals(musicArrayList.get(index).getFirstCharacter())) {
                resultPosition = index;
                break;
            }
        }
        return resultPosition;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DetailAdapter.MyViewHolder holder, int position) {
        //replace每个item中的内容，即每一行歌曲是一个toolbar，给toolbar中内容赋值
        holder.toolbar.setTitle(musicArrayList.get(position).getName());
        holder.toolbar.setSubtitle(musicArrayList.get(position).getAuthor() + " " +
                musicArrayList.get(position).getDuration());
        holder.toolbar.setNavigationIcon(new BitmapDrawable(holder.itemView.getContext().getResources()
                ,musicArrayList.get(position).getAlbumBitmap()));
    }

    @Override
    public int getItemCount() {
        return musicArrayList == null? 0 : musicArrayList.size();
    }

}
