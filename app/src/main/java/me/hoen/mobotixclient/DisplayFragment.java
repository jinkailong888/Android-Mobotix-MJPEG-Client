package me.hoen.mobotixclient;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class DisplayFragment extends Fragment {
    protected MjpegView mv;

    protected String username;
    protected String password;
    protected String host;

    protected OnTaskCompleted authenticationErrorCallback = new OnTaskCompleted() {
        @Override
        public void onTaskCompleted(MjpegInputStream result) {
            closeFragment();
        }
    };

    protected OnTaskCompleted loadStreamCallback = new OnTaskCompleted() {
        @Override
        public void onTaskCompleted(MjpegInputStream result) {
            mv.setSource(result);
            mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mv.showFps(true);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            username = getArguments().getString("username");
            password = getArguments().getString("password");
            host = getArguments().getString("host");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(me.hoen.mobotixclient.R.layout.fragment_display, container, false);

        String fullUrl;

        /*
        username = null;
        password = null;
        fullUrl = "http://trackfield.webcam.oregonstate.edu/axis-cgi/mjpg/video.cgi?resolution=800x600&amp%3bdummy=1333689998337";
        */

        fullUrl = "http://" + host + getString(R.string.flux_path);

        mv = (MjpegView) rootView.findViewById(me.hoen.mobotixclient.R.id.mv);

        new LoadCameraTask(loadStreamCallback, authenticationErrorCallback, username, password).execute(fullUrl);

        return rootView;
    }

    public void onPause() {
        super.onPause();
        mv.stopPlayback();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(me.hoen.mobotixclient.R.menu.menu_display, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case me.hoen.mobotixclient.R.id.action_back_to_login:
                closeFragment();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    protected void closeFragment(){
        getActivity().getFragmentManager().popBackStack();
    }
}
