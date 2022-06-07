package com.example.pages.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.example.pages.activity.DetailPlayerActivity;
import com.example.pages.myView.CharacterListView;
import com.example.pages.R;
import com.example.pages.adapter.SingleSongAdapter;
import com.example.pages.entity.Music;
import com.example.pages.utils.LetterComparater;
import com.example.pages.utils.MusicResolver;
import com.example.pages.utils.TitleDecoration;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleSongFragment#} factory method to
 * create an instance of this fragment.
 */
public class SingleSongFragment extends Fragment {
    private volatile static SingleSongFragment singleSongFragment = null;
    /*** 单曲界面 ***/
    private RecyclerView singleSongRecyclerView;
    private LinearLayoutManager singleSongLinearLayoutManager;
    private SingleSongAdapter singleSongAdapter;
    private ArrayList<Music> musicArrayList;
    private CharacterListView characterListView;
    private MusicResolver resolver;

    private Toolbar toolbar;
    private Toolbar lastToolbar;


    //监听回调
    private FragmentCallBack mFragmentCallBack;

    //定义一个监听，用于fragment向activity传递数据
    public  interface FragmentCallBack {
        //定义监听方法，这里主要是需要传递当前播放歌曲的item的position
        void getPosition(int position);
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SingleSongFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleSongFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleSongFragment newInstance(String param1, String param2) {
        if (singleSongFragment == null) {
            synchronized (SingleSongFragment.class) {
                if (singleSongFragment == null) {
                    singleSongFragment = new SingleSongFragment();
                    Bundle args = new Bundle();
                    args.putString(ARG_PARAM1, param1);
                    args.putString(ARG_PARAM2, param2);
                    singleSongFragment.setArguments(args);
                }
            }
        }
        return singleSongFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void getMusicData() {
        /**ContextCompat.checkSelfPermission会返回一个int类型数值
            参数1：环境上下文
            参数2：权限常量名
         **/
        int i = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        //如果权限没有被赋予则动态申请权限
        if (i != PackageManager.PERMISSION_GRANTED) {
            /**动态申请权限
                参数1：上下文
                参数2：权限常量名，在Manifest下
                参数3：requestCode
             **/
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        musicArrayList = resolver.getMusic(this.getContext());

        //对数据根据首字母进行排序
        Collections.sort(musicArrayList, new LetterComparater());

        singleSongAdapter.setData(musicArrayList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_song, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*** 单曲界面 ***/
        characterListView = (CharacterListView) view.findViewById(R.id.characer_list);

        singleSongRecyclerView = (RecyclerView) view.findViewById(R.id.music_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        singleSongRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        singleSongLinearLayoutManager = new LinearLayoutManager(this.getContext());
        singleSongRecyclerView.setLayoutManager(singleSongLinearLayoutManager);

        // specify an adapter (see also next example)
        musicArrayList = new ArrayList<>();

        singleSongAdapter = new SingleSongAdapter();
        singleSongRecyclerView.setAdapter(singleSongAdapter);

        resolver = new MusicResolver();

        getMusicData();

        //设置
        singleSongRecyclerView.addItemDecoration(new TitleDecoration(new TitleDecoration.TitleAttributes(getContext()),"singleSong"));
        initEvent();
        //调用Adapter中设置监听的方法
        singleSongAdapter.setMyOnItemClickListener(new SingleSongAdapter.MyOnItemClickListener() {
            @Override
            public void OnItemClickListener(View v) {
                //注意：此处通过 RecyclerView 的实例获取item的位置
                int position = singleSongRecyclerView.getChildAdapterPosition(v);
                /*此处填写触发点击事件的代码*/
                //回调
                if (mFragmentCallBack != null) {
                    mFragmentCallBack.getPosition(position);
                    Log.d("fragment","position: " + position);
                    Intent intent = new Intent();
                    intent.setClass(v.getContext(), DetailPlayerActivity.class);
                    intent.putExtra("locate",position);
                    v.getContext().startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(SingleSongFragment.singleSongFragment.getActivity()).toBundle());
                }
            }
        });
        //更新
        singleSongAdapter.notifyDataSetChanged();
    }

    //设置页面根据字母索引滑动
    private void initEvent() {
        characterListView.setOnLetterChangeListener(new CharacterListView.OnLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                int position = singleSongAdapter.getSortLettersFirstPosition(letter);
                if (position != -1) {
                    if (singleSongRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        LinearLayoutManager manager = (LinearLayoutManager) singleSongRecyclerView.getLayoutManager();
                        manager.scrollToPositionWithOffset(position, 0);
                    } else {
                        singleSongRecyclerView.getLayoutManager().scrollToPosition(position);
                    }
                }
            }
        });
    }


    ///onAttach 当 Fragment 与 Activity 绑定时调用
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ///获取绑定的监听
        if (context instanceof FragmentCallBack) {
            mFragmentCallBack = (FragmentCallBack) context;
        }
    }
    ///onDetach 当 Fragment 与 Activity 解除绑定时调用
    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentCallBack = null;
    }

}