package com.novext.taxerapp;

import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.novext.taxerapp.adapters.ViewPagerAdapter;
import com.novext.taxerapp.models.States;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener,ViewPager.OnPageChangeListener {

    protected View view;
    private Button btnNext,btnSkip;
    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;
    private String[] mTitle = {
        "SEGURO",
        "TIEMPO"
    };
    private String[] mMessages = {
            "Encuentra taxis libres sin gastar tiempo en buscarlos",
            "De una manera rapida y segura"
    };
    private int[] mImages ={
        R.mipmap.g5192,
        R.mipmap.g5535
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if(!States.alreadyStart()){
            States.started(true);
        }else{
            Intent i = new Intent(this,MapsActivity.class);
            startActivity(i);
            finish();
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        pager_indicator = (LinearLayout)findViewById(R.id.viewPagerCountDots);

        intro_images  = (ViewPager) findViewById(R.id.pager_introduction);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnSkip = (Button) findViewById(R.id.btnSkip);
        btnNext.setOnClickListener(this);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap();
            }
        });

        mAdapter = new ViewPagerAdapter(this,mTitle,mMessages,mImages);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);

        setUiPageViewController();


    }

    private void setUiPageViewController(){
        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(4,0,4,0);

            pager_indicator.addView(dots[i],params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnNext:
                intro_images.setCurrentItem((intro_images.getCurrentItem() < dotsCount)
                        ? intro_images.getCurrentItem() + 1 : 0);
                if(intro_images.getCurrentItem()==dotsCount)
                    break;
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

        if (position + 1 == dotsCount) {
            btnNext.setText("FINALIZAR");
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMap();
                }
            });
        }else {
            btnNext.setText("SIGUIENTE");
            btnNext.setOnClickListener(this);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void goToMap(){
        Intent i = new Intent(this,MapsActivity.class);
        startActivity(i);
        finish();
    }
}
