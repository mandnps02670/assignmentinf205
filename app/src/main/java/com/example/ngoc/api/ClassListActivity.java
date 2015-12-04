package com.example.ngoc.api;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassListActivity extends AppCompatActivity {

    ListView lv;
    ArrayAdapter<classObj> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        setTitle("Danh sách lớp học");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lv = (ListView) findViewById(R.id.listClass);

        try {
            setList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), StudentListActivity.class);
                classObj c = adapter.getItem(position);
                intent.putExtra("id", c.getId());
                intent.putExtra("title", c.getName());
                startActivity(intent);
            }
        });

        Button btnAddClass = (Button) findViewById(R.id.btnAddClass);
        btnAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddClassActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            setList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setList() throws Exception {
        JSONArray jArr = new JSONArray(getClass("https://nameless-wave-5939.herokuapp.com/classobjs.json").toString());
        List<classObj> list = new ArrayList<>();
        for (int i = 0; i < jArr.length(); i++) {
            JSONObject jObj = jArr.getJSONObject(i);
            classObj c = new classObj();
            c.setId(jObj.getString("id"));
            c.setName(jObj.getString("name"));
            c.setYears(jObj.getString("years"));
            list.add(c);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
    }

    private String getClass(String ip_address) throws Exception {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL url = new URL(ip_address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        StringBuilder sb = new StringBuilder();
        try {
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.connect();

            InputStream in = null;
            switch (connection.getResponseCode()) {
                case 200:
                    in = new BufferedInputStream(connection.getInputStream());
                    break;
                case 401:
                    in = null;
                    sb.append("Unauthorized");
                    break;
                default:
                    in = null;
                    sb.append("Unknown response code");
                    break;
            }

            if (in != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return sb.toString();
    }

}
