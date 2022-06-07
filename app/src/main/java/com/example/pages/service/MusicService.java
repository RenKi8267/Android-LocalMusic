package com.example.pages.service;

import static com.example.pages.utils.StaticValue.CLOSE;
import static com.example.pages.utils.StaticValue.NEXT;
import static com.example.pages.utils.StaticValue.PAUSE;
import static com.example.pages.utils.StaticValue.PLAY;
import static com.example.pages.utils.StaticValue.PREV;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.pages.R;
import com.example.pages.entity.Music;
import com.example.pages.receiver.NotificationClickReceiver;
import com.example.pages.utils.MusicResolver;
import com.example.pages.utils.MyBinderInterface;
import com.example.pages.utils.StaticValue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 音乐播放服务，绑定下方播放栏
 */
public class MusicService extends Service{
    public final MyBinder myBinder = new MyBinder();

    /**
     * 通知栏
     */
    private static NotificationManager manager;
    private static RemoteViews remoteViews;
    private Notification notification;
    /**
     * 通知ID
     */
    private int NOTIFICATION_ID = 1;
    //播放器
    private MediaPlayer mediaPlayer;
    //进度
    private int seekLength = 0;
    //当前播放列表下标
    private int currentIndex = 0;
    //播放的音乐列表
    private ArrayList<Music> musicArrayList;

    //音乐服务广播动作字符串
    public final static String MusicServiceReceiverAction = "localmusic.service.receiver";

    //广播接收器
    private MusicServicesBroadcastReceiver musicServicesBroadcastReceiver;

    //本地广播接收器
    private LocalBroadcastManager localBroadcastManager;

    /**
     * 音乐是否在播放
     */
    public static boolean isPlaying;

    /**
     * 定义广播常量
     */
    public class BROADCAST{
        /**
         * 创建音乐底部浮动栏
         */
        public final static int TYPE_CREATE_MUSIC_VIEW = -1;
        /**
         * 播放
         */
        public final static int TYPE_PLAY = 0;
        /**
         * 暂停
         */
        public final static int TYPE_PAUSE = -1;
        /**
         * 下一曲
         */
        public final static int TYPE_NEXT = 2;
        /**
         * 播放进度条最大值
         */
        public final static int TYPE_MAXPROGRESS = 3;
        /**
         * 播放进度条中的当前进度
         */
        public final static int TYPE_CurrentProgress = 4;

        public final static String TYPE_CurrentTime = "currentTime";
        public final static String TYPE_TotalTime = "totalTime";
        /**
         * 停止播放
         */
        public final static int TYPE_STOP = 5;
        /**
         * 增加播放列表
         */
        public final static int TYPE_ADDPLAYLIST = 6;
        /**
         * 设置播放栏的值
         */
        public final static int TYPE_SETPLAYBARVALUE = 7;
        /**
         * 专辑图片
         */
        public final static String TYPE_IMAGENAME = "music_Image";
        /**
         * 音乐名称
         */
        public final static String TYPE_MUSICNAME = "music_Name";
        /**
         * 歌手
         */
        public final static String TYPE_ARITSTNAME = "music_Artist";
        /**
         * 音乐进度最大值
         */
        public final static String TYPE_PROGRESSMAX_NAME = "music_ProgressMax";
        /**
         * 推送消息的时候键值对中的值名称
         */
        public final static String TYPE_TYPENAME = "type";
        public final static String TYPE_VALUENAME = "value";
    }


    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isPlaying = false;

        mediaPlayer = new MediaPlayer();
        currentIndex = 0;
        // 初始化通知栏
        initRemoteViews();
        Log.e("初始化通知栏","设置监听完成");

        //初始化广播
        musicServicesBroadcastReceiver = new MusicServicesBroadcastReceiver();
        Log.e("初始化广播","musicServicesBroadcastReceiver");
        IntentFilter intentFilter = new IntentFilter();
        //通知activity更新下方播放栏和播放界面的action
        intentFilter.addAction(PLAY);
        intentFilter.addAction(PREV);
        intentFilter.addAction(NEXT);
        intentFilter.addAction(CLOSE);
        Log.e("过滤器添加action",PLAY+" "+PREV+" "+NEXT+" "+CLOSE);
        //本地广播注册
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        localBroadcastManager.registerReceiver(musicServicesBroadcastReceiver,intentFilter);

        /**
         * 这里不能用本地广播，就用普通广播，否则无法收到广播消息
         *
         * **/
        registerReceiver(musicServicesBroadcastReceiver,intentFilter);
        Log.e("本地广播注册","localBroadcastManager");

        //初始化
        musicArrayList = new ArrayList<>();

        //展示通知栏
//        showNotification();
        initNotification();
        Log.e("展示通知栏","");
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Log.i("服务","onStartCommand: "+isPlaying);
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicServicesBroadcastReceiver != null)
//            localBroadcastManager.unregisterReceiver(musicServicesBroadcastReceiver);
            unregisterReceiver(musicServicesBroadcastReceiver);
    }

    /**
     * 初始化通知
     */
    private void initNotification() {
        String channelId = "play_control";
        String channelName = "播放控制";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        createNotificationChannel(channelId, channelName, importance);

        //点击整个通知时发送广播
        Intent intent = new Intent(getApplicationContext(), NotificationClickReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //初始化通知
        notification = new NotificationCompat.Builder(this, "play_control")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_seek_bar)
                .setLargeIcon(MusicResolver.getBitmapFromDrawable(this,R.drawable.ic_music))
                .setCustomContentView(remoteViews)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
    }


    /**
     * 创建通知渠道
     *
     * @param channelId   渠道id
     * @param channelName 渠道名称
     * @param importance  渠道重要性
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{0});
        channel.setSound(null, null);
        //获取系统通知服务
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    /**
     * 初始化自定义通知栏的按钮点击事件
     */
    private void initRemoteViews() {
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.music_notification);

        //通知栏控制器上一首按钮广播操作
        Intent intentPrev = new Intent(PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, intentPrev, 0);
        //为prev控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.iv_pre, prevPendingIntent);

        //通知栏控制器播放暂停按钮广播操作  //用于接收广播时过滤意图信息
        Intent intentPlay = new Intent(PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, intentPlay, 0);
        //为play控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.iv_play, playPendingIntent);

        //通知栏控制器下一首按钮广播操作
        Intent intentNext = new Intent(NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, intentNext, 0);
        //为next控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent);

        //通知栏控制器关闭按钮广播操作
        Intent intentClose = new Intent(CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, intentClose, 0);
        //为close控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_close, closePendingIntent);

        Log.e("设置通知栏按钮监听完成","");
    }



    /**
     *  通知栏播放音乐广播接收器
     */
    public class MusicServicesBroadcastReceiver extends BroadcastReceiver {
        public static final String TAG = "MusicReceiver";
        @Override
        public void onReceive(Context context, Intent intent){
            Log.e("收到广播","正在处理");
            //UI控制
            UIControl(intent.getAction(), TAG);
        }
    }

    /**
     * 页面的UI 控制 ，通过服务来控制页面和通知栏的UI
     *
     * @param state 状态码
     * @param tag
     */
    private void UIControl(String state, String tag) {
        switch (state) {
            case PLAY:
                if (mediaPlayer.isPlaying()) {
                    myBinder.pause();
                }
                else {
                    myBinder.play();
                }
                Log.e(tag,PLAY+" or "+PAUSE);
                break;
            case PREV:
                myBinder.playPrev();
                Log.e(tag,PREV);
                break;
            case NEXT:
                myBinder.playNext();
                Log.e(tag,NEXT);
                break;
            case CLOSE:
                //叉掉通知后，只是暂停播放
                myBinder.pause();
                manager.cancel(NOTIFICATION_ID);
                Log.e(tag,CLOSE);
                break;
            default:
                break;
        }
    }

    /**
     * 更改通知的信息和UI
     */
    public void updateNotificationShow() {
        //播放状态判断
        if (mediaPlayer.isPlaying()) {
            remoteViews.setImageViewResource(R.id.iv_play, R.drawable.ic_noti_pause);
        } else {
            remoteViews.setImageViewResource(R.id.iv_play, R.drawable.ic_noti_play);
        }
        //封面专辑
        remoteViews.setImageViewBitmap(R.id.iv_music_picture, musicArrayList.get(currentIndex).getAlbumBitmap());
        //歌曲名
        remoteViews.setTextViewText(R.id.tv_musicName, musicArrayList.get(currentIndex).getName());
        //歌手名
        remoteViews.setTextViewText(R.id.tv_singer, musicArrayList.get(currentIndex).getAuthor());
        //发送通知
        manager.notify(NOTIFICATION_ID, notification);
    }



    private class MyBinder extends Binder implements MyBinderInterface {
        @Override
        public MusicService getService() {
            return MusicService.this;
        }

        @Override
        public void reset() {
            mediaPlayer.reset();
        }

        @Override
        public void pause() {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                seekLength = mediaPlayer.getCurrentPosition();
            }
            StaticValue.Music_IsPlay = false;
            Intent intent = new Intent(MusicServiceReceiverAction);
            intent.putExtra(BROADCAST.TYPE_TYPENAME, BROADCAST.TYPE_PAUSE);
            localBroadcastManager.sendBroadcast(intent);
            updateNotificationShow();
        }

        @Override
        public void resume() {
            mediaPlayer.seekTo(seekLength);
            mediaPlayer.start();
            StaticValue.Music_IsPlay = true;
        }

        @Override
        public void play() {
            mediaPlayer.reset();

            try {
                mediaPlayer.setDataSource(musicArrayList.get(currentIndex).getUrl());
                mediaPlayer.prepare();
                mediaPlayer.seekTo(seekLength);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(!mediaPlayer.isPlaying()){
//                        setPlayMode();
                        playNext();
                    }
                }
            });
            } catch (IOException e) {
                e.printStackTrace();
            }
            StaticValue.Music_IsPlay = true;
            isPlaying = true;
            Intent intent = new Intent(MusicServiceReceiverAction);
            intent.putExtra(BROADCAST.TYPE_TYPENAME, BROADCAST.TYPE_SETPLAYBARVALUE);
            localBroadcastManager.sendBroadcast(intent);
            updateNotificationShow();
        }

        @Override
        public void playNext() {
            currentIndex += 1;
            if (currentIndex >= musicArrayList.size()){
                currentIndex = 0;
            }
            seekLength = 0;
            play();
        }

        @Override
        public void playPrev() {
            currentIndex -= 1;
            if (currentIndex < 0){
                currentIndex = musicArrayList.size() - 1;
            }
            seekLength = 0;
            play();
        }

        @Override
        public void release() {
            mediaPlayer.reset();
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        @Override
        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        @Override
        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        @Override
        public int getCurrentPosition() {
            return mediaPlayer.getCurrentPosition();
        }

        @Override
        public void seekTo(int length) {
            seekLength = length;
            mediaPlayer.seekTo(length);
            Intent intent = new Intent(MusicServiceReceiverAction);
            intent.putExtra(BROADCAST.TYPE_TYPENAME, BROADCAST.TYPE_SETPLAYBARVALUE);
            localBroadcastManager.sendBroadcast(intent);
        }

        @Override
        public int getCurrentIndex() {
            return currentIndex;
        }

        @Override
        public void setCurrentIndex(int currentIdx) {
            currentIndex = currentIdx;
        }

        @Override
        public void setPlayMode() {
            //目前是循环播放
//        if (playMode == 0)//全部循环
//        {
//            if (position == MusicResolver.musicArrayList.size() - 1)//默认循环播放
                if (getCurrentIndex() == musicArrayList.size() - 1)//默认循环播放
                {
                    currentIndex = 0;// 第一首
                    reset();
                    play();

                } else {
                    currentIndex++;
                    reset();
                    play();
                }
//        } else if (playMode == 1)//单曲循环
//        {
//            //position不需要更改
//            mediaPlayer.reset();
//            objectAnimator.pause();
//            needleImagv.startAnimation(rotateAnimation2);
//            prevAndnextplaying(Common.musicList.get(position).path);
//        }
//        else if (playMode == 2)//随机
//        {
//            position = (int) (Math.random() * Common.musicList.size());//随机播放
//            mediaPlayer.reset();
//            objectAnimator.pause();
//            needleImagv.startAnimation(rotateAnimation2);
//            prevAndnextplaying(Common.musicList.get(position).path);
//        }
        }

        @Override
        public void setMusicList(ArrayList<Music> musicList) {
            musicArrayList = musicList;
        }

        @Override
        public ArrayList<Music> getMusicList() {
            return musicArrayList;
        }

    }


    //格式化数字
    private String formatTime(int length) {
        Date date = new Date(length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");    //规定固定的格式
        String totaltime = simpleDateFormat.format(date);
        return totaltime;
    }
}