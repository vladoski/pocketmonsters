package com.albertinisodringa.pocketmonsters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * RankingActivity implements a RecycleView that displays the top 20 players in the game by XP
 */
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

        // Use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerRankingView.setLayoutManager(layoutManager);

        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences_key), Context.MODE_PRIVATE);

        // Game API handler
        ApiHandler api = new ApiHandler(sharedPreferences.getString("sessionId", null), getString(R.string.api_url), getApplicationContext());

        // Call to the API to get players in the ranking
        api.getRankingAsync(new VolleyEventListener() {

            @Override
            public void onSuccess(Object returnFromCallback) {
                List<Player> playerList = new ArrayList<>();
                if (returnFromCallback instanceof List<?>) {
                    playerList = (List<Player>) returnFromCallback; // Can't check type becuase it's a generic

                    // Create an instance of RankingAdapter to create the recycler view with players data
                    rankingAdapter = new RankingAdapter(playerList);
                    recyclerRankingView.setAdapter(rankingAdapter);
                }
            }

            @Override
            public void onFailure(Exception error) {
                ApiErrorHandler.handle(error, getApplicationContext());
            }
        });

    }

    /**
     * On back click.
     *
     * @param v the view
     */
// Go back to MainActivity (map)
    public void onBackClick(View v) {
        super.onBackPressed();
    }
}
