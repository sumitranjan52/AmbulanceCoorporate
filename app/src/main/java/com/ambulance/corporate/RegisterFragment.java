package com.ambulance.corporate;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.ambulance.corporate.Backend.Accounts;
import com.ambulance.corporate.Backend.FCMToken;
import com.ambulance.corporate.Backend.LocationToServer;
import com.ambulance.corporate.Common.AlertDialogBox;
import com.ambulance.corporate.Common.Common;
import com.ambulance.corporate.Common.NetworkErrorMessages;
import com.ambulance.corporate.Interfaces.AmbulanceDialogInterface;
import com.ambulance.corporate.Interfaces.VolleyJSONResponses;
import com.ambulance.corporate.Model.Driver;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private EditText name, username, email, phone, password, dl, vehicleNo;
    private ScrollView rootRegisterView;
    private Button btnCreate;

    private Context mContext;
    private Activity activity;

    private AlertDialog dialog;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        name = view.findViewById(R.id.txtName);
        username = view.findViewById(R.id.usernameRegister);
        email = view.findViewById(R.id.emailRegister);
        phone = view.findViewById(R.id.phoneRegister);
        password = view.findViewById(R.id.passwordRegister);
        dl = view.findViewById(R.id.drivingLicenceRegister);
        vehicleNo = view.findViewById(R.id.vehicleRegister);
        rootRegisterView = view.findViewById(R.id.rootRegisterView);
        btnCreate = view.findViewById(R.id.btnCreate);
        name.requestFocus();
        btnCreate.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        activity = (Activity) context;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCreate) {
            dialog = (new AlertDialogBox(mContext)).dialogBuilderWithoutAction("", "Registering. Please wait...", false);
            fnCreateANewAccount();
        }
    }

    private void fnCreateANewAccount() {

        String strName, strUsername, strEmail, strPhone, strPassword, strDl, strVehicleNo;
        strName = this.name.getText().toString();
        strUsername = this.username.getText().toString();
        strEmail = this.email.getText().toString();
        strPhone = this.phone.getText().toString();
        strPassword = this.password.getText().toString();
        strDl = this.dl.getText().toString();
        strVehicleNo = this.vehicleNo.getText().toString();

        /* Validation */
        if (TextUtils.isEmpty(strName) || TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strEmail)
                || TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strPassword) || TextUtils.isEmpty(strDl) || TextUtils.isEmpty(strVehicleNo)) {

            dialog.dismiss();
            (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Notice!"
                    , "Complete all fields and try again."
                    , true, "Ok"
                    , new AmbulanceDialogInterface() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        } else if (strPhone.length() < 10 || strPhone.length() > 10) {

            dialog.dismiss();
            (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Notice!"
                    , "Mobile number should be 10 digit long. It should not contain country code. Default is India (+91)"
                    , true, "Ok"
                    , new AmbulanceDialogInterface() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        } else if (strPassword.length() < 8) {

            dialog.dismiss();
            (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Notice!"
                    , "Password should be minimum of 8 characters."
                    , true, "Ok"
                    , new AmbulanceDialogInterface() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        } else {

            Driver driver = new Driver();
            driver.setName(strName);
            driver.setUsername(strUsername);
            driver.setEmail(strEmail);
            driver.setPhone(strPhone);
            driver.setPassword(strPassword);
            driver.setDl(strDl);
            driver.setVehicleNo(strVehicleNo);

            final Accounts newDriver = new Accounts(mContext, driver);
            newDriver.registerDriver(new VolleyJSONResponses() {
                @Override
                public void onSuccess(JSONObject response) {
                    dialog.dismiss();
                    try {

                        if (response.has("response")) {

                            JSONObject jsonObject = response.getJSONObject("response");
                            SharedPreferences sharedPreferences = mContext.getSharedPreferences("account", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId", jsonObject.getString("id"));
                            editor.putString("email", jsonObject.getJSONObject("details").getString("email"));
                            editor.putString("name", jsonObject.getJSONObject("details").getString("name"));
                            editor.putBoolean("auto-login", true);
                            editor.apply();

                            String userId = mContext.getSharedPreferences("account", MODE_PRIVATE).getString("userId", "");
                            String token = mContext.getSharedPreferences("firebase", MODE_PRIVATE).getString("fcmToken", "");
                            if (!userId.equals("") && !token.equals("")) {

                                new FCMToken(mContext, userId, token).sendTokenToServer(new VolleyJSONResponses() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Log.d("FCM_RESPONSE", response.toString());
                                        if (response.has("error")) {
                                            try {
                                                JSONArray errors = response.getJSONArray("error");
                                                for (int i = 0; i < errors.length(); i++) {
                                                    if (errors.getString(i).equals("EMPTY_USERID")) {
                                                        (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Error!"
                                                                , FCMToken.errorResponseMsg(errors.getString(i))
                                                                , true, "Ok"
                                                                , new AmbulanceDialogInterface() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                    } else if (errors.getString(i).equals("EMPTY_FCMTOKEN")) {
                                                        (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Error!"
                                                                , FCMToken.errorResponseMsg(errors.getString(i))
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
                                                        , FCMToken.errorResponseMsg(response.getString("invalid"))
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
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
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

                                new LocationToServer(userId, Common.latitude, Common.longitude, mContext).setLocationToServer(new VolleyJSONResponses() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Log.d("LOCATION_RESPONSE_S", response.toString());
                                        if (response.has("error")) {
                                            try {
                                                JSONArray errors = response.getJSONArray("error");
                                                for (int i = 0; i < errors.length(); i++) {
                                                    if (errors.getString(i).equals("EMPTY_DRIVER_ID")) {
                                                        (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Error!", LocationToServer.errorResponseMsg(errors.getString(i))
                                                                , true, "Ok"
                                                                , new AmbulanceDialogInterface() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                    } else if (errors.getString(i).equals("EMPTY_LATITUDE")) {
                                                        (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Error!", LocationToServer.errorResponseMsg(errors.getString(i))
                                                                , true, "Ok"
                                                                , new AmbulanceDialogInterface() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                    } else if (errors.getString(i).equals("EMPTY_LONGITUDE")) {
                                                        (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Error!", LocationToServer.errorResponseMsg(errors.getString(i))
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
                                        }
                                        if (response.has("invalid")) {
                                            try {
                                                (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                                                        , LocationToServer.errorResponseMsg(response.getString("invalid"))
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
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        Log.d("LOCATION_RESPONSE_R", error.toString());
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

                                Common.isLoggedIn = true;
                                startActivity(new Intent(mContext, MainActivity.class));
                                activity.finish();

                            }

                        } else if (response.has("error")) {

                            JSONArray jsonArray = response.getJSONArray("error");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                if (jsonArray.get(i).equals("EMPTY_NAME")) {

                                    name.setError(Accounts.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_USERNAME") || jsonArray.get(i).equals("USERNAME_ALREADY_REGISTERED")) {

                                    username.setError(Accounts.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_EMAIL") || jsonArray.get(i).equals("INVALID_EMAIL") || jsonArray.get(i).equals("EMAIL_ALREADY_REGISTERED")) {

                                    email.setError(Accounts.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_PASSWORD")) {

                                    password.setError(Accounts.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_PHONE") || jsonArray.get(i).equals("PHONE_ALREADY_REGISTERED")) {

                                    phone.setError(Accounts.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_DL") || jsonArray.get(i).equals("DL_ALREADY_REGISTERED")) {

                                    dl.setError(Accounts.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_VEHICLE_NUMBER") || jsonArray.get(i).equals("VEHICLE_NUMBER_ALREADY_REGISTERED")) {

                                    vehicleNo.setError(Accounts.errorMessages(jsonArray.getString(i)));

                                }

                            }

                        } else if (response.has("invalid")) {

                            (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                                    , Accounts.errorMessages(response.getString("invalid"))
                                    , true, "Ok"
                                    , new AmbulanceDialogInterface() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    dialog.dismiss();
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
        }
    }
}
