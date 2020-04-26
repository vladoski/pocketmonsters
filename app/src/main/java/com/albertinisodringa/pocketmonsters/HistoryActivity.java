package com.albertinisodringa.pocketmonsters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * HistoryActivity implements the activity that deals with the History of a player.
 * The history of a player is just a list of which MapElements has he dealt with (fought/eaten)
 */
public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerHistoryView;
    private RecyclerView.Adapter historyAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerHistoryView = findViewById(R.id.history_recycler_view);
        recyclerHistoryView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerHistoryView.setLayoutManager(layoutManager);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences_key), Context.MODE_PRIVATE);

        ApiHandler api = new ApiHandler(sharedPreferences.getString("sessionId", null), getString(R.string.api_url), getApplicationContext());

        api.getHistoryAsync(new VolleyEventListener() {
            @Override
            public void onSuccess(Object returnFromCallback) {
                List<History> historyList = new ArrayList<>();
                if (returnFromCallback instanceof ArrayList) {
                    historyList = (List<History>) returnFromCallback;
                }

                Log.d("History", "Reading History");

                for (int i = 0; i < historyList.size(); i++) {
                    Log.d("HistoryElement", historyList.get(i).toString());
                }

                Log.d("HistoryCount", historyList.size() + "");

                // Create an instance of HistoryAdapter to create the recycler view with players data
                historyAdapter = new HistoryAdapter(historyList);
                recyclerHistoryView.setAdapter(historyAdapter);
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
// Goes back to ProfileActivity if back button is clicked
    public void onBackClick(View v) {
        Log.d("ProfileActivity", "Back tap to MainActivity");
        super.onBackPressed();
    }
}
