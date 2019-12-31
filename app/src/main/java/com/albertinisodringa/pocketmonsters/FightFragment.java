package com.albertinisodringa.pocketmonsters;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class FightFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fight_fragment, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();

       /* String id = getArguments().getString(MainActivity.BUNDLE_MAPELEMENT_ID);
        //int map_element_id = Integer.parseInt(id);
        String map_element_name = getArguments().getString(MainActivity.BUNDLE_MAPELEMENT_NAME);
        TextView tv = getActivity().findViewById(R.id.fight_text);
        tv.setText("You have chosen to figth " + id + ".\n \nContinue?");
        Log.d("IDPORCODIO", id);
        Log.d("IDPORCODIONAME", map_element_name);

        ApiModel api = new ApiModel("v6LxCAWaIJGHoLxK", getString(R.string.api_url), getApplicationContext());

        api.getMapElementImageAsync(map_element_id, new VolleyEventListener() {
            @Override
            public void onSuccess(Object returnFromCallback) {
                byte[] map_element_img;
                if (returnFromCallback instanceof Byte[]){
                    map_element_img = (byte[])returnFromCallback;

                    ImageView map_element_picture = getActivity().findViewById(R.id.map_element_picture);
                    map_element_picture.setImageBitmap(BitmapFactory.decodeByteArray(map_element_img , 0, map_element_img.length));
                }
              }

            @Override
            public void onFailure(Exception error) {

            }
        });*/


    }

}
