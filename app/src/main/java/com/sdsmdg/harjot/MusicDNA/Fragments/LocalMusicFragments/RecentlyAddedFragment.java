package com.sdsmdg.harjot.MusicDNA.Fragments.LocalMusicFragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sdsmdg.harjot.MusicDNA.Activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.CustomBottomSheetDialogs.CustomLocalBottomSheetDialog;
import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;
import com.sdsmdg.harjot.MusicDNA.MusicDNAApplication;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.Utilities.CommonUtils;
import com.sdsmdg.harjot.MusicDNA.Utilities.Comparators.RecentlyAddedComparator;
import com.squareup.leakcanary.RefWatcher;

import java.util.Collections;

/**
 * Created by azatfanisovic on 16.03.17.
 */

public class RecentlyAddedFragment extends Fragment {

    RecentlyAddedAdapter adapter;
    LocalMusicFragment.OnLocalTrackSelectedListener mCallback;
    Context ctx;
    RecyclerView lv;
    LinearLayoutManager mLayoutManager2;
    HomeActivity activity;
    View bottomMarginLayout;

    public RecentlyAddedFragment() {
        // Required empty public constructor
    }

    public interface OnLocalTrackSelectedListener {
        void onLocalTrackSelected(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (LocalMusicFragment.OnLocalTrackSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
        ctx = context;
        activity = (HomeActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = CommonUtils.dpTopx(65, getContext());

        Collections.sort(HomeActivity.localTrackList, new RecentlyAddedComparator());
        Collections.sort(HomeActivity.finalLocalSearchResultList, new RecentlyAddedComparator());

        lv = (RecyclerView) view.findViewById(R.id.localMusicList);
        adapter = new RecentlyAddedAdapter(HomeActivity.finalLocalSearchResultList, getContext());
        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        lv.setLayoutManager(mLayoutManager2);
        lv.setItemAnimator(new DefaultItemAnimator());
        lv.setAdapter(adapter);

        lv.addOnItemTouchListener(new com.sdsmdg.harjot.MusicDNA.ClickItemTouchListener.ClickItemTouchListener(lv) {
            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {

                if (position >= 0) {
                    HomeActivity.queue.getQueue().clear();
                    for (int i = 0; i < HomeActivity.localTrackList.size(); i++) {
                        UnifiedTrack ut = new UnifiedTrack(true, HomeActivity.localTrackList.get(i), null);
                        HomeActivity.queue.getQueue().add(ut);
                    }
                    HomeActivity.queueCurrentIndex = getPosition(HomeActivity.finalLocalSearchResultList.get(position));
                    LocalTrack track = HomeActivity.finalLocalSearchResultList.get(position);
                    HomeActivity.localSelectedTrack = track;
                    HomeActivity.streamSelected = false;
                    HomeActivity.localSelected = true;
                    HomeActivity.queueCall = false;
                    HomeActivity.isReloaded = false;
                    mCallback.onLocalTrackSelected(-1);
                }

                return true;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                if (position >= 0) {
                    CustomLocalBottomSheetDialog localBottomSheetDialog = new CustomLocalBottomSheetDialog();
                    localBottomSheetDialog.setPosition(position);
                    localBottomSheetDialog.setLocalTrack(activity.finalLocalSearchResultList.get(position));
                    localBottomSheetDialog.show(activity.getSupportFragmentManager(), "local_song_bottom_sheet");
                }
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        Button mEndButton = new Button(getContext());
        mEndButton.setBackgroundColor(HomeActivity.themeColor);
        mEndButton.setTextColor(Color.WHITE);


    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutManager2.scrollToPositionWithOffset(0, 0);
    }

    public int getPosition(LocalTrack lt) {
        for (int i = 0; i < HomeActivity.localTrackList.size(); i++) {
            if (HomeActivity.localTrackList.get(i).getTitle().equals(lt.getTitle())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    public void updateAdapter() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

}
