package com.example.pages.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.pages.fragment.SingleSongFragment;
import com.example.pages.myView.CircleImageView;
import com.example.pages.service.MusicService;
import com.example.pages.R;
import com.example.pages.utils.MyBinderInterface;
import com.example.pages.utils.StaticValue;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * 活动名称
     */
    public static String ActivityName;
    /**
     * 广播接收器对象
     */
    public static UI_Receiver ui_receiver;
    /**
     * 应用内广播接收器
     */
    public static LocalBroadcastManager localBroadcastManager;
    /**
     * 活动对象
     */
    public static BaseActivity activity;
    /**
     * 根视野
     */
    private FrameLayout mContentContainer;
    /**
     * 浮动视野
     */
    private View mFloatView;

    /** 下方player **/
    protected ImageButton playerButton;
    protected ImageButton nextButton;
    protected ImageButton previousButton;
    protected ImageButton settingButton;
    protected CircleImageView musicPicture;
    protected TextView currentTimeText;
    protected TextView totalTimeText;
    protected TextView singerName;
    protected TextView musicName;
    protected SeekBar seekBar;


    /**
     * 音乐服务的Intent
     */
    public static Intent MusicServiceIntent;
    /**
     * 音乐服务Bind对象
     */
    public static MyBinderInterface myBinder;
    /**
     * 服务连接对象
     */
    public ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定成功后，取得MusicSercice提供的接口
            myBinder = (MyBinderInterface) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    //Handler实现向主线程进行传值
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            seekBar.setProgress((int) (msg.what));
            currentTimeText.setText(formatTime(msg.what));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        ViewGroup mDecorView = (ViewGroup) getWindow().getDecorView();
        mContentContainer = (FrameLayout) ((ViewGroup) mDecorView.getChildAt(0)).getChildAt(1);
        mFloatView =  LayoutInflater.from(getBaseContext()).inflate(R.layout.player_layout, null);
        activity = this;
        StaticValue.NowActivity = this;
		ui_receiver = new UI_Receiver();
		MusicServiceIntent = new Intent(activity,MusicService.class);
		//实现绑定操作
		bindService(MusicServiceIntent, conn, BIND_AUTO_CREATE);

//        /**
//         * 半透明状态栏
//         */
//
//        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
		setContentView(mFloatView);
		setupWindowAnimations();
		bind();
    }

    /** 绑定控件和监听事件 **/
    public void bind() {
        playerButton = mFloatView.findViewById(R.id.img_btn_play);
        playerButton.setOnClickListener(this);

        nextButton = mFloatView.findViewById(R.id.img_btn_next);
        nextButton.setOnClickListener(this);

        previousButton = mFloatView.findViewById(R.id.img_btn_previous);
        previousButton.setOnClickListener(this);

        singerName = mFloatView.findViewById(R.id.text_singer_name);
        singerName.setOnClickListener(this);

        musicName = mFloatView.findViewById(R.id.text_music_name);
        musicName.setOnClickListener(this);

        currentTimeText = mFloatView.findViewById(R.id.tv_current_time);
        currentTimeText.setOnClickListener(this);

        totalTimeText = mFloatView.findViewById(R.id.tv_total_time);
        totalTimeText.setOnClickListener(this);

        seekBar = mFloatView.findViewById(R.id.seekbar_controller);
        seekBar.setOnClickListener(this);

        settingButton = mFloatView.findViewById(R.id.img_btn_setting);
        settingButton.setOnClickListener(this);

        musicPicture = mFloatView.findViewById(R.id.img_btn_music_msg);

        //点击下方播放栏可跳转到播放界面
        mFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myBinder.getMusicList() == null || myBinder.getMusicList().size() == 0) {
                    Toast.makeText(BaseActivity.this,"播放列表为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(BaseActivity.activity, DetailPlayerActivity.class);
                    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(BaseActivity.this).toBundle());
                }
            }
        });
    }

    private void setupWindowAnimations() {
        Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setEnterTransition(slide);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        mContentContainer.addView(mFloatView,layoutParams);
    }

    /**
     * 在这里注册广播
     */
    @Override
    protected void onResume() {
        Log.i("注册广播接收器", "");
        overridePendingTransition(0, 0);//设置返回没有动画
        StaticValue.NowActivity = this;
        MusicServiceIntent = new Intent(this, MusicService.class);
//        startService(MusicServiceIntent);
        bindService(MusicServiceIntent, conn, BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.MusicServiceReceiverAction);
        //注册应用内广播接收器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(ui_receiver, intentFilter);

        if(myBinder != null) {
            if(myBinder.getMusicList() != null && myBinder.getMusicList().size() > 0)
            updateView();
        }

        super.onResume();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    /***
     * 设置这个可以实现前进Activity时候的无动画切换
     * @param intent
     */
    @Override
    public void startActivity(Intent intent){
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//设置切换没有动画，用来实现活动之间的无缝切换
        super.startActivity(intent);
    }

    /**
     *  在这里设置按下返回键，或者返回button的时候无动画
     */
    @Override
    public void finish(){
        overridePendingTransition(0, 0);//设置返回没有动画
        super.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed(){
        overridePendingTransition(0, 0);//设置返回没有动画
        super.onBackPressed();
    }

    /**
     * 取消广播
     */
    @Override
    protected void onDestroy() {
        overridePendingTransition(0, 0);//设置返回没有动画
        //取消服务绑定
        unbindService(conn);
        Log.i("取消服务和广播注册","");
        //取消注册应用内广播接收器
        localBroadcastManager.unregisterReceiver(ui_receiver);
        super.onDestroy();
    }

    //实现页面的更新
    public void updateView() {
        int index = myBinder.getCurrentIndex();
        musicName.setText(myBinder.getMusicList().get(index).getName());
        singerName.setText(myBinder.getMusicList().get(index).getAuthor());
        musicPicture.setImageBitmap(myBinder.getMusicList().get(index).getAlbumBitmap());
        if(myBinder.isPlaying())
            playerButton.setImageResource(R.drawable.play_pause);
        else
            playerButton.setImageResource(R.drawable.play_icon);
        totalTimeText.setText(myBinder.getMusicList().get(index).getDuration());
        seekBar.setMax(myBinder.getMusicList().get(index).getLength());
        MusicThread musicThread = new MusicThread();                                         //启动线程
        new Thread(musicThread).start();
    }

    /**
     *  广播接收器
     */
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
                    seekBar.setMax(value);
                    break;
                case MusicService.BROADCAST.TYPE_CurrentProgress:
                    value = intent.getIntExtra(MusicService.BROADCAST.TYPE_VALUENAME,0);
                    //更新进度条
                    Log.d("更新进度条",""+value+"ActivityName: "+ActivityName);
                    seekBar.setProgress(value);
                    break;
                case MusicService.BROADCAST.TYPE_PLAY:
                    //设置为暂停的图片
                    playerButton.setImageResource(R.drawable.play_pause);
                    break;
                case MusicService.BROADCAST.TYPE_PAUSE:
                    //设置为播放的图片
                    playerButton.setImageResource(R.drawable.play_icon);
                    break;
                case MusicService.BROADCAST.TYPE_SETPLAYBARVALUE://设置播放栏的值
                    updateView();
                    break;
            }
        }
    }

    //格式化数字
    private String formatTime(int length) {
        Date date = new Date(length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");    //规定固定的格式
        String totaltime = simpleDateFormat.format(date);
        return totaltime;
    }

    //创建一个类MusicThread实现Runnable接口，实现多线程
    class MusicThread implements Runnable {
        @Override
        public void run() {
            while (StaticValue.Music_IsPlay && myBinder.getMusicList().get(StaticValue.positon) != null) {
                try {
                    //让线程睡眠1000毫秒
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //放送给Handler现在的运行到的时间，进行ui更新
                handler.sendEmptyMessage(myBinder.getCurrentPosition());
            }
        }
    }
}
