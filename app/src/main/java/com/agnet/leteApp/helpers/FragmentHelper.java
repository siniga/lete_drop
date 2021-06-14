package com.agnet.leteApp.helpers;

import android.content.Context;

import com.agnet.leteApp.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;


/**
 * Created by KHAMIS on 6/15/2018.
 */

public class FragmentHelper {
    private FragmentActivity _context;

    public FragmentHelper(Context c){
        _context =  (FragmentActivity) c;
    }


    public void replaceWithbackStack(Fragment fragment, String tag, int view){
        //handle fragments
        // Begin the transaction
        FragmentTransaction ft = _context.getSupportFragmentManager().beginTransaction();

//        ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down);
        // Replace the contents of the container with the new fragment
        ft.replace(view, fragment, tag);

        ft.addToBackStack(tag);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();

    }

    public void replace(Fragment fragment, String tag, int view){
        //handle fragments
        // Begin the transaction
        FragmentTransaction ft = _context.getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(view, fragment, tag);

        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
    }

    public void replaceWithAnimSlideFromLeft(Fragment fragment, String tag, int view){
        FragmentTransaction transaction = _context.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(view, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void replaceWithAnimSlideFromRight(Fragment fragment, String tag, int view){
        FragmentTransaction transaction = _context.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(view, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }


}
