package com.example.myapplication.UI.LentaListFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.LIST_LENTA.LentaAdapter;
import com.example.myapplication.LIST_LENTA.p_lenta_item;
import com.example.myapplication.LIST_USER.UserAdapter;
import com.example.myapplication.LIST_USER.p_user_item;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class LentaListFragment extends Fragment {

    private RecyclerView recyclerView;
    private LentaAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lenta_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LentaAdapter(inflater, getData());
        recyclerView.setAdapter(adapter);
        return view;
    }

    private List<p_lenta_item> getData() {
        ArrayList<p_lenta_item> list = new ArrayList<p_lenta_item>();
        p_lenta_item item = new p_lenta_item();
        item.image = R.drawable.food;
        item.name = "food";
        list.add(item);
        item = new p_lenta_item();
        item.image = R.drawable.youtube;
        item.name = "youtosyuiydsusduyfisdyfusidyfiusdyfuidsyfiusydfiuysdfiuyfiudsyuifdsube";
        list.add(item);
        return list;
    }
}