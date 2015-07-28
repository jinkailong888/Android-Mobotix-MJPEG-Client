package me.hoen.mobotixclient;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {
    public static final String TAG = "me.example";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(me.hoen.mobotixclient.R.layout.main);

        Fragment f = new LoginFragment();

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .add(me.hoen.mobotixclient.R.id.content, f)
                .addToBackStack("login")
                .commit();
        fm.executePendingTransactions();
    }

}
