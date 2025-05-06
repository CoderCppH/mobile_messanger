package com.example.myapplication.ACTIVITY;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.API.HttpClient;
import com.example.myapplication.CONFIG_USER.ConfigUser;
import com.example.myapplication.GL.GL;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.SETUP.SetUp;
import com.google.gson.Gson;

public class SettingsActivity extends AppCompatActivity {
    EditText first_name;
    EditText last_name;
    TextView email;
    ConfigUser config;
    HttpClient client;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_for_main_activity);
        new SetUp(this);
        config = new ConfigUser(this);
        first_name = findViewById(R.id.settings_id_edit_first_name);
        last_name = findViewById(R.id.settings_id_edit_last_name);
        email = findViewById(R.id.settings_id_edit_email);
        email.setText(config.get_user().email);
        first_name.setText(config.get_user().first_name);
        last_name.setText(config.get_user().last_name);
        client = new HttpClient();
    }
    public void btn_update_data_user(View view) {
        if (first_name.length() > 0 && last_name.length() > 0 && email.length() > 0) {
            String text_fn = first_name.getText().toString();
            String text_ln = last_name.getText().toString();
            String text_email = email.getText().toString();
            first_name.setText("");
            last_name.setText("");

            var person = config.get_user();
            person.first_name = text_fn;
            person.last_name = text_ln;
            person.email = text_email;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();

                    var res = client.PUT(GL.url_api_server + "users", gson.toJson(person));
                    if (res.equals("{\"message\":\"success updates user\"}")) {
                        config.edit_config_user(person);
                    }
                }
            }).start();
        }
    }
    public void btn_delete_user(View view) {
        var person = config.get_user();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri = String.format( GL.url_api_server + "users/%d", person.id);
                var res = client.DELETE(uri);

                if(res.equals("{\"message\":\"success deleted user\"}")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NativeMainClass();
                        }
                    });
                }
            }
        }).start();
    }
    private void NativeMainClass() {
        Log.d("Config.User: Deleted: ", config.Delet().toString());
        finishAffinity();
        var intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void btn_exit_clicked(View view) {
        NativeMainClass();
    }
}
