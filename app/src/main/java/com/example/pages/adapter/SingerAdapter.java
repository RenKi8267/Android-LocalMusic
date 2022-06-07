package com.example.pages.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pages.R;
import com.example.pages.activity.DetailActivity;
import com.example.pages.entity.Singer;

import java.util.ArrayList;

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.MyViewHolder>{
    private ArrayList<Singer> singerArrayList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // 每个item
        public Toolbar toolbar;

        public MyViewHolder(View v) {
            super(v);
            toolbar = v.findViewById(R.id.singer_item);
            toolbar.inflateMenu(R.menu.singer_item_menu);
            //设置整个toolbar点击事件
            toolbar.setOnClickListener(new View.OnClickListener() {
                //点击可跳转到歌曲播放页面
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    intent.putExtra("title",toolbar.getTitle());
                    intent.putExtra("type","singer");
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    public void setData(ArrayList<Singer> mySinger) {
        this.singerArrayList = mySinger;
    }

    public ArrayList<Singer> getMusicArrayList() {
        return singerArrayList;
    }

    public String getSortLetters(int position) {
        if (singerArrayList == null || singerArrayList.isEmpty()) {
            return null;
        }
        return singerArrayList.get(position).getFirstCharacter();
    }

    public int getSortLettersFirstPosition(String letters) {
        if (singerArrayList == null || singerArrayList.isEmpty()) {
            return -1;
        }
        int position = -1;
        for (int index = 0; index < singerArrayList.size(); index++) {
            if (singerArrayList.get(index).getFirstCharacter().equals(letters)) {
                position = index;
                break;
            }
        }
        return position;
    }

    public int getNextSortLetterPosition(int position) {
        if (singerArrayList == null || singerArrayList.isEmpty() || singerArrayList.size() <= position + 1) {
            return -1;
        }
        int resultPosition = -1;
        for (int index = position + 1; index < singerArrayList.size(); index++) {
            if (!singerArrayList.get(position).getFirstCharacter().equals(singerArrayList.get(index).getFirstCharacter())) {
                resultPosition = index;
                break;
            }
        }
        return resultPosition;
    }

    @Override
    public void onBindViewHolder(SingerAdapter.MyViewHolder holder, int position) {
        //replace每个item中的内容，即每一行歌曲是一个toolbar，给toolbar中内容赋值
        holder.toolbar.setTitle(singerArrayList.get(position).getName());
        holder.toolbar.setSubtitle(singerArrayList.get(position).getAlbumNum() + "张专辑 " +
                singerArrayList.get(position).getTotalMusic() + "首歌曲");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.singer_item, parent, false);
        SingerAdapter.MyViewHolder vh = new SingerAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return singerArrayList == null? 0 : singerArrayList.size();
    }
}
