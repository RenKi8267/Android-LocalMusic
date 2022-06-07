package com.example.pages.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.pages.databinding.ActivityLocalAtivityBinding;
import com.example.pages.entity.Music;
import com.example.pages.fragment.AlbumFragement;
import com.example.pages.fragment.DirectoryFragment;
import com.example.pages.R;
import com.example.pages.fragment.SingerFragment;
import com.example.pages.fragment.SingleSongFragment;
import com.example.pages.adapter.TabAdapter;
import com.example.pages.utils.MusicResolver;
import com.example.pages.utils.StaticValue;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;


public class LocalActivity extends BaseActivity implements SingleSongFragment.FragmentCallBack {
    //活动对象
    public static BaseActivity activity;
    private Toolbar localtoolbar;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    protected static ArrayList<Fragment> fragments = new ArrayList<>();
    protected static ArrayList<String> tabs = new ArrayList<>();
    private SingleSongFragment singleSongFragment = SingleSongFragment.newInstance("","");
    private AlbumFragement albumFragement = AlbumFragement.newInstance("","");
    private SingerFragment singerFragment = SingerFragment.newInstance("","");
    private DirectoryFragment directoryFragment = DirectoryFragment.newInstance("","");

    //接口，获取fragment传过来的值
    @Override
    public void getPosition(int position) {
//        this.position = position;
        StaticValue.positon = position;
        myBinder.setMusicList(MusicResolver.musicArrayList);
        myBinder.setCurrentIndex(position);
        myBinder.seekTo(0);
        myBinder.play();
        Log.e("点击播放",position+"");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_local_ativity);

        activity = LocalActivity.this;
        ActivityName = StaticValue.ActivityName.MainActivityName;
        StaticValue.MainActivity = this;
        StaticValue.MainView = findViewById(R.id.view_pager);
        StaticValue.NowActivity = this;

		bindIdAndListener();

        /*** tab的切换 ***/
        initData();
        initView();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {               //seekbar设置监听，实现指哪放到哪
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if(myBinder.getMusicList() == null || myBinder.getMusicList().size() == 0) {
                        Toast.makeText(LocalActivity.this,"播放列表为空",Toast.LENGTH_SHORT).show();
                        seekBar.setProgress(0);
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

    /** 绑定控件和监听事件 **/
    public void bindIdAndListener() {
        localtoolbar = findViewById(R.id.local_toolbar);
        localtoolbar.inflateMenu(R.menu.toolbar_menu);

        //设置返回按钮的事件
        localtoolbar.setNavigationOnClickListener(this);
    }

    /***
     * tab的切换
     */
    private void initData() {
        if (tabs.contains("单曲"))
            return;
        tabs.add("单曲");
        tabs.add("专辑");
        tabs.add("歌手");
        tabs.add("文件夹");
        fragments.add(singleSongFragment);
        fragments.add(albumFragement);
        fragments.add(singerFragment);
        fragments.add(directoryFragment);
    }

    private void initView() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager2) findViewById(R.id.view_pager);

        //设置TabLayout的模式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager(),getLifecycle(),fragments));
        //TabLayout和Viewpager2进行关联
        new TabLayoutMediator(tabLayout, viewPager, true,true,new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabs.get(position));
            }
        }).attach();
        tabLayout.getTabAt(0).select();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.local_toolbar:
                //顶部toolbar的返回按钮事件
                break;

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
                }
                break;
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
    protected void onPause() {
        super.onPause();
        for (Music music : MusicResolver.musicArrayList
        ) {
            music.setPlaying(false);
        }
        MusicResolver.musicArrayList.get(myBinder.getCurrentIndex()).setPlaying(true);
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