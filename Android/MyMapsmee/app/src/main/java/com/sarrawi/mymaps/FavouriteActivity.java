package com.sarrawi.mymaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sarrawi.mymaps.components.FavouriteRecyclerAdapter;
import com.sarrawi.mymaps.components.LoadingDialogBar;
import com.sarrawi.mymaps.entities.FavouriteLocation;
import com.sarrawi.mymaps.entities.User;
import com.sarrawi.mymaps.requests.AllFavouriteLocationRequest;
import com.sarrawi.mymaps.responses.FavouriteLocationResponse;
import com.sarrawi.mymaps.responses.SearchesResponse;
import com.sarrawi.mymaps.utils.LdgoApi;
import com.sarrawi.mymaps.utils.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteActivity extends AppCompatActivity {

    ImageButton btnGoToHome;
    ArrayList<FavouriteLocation> savedLocations;
    private SharedPreferences sp;
    private LdgoApi ldgoApi;
    private TextView textViewResult;
    private RecyclerView recyclerView;
    private String resItems;
    LoadingDialogBar loadingDialogBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        loadingDialogBar = new LoadingDialogBar(this);
        loadingDialogBar.ShowDialog("loading...");

        textViewResult = findViewById(R.id.text_view_result);
        btnGoToHome = findViewById(R.id.goToMapsBtn);

        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        String jwt = sp.getString( "jwt", "");

        btnGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {
            ldgoApi = RetrofitClient.getRetrofitInstance().create(LdgoApi.class);
            Call<AllFavouriteLocationRequest> call = ldgoApi.getFavouritesLocations(jwt);
            call.enqueue(new Callback<AllFavouriteLocationRequest>() {
                @Override
                public void onResponse(Call<AllFavouriteLocationRequest> call, Response<AllFavouriteLocationRequest> response) {
                    if(response.isSuccessful()){
                        ArrayList<FavouriteLocationResponse> res = response.body().getData();
                        for(FavouriteLocationResponse r : res){
                            String content = "";
                            content += "Name: " + r.getAttributes().getFormatted_address() + "\n";
                            content += "" + r.getAttributes().getName() + "\n\n";
                            textViewResult.append(content);
                        }
                    }

                    loadingDialogBar.HideDialog();
                }

                @Override
                public void onFailure(Call<AllFavouriteLocationRequest> call, Throwable t) {

                }
            });
        }catch (Exception e) { }
    }
}