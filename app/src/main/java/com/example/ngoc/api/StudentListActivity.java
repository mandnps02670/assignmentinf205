package com.example.ngoc.api;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

public class StudentListActivity extends AppCompatActivity {

    ArrayAdapter<studentObj> adapter;
    ListView lv;
    String classId, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        title = getIntent().getExtras().getString("title");
        setTitle("Lớp " + title);

        lv = (ListView) findViewById(R.id.listStu);

        try {
            classId = getIntent().getExtras().getString("id");
            setList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                studentObj s = adapter.getItem(position);
                String sId = s.getId();
                try {
                    deleteStudent("https://nameless-wave-5939.herokuapp.com/students/", sId);
                    setList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        Button btnAddStu = (Button) findViewById(R.id.btnAddStu);
        btnAddStu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddStudentActivity.class);
                intent.putExtra("id", classId);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            title = getIntent().getExtras().getString("title");
            classId = getIntent().getExtras().getString("id");
            setList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setList() throws Exception {
        JSONArray jArr = new JSONArray(getStudent("https://nameless-wave-5939.herokuapp.com/students.json").toString());
        List<studentObj> list = new ArrayList<>();
        for (int i = 0; i < jArr.length(); i++) {
            JSONObject jObj = jArr.getJSONObject(i);
            if (Integer.parseInt(jObj.getString("class_id")) == Integer.parseInt(classId)) {
                studentObj s = new studentObj();
                s.setId(jObj.getString("id"));
                s.setName(jObj.getString("name"));
                s.setEmail(jObj.getString("email"));
                s.setAddress(jObj.getString("address"));
                s.setPhone(jObj.getString("phone"));
                list.add(s);
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
    }

    private String deleteStudent(String ip_address, String id) throws Exception {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL url = new URL(ip_address + id + ".json");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        StringBuilder sb = new StringBuilder();
        try {
            connection.setRequestMethod("DELETE");
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

    private String getStudent(String ip_address) throws Exception {
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
