package id.lombokit.LapakDesaPedagang;

        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.AuthFailureError;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.HashMap;
        import java.util.Map;

        import id.lombokit.LapakDesaPedagang.Notifikasi.NotificationActivity;
        import id.lombokit.LapakDesaPedagang.Root_url.EndPoints;
        import id.lombokit.LapakDesaPedagang.SessionManager.SessionManager;
        import id.lombokit.LapakDesaPedagang.Root_url.Url_root;

public class AboutActivity extends AppCompatActivity {
    Url_root url_root = new Url_root();
    String url_pihakDesa = url_root.Url+"/pihak_desa.php";

    ImageView kembali;
    TextView judul;
    SessionManager sessionManager;
    String nama_desa;
    String tentang_desa;
    String email;
    String telpondesa;
    String kontak_kesehatan;
    String kontak_keamanan;
    String kontak_khusus;
    String kepala_desa;
    String sekdes;
    String kamtibmas;
    String kecamatan;

    TextView
            textViewNama_desa,
            textViewKecamatan,
            textViewTentangdesa,
            textViewtelpon,
            textViewEmail,
            textViewKontakKeamanan,
            textViewKontakKesehatan,
            textViewKontakKusus,
            textViewKepalaDesa,
            textViewSekdes,
            textViewKamtibmas,badge_value;
    ImageView imageViewNotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        sessionManager = new SessionManager(this);
       Toast.makeText(getApplicationContext(),""+sessionManager.getSpIddesa(),Toast.LENGTH_LONG).show();
        init();
        loadData();
        eventClik();
        viewBadge();
    }
    private void init() {
        textViewNama_desa = findViewById(R.id.desa);
        textViewKecamatan = findViewById(R.id.kec);
        kembali = findViewById(R.id.back);
        judul = findViewById(R.id.title);
        textViewTentangdesa = findViewById(R.id.tentang_desa);
        textViewtelpon = findViewById(R.id.tlpn);
        textViewEmail = findViewById(R.id.email);
        textViewKontakKeamanan = findViewById(R.id.kontak_keamanan);
        textViewKontakKesehatan = findViewById(R.id.kontak_kesehatan);
        textViewKontakKusus = findViewById(R.id.kontak_khusus);
        textViewKepalaDesa = findViewById(R.id.kepala_desa);
        textViewSekdes = findViewById(R.id.sekdes);
        textViewKamtibmas = findViewById(R.id.kamtibmas);
        imageViewNotif = findViewById(R.id.notif);
        badge_value = findViewById(R.id.count_notif);
        imageViewNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });
    }

    private void loadData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_pihakDesa, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i =0;i<jsonArray.length();i++){
                        JSONObject data = jsonArray.getJSONObject(i);
                        nama_desa = data.getString("nama_desa");
                        tentang_desa = data.getString("tentang_desa");
                        email = data.getString("email");
                        telpondesa =data.getString("telpon");
                        kontak_kesehatan = data.getString("kontak_kesehatan");
                        kontak_keamanan = data.getString("kontak_keamanan");
                        kontak_khusus = data.getString("kontak_khusus");
                        kepala_desa = data.getString("kepala_desa");
                        sekdes = data.getString("sekdes");
                        kamtibmas = data.getString("kamtibmas");
                        kecamatan = data.getString("kecamatan");

                        textViewEmail.setText(email);
                        textViewtelpon.setText(telpondesa);
                        textViewKontakKeamanan.setText(kontak_keamanan);
                        textViewKontakKesehatan.setText(kontak_kesehatan);
                        textViewTentangdesa.setText(tentang_desa);
                        textViewKontakKusus.setText(kontak_khusus);
                        textViewKepalaDesa.setText(kepala_desa);
                        textViewSekdes.setText(sekdes);
                        textViewKamtibmas.setText(kamtibmas);
                        textViewNama_desa.setText(nama_desa);
                        textViewKecamatan.setText(kecamatan);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("nama_desa",String.valueOf(sessionManager.getSpIddesa()));
                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void eventClik() {
        judul.setText((CharSequence) "Tentang");
        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            }
        });
        textViewtelpon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatshap("+62"+telpondesa);

            }
        });
        textViewKontakKeamanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatshap("+62"+kontak_keamanan);
            }
        });
        textViewKontakKesehatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatshap("+62"+kontak_kesehatan);
            }
        });
        textViewKontakKusus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatshap("+62"+kontak_khusus);
            }
        });


    }

    private void openWhatshap(String s) {
        String url = "https://api.whatsapp.com/send?phone=" + s;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    private void viewBadge() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_BADGE_NOTIFIKASI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject data  = jsonArray.getJSONObject(i);
                        int badge = data.getInt("badge");
                        badge_value.setText(""+badge);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("id_sup",sessionManager.getSpIduser());

                return map;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
