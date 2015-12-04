package com.example.ngoc.api;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddStudentActivity extends AppCompatActivity {

    String classId, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        classId = getIntent().getExtras().getString("id");
        title = getIntent().getExtras().getString("title");

        final EditText edtSname = (EditText) findViewById(R.id.edtSname);
        final EditText edtSemail = (EditText) findViewById(R.id.edtSemail);
        final EditText edtSaddr = (EditText) findViewById(R.id.edtSaddress);
        final EditText edtSphone = (EditText) findViewById(R.id.edtSphone);

        Button btnAddStu = (Button) findViewById(R.id.btnAddStu);
        btnAddStu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtSname.getText().toString();
                String mail = edtSemail.getText().toString();
                String addr = edtSaddr.getText().toString();
                String phone = edtSphone.getText().toString();

                try {
                    postStudent("https://nameless-wave-5939.herokuapp.com/students.json", name, mail, addr, phone, classId);
                    Intent intent = new Intent(getApplicationContext(), StudentListActivity.class);
                    intent.putExtra("id", classId);
                    intent.putExtra("title", title);
                    startActivity(intent);
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private String postStudent(String ip_address, String name, String email, String addr, String phone, String classID) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL url = new URL(ip_address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        StringBuilder sb = new StringBuilder();
        try {
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();

            JSONObject student = new JSONObject();
            student.put("name", name);
            student.put("email", email);
            student.put("address", addr);
            student.put("phone", phone);
            student.put("class_id", classID);
            JSONObject data = new JSONObject();
            data.put("student", student);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data.toString().getBytes());
            outputStream.close();

            InputStream in = null;
            switch (connection.getResponseCode()) {
                case 200:
                    in = new BufferedInputStream(connection.getInputStream());
                    break;
                case 401:
                    in = null;
                    sb.append("Unauthorized");
                    break;
                case 404:
                    in = null;
                    sb.append("Do not exist");
                    break;
                case 422:
                    in = new BufferedInputStream(connection.getErrorStream());
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
