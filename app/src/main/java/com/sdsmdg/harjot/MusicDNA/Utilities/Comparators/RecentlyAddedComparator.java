package com.sdsmdg.harjot.MusicDNA.Utilities.Comparators;

import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;

import java.util.Comparator;

/**
 * Created by Sadyksaj on 16.03.2017.
 */

public class RecentlyAddedComparator implements Comparator<LocalTrack> {
    @Override
    public int compare(LocalTrack lhs, LocalTrack rhs) {
        return lhs.getDateAdded().compareTo(rhs.getDateAdded());
    }
}
