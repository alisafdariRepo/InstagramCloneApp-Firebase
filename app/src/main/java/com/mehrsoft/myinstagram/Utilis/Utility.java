package com.mehrsoft.myinstagram.Utilis;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.mehrsoft.myinstagram.R;

public class Utility {


    public static void navigateFragment(Context context, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


    public static class HawkKey {
        public static final String PROFILE_ID = "profiled";
        public static final String PUBLISHER = "publisher";
        public static final String POST_ID = "post_id";

    }



}
