package com.albertinisodringa.pocketmonsters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    private RecyclerView recyclerRankingView;
    private RecyclerView.Adapter rankingAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_activity);

        recyclerRankingView = findViewById(R.id.recycler_ranking);
        recyclerRankingView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerRankingView.setLayoutManager(layoutManager);

        //prendo la lista
        ApiModel api = new ApiModel("v6LxCAWaIJGHoLxK", getString(R.string.api_url), getApplicationContext());

        api.getRankingAsync(new VolleyEventListener() {

            @Override
            public void onSuccess(Object returnFromCallback) {
                List<Player> playerList = new ArrayList<>();
                if (returnFromCallback instanceof List<?>) {
                    playerList = (List<Player>) returnFromCallback; // Can't check type becuase it's a generic
                    rankingAdapter = new RankingAdapter(playerList);
                    recyclerRankingView.setAdapter(rankingAdapter);
                }
            }

            @Override
            public void onFailure(Exception error) {
                Log.d("MainActivity", "Error getting ranking");
            }
        });

    }

    public void onBackClick(View v) {
        Log.d("RankingActivity", "Back tap to main activity");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

}
