package id.lombokit.LapakDesaPedagang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.lombokit.LapakDesaPedagang.Notifikasi.NotificationActivity;
import id.lombokit.LapakDesaPedagang.Pesanan.UserPemesan;
import id.lombokit.LapakDesaPedagang.Root_url.EndPoints;
import id.lombokit.LapakDesaPedagang.Root_url.Url_root;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionAlert;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionManager;

public class DashboardActivity extends AppCompatActivity {
    SessionManager sessionManager;
    SessionAlert sessionAlert;

    Url_root root = new Url_root();
    String slug_profil = "/view_profil.php";
    String url_profil = root.Url + slug_profil;
    String url_pihakDesa = root.Url+"/pihak_desa.php";
    CardView tentang, profil, sembako, tambahBarang;
    RelativeLayout logout;
    ImageView kembali, pemberitahuan;
    CircleImageView  user_photo;
    TextView judul;
    TextView textViewNama, textViewToko,badge_value;
    AlertDialog.Builder dialog;
    ImageView imageViewNotif;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        sessionManager = new SessionManager(this);
        sessionAlert = new SessionAlert(this);

        init();
        eventClick();
        getDataUser();
        viewBadge();


    }
    private void init() {
        tentang = findViewById(R.id.tentang);
        profil = findViewById(R.id.profil);
        sembako = findViewById(R.id.sembako);
        textViewToko = findViewById(R.id.user_name);
        logout = findViewById(R.id.logout);
        logout.setVisibility(View.VISIBLE);
        kembali = findViewById(R.id.back);
        judul = findViewById(R.id.title);
        pemberitahuan = findViewById(R.id.notif);
        tambahBarang = findViewById(R.id.tambah_barang);
        user_photo = findViewById(R.id.user_photo);
        dialog = new AlertDialog.Builder(DashboardActivity.this);
        imageViewNotif = findViewById(R.id.notif);
        badge_value = findViewById(R.id.count_notif);
    }

    private void eventClick() {

        tambahBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddBarang.class));
            }
        });
        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            }
        });
        pemberitahuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });

        judul.setText((CharSequence) "Dashboard");

        tentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url_pihakDesa, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("kosong")) {
                            dialog.setMessage("Desa anda belum mendaftar");
                            dialog.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }else{
                            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                        }



                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String>map = new HashMap<>();
                        map.put("id_desa",String.valueOf(sessionManager.getSpIddesa()));
                        return map;
                    }
                };
                Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

            }
        });

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        sembako.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserPemesan.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                builder.setMessage("Apakah anda ingin keluar ?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sessionManager.saveBoolean(SessionManager.SP_LOGINED, false);
                        sessionAlert.saveBoolean(SessionAlert.SP_TRUE, false);
                        Intent intent = new Intent(DashboardActivity.this, PIlihLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        imageViewNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });
    }

    private void getDataUser() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_profil, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        String nama_toko = data.getString("toko");
                        String photo = data.getString("photo");
                        if (photo.equals("")){
                            user_photo.setImageResource(R.drawable.kantor_desa);
                        }else{
                            Glide.with(getApplicationContext()).load(root.root + "upload/suplayer/" + photo).into(user_photo);
                        }

                        textViewToko.setText(Utilities.toTitleCase(nama_toko));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id_suplayer", sessionManager.getSpIduser());
                return map;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
