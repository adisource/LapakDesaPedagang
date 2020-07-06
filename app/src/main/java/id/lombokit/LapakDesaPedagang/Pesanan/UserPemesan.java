package id.lombokit.LapakDesaPedagang.Pesanan;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.lombokit.LapakDesaPedagang.DashboardActivity;
import id.lombokit.LapakDesaPedagang.Models.List_user;
import id.lombokit.LapakDesaPedagang.Models.Users;
import id.lombokit.LapakDesaPedagang.Notifikasi.NotificationActivity;
import id.lombokit.LapakDesaPedagang.R;
import id.lombokit.LapakDesaPedagang.Root_url.EndPoints;
import id.lombokit.LapakDesaPedagang.Root_url.Url_root;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionAlert;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionManager;
import id.lombokit.LapakDesaPedagang.Utilities;

public class UserPemesan extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    Url_root root = new Url_root();
    String slug_userB = "/view_userBelanja.php";
    String save_jadwal_pesan = root.Url + "/save_jadwal_pesan.php";
    String url_view_jadwal_pesan = root.Url + "/view_jadwal_pesanan.php";
    String url_update_jadwal_pesan = root.Url+"/update_jadwal_pesanan.php";
    String url_profil = root.Url+"/view_profil.php";
    String url_userB = root.Url + slug_userB;


    SessionManager sessionManager;
    SessionAlert sessionAlert;

    private List_user list_user;
    private RecyclerView recyclerView;
    private Recycle_adapter adapter;

    RelativeLayout layout_belanja_user;

    TextView textViewJudul,badge_value;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;
    ImageView back;

    TimePicker jam;

    TextView textViewJadwal_pesan, textViewJadwal_pengepulan, textViewJadwal_pengiriman;
    Button set_jadwal_pesan, set_jadwal_pengepulan, set_jadwal_pengiriman;

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    Calendar calendar;
    ImageView imageViewNotif;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pesanan);
        init();
        visibelityFab();
        eventSwipe();
        eventClik();
        object();
        viewBadge();
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        sessionManager = new SessionManager(this);
        sessionAlert = new SessionAlert(this);
        textViewJudul = findViewById(R.id.title);
        textViewJudul.setText("Daftar Pesanan");
        recyclerView = findViewById(R.id.list_user);
        swipeRefreshLayout = findViewById(R.id.swipe);

        fab = findViewById(R.id.jadwal_pesanan);
        back = findViewById(R.id.back);
        imageViewNotif = findViewById(R.id.notif);
        badge_value = findViewById(R.id.count_notif);

        //calendar = Calendar.getInstance();


    }
    private void visibelityFab() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_profil, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject data  = jsonArray.getJSONObject(i);
                    int bumdes = data.getInt("bumdes");
                    //Toast.makeText(getApplicationContext(),""+bumdes,Toast.LENGTH_LONG).show();
                    if (bumdes==0){
                        fab.setVisibility(View.GONE);
                    }

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
            map.put("id_suplayer",sessionManager.getSpIduser());
            return map;
        }
    };
        Volley.newRequestQueue(this).add(stringRequest);

    }
    private void eventClik() {
        fab.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Rect displayRectangle = new Rect();
                Window window = getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                dialog = new AlertDialog.Builder(UserPemesan.this);
                inflater = getLayoutInflater();
                dialogView = inflater.inflate(R.layout.setting_jadwal_pesanan, null);
                dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));
                dialogView.setMinimumHeight((int) (displayRectangle.height() * 0.9f));
                jam = dialogView.findViewById(R.id.jam);
                textViewJadwal_pesan = dialogView.findViewById(R.id.jadwal_pesanan);
                textViewJadwal_pengepulan = dialogView.findViewById(R.id.jadwal_pengepulan);
                textViewJadwal_pengiriman = dialogView.findViewById(R.id.jadwal_pengiriman);
                set_jadwal_pesan = dialogView.findViewById(R.id.set_jadwal_pesanan);
                set_jadwal_pengepulan = dialogView.findViewById(R.id.set_jadwal_pengepulan);
                set_jadwal_pengiriman = dialogView.findViewById(R.id.set_jadwal_pengiriman);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                final int[] get_hour_from_picker = new int[1];
                final int[] get_minute_from_picker = new int[1];


                StringRequest stringRequest = new StringRequest(Request.Method.POST, url_view_jadwal_pesan, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                String jam_pesanan = data.getString("jam_pesan");
                                String jam_pengepulan = data.getString("jam_pengepulan");
                                String jam_pengiriman = data.getString("jam_pengiriman");
                                textViewJadwal_pesan.setText(jam_pesanan);
                                textViewJadwal_pengepulan.setText(jam_pengepulan);
                                textViewJadwal_pengiriman.setText(jam_pengiriman);

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
                        map.put("id_sup", sessionManager.getSpIduser());
                        return map;
                    }
                };
                Volley.newRequestQueue(UserPemesan.this).add(stringRequest);

                jam.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        get_hour_from_picker[0] = hourOfDay;
                        get_minute_from_picker[0] = minute;

                    }
                });

                set_jadwal_pesan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textViewJadwal_pesan.setText(new StringBuilder().append(get_hour_from_picker[0]).append(":").append(get_minute_from_picker[0]));


                    }
                });
                set_jadwal_pengepulan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textViewJadwal_pengepulan.setText(new StringBuilder().append(get_hour_from_picker[0]).append(":").append(get_minute_from_picker[0]));
                    }
                });
                set_jadwal_pengiriman.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textViewJadwal_pengiriman.setText(new StringBuilder().append(get_hour_from_picker[0]).append(":").append(get_minute_from_picker[0]));
                    }
                });

                dialog.setPositiveButton("Simpan", null);



                dialog.setNeutralButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_update_jadwal_pesan, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.contains("success")) {
                                    Toast.makeText(getApplicationContext(), "Berhasil diupdate", Toast.LENGTH_LONG).show();
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
                                map.put("jam_pesanan", textViewJadwal_pesan.getText().toString());
                                map.put("jam_pengepulan", textViewJadwal_pengepulan.getText().toString());
                                map.put("jam_pengiriman", textViewJadwal_pengiriman.getText().toString());
                                map.put("id_sup", sessionManager.getSpIduser());
                                return map;
                            }
                        };
                        Volley.newRequestQueue(UserPemesan.this).add(stringRequest);

                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                final AlertDialog alertDialog = dialog.create();
                alertDialog.show();

            if (sessionAlert.getSpLogined()==true){
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
            }else {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, save_jadwal_pesan, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.contains("success")) {
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition(0, 0);
                                    sessionAlert.saveBoolean(SessionAlert.SP_TRUE,true);
                                    Toast.makeText(getApplicationContext(), "Berhasil ditambah", Toast.LENGTH_LONG).show();
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
                                map.put("jam_pesanan", textViewJadwal_pesan.getText().toString());
                                map.put("jam_pengepulan", textViewJadwal_pengepulan.getText().toString());
                                map.put("jam_pengiriman", textViewJadwal_pengiriman.getText().toString());
                                map.put("id_sup", sessionManager.getSpIduser());
                                return map;
                            }
                        };
                        Volley.newRequestQueue(UserPemesan.this).add(stringRequest);

                    }


                });
            }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            }
        });
        imageViewNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });
    }
    @SuppressLint("ResourceAsColor")
    private void eventSwipe() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                loadData();

            }
        });
    }

    private void object() {
        list_user = new List_user();
        list_user.usersArrayList = new ArrayList<>();
        adapter = new Recycle_adapter(getApplicationContext(), list_user);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        if (list_user.usersArrayList != null) {
            list_user.usersArrayList.clear();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_userB, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                   /* if (response.contains("kosong")) {
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.massage, null);
                        layout_belanja_user.addView(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        recyclerView.setVisibility(View.GONE);
                    }*/
                    try {
                        //recyclerView.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            int id = data.getInt("id");
                            String nama = data.getString("nama");
                            String telpon = data.getString("telpon");
                            int total_belanja = data.getInt("total");
                            int jumlah_belanja = data.getInt("jml_barang");
                            String  desa  = data.getString("desa");
                            int status_0 = data.getInt("status_0");
                            int status_1 = data.getInt("status_1");
                            int status_2 = data.getInt("status_2");

                            Users users = new Users(
                                    id,
                                    nama,
                                    telpon,
                                    total_belanja,
                                    jumlah_belanja,
                                    desa,
                                    status_0,
                                    status_1,
                                    status_2

                            );
                            list_user.usersArrayList.add(users);
                        }
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("id_sup", sessionManager.getSpIduser());

                    return map;
                }
            };
            Volley.newRequestQueue(this).add(stringRequest);

        }

    }
    public class Recycle_adapter extends RecyclerView.Adapter<Recycle_adapter.My_viewHolder> {
        private Context context;
        private List_user list_user;

        public Recycle_adapter(Context context, List_user list_user) {
            this.context = context;
            this.list_user = list_user;
        }

        @NonNull
        @Override
        public My_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_user, parent, false);
            return new Recycle_adapter.My_viewHolder(viewItem);
        }

        public class My_viewHolder extends RecyclerView.ViewHolder {

            TextView textViewNama,
                    textViewTlp,
                    textViewAlamat,
                    textViewCount,
                    textViewSum,
                    textViewStatus_0,
                    textViewStatus_1,
                    textViewStatus_2
            ;

            ImageView imageViewCall,
                    imageViewStatus_0,
                    imageViewStatus_1,
                    imageViewStatus_2;


            public My_viewHolder(@NonNull View itemView) {
                super(itemView);

                textViewNama = itemView.findViewById(R.id.nama_user);
                textViewTlp = itemView.findViewById(R.id.no_hp);
                textViewCount = itemView.findViewById(R.id.v_count);
                textViewSum = itemView.findViewById(R.id.sum);
                imageViewCall= itemView.findViewById(R.id.call);
                textViewStatus_0 = itemView.findViewById(R.id.v_tunda);
                textViewStatus_1 = itemView.findViewById(R.id.v_proses);
                textViewStatus_2 = itemView.findViewById(R.id.v_selesai);
                imageViewStatus_0 = itemView.findViewById(R.id.status_0);
                imageViewStatus_1 = itemView.findViewById(R.id.status_1);
                imageViewStatus_2 = itemView.findViewById(R.id.status_2);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final My_viewHolder holder, final int position) {

            holder.textViewNama.setText(Utilities.toTitleCase(list_user.getUsersArrayList().get(position).getNama()));
            holder.textViewTlp.setText(list_user.getUsersArrayList().get(position).getTelpon());
            holder.textViewStatus_0.setText(""+list_user.getUsersArrayList().get(position).getStatus_0());
            holder.textViewStatus_1.setText(""+list_user.getUsersArrayList().get(position).getStatus_1());
            holder.textViewStatus_2.setText(""+list_user.getUsersArrayList().get(position).getStatus_2());
            holder.imageViewStatus_0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id_user",list_user.getUsersArrayList().get(position).getId());
                    bundle.putString("desa",list_user.getUsersArrayList().get(position).getDesa());
                    DaftarBarangStatusTunda daftarBarangStatusTunda = new DaftarBarangStatusTunda();
                    daftarBarangStatusTunda.setArguments(bundle);
                    recyclerView.setVisibility(View.GONE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.framelayout,daftarBarangStatusTunda)
                            .addToBackStack(null).commit();

                }
            });
            holder.imageViewStatus_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id_user",list_user.getUsersArrayList().get(position).getId());
                    bundle.putString("desa",list_user.getUsersArrayList().get(position).getDesa());
                    DaftarBarangStatusProses daftarBarangStatusproses = new DaftarBarangStatusProses();
                    daftarBarangStatusproses.setArguments(bundle);
                    recyclerView.setVisibility(View.GONE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.framelayout,daftarBarangStatusproses)
                            .addToBackStack(null).commit();

                }
            });
            holder.imageViewStatus_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id_user",list_user.getUsersArrayList().get(position).getId());
                    bundle.putString("desa",list_user.getUsersArrayList().get(position).getDesa());
                    DaftarBarangStatusSukses daftarBarangStatusSukses = new DaftarBarangStatusSukses();
                    daftarBarangStatusSukses.setArguments(bundle);
                    recyclerView.setVisibility(View.GONE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.framelayout,daftarBarangStatusSukses)
                            .addToBackStack(null).commit();

                }
            });
                    /*holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ListBarangStatusBerbeda.class);
                    intent.putExtra("id_user", list_user.getUsersArrayList().get(position).getId());
                    intent.putExtra("desa",list_user.getUsersArrayList().get(position).getDesa());
                    startActivity(intent);
                }
            });
*/
            int convert_total = list_user.usersArrayList.get(position).getTotal_belanja();
            Locale localeID = new Locale("in", "ID");
            NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(localeID);
            holder.textViewSum.setText(rupiahFormat.format((double) convert_total));
            holder.textViewCount.setText(""+list_user.getUsersArrayList().get(position).getJumlah());
            holder.imageViewCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new AlertDialog.Builder(UserPemesan.this);
                    dialog.setMessage("Apakah anda ingin menghubungi "+Utilities.toTitleCase(list_user.getUsersArrayList().get(position).getNama()));
                    dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callPhoneNumber(list_user.getUsersArrayList().get(position).getTelpon());
                        }
                    });
                    dialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            });


        }
        @Override
        public int getItemCount() {
            return list_user.usersArrayList.size();
        }
    }


    public void callPhoneNumber(String telpon)
    {
        try
        {
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(UserPemesan.this, new String[]{Manifest.permission.CALL_PHONE}, 101);

                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + telpon));
                startActivity(callIntent);

            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }




}
