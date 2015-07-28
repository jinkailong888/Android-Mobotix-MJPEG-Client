package me.hoen.mobotixclient;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {
    protected EditText usernameEt;
    protected EditText passwordEt;
    protected EditText hostEt;

    protected boolean isDebuggable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isDebuggable = (0 != (getActivity().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEt = (EditText) rootView.findViewById(R.id.username);
        passwordEt = (EditText) rootView.findViewById(R.id.password);
        hostEt = (EditText) rootView.findViewById(R.id.host);

        /*
        if (isDebuggable) {
            usernameEt.setText(getString(R.string.default_username));
            passwordEt.setText(getString(R.string.default_password));
            hostEt.setText(getString(R.string.default_host));
        }
        */


        Button displayBt = (Button) rootView.findViewById(me.hoen.mobotixclient.R.id.display);
        displayBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    Fragment f = new DisplayFragment();
                    Bundle b = new Bundle();
                    b.putString("username", usernameEt.getText().toString());
                    b.putString("password", passwordEt.getText().toString());
                    b.putString("host", hostEt.getText().toString());
                    f.setArguments(b);

                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction()
                            .replace(me.hoen.mobotixclient.R.id.content, f)
                            .addToBackStack("display")
                            .commit();
                    fm.executePendingTransactions();
                }
            }
        });

        return rootView;
    }

    protected boolean isFormValid() {
        boolean isValid = true;

        if (TextUtils.isEmpty(usernameEt.getText().toString())) {
            usernameEt.setError(getString(me.hoen.mobotixclient.R.string.empty_field));
            isValid = false;
        }

        if (TextUtils.isEmpty(passwordEt.getText().toString())) {
            passwordEt.setError(getString(me.hoen.mobotixclient.R.string.empty_field));
            isValid = false;
        }

        if (TextUtils.isEmpty(hostEt.getText().toString())) {
            hostEt.setError(getString(me.hoen.mobotixclient.R.string.empty_field));
            isValid = false;
        }

        return isValid;
    }

}
