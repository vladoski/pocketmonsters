package com.albertinisodringa.pocketmonsters;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {
    private List<Player> playerList;

    public RankingAdapter(List<Player> playerList) {
        this.playerList = playerList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView playerProfilePicture;
        private TextView playerNameRanking;
        private TextView experiencePointRanking;
        private TextView rankingPosition;

        public ViewHolder(View view) {
            super(view);
            this.playerProfilePicture = view.findViewById(R.id.player_profile_picture);
            this.playerNameRanking = view.findViewById(R.id.player_name_ranking);
            this.experiencePointRanking = view.findViewById(R.id.experience_point_ranking);
            this.rankingPosition = view.findViewById(R.id.ranking_position);
        }

        public void setPlayerRanking(Player playerRanking, int position) {
            this.playerProfilePicture.setImageBitmap(BitmapFactory.decodeByteArray(playerRanking.getImage(), 0, playerRanking.getImage().length));
            if (playerRanking.getUsername() == null) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_profile_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setPlayerRanking(this.playerList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }
}

