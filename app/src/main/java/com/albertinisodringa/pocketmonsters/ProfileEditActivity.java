package com.albertinisodringa.pocketmonsters;

import androidx.appcompat.app.AppCompatActivity;

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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * ProfileEditActivity lets the user to edit its name and uploading a new photo for his profile
 */
public class ProfileEditActivity extends AppCompatActivity {

    private static final String PROFILE_EDITED_SUCCESSFULLY_MESSAGE = "The profile has been edited successfully";
    private static final int IMAGE_MAX_BYTE_SIZE = 100000;
    private static final String IMAGE_MAX_BYTE_SIZE_MESSAGE = "Image has to be less than 100KB";
    private static final String NAME_LENGTH_LESS_THAN_CHARACTERS_MESSAGE = "Name has to be less than 16 characters";
    private static final int NAME_LENGTH_LESS_THAN_CHARACTERS = 16;

    private Bitmap profileImage = null;
    private boolean isProfileImageSet = false;
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    /**
     * The Profile image set image view.
     */
    ImageView profileImageSetImageView = null;
    /**
     * The Uploaded image view.
     */
    CircleImageView uploadedImageView = null;
    final private int REQUEST_CODE = 1; // Code useful for the result of the startActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit_activity);

        final String playerUsername = getIntent().getStringExtra("profileUsername");

        final EditText editText = findViewById(R.id.name_edit_text);
        if(playerUsername.equals("null")){
            editText.setText("No name");
        } else {
            editText.setText(playerUsername); // Set username from ProfileActivity that requested the profile from API
        }

        profileImageSetImageView = findViewById(R.id.profile_image_uploaded_flag_image_view);
        profileImageSetImageView.setVisibility(View.INVISIBLE);

        uploadedImageView = findViewById(R.id.uploaded_circle_image_view);
        uploadedImageView.setVisibility(View.INVISIBLE);

        Button button = findViewById(R.id.upload_new_picture_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens a Gallery fragment to choose the new profile image
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_CODE);
            }
        });

        findViewById(R.id.edit_profile_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SharedPreferences used for retreiving the sessionId
                final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences_key), Context.MODE_PRIVATE);

                // Get API model
                ApiHandler api = new ApiHandler(sharedPreferences.getString("sessionId", null), getString(R.string.api_url), getApplicationContext());

                // Convert Bitmap array stream to byte array if image is not null
                byte[] profileImageByteArray = null;
                if (isProfileImageSet && profileImage != null) {
                    profileImageByteArray = byteArrayOutputStream.toByteArray();
                }

                String editName = editText.getText().toString();

                if (editName.length() < NAME_LENGTH_LESS_THAN_CHARACTERS) {
                    // Set name and/or image profile by making a call to the API
                    api.setProfileAsync(editName, profileImageByteArray, new VolleyEventListener() {
                        @Override
                        public void onSuccess(Object returnFromCallback) {
                            // Go to the ProfileActivity and send an OK message
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.putExtra("Message", PROFILE_EDITED_SUCCESSFULLY_MESSAGE);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Exception error) {
                            ApiErrorHandler.handle(error, getApplicationContext());
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), NAME_LENGTH_LESS_THAN_CHARACTERS_MESSAGE, Toast.LENGTH_LONG).show();
                }
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

                // Compress image in a byte array output stream
                profileImage.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

                // If the compressed profile image size is greater than the max byte size, launch an error
                if (byteArrayOutputStream.size() < IMAGE_MAX_BYTE_SIZE) {
                    isProfileImageSet = true;
                    profileImageSetImageView.setVisibility(View.VISIBLE); // Set visibility to the tick

                    uploadedImageView.setImageBitmap(profileImage);
                    uploadedImageView.setVisibility(View.VISIBLE);
                } else {
                    isProfileImageSet = false;
                    profileImage = null;
                    profileImageSetImageView.setVisibility(View.INVISIBLE);
                    uploadedImageView.setVisibility(View.INVISIBLE);

                    Toast toast = Toast.makeText(getApplicationContext(), IMAGE_MAX_BYTE_SIZE_MESSAGE, Toast.LENGTH_SHORT);
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
