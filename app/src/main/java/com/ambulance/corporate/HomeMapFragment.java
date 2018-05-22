package com.ambulance.corporate;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ambulance.corporate.Backend.LocationToServer;
import com.ambulance.corporate.Common.AlertDialogBox;
import com.ambulance.corporate.Common.Common;
import com.ambulance.corporate.Common.NetworkErrorMessages;
import com.ambulance.corporate.Interfaces.AmbulanceDialogInterface;
import com.ambulance.corporate.Interfaces.VolleyJSONResponses;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap mMap;

    private boolean locationOn = true;
    private boolean receiverRegistered = false;

    private BroadcastReceiver broadcastReceiver;

    private Context mContext;
    private Activity activity;
    private FloatingActionButton fab;


    public HomeMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        activity.setTitle("Ambulance");
        Log.d("MAP_FRAG","onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("MAP_FRAG","onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_map, container, false);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = mContext.getSharedPreferences("account", Context.MODE_PRIVATE).getString("userId","");
                if (!locationOn){
                    if (!receiverRegistered){
                        mContext.registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
                        receiverRegistered = true;
                    }
                    locationOn = true;
                    fab.setImageResource(R.drawable.ic_location_off_white_24dp);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Common.latitude,Common.longitude))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_red)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.latitude,Common.longitude),15.0f));

                    new LocationToServer(userId,Common.latitude,Common.longitude,mContext).setLocationToServer(new VolleyJSONResponses() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d("LOCATION_RESPONSE_S",response.toString());
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Log.d("LOCATION_RESPONSE_S",error.toString());
                            try{
                                (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                                        , NetworkErrorMessages.networkErrorMsg(error.networkResponse.statusCode)
                                        , true, "Ok"
                                        , new AmbulanceDialogInterface() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }else{
                    locationOn = false;
                    fab.setImageResource(R.drawable.ic_location_on_white_24dp);
                    mMap.clear();

                    new LocationToServer(userId,mContext).deleteLocationFromServer(new VolleyJSONResponses() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d("LOCATION_RESPONSE_F",response.toString());
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Log.d("LOCATION_RESPONSE_F",error.toString());
                            try{
                                (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                                        , NetworkErrorMessages.networkErrorMsg(error.networkResponse.statusCode)
                                        , true, "Ok"
                                        , new AmbulanceDialogInterface() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    if (broadcastReceiver != null) {
                        if (receiverRegistered){
                            mContext.unregisterReceiver(broadcastReceiver);
                            receiverRegistered = false;
                        }
                    }
                }
            }
        });

        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return view;
    }

    /**
     * Manipulates the main once available.
     * This callback is triggered when the main is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAP_FRAG","MapCallback");

        mMap = googleMap;

        Log.d("MAP",mMap.toString());

        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.addMarker(new MarkerOptions().position(new LatLng(Common.latitude,Common.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_red)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.latitude,Common.longitude),15.0f));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("MAP_FRAG","onAttach");
        mContext = context;
        activity = (Activity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MAP_FRAG","onResume");
        mMapView.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        Common.latitude = intent.getExtras().getDouble("lat");
                        Common.longitude = intent.getExtras().getDouble("lng");
                        String dId = mContext.getSharedPreferences("account", Context.MODE_PRIVATE).getString("userId","");
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Common.latitude,Common.longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_red)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.latitude,Common.longitude),15.0f));
                        new LocationToServer(dId,Common.latitude,Common.longitude,mContext).updateLocationToServer(new VolleyJSONResponses() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                Log.d("LOCATION_RESPONSE_HMF",response.toString());
                            }

                            @Override
                            public void onError(VolleyError error) {
                                Log.d("LOCATION_RESPONSE_HMF",error.toString());
                                try{
                                    (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                                            , NetworkErrorMessages.networkErrorMsg(error.networkResponse.statusCode)
                                            , true, "Ok"
                                            , new AmbulanceDialogInterface() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        if (!receiverRegistered){
            mContext.registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
            receiverRegistered = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MAP_FRAG","onPause");
        mMapView.onPause();
        if (broadcastReceiver != null) {
            if (receiverRegistered){
                mContext.unregisterReceiver(broadcastReceiver);
                receiverRegistered = false;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MAP_FRAG","onDestroy");
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("MAP_FRAG","onLowMemory");
        mMapView.onLowMemory();
    }
}
