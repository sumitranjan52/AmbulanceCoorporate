package com.ambulance.corporate;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ambulance.corporate.Backend.FareManagement;
import com.ambulance.corporate.Common.AlertDialogBox;
import com.ambulance.corporate.Common.Common;
import com.ambulance.corporate.Common.NetworkErrorMessages;
import com.ambulance.corporate.Interfaces.AmbulanceDialogInterface;
import com.ambulance.corporate.Interfaces.VolleyJSONResponses;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class FareFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private Activity activity;

    private TextView fareAmount;
    private LinearLayout fareSummary;
    private Button btnDone;

    private AlertDialog dialog;

    public FareFragment() {
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
        activity.setTitle("Total Fare");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fare, container, false);

        dialog = (new AlertDialogBox(mContext)).dialogBuilderWithoutAction("Calculating fare", "Please wait...", false);

        fareAmount = view.findViewById(R.id.fareAmount);
        fareSummary = view.findViewById(R.id.fareSummary);
        btnDone = view.findViewById(R.id.btnDone);

        btnDone.setOnClickListener(this);

        new FareManagement(mContext, Common.emergencyRequestBookingID).getFareFromServer(new VolleyJSONResponses() {
            @Override
            public void onSuccess(JSONObject response) {
                dialog.dismiss();
                if (response.has("response")) {

                    try {
                        JSONObject jsonObject = response.getJSONObject("response");
                        fareAmount.setText("₹" + jsonObject.getString("total"));

                        TextView base = new TextView(mContext);
                        TextView discount = new TextView(mContext);
                        TextView taxes = new TextView(mContext);
                        base.setText("Base fare: " + jsonObject.getString("base"));
                        if (!jsonObject.getString("discount").equals("0")) {
                            discount.setText("Discount: " + jsonObject.getString("discount"));
                        }
                        taxes.setText("Taxes: " + jsonObject.getString("taxes"));

                        fareSummary.addView(base);
                        fareSummary.addView(taxes);
                        if (!jsonObject.getString("discount").equals("0")) {
                            fareSummary.addView(discount);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d("TAG", "onSuccess: " + response.toString());
                }
            }

            @Override
            public void onError(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
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

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnDone:

                startActivity(new Intent(mContext, MainActivity.class));
                activity.finish();

                break;

        }
    }
}
