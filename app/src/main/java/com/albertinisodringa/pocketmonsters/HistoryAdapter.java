package com.albertinisodringa.pocketmonsters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> historyList;

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    /* Provide a reference to the views for each data item
        you provide access to all the views for a data item in a view holder. */

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView historyCircleImageView;
        private TextView historyMapElementName;
        private TextView historyStatsTextView;
        private Resources res;
        private Intent intent;

        public ViewHolder(View view) {
            super(view);
            this.historyCircleImageView = view.findViewById(R.id.history_button_circle_image_view);
            this.historyMapElementName = view.findViewById(R.id.history_map_element_text_view);
            this.historyStatsTextView = view.findViewById(R.id.history_stats_text_view);

            this.intent = new Intent(getApplicationContext(), HistoryDetailActivity.class);
        }

        public void setHistory(final History historyElement) {

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.sharedpreferences_key), Context.MODE_PRIVATE);
            ApiHandler api = new ApiHandler(sharedPreferences.getString("sessionId", null), getApplicationContext().getString(R.string.api_url), getApplicationContext());

            api.getMapAsync(new VolleyEventListener() {
                @Override
                public void onSuccess(Object returnFromCallback) {
                    List<MapElement> mapElementList = new ArrayList<>();
                    if (returnFromCallback instanceof List) {
                        mapElementList = (List<MapElement>) returnFromCallback;
                    }

                    MapElement mapElement = null;
                    for (int i = 0; i < mapElementList.size(); i++) {
                        if (mapElementList.get(i).getId() == historyElement.getMapElementId()) {
                            mapElement = mapElementList.get(i);
                            break;
                        }
                    }

                    getMapElementImageCallback(mapElement, historyElement);
                }

                @Override
                public void onFailure(Exception error) {
                    ApiModelErrorHandler.handle(error, getApplicationContext());
                }
            });
        }

        private void getMapElementImageCallback(MapElement mapElement, final History historyElement) {
            this.historyMapElementName.setText(mapElement.getName());
            this.historyStatsTextView.setText("Counter: " + historyElement.getCounter());

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.sharedpreferences_key), Context.MODE_PRIVATE);
            ApiHandler api = new ApiHandler(sharedPreferences.getString("sessionId", null), getApplicationContext().getString(R.string.api_url), getApplicationContext());

            api.getMapElementImageAsync(mapElement, new VolleyEventListener() {
                @Override
                public void onSuccess(Object returnFromCallback) {
                    if (returnFromCallback instanceof byte[]) {
                        historyElement.setImage((byte[]) returnFromCallback);
                    }

                    setHistoryCircleImageViewCallback(historyElement);

                    historyCircleImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("mapElementId", historyElement.getMapElementId());
                            intent.putExtra("counter", historyElement.getCounter());
                            getApplicationContext().startActivity(intent);
                        }
                    });
                }

                @Override
                public void onFailure(Exception error) {
                    ApiModelErrorHandler.handle(error, getApplicationContext());
                }
            });
        }

        private void setHistoryCircleImageViewCallback(History historyElement) {
            // Set profile image on ImageView
            Bitmap historyImageBitmap = BitmapFactory.decodeByteArray(historyElement.getImage(), 0, historyElement.getImage().length);

            // If the profileImage is not a valid Bitmap, then display default profile image
            if (historyImageBitmap == null) {
                historyCircleImageView.setImageResource(R.drawable.profile_default);
            } else {
                // Display original image from the API
                historyCircleImageView.setImageBitmap(historyImageBitmap);
            }
        }
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create new views (invoked by the layout manager)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_activity_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Replace the contents of a view (invoked by the layout manager)
        holder.setHistory(this.historyList.get(position));
    }

    @Override
    public int getItemCount() {
        // Return the size of your dataset
        return historyList.size();
    }
}

