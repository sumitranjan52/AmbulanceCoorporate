package com.ambulance.corporate;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

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
public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextView forgotLink, createAccountLink;
    private Button btnLogin;
    private EditText username, password;
    private ScrollView rootLoginView;

    private Context mContext;
    private Activity activity;

    private AlertDialog dialog;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        username = view.findViewById(R.id.usernameLogin);
        password = view.findViewById(R.id.passwordLogin);
        btnLogin = view.findViewById(R.id.btnLogin);
        forgotLink = view.findViewById(R.id.forgetLink);
        createAccountLink = view.findViewById(R.id.createAccountLink);
        rootLoginView = view.findViewById(R.id.rootLoginView);
        username.requestFocus();

        btnLogin.setOnClickListener(this);
        createAccountLink.setOnClickListener(this);
        forgotLink.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.btnLogin:
                dialog = (new AlertDialogBox(mContext)).dialogBuilderWithoutAction("", "Signing in. Please wait...", false);
                fnLogin();
                break;

            case R.id.createAccountLink:
                DefaultActivity.fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left
                                , android.R.anim.slide_out_right
                                , android.R.anim.slide_in_left
                                , android.R.anim.slide_out_right)
                        .addToBackStack("ambulance")
                        .replace(R.id.fragment_container, new RegisterFragment())
                        .commit();
                break;

            case R.id.forgetLink:
                DefaultActivity.fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left
                                , android.R.anim.slide_out_right
                                , android.R.anim.slide_in_left
                                , android.R.anim.slide_out_right)
                        .addToBackStack("ambulance")
                        .replace(R.id.fragment_container, new ForgotPasswordFragment())
                        .commit();
                break;
        }
    }

    private void fnLogin() {

        String strUsername, strPassword;
        strUsername = username.getText().toString();
        strPassword = password.getText().toString();

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strPassword)) {

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
            driver.setUsername(strUsername);
            driver.setPassword(strPassword);

            final Accounts oldDriver = new Accounts(mContext, driver);
            oldDriver.loginDriver(new VolleyJSONResponses() {
                @Override
                public void onSuccess(JSONObject response) {

                    dialog.dismiss();
                    try {

                        if (response.has("response")) {

                            JSONObject jsonObject = response.getJSONObject("response").getJSONObject("response");
                            SharedPreferences sharedPreferences = mContext.getSharedPreferences("account", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId", jsonObject.getString("_id"));
                            editor.putString("email", jsonObject.getString("email"));
                            editor.putString("name", jsonObject.getString("name"));
                            editor.putBoolean("auto-login", true);
                            editor.apply();

                            String userId = mContext.getSharedPreferences("account",MODE_PRIVATE).getString("userId","");
                            String token = mContext.getSharedPreferences("firebase",MODE_PRIVATE).getString("fcmToken","");
                            if (!userId.equals("") && !token.equals("")){

                                new FCMToken(mContext,userId,token).sendTokenToServer(new VolleyJSONResponses() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Log.d("FCM_RESPONSE",response.toString());
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
                                        Log.d("FCM_RESPONSE",error.toString());
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

                                new LocationToServer(userId,Common.latitude,Common.longitude,mContext).setLocationToServer(new VolleyJSONResponses() {
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
                                        Log.d("LOCATION_RESPONSE_L",error.toString());
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

                            Common.isLoggedIn = true;
                            startActivity(new Intent(mContext,MainActivity.class));
                            activity.finish();

                        } else if (response.has("error")) {

                            JSONArray jsonArray = response.getJSONArray("error");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                if (jsonArray.get(i).equals("EMPTY_USERNAME")) {

                                    username.setError(Accounts.errorMessages(jsonArray.getString(i)));

                                } else if (jsonArray.get(i).equals("EMPTY_PASSWORD")) {

                                    password.setError(Accounts.errorMessages(jsonArray.getString(i)));

                                }

                            }

                        } else if (response.has("invalid")) {

                            (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                                    , LocationToServer.errorResponseMsg(response.getString("invalid"))
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
                    Log.d("LOGIN",error.toString());
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
