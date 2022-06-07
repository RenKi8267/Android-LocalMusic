package com.example.pages.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pages.myView.CharacterListView;
import com.example.pages.R;
import com.example.pages.adapter.DirectoryAdapter;
import com.example.pages.entity.Directory;
import com.example.pages.utils.DirectoryLetterComparater;
import com.example.pages.utils.MusicResolver;
import com.example.pages.utils.TitleDecoration;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DirectoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DirectoryFragment extends Fragment {
    private volatile static DirectoryFragment directoryFragment = null;

    /*** 文件夹界面 ***/
    private RecyclerView directoryRecyclerView;
    private LinearLayoutManager directoryLinearLayoutManager;
    private DirectoryAdapter directoryAdapter;
    private ArrayList<Directory> directoryArrayList;
    private CharacterListView characterListView;
    private MusicResolver resolver;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DirectoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DirectoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DirectoryFragment newInstance(String param1, String param2) {
        if (directoryFragment == null) {
            synchronized (DirectoryFragment.class) {
                if (directoryFragment == null) {
                    directoryFragment = new DirectoryFragment();
                    Bundle args = new Bundle();
                    args.putString(ARG_PARAM1, param1);
                    args.putString(ARG_PARAM2, param2);
                    directoryFragment.setArguments(args);
                }
            }
        }
        return directoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_directory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*** 文件夹界面 ***/
        characterListView = (CharacterListView) view.findViewById(R.id.characer_list);

        directoryRecyclerView = (RecyclerView) view.findViewById(R.id.directory_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        directoryRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        directoryLinearLayoutManager = new LinearLayoutManager(this.getContext());
        directoryRecyclerView.setLayoutManager(directoryLinearLayoutManager);

        // specify an adapter (see also next example)
        directoryArrayList = new ArrayList<>();

        directoryAdapter = new DirectoryAdapter();
        directoryRecyclerView.setAdapter(directoryAdapter);

        resolver = new MusicResolver();

        getDirectoryData();

        //设置标题
        directoryRecyclerView.addItemDecoration(new TitleDecoration(new TitleDecoration.TitleAttributes(getContext()),"directory"));

        initEvent();
    }

    private void getDirectoryData() {
        directoryArrayList = resolver.getDirectory();

        //对数据根据首字母进行排序
        Collections.sort(directoryArrayList, new DirectoryLetterComparater());

        directoryAdapter.setData(directoryArrayList);
    }

    //设置页面根据字母索引滑动
    private void initEvent() {
        characterListView.setOnLetterChangeListener(new CharacterListView.OnLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                int position = directoryAdapter.getSortLettersFirstPosition(letter);
                if (position != -1) {
                    if (directoryRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        LinearLayoutManager manager = (LinearLayoutManager) directoryRecyclerView.getLayoutManager();
                        manager.scrollToPositionWithOffset(position, 0);
                    } else {
                        directoryRecyclerView.getLayoutManager().scrollToPosition(position);
                    }
                }
            }
        });
    }
}