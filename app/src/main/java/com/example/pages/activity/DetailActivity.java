package com.example.pages.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pages.myView.CharacterListView;
import com.example.pages.R;
import com.example.pages.adapter.DetailAdapter;
import com.example.pages.databinding.ActivityDetailBinding;
import com.example.pages.entity.Album;
import com.example.pages.entity.Directory;
import com.example.pages.entity.Music;
import com.example.pages.entity.Singer;
import com.example.pages.utils.MusicResolver;
import com.example.pages.utils.StaticValue;
import com.example.pages.utils.TitleDecoration;
import java.util.ArrayList;

public class DetailActivity extends BaseActivity{
    private ActivityDetailBinding binding;
    private String title;
    private String type;
    private DetailAdapter detailAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Music> musicArrayList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        final Intent intent = getIntent();
        title = intent.getStringExtra("title");
        type = intent.getStringExtra("type");

        setSupportActionBar(binding.detailToolbar);
        getSupportActionBar().setTitle(title);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.detailToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailActivity.this.finish();
            }
        });

        init();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {               //seekbar设置监听，实现指哪放到哪
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if(myBinder.getMusicList() == null || myBinder.getMusicList().size() == 0) {
                        Toast.makeText(DetailActivity.this,"播放列表为空",Toast.LENGTH_SHORT).show();
                    }
                    else
                        myBinder.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    //初始化recycler
    private void init() {
        layoutManager = new LinearLayoutManager(this);
        binding.detailRecycler.setLayoutManager(layoutManager);
        detailAdapter = new DetailAdapter();
        binding.detailRecycler.setAdapter(detailAdapter);

        findMusicList();

        detailAdapter.setData(musicArrayList);

        binding.detailRecycler.addItemDecoration(new TitleDecoration(new TitleDecoration.TitleAttributes(this),"detail"));

        initEvent();

        detailAdapter.setMyOnItemClickListener(new DetailAdapter.MyOnItemClickListener() {
            @Override
            public void OnItemClickListener(int postion) {
                StaticValue.positon = postion;
                myBinder.setMusicList(musicArrayList);
                myBinder.setCurrentIndex(postion);
                myBinder.seekTo(0);
                myBinder.play();
            }
        });

        detailAdapter.notifyDataSetChanged();

    }

    //设置页面根据字母索引滑动
    private void initEvent() {
        binding.characerList.setOnLetterChangeListener(new CharacterListView.OnLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                int position = detailAdapter.getSortLettersFirstPosition(letter);
                if (position != -1) {
                    if (binding.detailRecycler.getLayoutManager() instanceof LinearLayoutManager) {
                        LinearLayoutManager manager = (LinearLayoutManager) binding.detailRecycler.getLayoutManager();
                        manager.scrollToPositionWithOffset(position, 0);
                    } else {
                        binding.detailRecycler.getLayoutManager().scrollToPosition(position);
                    }
                }
            }
        });
    }

    //通过传过来的title找到对应专辑的所有歌曲
    private void findMusicList() {
        if (type.equals("album")) {
            for (Album album : MusicResolver.albumArrayList) {
                if (album.getAlbumName().equals(title)) {
                    this.musicArrayList = album.getMusicArrayList();
                    break;
                }
            }
        }
        else if (type.equals("singer")) {
            for (Singer singer : MusicResolver.singerArrayList) {
                if (singer.getName().equals(title)) {
                    this.musicArrayList = singer.getMusicArrayList();
                    break;
                }
            }
        }
        else if (type.equals("directory")) {
            for (Directory directory : MusicResolver.directoryArrayList) {
                if (directory.getName().equals(title)) {
                    this.musicArrayList = directory.getMusicArrayList();
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_btn_play:
                if(myBinder.getMusicList() == null || myBinder.getMusicList().size() == 0) {
                    Toast.makeText(this,"播放列表为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (myBinder.isPlaying()) {
                        myBinder.pause();
                        Log.e("点击暂停","监听到啦");
                    } else {
                        myBinder.play();
                    }
                    break;
                }

            case R.id.img_btn_previous:
                if(myBinder.getMusicList() == null || myBinder.getMusicList().size() == 0) {
                    Toast.makeText(this,"播放列表为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!myBinder.isPlaying()) {
                        playerButton.setImageResource(R.drawable.play_pause);
                    }
                    myBinder.playPrev();
                }
                break;

            case R.id.img_btn_next:
                if(myBinder.getMusicList() == null || myBinder.getMusicList().size() == 0) {
                    Toast.makeText(this,"播放列表为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!myBinder.isPlaying()) {
                        playerButton.setImageResource(R.drawable.play_pause);
                    }
                    myBinder.playNext();
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}