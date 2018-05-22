package com.ambulance.corporate;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ambulance.corporate.Backend.Accounts;
import com.ambulance.corporate.Common.AlertDialogBox;
import com.ambulance.corporate.Common.NetworkErrorMessages;
import com.ambulance.corporate.Interfaces.AmbulanceDialogInterface;
import com.ambulance.corporate.Interfaces.VolleyJSONResponses;
import com.ambulance.corporate.Model.Driver;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class DriverAccountFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private Activity activity;

    private TextView username, dl, vehicle;
    private EditText name, email, phone, password;
    private Button btnEdit, btnUpdate, btnCancel;

    public DriverAccountFragment() {
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
        activity.setTitle("Your Account");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_account, container, false);

        username = view.findViewById(R.id.username);
        dl = view.findViewById(R.id.drivingLicence);
        vehicle = view.findViewById(R.id.vehicleNo);
        name = view.findViewById(R.id.edtName);
        email = view.findViewById(R.id.edtEmail);
        phone = view.findViewById(R.id.edtPhone);
        password = view.findViewById(R.id.edtPassword);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnEdit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        String driverId = mContext.getSharedPreferences("account",Context.MODE_PRIVATE).getString("userId","");
        new Accounts(mContext).checkUserId(driverId, new VolleyJSONResponses() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.has("response")){
                    try {
                        JSONObject jsonObject = response.getJSONObject("response").getJSONObject("response");
                        username.setText(jsonObject.getString("username"));
                        name.setText(jsonObject.getString("name"));
                        email.setText(jsonObject.getString("email"));
                        phone.setText(jsonObject.getString("phone"));
                        dl.setText(jsonObject.getString("driving-licence"));
                        vehicle.setText(jsonObject.getString("vehicle-number"));
                        password.setText("********");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Log.d("TAG", "onSuccess: "+response.toString());
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
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

        return view;
    }

    private void visibleBtn(Button btn){
        btn.setVisibility(View.VISIBLE);
    }
    private void goneBtn(Button btn){
        btn.setVisibility(View.GONE);
    }

    private void enableEditText(EditText editText){
        editText.setEnabled(true);
    }
    private void disableEditText(EditText editText){
        editText.setEnabled(false);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnUpdate:

                btnUpdate.setEnabled(false);

                String Name = name.getText().toString();
                String UserName = username.getText().toString();
                String Password = password.getText().toString();
                String Email = email.getText().toString();
                String Phone = phone.getText().toString();

                if (Name.length() > 3 && (Password.length() == 0 || Password.length() > 7) && Email.length() > 5 && Phone.length() == 10){
                    Driver driver = new Driver(Name,UserName,Password,Email,Phone,null,null);
                    new Accounts(mContext,driver).updateDriverDetails(new VolleyJSONResponses() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            if (response.has("response")){
                                goneBtn(btnCancel);
                                visibleBtn(btnEdit);
                                btnUpdate.setEnabled(true);
                                goneBtn(btnUpdate);

                                disableEditText(name);
                                disableEditText(email);
                                disableEditText(phone);
                                disableEditText(password);
                                password.setText("********");
                            }else{
                                Log.d("TAG", "onSuccess: "+response.toString());
                                btnUpdate.setEnabled(true);
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            btnUpdate.setEnabled(true);
                            error.printStackTrace();
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
                    try{
                        (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                                , "Fields should be of appropriate length."
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

                break;

            case R.id.btnEdit:

                goneBtn(btnEdit);
                visibleBtn(btnCancel);
                visibleBtn(btnUpdate);

                enableEditText(name);
                enableEditText(email);
                enableEditText(phone);
                enableEditText(password);
                password.setText("");

                break;

            case R.id.btnCancel:

                goneBtn(btnCancel);
                visibleBtn(btnEdit);
                goneBtn(btnUpdate);

                disableEditText(name);
                disableEditText(email);
                disableEditText(phone);
                disableEditText(password);
                password.setText("********");

                break;

        }

    }
}
