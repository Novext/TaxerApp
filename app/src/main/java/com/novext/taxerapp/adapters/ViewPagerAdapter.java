package com.novext.taxerapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novext.taxerapp.App;
import com.novext.taxerapp.R;

/**
 * Created by angel on 10/3/16.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private int[] mImages;
    private String[] mMessages;
    private String[] mTitles;

    public ViewPagerAdapter(Context mContext, String[] mTitles, String[] mMessages, int[] mImages) {
        this.mContext = mContext;
        this.mImages = mImages;
        this.mMessages = mMessages;
        this.mTitles = mTitles;
    }

    @Override
    public int getCount() {
        return mImages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false);
        TextView txtMessage = (TextView) itemView.findViewById(R.id.message_pager_item);
        TextView txtTitle = (TextView) itemView.findViewById(R.id.title_pager_item);
        ImageView imgView = (ImageView) itemView.findViewById(R.id.imgPager);
        txtMessage.setText(mMessages[position]);
        txtTitle.setText(mTitles[position]);
        imgView.setImageResource(mImages[position]);

        container.addView(itemView);

        return itemView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
