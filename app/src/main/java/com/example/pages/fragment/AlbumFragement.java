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
import com.example.pages.adapter.AlbumAdapter;
import com.example.pages.entity.Album;
import com.example.pages.utils.AlbumLetterComparater;
import com.example.pages.utils.MusicResolver;
import com.example.pages.utils.TitleDecoration;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumFragement#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumFragement extends Fragment {
    private volatile static AlbumFragement albumFragement = null;

    /*** 专辑界面 ***/
    private RecyclerView albumRecyclerView;
    private LinearLayoutManager albumLinearLayoutManager;
    private AlbumAdapter albumAdapter;
    private ArrayList<Album> albumArrayList;
    private CharacterListView characterListView;
    private MusicResolver resolver;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AlbumFragement() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumFragement.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumFragement newInstance(String param1, String param2) {
        if (albumFragement == null) {
            synchronized (AlbumFragement.class) {
                if (albumFragement == null) {
                    albumFragement = new AlbumFragement();
                    Bundle args = new Bundle();
                    args.putString(ARG_PARAM1, param1);
                    args.putString(ARG_PARAM2, param2);
                    albumFragement.setArguments(args);
                }
            }
        }
        return albumFragement;
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
        return inflater.inflate(R.layout.fragment_album_fragement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*** 专辑界面 ***/
        characterListView = (CharacterListView) view.findViewById(R.id.characer_list);

        albumRecyclerView = (RecyclerView) view.findViewById(R.id.album_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        albumRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        albumLinearLayoutManager = new LinearLayoutManager(this.getContext());
        albumRecyclerView.setLayoutManager(albumLinearLayoutManager);

        // specify an adapter (see also next example)
        albumArrayList = new ArrayList<>();

        albumAdapter = new AlbumAdapter();
        albumRecyclerView.setAdapter(albumAdapter);

        resolver = new MusicResolver();

        getAlbumData();

        //设置标题
        albumRecyclerView.addItemDecoration(new TitleDecoration(new TitleDecoration.TitleAttributes(getContext()),"album"));

        initEvent();
    }

    private void getAlbumData() {
        albumArrayList = resolver.getAlum();

        //对数据根据首字母进行排序
        Collections.sort(albumArrayList, new AlbumLetterComparater());

        albumAdapter.setData(albumArrayList);
    }

    //设置页面根据字母索引滑动
    private void initEvent() {
        characterListView.setOnLetterChangeListener(new CharacterListView.OnLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                int position = albumAdapter.getSortLettersFirstPosition(letter);
                if (position != -1) {
                    if (albumRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        LinearLayoutManager manager = (LinearLayoutManager) albumRecyclerView.getLayoutManager();
                        manager.scrollToPositionWithOffset(position, 0);
                    } else {
                        albumRecyclerView.getLayoutManager().scrollToPosition(position);
                    }
                }
            }
        });
    }
}