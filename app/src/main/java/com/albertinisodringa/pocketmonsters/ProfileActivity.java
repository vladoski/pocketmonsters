package com.albertinisodringa.pocketmonsters;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences_key), Context.MODE_PRIVATE);

        ApiModel api = new ApiModel(sharedPreferences.getString("sessionId", null), getString(R.string.api_url), getApplicationContext());

        Log.d("VLADO", sharedPreferences.getString("sessionId", null));

        // API request for the player profile
        api.getProfileAsync(new VolleyEventListener() {
            @Override
            public void onSuccess(Object returnFromCallback) {
                Player player = new Player();
                if (returnFromCallback instanceof Player) {
                    player = (Player) returnFromCallback;
                }

                final String playerUsername = player.getUsername(); // Used because the final requirement inside the anonymous class

                // Set name on TextView
                TextView nameTextView = findViewById(R.id.name);
                nameTextView.setText(player.getUsername() == null ? "No name" : player.getUsername()); // Writes no name if the name is null (not set)

                // Set profile image on ImageView
                CircleImageView profileImageView = findViewById(R.id.uploadedImageView);
                Bitmap profileImageBitmap = BitmapFactory.decodeByteArray(player.getImage(), 0, player.getImage().length);

                // If the profileImage is not a valid Bitmap, then display default profile image
                if (profileImageBitmap == null) {
                    profileImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.profile_default));
                } else {
                    // Display original image from the API
                    profileImageView.setImageBitmap(profileImageBitmap);
                }

                // Set life points on TextView
                TextView lifePointsTextView = findViewById(R.id.life_point);
                lifePointsTextView.setText(player.getLifePoints() + " LP");

                // Set experience points on TextView
                TextView experiencePointsTextView = findViewById(R.id.experience_point);
                experiencePointsTextView.setText(player.getExperiencePoints() + " XP");

                // Image used as button for going to the ProfileEditActivity
                ImageView editImageView = findViewById(R.id.editImageView);

                editImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ProfileEditActivity.class);
                        intent.putExtra("profileUsername", playerUsername);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Exception error) {
                ApiModelErrorHandler.handle(error, getApplicationContext());
            }
        });
    }
}
