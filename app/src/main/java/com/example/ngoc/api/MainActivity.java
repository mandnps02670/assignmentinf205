package com.example.ngoc.api;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText edtAcc = (EditText) findViewById(R.id.account);
        final EditText edtPwd = (EditText) findViewById(R.id.password);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAcc.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "show errors", Toast.LENGTH_SHORT).show();
                } else if (edtPwd.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "show errors", Toast.LENGTH_SHORT).show();
                } else if (edtAcc.getText().toString().equals("admin") && edtPwd.getText().toString().equals("admin")) {
                    Intent intent = new Intent(MainActivity.this, ClassListActivity.class);
                    finish();
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "show errors", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
