package com.example.ngoc.api;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        setTitle("Thêm lớp");

        final EditText edtCname = (EditText) findViewById(R.id.edtCname);
        final EditText edtCyears = (EditText) findViewById(R.id.edtCyears);

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName = edtCname.getText().toString();
                String strYears = edtCyears.getText().toString();
                try {
                    postClass("https://nameless-wave-5939.herokuapp.com/classobjs.json", strName, strYears);
                    Intent intent = new Intent(getApplicationContext(), ClassListActivity.class);
                    startActivity(intent);
                    finish();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "errors: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private String postClass(String ip_address, String name, String years) throws IOException {
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

            JSONObject classobj = new JSONObject();
            classobj.put("name", name);
            classobj.put("years", years);
            JSONObject data = new JSONObject();
            data.put("classobj", classobj);

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
