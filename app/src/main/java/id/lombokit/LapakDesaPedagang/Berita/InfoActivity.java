package id.lombokit.LapakDesaPedagang.Berita;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import id.lombokit.LapakDesaPedagang.Adapters.InfoPagerAdapter;
import id.lombokit.LapakDesaPedagang.Adapters.TimingsAdapter;
import id.lombokit.LapakDesaPedagang.DashboardActivity;
import id.lombokit.LapakDesaPedagang.Notifikasi.NotificationActivity;
import id.lombokit.LapakDesaPedagang.R;

public class InfoActivity extends AppCompatActivity {

    private InfoPagerAdapter infoPagerAdapter;
    private TabLayout customTab;
    private ViewPager pager;

    Typeface mTypeface;
    ImageView kembali, pemberitahuan;
    TextView judul;


    private Dialog bottomDialog;
    private TextView tvToday,tvTomorrow;
    private LinearLayout linearDialog;
    private FrameLayout frameOk;
    private RecyclerView rvTimings;
    private TimingsAdapter timingsAdapter;

    private String[] NAMES_TIMMING = {"9 TO 10 am","10 to 11 am","11 to 12 am","12 to 1 pm","1 to 2 pm","2 to 3 pm","3 to 4 pm"};
    private ArrayList<String> stringArrayList = new ArrayList<>();
    private ImageView btn;

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
