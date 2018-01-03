package com.example.paul.personalproject;

import android.annotation.TargetApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivty extends AppCompatActivity implements ServerCommunication.RegisterDataCallback{

    public Button registerButton, signinButton;
    public String _username,_email,_password;
    public EditText e_username, e_email, e_password;

    @Override
    @TargetApi(19)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activty);

        registerButton = (Button) findViewById(R.id.register_button);
        signinButton = (Button) findViewById(R.id.email_sign_in_button);

        e_username = (EditText) findViewById(R.id.username);
        e_email = (EditText) findViewById(R.id.email);
        e_password = (EditText) findViewById(R.id.password);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _username = e_username.getText().toString();
                _email = e_email.getText().toString();
                _password = e_password.getText().toString();

                ServerCommunication serverCommunication = new ServerCommunication(RegisterActivty.this, SERVER_MODE.REGISTER);

                serverCommunication.execute();
            }
        });

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void returnRegisterData(String result, SERVER_RESPONSE server_response) {
        if(server_response == SERVER_RESPONSE.SUCCESS) {
            if(result.equalsIgnoreCase("true")) {

            }
        }
        else if(server_response == SERVER_RESPONSE.FAILURE) {
            Toast.makeText(RegisterActivty.this, "Failure: Please Retry", Toast.LENGTH_SHORT).show();
        }
    }

}
