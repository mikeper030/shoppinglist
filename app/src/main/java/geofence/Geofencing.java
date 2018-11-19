package geofence;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Geofencing implements ResultCallback {

    // Constants
    public static final String TAG = Geofencing.class.getSimpleName();
    private static final float GEOFENCE_RADIUS = 50; // 50 meters
    private static final long GEOFENCE_TIMEOUT = 168 * 60 * 60 * 1000; // 24 hours
    private GeofencingClient mGeofencingClient;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private Activity mActivity;
    public Geofencing(Context context, GoogleApiClient client, Activity activity) {
        mContext = context;
        mGoogleApiClient = client;
        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<>();
        mActivity=activity;
        mGeofencingClient = LocationServices.getGeofencingClient(mContext);
    }

    /***
     * Registers the list of Geofences specified in mGeofenceList with Google Place Services
     * Uses {@code #mGoogleApiClient} to connect to Google Place Services
     * Uses {@link #getGeofencingRequest} to get the list of Geofences to be registered
     * Uses {@link #getGeofencePendingIntent} to get the pending intent to launch the IntentService
     * when the Geofence is triggered
     * Triggers {@link #onResult} when the geofences have been registered successfully
     */
    public void registerAllGeofences() {


        // Check that the API client is connected and that the list has Geofences in it
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected() ||
                mGeofenceList == null || mGeofenceList.size() == 0) {
            return;
        }
        try {
        mGeofencingClient.addGeofences(getGeofencingRequest(),getGeofencePendingIntent(null))
                .addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                     Log.d("geofences ","added successfully");
//                     Log.d("task",task.getResult().toString());
//                     Log.d("TASK",task.getException().toString());
                    }
                })
                .addOnFailureListener(mActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String v = e.getMessage();
                        Log.d("geofences fail",v);
                    }
                });
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, securityException.getMessage());
        }
    }

    /***
     * Unregisters all the Geofences created by this app from Google Place Services
     * Uses {@code #mGoogleApiClient} to connect to Google Place Services
     * Uses {@link #getGeofencePendingIntent} to get the pending intent passed when
     * registering the Geofences in the first place
     * Triggers {@link #onResult} when the geofences have been unregistered successfully
     */
    public void unRegisterAllGeofences() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            return;
        }
        try {
            mGeofencingClient.removeGeofences(getGeofencePendingIntent(null))
                    .addOnSuccessListener(mActivity, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                 Log.d("removed","all geofences");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, securityException.getMessage());
        }
    }
    public void deleteGeofence(String placeID){
      List<String> list= new ArrayList<>();
      list.add(placeID);
        mGeofencingClient.removeGeofences(list).addOnFailureListener(mActivity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("remove","failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("remove","success");
            }
        });
    }

    /***
     * Updates the local ArrayList of Geofences using data from the passed in list
     * Uses the Place ID defined by the API as the Geofence object Id
     *
     * @param places the PlaceBuffer result of the getPlaceById call
     */
    public void updateGeofencesList(PlaceBuffer places,ArrayList<String> name) {

        String temp=name.toString();
                temp=temp.replace("]"," ").replace("["," ");
        mGeofenceList = new ArrayList<>();
        if (places == null || places.getCount() == 0) return;
        for (Place place : places) {
            // Read the place information from the DB cursor
            String placeUID = place.getId();
            double placeLat = place.getLatLng().latitude;
            double placeLng = place.getLatLng().longitude;
            // Build a Geofence object
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(temp)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(placeLat, placeLng, GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            // Add it to the list
            mGeofenceList.add(geofence);
        }
    }

    /***
     * Creates a GeofencingRequest object using the mGeofenceList ArrayList of Geofences
     * Used by {@code #registerGeofences}
     *
     * @return the GeofencingRequest object
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    /***
     * Creates a PendingIntent object using the GeofenceTransitionsIntentService class
     * Used by {@code #registerGeofences}
     *
     * @return the PendingIntent object
     */
    private PendingIntent getGeofencePendingIntent(String name) {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        if(name!=null) {
            intent.putExtra("name", name);
        }
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);

        return mGeofencePendingIntent;
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG, String.format("Error adding/removing geofence : %s",
                result.getStatus().toString()));
    }

}



