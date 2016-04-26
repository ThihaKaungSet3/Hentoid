package me.devsaki.hentoid.holders;

import android.graphics.Color;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import me.devsaki.hentoid.R;
import me.devsaki.hentoid.listener.ItemClickListener;
import me.devsaki.hentoid.listener.ItemLongClickListener;
import me.devsaki.hentoid.listener.ItemTouchViewListener;

/**
 * Created by avluis on 04/23/2016.
 * <p/>
 * TODO: Add tvSavedDate
 */
public class ContentHolder extends RecyclerView.ViewHolder implements
        OnClickListener, OnLongClickListener, ItemTouchViewListener {
    public final TextView tvTitle;
    public final ImageView ivCover;
    public final TextView tvSeries;
    public final TextView tvArtist;
    public final TextView tvTags;
    public final ImageView ivSite;
    // public final TextView tvSavedDate;

    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;

    public ContentHolder(final View itemView,
                         ItemClickListener clickListener, ItemLongClickListener longClickListener) {
        super(itemView);

        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        ivCover = (ImageView) itemView.findViewById(R.id.ivCover);
        tvSeries = (TextView) itemView.findViewById(R.id.tvSeries);
        tvArtist = (TextView) itemView.findViewById(R.id.tvArtist);
        tvTags = (TextView) itemView.findViewById(R.id.tvTags);
        ivSite = (ImageView) itemView.findViewById(R.id.ivSite);
        // tvSavedDate = (TextView) itemView.findViewById(R.id.tvSavedDate);

        this.mClickListener = clickListener;
        this.mLongClickListener = longClickListener;

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

        itemView.setClickable(true);
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            mClickListener.onItemClick(v, getLayoutPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mLongClickListener != null) {
            mLongClickListener.onItemLongClick(v, getLayoutPosition());
        }
        return true;
    }

    @Override
    public void onItemSelected() {
        String bgColor = "#400404";
        itemView.setBackgroundColor(Color.parseColor(bgColor));
    }

    @Override
    public void onItemClear() {
        itemView.setBackgroundColor(0);
    }
}