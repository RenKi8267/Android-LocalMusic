package com.example.pages.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.example.pages.R;
import com.example.pages.databinding.ActivityDetailPlayerBinding;
import com.example.pages.service.MusicService;
import com.example.pages.utils.MusicResolver;
import com.example.pages.utils.StaticValue;

public class DetailPlayerActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityDetailPlayerBinding binding;

    /**
     * 广播接收器对象
     */
    public UI_Receiver ui_receiver;
    /**
     * 应用内广播接收器
     */
    public LocalBroadcastManager localBroadcastManager;

    //Handler实现向主线程进行传值
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            binding.playerPage.seekBar.setProgress((int) (msg.what));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailPlayerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        /**
         * 开启过渡动画
         */
        setupWindowAnimations();
        //绑定监听
        init();

        ui_receiver = new UI_Receiver();

        //创建activity时就根据service来更新界面
        updateView();

        binding.playerPage.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    BaseActivity.myBinder.seekTo(i);
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

    private void setupWindowAnimations() {
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(fade);
        Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setReturnTransition(slide);
    }

    public void init() {
        binding.playerPage.returnButton.setOnClickListener(this);
        binding.playerPage.playOrPauseBtn.setOnClickListener(this);
        binding.playerPage.playLastBtn.setOnClickListener(this);
        binding.playerPage.playNextBtn.setOnClickListener(this);
        binding.playerPage.playWayBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_button:
                Log.e("点击返回","监听到啦");
                DetailPlayerActivity.this.finish();
                break;

            case R.id.play_way_btn:

                break;

            case R.id.play_last_btn:
                if (!BaseActivity.myBinder.isPlaying()) {
                    binding.playerPage.playOrPauseBtn.setBackgroundResource(R.drawable.ic_zanting);
                }
                BaseActivity.myBinder.playPrev();
                break;

            case R.id.play_next_btn:
                if (!BaseActivity.myBinder.isPlaying()) {
                    binding.playerPage.playOrPauseBtn.setBackgroundResource(R.drawable.ic_zanting);
                }
                BaseActivity.myBinder.playNext();
                break;

            case R.id.play_or_pause_btn:
                if (BaseActivity.myBinder.isPlaying()) {
                    BaseActivity.myBinder.pause();
                    Log.e("点击暂停","监听到啦");
                } else {
                    BaseActivity.myBinder.play();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.MusicServiceReceiverAction);
        //注册应用内广播接收器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(ui_receiver, intentFilter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册应用内广播接收器
        localBroadcastManager.unregisterReceiver(ui_receiver);
    }

    public void updateView() {
        int index = BaseActivity.myBinder.getCurrentIndex();

        binding.playerPage.seekBar.setMax(BaseActivity.myBinder.getMusicList().get(index).getLength());

        binding.playerPage.textViewArtist.setText(BaseActivity.myBinder.getMusicList().get(index).getAuthor());
        binding.playerPage.textMusicName.setText(BaseActivity.myBinder.getMusicList().get(index).getName());

        binding.playerPage.playOrPauseBtn.setBackgroundResource(R.drawable.ic_zanting);

        //如果是默认图片则改成默认专辑图片
        if (BaseActivity.myBinder.getMusicList().get(index).getAlbumBitmap().sameAs(MusicResolver.getBitmapFromDrawable(this,R.drawable.ic_music)))
            binding.playerPage.sivIcon.setImageResource(R.drawable.default_record_album);
        else
            binding.playerPage.sivIcon.setImageBitmap(BaseActivity.myBinder.getMusicList().get(index).getAlbumBitmap());

        DetailPlayerActivity.MusicThread musicThread = new DetailPlayerActivity.MusicThread();                                         //启动线程
        new Thread(musicThread).start();
    }

    private class UI_Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            int type = intent.getIntExtra(MusicService.BROADCAST.TYPE_TYPENAME,-2);
            int value = 0;
            Log.i("收到消息",type+"");
            switch(type)
            {
                case MusicService.BROADCAST.TYPE_MAXPROGRESS:
                    value = intent.getIntExtra(MusicService.BROADCAST.TYPE_VALUENAME,0);
                    //更新进度条
                    Log.i("进度条最大值",""+value);
                    binding.playerPage.seekBar.setMax(value);
                    break;
                case MusicService.BROADCAST.TYPE_CurrentProgress:
                    value = intent.getIntExtra(MusicService.BROADCAST.TYPE_VALUENAME,0);
                    //更新进度条
                    binding.playerPage.seekBar.setProgress(value);
                    break;
                case MusicService.BROADCAST.TYPE_PLAY:
                    //设置为暂停的图片
                    binding.playerPage.playOrPauseBtn.setBackgroundResource(R.drawable.ic_zanting);
                    break;
                case MusicService.BROADCAST.TYPE_PAUSE:
                    //设置为播放的图片
                    binding.playerPage.playOrPauseBtn.setBackgroundResource(R.drawable.bofang);
                    break;
                case MusicService.BROADCAST.TYPE_SETPLAYBARVALUE://设置播放栏的值
                    updateView();
                    break;
            }
        }
    }

    //创建一个类MusicThread实现Runnable接口，实现多线程
    class MusicThread implements Runnable {
        @Override
        public void run() {
            while (StaticValue.Music_IsPlay && BaseActivity.myBinder.getMusicList().get(StaticValue.positon) != null) {
                try {
                    //让线程睡眠1000毫秒
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //放送给Handler现在的运行到的时间，进行ui更新
                handler.sendEmptyMessage(BaseActivity.myBinder.getCurrentPosition());
            }
        }
    }
}