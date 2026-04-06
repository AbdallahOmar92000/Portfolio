package com.sarrawi.mymaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sarrawi.mymaps.components.LoadingDialogBar;
import com.sarrawi.mymaps.entities.User;
import com.sarrawi.mymaps.entities.UserLogin;
import com.sarrawi.mymaps.utils.LdgoApi;
import com.sarrawi.mymaps.utils.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextView btnSignIn;
    private Button btnRegister;
    private EditText inputEmail, inputPassword, inputName;
    private SharedPreferences sp;
    LoadingDialogBar loadingDialogBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnSignIn = findViewById(R.id.btnSignIn);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputName = findViewById(R.id.inputName);
        btnRegister = findViewById(R.id.btnRegister);
        loadingDialogBar = new LoadingDialogBar(this);

        sp = getSharedPreferences("user", Context.MODE_PRIVATE);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser(){
        if (TextUtils.isEmpty(inputEmail.getText().toString()) ||
                TextUtils.isEmpty(inputPassword.getText().toString()) ||
                TextUtils.isEmpty(inputName.getText().toString())
        ){
            Toast.makeText(this, "Please enter all fields!!", Toast.LENGTH_LONG).show();
        }else{
            if(inputPassword.getText().toString().length() < 6) {
                Toast.makeText(this, "Password too short!!", Toast.LENGTH_LONG).show();
            } else {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                    LdgoApi ldgoApi = RetrofitClient.getRetrofitInstance().create(LdgoApi.class);

                    String name = inputName.getText().toString();
                    String email = inputEmail.getText().toString();
                    String password = inputPassword.getText().toString();

                    // NOTE: send only email + password + name
                    Call<UserLogin> call = ldgoApi.register(name, email, password);

                    loadingDialogBar.ShowDialog("loading...");
                    call.enqueue(new Callback<UserLogin>() {
                        @Override
                        public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                            loadingDialogBar.HideDialog();

                            if (!response.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Email already taken", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            saveData(response.body().getUser().getEmail(),
                                    response.body().getUser().getId(),
                                    response.body().getJwt());
                        }

                        @Override
                        public void onFailure(Call<UserLogin> call, Throwable t) {
                            loadingDialogBar.HideDialog();
                            Toast.makeText(RegisterActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void saveData(String email, String userID, String jwt) {
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("email", email);
        editor.putString("userID", userID);
        editor.putString("jwt", "Bearer "+ jwt);
        editor.commit();

        loadData();
    }

    public void loadData() {
        sp = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String jwt = sp.getString("jwt", "");

        if(jwt != null && !jwt.trim().isEmpty()) {
            LdgoApi ldgoApi = RetrofitClient.getRetrofitInstance().create(LdgoApi.class);
            Call<User> call = ldgoApi.getMe(jwt);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }
                    Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
//
//
//
//package com.sarrawi.mymaps;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.sarrawi.mymaps.components.LoadingDialogBar;
//import com.sarrawi.mymaps.entities.User;
//import com.sarrawi.mymaps.entities.UserLogin;
//import com.sarrawi.mymaps.utils.LdgoApi;
//import com.sarrawi.mymaps.utils.RetrofitClient;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
////AIzaSyCJCOfp00o1_KKwcx2ndAm1_uOb_fa_lKc
//public class RegisterActivity extends AppCompatActivity {
//
//    private TextView btnSignIn;
//    private Button btnRegister;
//    private EditText inputEmail, inputPassword, inputUsername, inputName;
//    private SharedPreferences sp;
//    LoadingDialogBar loadingDialogBar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        btnSignIn = findViewById(R.id.btnSignIn);
//
//        inputEmail = findViewById(R.id.inputEmail);
//        inputPassword = findViewById(R.id.inputPassword);
//        inputUsername = findViewById(R.id.inputUsername);
//        inputName = findViewById(R.id.inputName);
//        btnRegister = findViewById(R.id.btnRegister);
//        loadingDialogBar = new LoadingDialogBar(this);
//
//        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
//
//        btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                registerUser();
//            }
//        });
//    }
//
//    private void registerUser(){
//        if (TextUtils.isEmpty(inputEmail.getText().toString()) ||
//                TextUtils.isEmpty(inputPassword.getText().toString()) ||
//                TextUtils.isEmpty(inputUsername.getText().toString()) ||
//                TextUtils.isEmpty(inputName.getText().toString())
//        ){
//            Toast.makeText(this, "Please enter all fields!!", Toast.LENGTH_LONG).show();
//        }else{
//            if(inputPassword.getText().toString().length() < 6) {
//                Toast.makeText(this, "Password too short!!", Toast.LENGTH_LONG).show();
//            } else {
//                if (android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
//                    LdgoApi ldgoApi = RetrofitClient.getRetrofitInstance().create(LdgoApi.class);
//                    String name = inputName.getText().toString();
//                    String email = inputEmail.getText().toString();
//                    String username = inputUsername.getText().toString();
//                    String password = inputPassword.getText().toString();
//                    Call<UserLogin> call = ldgoApi.register(name, email, username, password);
//                    loadingDialogBar.ShowDialog("loading...");
//                    call.enqueue(new Callback<UserLogin>() {
//                        @Override
//                        public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
//                            if (!response.isSuccessful()) {
//                                loadingDialogBar.HideDialog();
//                                Toast.makeText(RegisterActivity.this, "Email or Username are already taken", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            saveData(response.body().getUser().getUsername(), response.body().getUser().getId(), response.body().getJwt());
//                        }
//
//                        @Override
//                        public void onFailure(Call<UserLogin> call, Throwable t) {
//                            Toast.makeText(RegisterActivity.this, "Network Error", Toast.LENGTH_SHORT);
//                        }
//                    });
//                } else {
//                    Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }
//
//    public void saveData(String username, String userID, String jwt) {
//        SharedPreferences.Editor editor = sp.edit();
//
//        editor.putString("username", username);
//        editor.putString("userID", userID);
//        editor.putString("jwt", "Bearer "+ jwt);
//        editor.commit();
//
//        loadData();
//    }
//
//    public void loadData() {
//        sp = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
//        String jwt = sp.getString( "jwt", "");
//
//        if(jwt != null && !jwt.trim().isEmpty()) {
//            LdgoApi ldgoApi = RetrofitClient.getRetrofitInstance().create(LdgoApi.class);
//            Call<User> call = ldgoApi.getMe(jwt);
//            call.enqueue(new Callback<User>() {
//                @Override
//                public void onResponse(Call<User> call, Response<User> response) {
//                    if (!response.isSuccessful()) {
//                        return;
//                    }
//                    Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
//                    startActivity(intent);
//                }
//
//                @Override
//                public void onFailure(Call<User> call, Throwable t) {
//                    Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT);
//                }
//            });
//
//        }
//    }
//}