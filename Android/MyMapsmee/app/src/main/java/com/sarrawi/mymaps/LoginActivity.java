package com.sarrawi.mymaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sarrawi.mymaps.components.LoadingDialogBar;
import com.sarrawi.mymaps.entities.LoginResponse;
import com.sarrawi.mymaps.entities.User;
import com.sarrawi.mymaps.utils.LdgoApi;
import com.sarrawi.mymaps.utils.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private TextView btnSignUp;
    private SharedPreferences sp;
    LoadingDialogBar loadingDialogBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        loadingDialogBar = new LoadingDialogBar(this);

        sp = getSharedPreferences("user", Context.MODE_PRIVATE);

        loadData();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSendLoginRequest();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void btnSendLoginRequest(){
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Please enter all fields!!", Toast.LENGTH_LONG).show();
            return;
        }

        loadingDialogBar.ShowDialog("loading...");

        LdgoApi ldgoApi = RetrofitClient.getRetrofitInstance().create(LdgoApi.class);
        Call<LoginResponse> call = ldgoApi.login(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loadingDialogBar.HideDialog();

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(LoginActivity.this, "Username or password incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }

                String access = response.body().getAccess();
                String refresh = response.body().getRefresh();

                saveData(access, refresh);

                // هنا نعمل getMe لكي نحصل على userID
                getMe(access);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loadingDialogBar.HideDialog();
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveData(String access, String refresh) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("access", access);
        editor.putString("refresh", refresh);
        editor.apply();
    }

    public void loadData() {
        String access = sp.getString("access", "");

        if (access != null && !access.trim().isEmpty()) {

            String bearer = "Bearer " + access;

            LdgoApi ldgoApi = RetrofitClient.getRetrofitInstance().create(LdgoApi.class);
            Call<User> call = ldgoApi.getMe(bearer);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }
                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getMe(String accessToken) {

        LdgoApi ldgoApi = RetrofitClient.getRetrofitInstance().create(LdgoApi.class);

        Call<User> call = ldgoApi.getMe("Bearer " + accessToken);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {

                    String userID = response.body().getId();

                    // حفظ userID
                    SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("userID", userID);
                    editor.apply();

                    // فتح Maps
                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
