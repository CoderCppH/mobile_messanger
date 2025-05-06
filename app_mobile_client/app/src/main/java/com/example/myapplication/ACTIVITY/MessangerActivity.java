package com.example.myapplication.ACTIVITY;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.API.HttpClient;
import com.example.myapplication.API.json_p_message;
import com.example.myapplication.CONFIG_USER.ConfigUser;
import com.example.myapplication.CONFIG_USER.Person;
import com.example.myapplication.LIST_USER.p_user_item;
import com.example.myapplication.GL.GL;
import com.example.myapplication.R;
import com.example.myapplication.SETUP.SetUp;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class MessangerActivity extends AppCompatActivity {
    private HttpClient api;
    private ConfigUser configUser;
    private p_user_item friend;
    private Person me_person;

    // Класс модели сообщения
    public static class Message {
        public String text;
        public boolean isLeft; // true - сообщение слева, false - справа

        public Message(String text, boolean isLeft) {
            this.text = text;
            this.isLeft = isLeft;
        }
    }

    // Кастомный адаптер для сообщений
    public class MessageAdapter extends ArrayAdapter<Message> {
        public MessageAdapter(ArrayList<Message> messages) {
            super(MessangerActivity.this, 0, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Message message = getItem(position);

            if (message.isLeft) {
                if (convertView == null || convertView.getTag() != Boolean.TRUE) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_left, parent, false);
                    convertView.setTag(Boolean.TRUE);
                }
            } else {
                if (convertView == null || convertView.getTag() != Boolean.FALSE) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_right, parent, false);
                    convertView.setTag(Boolean.FALSE);
                }
            }

            TextView messageText = convertView.findViewById(R.id.message_id_edit_text);
            messageText.setText(message.text);

            return convertView;
        }
    }

    private ListView listView;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesanger_user);
        var setup = new SetUp(this);

        listView = findViewById(R.id.message_id_list_view);
        configUser = new ConfigUser(this);
        me_person = configUser.get_user();
        api = new HttpClient();

        // Инициализация друга из intent
        String person_json = getIntent().getStringExtra("person_json");
        friend = new Gson().fromJson(person_json, p_user_item.class);

        // Установка отображения информации друга
        TextView fullnameView = findViewById(R.id.message_id_fullname);
        TextView emailView = findViewById(R.id.message_id_email);
        fullnameView.setText(friend.getFullName());
        emailView.setText(friend.getEmail());

        // Загрузка сообщений из сети в фоновом потоке
        loadMessagesFromServer();
    }

    private void loadMessagesFromServer() {
        new Thread(() -> {
            try {
                String url = GL.url_api_server + String.format("messagers/%s/%s", me_person.id, friend.getId());
                Log.d("URI_GET_MESSAGE", url);
                String res = api.GET(url);

                if (res != null) {
                    json_p_message[] json_array = new Gson().fromJson(res, json_p_message[].class);
                    ArrayList<Message> messages = new ArrayList<>();

                    for (json_p_message item : json_array) {
                        boolean isLeft = item.user_from_id != me_person.id;
                        messages.add(new Message(item.message, isLeft));
                    }

                    runOnUiThread(() -> {
                        // Проверяем, находится ли пользователь внизу списка (с запасом в 1 элемент)
                        int lastVisiblePosition = listView.getLastVisiblePosition();
                        int count = adapter == null ? 0 : adapter.getCount();

                        boolean isAtBottom = (lastVisiblePosition >= count - 2);

                        if (adapter == null) {
                            adapter = new MessageAdapter(messages);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.clear();
                            adapter.addAll(messages);
                            adapter.notifyDataSetChanged();
                        }

                        if (isAtBottom) {
                            listView.setSelection(adapter.getCount() - 1);
                        }
                        // Если не внизу, то не прокручиваем, чтобы пользователь мог читать старые сообщения
                    });
                } else {
                    Log.e("MessangerActivity", "Response from server is null");
                }
            } catch (Exception e) {
                Log.e("MessangerActivity", "Error loading messages", e);
            }
        }).start();
    }

    private Handler handler = new Handler();
    private Runnable refreshMessagesRunnable;
    private void startRefreshingMessages() {
        refreshMessagesRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessagesFromServer(); // ваш метод загрузки сообщений для текущего чата
                handler.postDelayed(this, 3000); // вызов через 3 секунды
            }
        };
        handler.post(refreshMessagesRunnable);
    }

    private void stopRefreshingMessages() {
        if (handler != null && refreshMessagesRunnable != null) {
            handler.removeCallbacks(refreshMessagesRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRefreshingMessages(); // начинаем обновление при возвращении на экран
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRefreshingMessages(); // останавливаем, когда экран неактивен
    }
    public void OnButtonClickSendMessage(View view) {

        EditText messageEditText = findViewById(R.id.message_id_send_text);
        String text = messageEditText.getText().toString().trim();

        Log.d("EDIT_TEXT_LOG", text);
        if (text != null && text.length() > 0) {
            Log.d("INIT_TEXT_LOG", "TRUE");

            // Очищаем поле ввода сразу после нажатия кнопки
            messageEditText.setText("");


            // Создаем новый поток для выполнения сетевого запроса
            new Thread(() -> {
                json_p_message json_message = new json_p_message();
                json_message.user_from_id = me_person.id;
                json_message.user_to_id = friend.getId();
                json_message.message = text;
                json_message.id = -1;

                String postUrl = GL.url_api_server + "messagers/";
                String message = json_message.message != null ? json_message.message : "";
                String json = String.format("{ \"id\":-1, \"message\":\"%s\", \"user_from_id\":%d, \"user_to_id\":%d }",
                        message.replace("\"", "\\\""), // Экранирование кавычек
                        json_message.user_from_id,
                        json_message.user_to_id);
                Log.d("JSON_MESSAGE", json);
                String res = null;

                try {
                    res = api.POST(postUrl, json);
                } catch (IOException e) {
                    Log.d("ERROR_POST_REQUEST", e.getMessage());
                }

                // Обработка ответа и обновление UI должны выполняться в основном потоке
                String finalRes = res; // Для использования в runOnUiThread
                runOnUiThread(() -> {
                    Log.d("MessangerActivity", "POST response: " + finalRes);
                    if (finalRes != null) {
                        if ("{\"message\": \"success inserts messagers\"}".equals(finalRes)) {
                            // После успешной отправки обновляем список сообщений
                            loadMessagesFromServer();
                            // Проверяем, находится ли пользователь внизу списка
                            if (listView.getLastVisiblePosition() >= adapter.getCount() - 1) {
                                listView.setSelection(adapter.getCount() - 1); // Прокручиваем вниз, если пользователь внизу
                            }
                        } else {
                            Log.e("MessangerActivity", "Failed to send message: " + finalRes);
                        }
                    }
                    listView.setSelection(adapter.getCount() - 1);
                });


            }).start();

        }
    }
}