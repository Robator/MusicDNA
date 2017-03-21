package com.sdsmdg.harjot.MusicDNA.Fragments.LocalMusicFragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.Activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.imageLoader.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by azatfanisovic on 21.03.17.
 */

public class RecentlyAddedAdapter extends RecyclerView.Adapter<RecentlyAddedAdapter.MyViewHolder> {

    private List<LocalTrack> localTracks;
    private Context ctx;
    ImageLoader imgLoader;
    String prevDate = "";
    byte separatePos[] = new byte[HomeActivity.localTrackList.size()];

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView art;
        TextView title, artist, dateAdded;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.img_r);
            title = (TextView) view.findViewById(R.id.title_r);
            artist = (TextView) view.findViewById(R.id.url_r);
            dateAdded = (TextView) view.findViewById(R.id.separator);
        }
    }

    public RecentlyAddedAdapter(List<LocalTrack> localTracks, Context ctx) {
        this.localTracks = localTracks;
        this.ctx = ctx;
        imgLoader = new ImageLoader(ctx);
    }

    @Override
    public RecentlyAddedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_recent, parent, false);

        return new RecentlyAddedAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecentlyAddedAdapter.MyViewHolder holder, int position) {
        LocalTrack track = localTracks.get(position);

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
        String date = DATE_FORMAT.format(track.getDateAdded());
        switch (separatePos[position]){
            case 0:
                if(!prevDate.equals(date)) {
                    holder.dateAdded.setText(date);
                    prevDate = date;
                    holder.dateAdded.setVisibility(View.VISIBLE);
                    separatePos[position] = 1;
                }else {
                    holder.dateAdded.setVisibility(View.GONE);
                    separatePos[position] = 2;
                }
                break;
            case 1:
                holder.dateAdded.setText(date);
                holder.dateAdded.setVisibility(View.VISIBLE);
                prevDate = date;
                break;
            case 2:
                holder.dateAdded.setVisibility(View.GONE);
                break;
        }
        holder.title.setText(track.getTitle());
        holder.artist.setText(track.getArtist());
        imgLoader.DisplayImage(track.getPath(), holder.art);
    }

    @Override
    public int getItemCount() {
        return localTracks.size();
    }

    public static Bitmap getAlbumArt(String path) {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap bitmap = null;

        byte[] data = mmr.getEmbeddedPicture();
        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            return bitmap;
        } else {
            return null;
        }
    }
}
