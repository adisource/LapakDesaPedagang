package id.lombokit.LapakDesaPedagang.Berita;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.uncopt.android.widget.text.justify.JustifiedTextView;

import id.lombokit.LapakDesaPedagang.R;

public class DetailList_Berita extends AppCompatActivity {
    ImageView imageberita;
    TextView textViewJudul;
    JustifiedTextView textViewIsi;
    TextView textViewJamPost,nameAction;
    String image_berita,judul_berita,isi_berita,jam_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_list__berita);
        imageberita = findViewById(R.id.image_berita);
        textViewJudul = findViewById(R.id.judul);
        textViewIsi = findViewById(R.id.isi_berita);
        textViewJamPost = findViewById(R.id.jam_post);
        nameAction = findViewById(R.id.title);
        nameAction.setText("Detail Berita");

        image_berita= getIntent().getStringExtra("image");
        judul_berita = getIntent().getStringExtra("judul");
        isi_berita = getIntent().getStringExtra("isi_berita");
        jam_post = getIntent().getStringExtra("jam_post");

        textViewJudul.setText(judul_berita);
        textViewIsi.setText(isi_berita);
        textViewJamPost.setText(jam_post);

    }

}
