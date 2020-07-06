package id.lombokit.LapakDesaPedagang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.tabs.TabLayout;

import id.lombokit.LapakDesaPedagang.Adapters.InfoPagerAdapter;
import id.lombokit.LapakDesaPedagang.Notifikasi.NotificationActivity;

public class InfoActivity extends AppCompatActivity {

    private InfoPagerAdapter infoPagerAdapter;
    private TabLayout customTab;
    private ViewPager pager;

    Typeface mTypeface;
    ImageView kembali, pemberitahuan;
    TextView judul;



    Animation slideUpAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        kembali = findViewById(R.id.back);
        judul = findViewById(R.id.title);
        pemberitahuan = findViewById(R.id.notif);

        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            }
        });

        pemberitahuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });

        judul.setText((CharSequence) "Info Desa");











        pager = (ViewPager)findViewById(R.id.pager);
        customTab=  findViewById(R.id.customTab);


        customTab.addTab(customTab.newTab().setText("BERITA"));

        mTypeface = Typeface.createFromAsset(this.getAssets(), "roboto_regular.ttf");
        ViewGroup vg = (ViewGroup) customTab.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(mTypeface, Typeface.NORMAL);
                }
            }
        }



        infoPagerAdapter = new InfoPagerAdapter
                (getSupportFragmentManager(), customTab.getTabCount());
        pager.setAdapter(infoPagerAdapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(customTab));
        pager.setOffscreenPageLimit(customTab.getTabCount());

        customTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }


}
