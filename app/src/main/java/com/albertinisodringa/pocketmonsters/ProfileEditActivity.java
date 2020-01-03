package com.albertinisodringa.pocketmonsters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.BitmapCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileEditActivity extends AppCompatActivity {

    public static final String PROFILE_EDITED_SUCCESFULLY_MESSAGE = "The profile has been edited successfully";
    public static final String IMAGE_LESS_THAN_100KB_MESSAGE = "Image has to be less thank 100KB";

    private Bitmap profileImage = null;
    private boolean isProfileImageSet = false;
    ImageView profileImageSetImageView = null;
    ImageView uploadedImageView = null;
    final private int REQUEST_CODE = 1; // Code useful for the result of the startActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit_activity);

        final EditText editText = findViewById(R.id.nameEditText);
        editText.setText(getIntent().getStringExtra("profileUsername")); // Set username from ProfileActivity that requested the profile from API

        profileImageSetImageView = findViewById(R.id.profileImageSetImageView);
        profileImageSetImageView.setVisibility(View.INVISIBLE);

        uploadedImageView = findViewById(R.id.uploadedImageView);
        uploadedImageView.setVisibility(View.INVISIBLE);

        // TODO: change button with an ImageView
        Button button = findViewById(R.id.uploadNewPictureButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens a Gallery fragment to choose the new profile image
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_CODE);
            }
        });

        // TODO: change button with an ImageView
        findViewById(R.id.editProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SharedPreferences used for retreiving the sessionId
                final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences_key), Context.MODE_PRIVATE);

                // Get API model
                ApiModel api = new ApiModel(sharedPreferences.getString("sessionId", null), getString(R.string.api_url), getApplicationContext());


                // Convert Bitmap to byte array if image is not null
                byte[] profileImageByteArray = null;
                if (isProfileImageSet && profileImage != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    profileImage.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                    profileImageByteArray = byteArrayOutputStream.toByteArray();
                }

                // Set name and/or image profile by making a call to the API
                api.setProfileAsync(editText.getText().toString(), profileImageByteArray, new VolleyEventListener() {
                    @Override
                    public void onSuccess(Object returnFromCallback) {
                        // Go to the ProfileActivity and send an OK message
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("Message", PROFILE_EDITED_SUCCESFULLY_MESSAGE);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Exception error) {
                        ApiModelErrorHandler.handle(error, getApplicationContext());
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();

            try {
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                profileImage = BitmapFactory.decodeStream(imageStream);

                // If the profile image size is greater than 100KB, launch an error
                int profileImageSize = BitmapCompat.getAllocationByteCount(profileImage);
                if (profileImageSize < 100000) {
                    isProfileImageSet = true;
                    profileImageSetImageView.setVisibility(View.VISIBLE); // Set visibility to the tick


                    uploadedImageView.setImageBitmap(profileImage);
                    uploadedImageView.setVisibility(View.VISIBLE);
                } else {
                    isProfileImageSet = false;
                    profileImage = null;
                    profileImageSetImageView.setVisibility(View.INVISIBLE);
                    uploadedImageView.setVisibility(View.INVISIBLE);

                    Toast toast = Toast.makeText(getApplicationContext(), IMAGE_LESS_THAN_100KB_MESSAGE, Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (FileNotFoundException e) {
                // TODO: handle this exception in a better way
                Log.d("ProfileEditActivity", e.getMessage());
            }

        } else {
            Log.d("ProfileEditActivity", "resultCode NOT RESULT_OK");
        }
    }
}
