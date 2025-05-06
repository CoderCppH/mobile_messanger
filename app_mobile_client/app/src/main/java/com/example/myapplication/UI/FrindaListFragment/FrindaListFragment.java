package com.example.myapplication.UI.FrindaListFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.ACTIVITY.FriendAddActivity;
import com.example.myapplication.ACTIVITY.MessangerActivity;
import com.example.myapplication.API.HttpClient;
import com.example.myapplication.API.json_p_user;
import com.example.myapplication.GL.GL;
import com.example.myapplication.LIST_USER.UserAdapter;
import com.example.myapplication.LIST_USER.p_user_item;
import com.example.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FrindaListFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<p_user_item> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frinda_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        adapter = new UserAdapter(inflater, list);

        // Устанавливаем слушателя клика
        adapter.setOnItemClickListener(position -> {
            p_user_item clickedItem = list.get(position);
            var intent_mess = new Intent(view.getContext(), MessangerActivity.class);
            intent_mess.putExtra("person_json",new Gson().toJson(clickedItem));
            startActivity(intent_mess);
        });

        recyclerView.setAdapter(adapter);

        Init(view);
        fetchData();

        return view;
    }

    private void fetchData() {
        CompletableFuture.runAsync(() -> {
            try {
                HttpClient api = new HttpClient();
                String response = api.GET(GL.url_api_server + "users");
                Log.d("API.GET.USERS", response);

                Gson gson = new Gson();
                Type userListType = new TypeToken<List<json_p_user>>() {}.getType();
                List<json_p_user> userList = gson.fromJson(response, userListType);

                for (json_p_user user : userList) {
                    p_user_item p_usr = new p_user_item(user.id,
                            R.drawable.ic_launcher_foreground,
                            user.first_name + " " + user.last_name,
                            user.email);
                    list.add(p_usr);
                }

                if(getActivity() == null) return;

                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                Log.e("API.GET.USERS", "Error fetching data", e);
            }
        });
    }

    private void Init(View view) {
        Button btn = view.findViewById(R.id.btn_add_friend);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), FriendAddActivity.class);
            startActivity(intent);
        });
    }

}
