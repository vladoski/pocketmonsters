package com.albertinisodringa.pocketmonsters;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private List<Player> playerList;

    public RankingAdapter(List<Player> playerList) {
        this.playerList = playerList;
    }

    /* Provide a reference to the views for each data item
        you provide access to all the views for a data item in a view holder. */

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView playerProfilePicture;
        private TextView playerNameRanking;
        private TextView experiencePointRanking;
        private TextView rankingPosition;
        private Resources res;

        public ViewHolder(View view) {
            super(view);
            this.playerProfilePicture = view.findViewById(R.id.player_button_circle_image_view);
            this.playerNameRanking = view.findViewById(R.id.player_name_ranking_text_view);
            this.experiencePointRanking = view.findViewById(R.id.experience_points_ranking_text_view);
            this.rankingPosition = view.findViewById(R.id.ranking_position_text_view);
        }

        public void setPlayerRanking(Player playerRanking, int position) {

            // Set profile image on ImageView
            Bitmap profileImageBitmap = BitmapFactory.decodeByteArray(playerRanking.getImage(), 0, playerRanking.getImage().length);

            // If the profileImage is not a valid Bitmap, then display default profile image
            if (profileImageBitmap == null) {
                playerProfilePicture.setImageResource(R.drawable.profile_default);
            } else {
                // Display original image from the API
                playerProfilePicture.setImageBitmap(profileImageBitmap);
            }

            Log.d("Ranking", position +"  "+playerRanking.getImage().toString());


            if (playerRanking.getUsername().equals("null") || playerRanking.getUsername() == null) {
                this.playerNameRanking.setText("No name");
            } else {
                this.playerNameRanking.setText(playerRanking.getUsername());
            }
            this.experiencePointRanking.setText(playerRanking.getExperiencePoints() + "");
            this.rankingPosition.setText((position + 1) + "");
        }
    }

    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create new views (invoked by the layout manager)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_profile_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Replace the contents of a view (invoked by the layout manager)
        holder.setPlayerRanking(this.playerList.get(position), position);
    }

    @Override
    public int getItemCount() {
        // Return the size of your dataset
        return playerList.size();
    }
}

