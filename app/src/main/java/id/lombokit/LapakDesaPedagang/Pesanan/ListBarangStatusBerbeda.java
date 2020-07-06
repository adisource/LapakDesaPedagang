package id.lombokit.LapakDesaPedagang.Pesanan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import id.lombokit.LapakDesaPedagang.Adapters.DaftarBarangPesananAdapter;
import id.lombokit.LapakDesaPedagang.R;

public class ListBarangStatusBerbeda extends AppCompatActivity {
    private DaftarBarangPesananAdapter daftarBarangPesananAdapter;
    private TabLayout customTab;
    private ViewPager pager;
    TextView judul;
    ImageView back;

    String id_user,desa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_barang_status_berbeda);

        judul = findViewById(R.id.title);
        back = findViewById(R.id.back);
        judul.setText("Daftar barang");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UserPemesan.class) ;
                startActivity(intent);
            }
        });

        pager = (ViewPager)findViewById(R.id.pager);
        customTab=  findViewById(R.id.customTab);
        id_user = getIntent().getStringExtra("id_user");
        desa = getIntent().getStringExtra("desa");
        int parse = Integer.parseInt(id_user);



        customTab.addTab(customTab.newTab().setText("TERTUNDA"));
        customTab.addTab(customTab.newTab().setText("PROSES"));
        customTab.addTab(customTab.newTab().setText("SELESAI"));
        daftarBarangPesananAdapter = new DaftarBarangPesananAdapter(getSupportFragmentManager(),customTab.getTabCount(),parse,desa);
        pager.setAdapter(daftarBarangPesananAdapter);
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
