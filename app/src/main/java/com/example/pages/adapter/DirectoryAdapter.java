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
import com.example.pages.entity.Directory;

import java.util.ArrayList;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.MyViewHolder>{
    private ArrayList<Directory> directoryArrayList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // 每个item
        public Toolbar toolbar;

        public MyViewHolder(View v) {
            super(v);
            toolbar = v.findViewById(R.id.directory_item);
            toolbar.inflateMenu(R.menu.directory_item_menu);
            //设置整个toolbar点击事件
            toolbar.setOnClickListener(new View.OnClickListener() {
                //点击可跳转到歌曲播放页面
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    intent.putExtra("title",toolbar.getTitle());
                    intent.putExtra("type","directory");
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    public void setData(ArrayList<Directory> myDirectory) {
        this.directoryArrayList = myDirectory;
    }

    public ArrayList<Directory> getMusicArrayList() {
        return directoryArrayList;
    }

    public String getSortLetters(int position) {
        if (directoryArrayList == null || directoryArrayList.isEmpty()) {
            return null;
        }
        return directoryArrayList.get(position).getFirstCharacter();
    }

    public int getSortLettersFirstPosition(String letters) {
        if (directoryArrayList == null || directoryArrayList.isEmpty()) {
            return -1;
        }
        int position = -1;
        for (int index = 0; index < directoryArrayList.size(); index++) {
            if (directoryArrayList.get(index).getFirstCharacter().equals(letters)) {
                position = index;
                break;
            }
        }
        return position;
    }

    public int getNextSortLetterPosition(int position) {
        if (directoryArrayList == null || directoryArrayList.isEmpty() || directoryArrayList.size() <= position + 1) {
            return -1;
        }
        int resultPosition = -1;
        for (int index = position + 1; index < directoryArrayList.size(); index++) {
            if (!directoryArrayList.get(position).getFirstCharacter().equals(directoryArrayList.get(index).getFirstCharacter())) {
                resultPosition = index;
                break;
            }
        }
        return resultPosition;
    }


    @Override
    public void onBindViewHolder(DirectoryAdapter.MyViewHolder holder, int position) {
        //replace每个item中的内容，即每一行歌曲是一个toolbar，给toolbar中内容赋值
        holder.toolbar.setTitle(directoryArrayList.get(position).getName());
        holder.toolbar.setSubtitle(directoryArrayList.get(position).getTotalMusic() + " 首  " +
                directoryArrayList.get(position).getUrl());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.directory_item, parent, false);
        DirectoryAdapter.MyViewHolder vh = new DirectoryAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return directoryArrayList == null? 0 : directoryArrayList.size();
    }
}
