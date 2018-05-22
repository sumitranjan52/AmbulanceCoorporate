/*
 * Copyright (c) 2018. Sumit Ranjan
 */

package com.ambulance.corporate;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ambulance.corporate.Backend.RideRequest;
import com.ambulance.corporate.Common.AlertDialogBox;
import com.ambulance.corporate.Common.ListViewArrayAdapter;
import com.ambulance.corporate.Common.NetworkErrorMessages;
import com.ambulance.corporate.Interfaces.AmbulanceDialogInterface;
import com.ambulance.corporate.Interfaces.VolleyJSONResponses;
import com.ambulance.corporate.Model.RideBooking;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookingHistoryFragment extends Fragment {

    private Context mContext;
    private Activity activity;

    private ListView bookingHistory;
    private SwipeRefreshLayout swipeRefreshLayout;

    private AlertDialog dialog;

    private View view;

    MenuItem total;

    public BookingHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        activity.setTitle("Booking History");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_booking_history, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refreshSwipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBookingsFromServer();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        getBookingsFromServer();
        return view;
    }

    private void getBookingsFromServer() {

        dialog = (new AlertDialogBox(mContext)).dialogBuilderWithoutAction("Fetching", "Please wait...", false);
        final List<RideBooking> rides = new ArrayList<>();
        rides.clear();
        String driverId = mContext.getSharedPreferences("account", Context.MODE_PRIVATE).getString("userId", "");
        Log.d("driverId", driverId);
        RideRequest request = new RideRequest();
        request.setContext(mContext);
        request.setDriverId(driverId);

        request.getRideDetailFromServer(new VolleyJSONResponses() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.has("response")) {

                    try {
                        JSONArray rideArray = response.getJSONArray("response");
                        long totalFare = 0;
                        for (int i = 0; i < rideArray.length(); i++) {
                            RideBooking booking = new RideBooking();
                            booking.setId(rideArray.getJSONObject(i).getString("_id"));
                            long fare = Math.round(rideArray.getJSONObject(i).getDouble("fare"));
                            totalFare += fare;
                            booking.setFare(fare);
                            booking.setRiderName(rideArray.getJSONObject(i).getString("name"));
                            if (rideArray.getJSONObject(i).getString("status").equals("cancelledByRider")
                                    || rideArray.getJSONObject(i).getString("status").equals("cancelledByDriver")) {
                                booking.setStatus("Cancelled");
                            } else {
                                booking.setStatus(rideArray.getJSONObject(i).getString("status"));
                            }
                            booking.setPickupAddress(rideArray.getJSONObject(i).getString("pickup"));
                            booking.setDestinationAddress(rideArray.getJSONObject(i).getString("destination"));
                            rides.add(booking);
                        }
                        total.setTitle("₹ " + totalFare);
                        if (rides.size() > 0) {
                            bookingHistory = view.findViewById(R.id.bookingHistory);
                            ListViewArrayAdapter adapter = new ListViewArrayAdapter(mContext, R.layout.booking_history_list_layout, rides);
                            bookingHistory.setAdapter(adapter);
                        } else {
                            (new AlertDialogBox(getContext())).dialogBuilderWithSingleAction(
                                    "No record!"
                                    , "No Booking(s) are made to you."
                                    , true
                                    , "Ok"
                                    , new AmbulanceDialogInterface() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }
                            );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (response.has("error")) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("error");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (jsonArray.getString(i).equals("EMPTY_DRIVER_ID")) {
                                (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                                        , RideRequest.errorResponseMsg(jsonArray.getString(i))
                                        , true, "Ok"
                                        , new AmbulanceDialogInterface() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (response.has("invalid")) {
                    try {
                        (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                                , RideRequest.errorResponseMsg(response.getString("invalid"))
                                , true, "Ok"
                                , new AmbulanceDialogInterface() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                dialog.dismiss();
                Log.d("BOOKHIS", error.toString());
                try {
                    (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                            , NetworkErrorMessages.networkErrorMsg(error.networkResponse.statusCode)
                            , true, "Ok"
                            , new AmbulanceDialogInterface() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fare_summary, menu);
        total = menu.findItem(R.id.fareSummaryMenu);
    }
}
