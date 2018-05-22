package com.ambulance.corporate;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.ambulance.corporate.Backend.Accounts;
import com.ambulance.corporate.Common.AlertDialogBox;
import com.ambulance.corporate.Common.NetworkErrorMessages;
import com.ambulance.corporate.Interfaces.AmbulanceDialogInterface;
import com.ambulance.corporate.Interfaces.VolleyJSONResponses;
import com.ambulance.corporate.Model.Driver;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    private Button btnForgot;
    private EditText usernameForgot;
    private ScrollView rootForgotPasswordView;

    private Context mContext;

    private AlertDialog dialog;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        btnForgot = view.findViewById(R.id.btnForgot);
        usernameForgot = view.findViewById(R.id.usernameForgot);
        rootForgotPasswordView = view.findViewById(R.id.rootForgotPasswordView);
        usernameForgot.requestFocus();

        btnForgot.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnForgot){
            dialog = (new AlertDialogBox(mContext)).dialogBuilderWithoutAction("", "Please wait...", false);
            fnForgot();
            usernameForgot.setText("");
        }
    }

    private void fnForgot() {

        String strUsername;
        strUsername = usernameForgot.getText().toString();

        if (TextUtils.isEmpty(strUsername)) {

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

        } else {

            Driver driver = new Driver();
            driver.setUsername(strUsername);

            final Accounts oldDriver = new Accounts(mContext, driver);
            oldDriver.resetPassword(new VolleyJSONResponses() {
                @Override
                public void onSuccess(JSONObject response) {

                    dialog.dismiss();
                    try {

                        if (response.has("response")) {

                            (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Congratulations!"
                                    , Accounts.errorMessages(response.getString("response"))
                                    , true, "Ok"
                                    , new AmbulanceDialogInterface() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                        } else if (response.has("error")) {

                            JSONArray jsonArray = response.getJSONArray("error");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                if (jsonArray.get(i).equals("EMPTY_USERNAME")) {

                                    usernameForgot.setError(Accounts.errorMessages(jsonArray.getString(i)));

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
