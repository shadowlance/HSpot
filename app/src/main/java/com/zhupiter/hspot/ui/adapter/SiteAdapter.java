package com.zhupiter.hspot.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zhupiter.hspot.R;
import com.zhupiter.hspot.cilent.manager.SiteManager;

/**
 * Created by zhupiter on 17-3-17.
 */

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.SiteViewHolder>
{

    private SiteManager mSiteManager;

    public SiteAdapter(Context context) {
        mSiteManager = new SiteManager(context);
    }

    @Override
    public SiteViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // TODO: 17-3-21 试试true？
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_site_info,parent,false);
        return new SiteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SiteViewHolder holder, int position)
    {
        Cursor siteList = mSiteManager.findAllSite();
        while (siteList.moveToNext()){
            String name = siteList.getString(siteList.getColumnIndex("siteName"));
            holder.siteTitle.setText(name);
            // TODO: 17-3-31 Add image and transform cursor to List
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        public void onClick(View v, int position);

        public void onLongClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount()
    {
        return mSiteManager.getSiteCount();
    }

    class SiteViewHolder extends RecyclerView.ViewHolder
    {
        TextView siteTitle;
        SimpleDraweeView siteImage;

        public SiteViewHolder(View itemView) {
            super(itemView);
            siteTitle = (TextView) itemView.findViewById(R.id.title_site);
            siteImage = (SimpleDraweeView) itemView.findViewById(R.id.image_site);
        }
    }
}
