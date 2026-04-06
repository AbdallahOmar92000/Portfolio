package com.sarrawi.mymaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sarrawi.mymaps.components.LoadingDialogBar;
import com.sarrawi.mymaps.entities.User;
import com.sarrawi.mymaps.utils.LdgoApi;
import com.sarrawi.mymaps.utils.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {

    EditText username, email, name, language, landmark;
    private SharedPreferences sp;
    private User user;
    CardView btnUpdateProfile;
    LdgoApi ldgoApi;
    LoadingDialogBar loadingDialogBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        loadingDialogBar = new LoadingDialogBar(this);
        loadingDialogBar.ShowDialog("loading...");

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        language = findViewById(R.id.language);
        landmark = findViewById(R.id.landmark);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        sp = getSharedPreferences("user", Context.MODE_PRIVATE);

        ldgoApi = RetrofitClient.getRetrofitInstance().create(LdgoApi.class);
        fetchUser(ldgoApi);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });
    }

    private void fetchUser (LdgoApi ldgoApi) {
        String userID = sp.getString( "userID", "");
        Call<User> call = ldgoApi.getUser(userID);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(EditUserActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                setUser(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditUserActivity.this, t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    private void setUser(User user){
        this.user = user;
        name.setText(user.getName().toString());
        username.setText(user.getEmail().toString());
        email.setText(user.getEmail().toString());
        language.setText(user.getLanguage().toString());
        landmark.setText(user.getLandmark().toString());
        loadingDialogBar.HideDialog();
    }

    private void updateUser(){
        loadingDialogBar.ShowDialog("loading...");
        String newUsername = username.getText().toString();
        String newName = name.getText().toString();
        String newEmail = email.getText().toString();
        String newLanguage = language.getText().toString();
        String newLandmark = landmark.getText().toString();

        if(newUsername != null && !newUsername.trim().isEmpty()){
            user.setEmail(newUsername);
        }

        if(newName != null && !newName.trim().isEmpty()){
            user.setName(newName);
        }

        if(newEmail != null && !newEmail.trim().isEmpty()){
            user.setEmail(newEmail);
        }

        if(newLanguage != null && !newLanguage.trim().isEmpty()){
            user.setLanguage(newLanguage);
        }

        if(newLandmark != null && !newLandmark.trim().isEmpty()){
            user.setLandmark(newLandmark);
        }

        Call<User> call = ldgoApi.updateUserField(user.getId(), user.getName(), user.getEmail(), user.getEmail(), user.getUseMetric());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(EditUserActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(EditUserActivity.this, "profile updated", Toast.LENGTH_SHORT).show();

                setUser(response.body());
                Intent intent = new Intent(EditUserActivity.this, ProfileActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditUserActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("errry", t.getMessage());
            }
        });
    }
}