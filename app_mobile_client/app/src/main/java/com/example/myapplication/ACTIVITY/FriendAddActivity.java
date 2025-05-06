package com.example.myapplication.ACTIVITY;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.API.HttpClient;
import com.example.myapplication.API.json_p_user;
import com.example.myapplication.GL.GL;
import com.example.myapplication.LIST_USER.UserAdapter;
import com.example.myapplication.LIST_USER.p_user_item;
import com.example.myapplication.R;
import com.example.myapplication.SETUP.SetUp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;


public class FriendAddActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        new SetUp(this);

        recyclerView = findViewById(R.id.recycle_view_friends_on_add);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Запускаем корутину для получения данных
        fetchUsers();
    }

    private void fetchUsers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient api = new HttpClient();
                    String response = api.GET(GL.url_api_server + "users");
                    Log.d("API.GET.USERS", response);

                    Gson gson = new Gson();
                    Type userListType = new TypeToken<List<json_p_user>>() {}.getType();
                    final List<json_p_user> userList = gson.fromJson(response, userListType);

                    final ArrayList<p_user_item> adapterList = new ArrayList<>();
                    for (json_p_user user : userList) {
                        adapterList.add(new p_user_item(user.id, R.drawable.ic_launcher_foreground, user.first_name, user.email));
                    }

                    // Обновление UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing() && !isDestroyed()) {
                                adapter = new UserAdapter(getLayoutInflater(), adapterList);
                                recyclerView.setAdapter(adapter);
                            } else {
                                Log.w("FriendAddActivity", "Activity is finishing or destroyed, не обновляем UI");
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("FriendAddActivity", "Ошибка при загрузке пользователей", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FriendAddActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}