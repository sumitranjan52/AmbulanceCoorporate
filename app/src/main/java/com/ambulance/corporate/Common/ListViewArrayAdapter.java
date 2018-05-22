/*
 * Copyright (c) 2018. Sumit Ranjan
 */

package com.ambulance.corporate.Common;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ambulance.corporate.Interfaces.AmbulanceDialogInterface;
import com.ambulance.corporate.Model.RideBooking;
import com.ambulance.corporate.R;

import java.util.List;

/**
 * Created by sumit on 06-Apr-18.
 */

public class ListViewArrayAdapter extends ArrayAdapter {

    private int resource;
    private List<RideBooking> rideBookingList;
    private LayoutInflater inflater;

    public ListViewArrayAdapter(@NonNull Context context, int resource, @NonNull List<RideBooking> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.rideBookingList = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
        }
        if (rideBookingList == null) {
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
        } else {
            TextView id, riderName, fare, status, pickup, destination;
            id = convertView.findViewById(R.id.bookingId);
            riderName = convertView.findViewById(R.id.riderOrDriverName);
            fare = convertView.findViewById(R.id.fare);
            status = convertView.findViewById(R.id.bookingStatus);
            pickup = convertView.findViewById(R.id.pickup);
            destination = convertView.findViewById(R.id.destination);
            id.setText(rideBookingList.get(position).getId());
            riderName.setText(rideBookingList.get(position).getRiderName());
            fare.setText("₹ " + rideBookingList.get(position).getFare());
            if (rideBookingList.get(position).getStatus().equals("Cancelled")){
                status.setText(rideBookingList.get(position).getStatus());
                status.setTextColor(Color.RED);
            }else{
                status.setText(rideBookingList.get(position).getStatus());
                status.setTextColor(Color.rgb(85,139,47));
            }
            pickup.setText(rideBookingList.get(position).getPickupAddress());
            destination.setText(rideBookingList.get(position).getDestinationAddress());
        }
        return convertView;
    }
}
