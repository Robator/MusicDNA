package com.example.harjot.musicstreamer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.sdsmdg.harjot.MusicDNA.Activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.Fragments.PlayerFragment.PlayerFragment;
import com.sdsmdg.harjot.MusicDNA.R;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by azatfanisovic on 27.03.17.
 */

public class PlayerFragmentTest extends ActivityInstrumentationTestCase2<HomeActivity> {

    public PlayerFragmentTest(){
        super(HomeActivity.class);
    }
    //we need HomeActivity because it uses PlayerFragment.
    HomeActivity activity;
    PlayerFragment fragment;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        fragment = new PlayerFragment();
    }

    //check if fragment is initialized correctly
    @Test
    public void testFragment(){
        startFragment(fragment);
        assertNotNull(fragment);
    }

    //checking if we succesfully retrived audioVisualization
    @Test
    public void testAudioVisualization(){
        AudioVisualization audioVisualization = (AudioVisualization) fragment.getView().findViewById(R.id.visualizer_view);
        assertNotNull(audioVisualization);
    }

    //check if activity initialized correctly
    @Test
    public void testActivity(){
        assertNotNull(activity);
    }

    //starting playerfragment in activity to check for audioVisualization
    private void startFragment( Fragment fragment ) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null );
        fragmentTransaction.commit();
    }
}
