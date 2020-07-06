package id.lombokit.LapakDesaPedagang;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.lombokit.LapakDesaPedagang.Models.ModelDesa;
import id.lombokit.LapakDesaPedagang.Models.ModelKab;
import id.lombokit.LapakDesaPedagang.Models.ModelKecamatan;
import id.lombokit.LapakDesaPedagang.Root_url.Url_root;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionManager;

public class DaftarAkunBaru extends AppCompatActivity {
    Url_root url_root = new Url_root();
    String url_get_nama_desa = url_root.Url + "get_nama_desa.php";
    String url_kab = url_root.Url + "view_kab.php";
    String url_kec = url_root.Url + "view_kec.php";
    String url_desa = url_root.Url + "view_desa.php";

    String url_register_suplayer = url_root.Url + "register_suplayer.php";
    TextView textViewDaftar, textViewLogin, textViewJudul;
    Spinner spinnerNama_desa, spinnerPilihPenyedia, spinner_kab, spinner_kec;
    SessionManager sessionManager;

    List<ModelDesa> modelDesas = new ArrayList<>();
    List<ModelKecamatan> modelKecamatans = new ArrayList<>();
    List<ModelKab> modelKabs = new ArrayList<>();
    List<String> listKab = new ArrayList<>();
    List<String> listnama_desa = new ArrayList<>();
    List<String> listKecamatan = new ArrayList<>();

    private String[] penyedia = {"Pilih Jenis Penyuplai", "BUMDES/BUMKEL", "UMKM"};

    int id_desa;
    String s_id_desa,clik;
    int bumdes;
    FirebaseAuth auth;

    EditText editTextUsername,
            editTextNama,
            editTextKontak,
            editTextemail,
            editTextAlamat,
            editTextPassword;

    String s_username, s_nama, s_kontak, s_email, s_alamat, s_password;
    AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        getStringFromLogin();
        eventClik();
        //getDataDesa();
        get_id();
        cekKoneksi();
    }



    private void init() {
        editTextUsername = findViewById(R.id.username);
        editTextNama = findViewById(R.id.nama_toko);
        editTextKontak = findViewById(R.id.kontak);
        editTextemail = findViewById(R.id.alamat_email);
        editTextAlamat = findViewById(R.id.alamat);
        editTextPassword = findViewById(R.id.pass);
        textViewDaftar = findViewById(R.id.daftar);
        textViewJudul = findViewById(R.id.title);
        textViewLogin = findViewById(R.id.login);
        spinnerNama_desa = findViewById(R.id.nama_desa);
        spinner_kec = findViewById(R.id.nama_kec);
        spinner_kab = findViewById(R.id.pilih_kab);
        spinnerPilihPenyedia = findViewById(R.id.pilih_penyedia);
        textViewJudul.setText("Daftar pengguna baru");
        dialog = new AlertDialog.Builder(this);
        sessionManager = new SessionManager(this);
        auth = FirebaseAuth.getInstance();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spiner_item, penyedia);
        spinnerPilihPenyedia.setAdapter(adapter);
    }
    private void getStringFromLogin() {
        try {
            clik = getIntent().getStringExtra("login");
            s_email = getIntent().getStringExtra("email");
            editTextemail.setText(s_email);
        }catch (Exception e){

        }

    }


    private void eventClik() {
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    private boolean adaInternet() {
        ConnectivityManager koneksi = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return koneksi.getActiveNetworkInfo() != null;
    }

    private void cekKoneksi() {
        if (adaInternet()) {
            loadKabupaten();
        } else {
            Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet", Toast.LENGTH_LONG).show();
        }


    }

    private void loadKabupaten() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_kab, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listKab.add(0, "Pilih kabupaten");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        int id = data.getInt("id_kab");
                        String nama = data.getString("nama");

                        ModelKab modelKab = new ModelKab(
                                id,
                                nama
                        );
                        modelKabs.add(modelKab);
                    }

                    for (int i = 0; i < modelKabs.size(); i++) {
                        listKab.add(modelKabs.get(i).getNama_kab());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.spiner_item, listKab);
                    spinner_kab.setAdapter(spinnerAdapter);
                    spinner_kab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String seleted = (String) parent.getItemAtPosition(position);
                            if (!spinner_kab.getSelectedItem().toString().equals("Pilih kabupaten")) {
                                int id_kab = modelKabs.get(position - 1).getId_kab();
                                getKec(id_kab);
                            } else {
                                spinner_kec.setAdapter(null);
                                spinnerNama_desa.setAdapter(null);
                                modelKecamatans.clear();
                                listKecamatan.clear();
                                modelDesas.clear();
                                listnama_desa.clear();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void getKec(final int id_kab) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_kec, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        int id_kec = data.getInt("id_kec");
                        int id_kab = data.getInt("id_kab");
                        String nama = data.getString("nama_kec");
                        //Toast.makeText(getApplicationContext(),""+nama,Toast.LENGTH_LONG).show();
                        ModelKecamatan modelKec = new ModelKecamatan(
                                id_kec,
                                id_kab,
                                nama

                        );
                        modelKecamatans.add(modelKec);

                    }
                    listKecamatan.add(0, "Pilih kecamatan");
                    for (int i = 0; i < modelKecamatans.size(); i++) {
                        listKecamatan.add(modelKecamatans.get(i).getNama_kecamatan());

                    }
                    ArrayAdapter<String> spinnerAdapterKec = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.spiner_item, listKecamatan);
                    spinner_kec.setAdapter(spinnerAdapterKec);
                    spinner_kec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (!spinner_kec.getSelectedItem().toString().equals("Pilih kecamatan")) {
                                //String seleted = (String) parent.getItemAtPosition(position);
                                int id_kec = modelKecamatans.get(position - 1).getId_kec();
                                getDesa(id_kec);
                                modelDesas.clear();
                                listnama_desa.clear();
                            } else {
                                spinnerNama_desa.setAdapter(null);
                                modelDesas.clear();
                                listnama_desa.clear();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

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
                map.put("id_kab", String.valueOf(id_kab));
                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    //region Fungsi untuk Desa
    private void getDesa(final int id_kecamatan) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_desa, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listnama_desa.add(0, "Pilih desa");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        int id_desa = data.getInt("id_desa");
                        int id_kec = data.getInt("id_kec");
                        String nama = data.getString("nama_desa");

                        ModelDesa modelDesa = new ModelDesa(
                                id_desa,
                                id_kec,
                                nama

                        );
                        modelDesas.add(modelDesa);

                    }

                    for (int i = 0; i < modelDesas.size(); i++) {
                        listnama_desa.add(modelDesas.get(i).getNama_desa());

                    }
                    ArrayAdapter<String> spinnerAdapterDesa = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.spiner_item, listnama_desa);
                    spinnerNama_desa.setAdapter(spinnerAdapterDesa);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id_kec", String.valueOf(id_kecamatan));
                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

        spinnerNama_desa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinnerNama_desa.getSelectedItem().toString().equals("Pilih Desa")) {
                    try {
                        id_desa = modelDesas.get(position - 1).getId_desa();
                    } catch (Exception e) {

                    }


                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    //endregion


    private void get_id() {
        textViewDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    bumdes = spinnerPilihPenyedia.getSelectedItemPosition();
                    if (bumdes == 2) {
                        bumdes = 0;
                    }
                    s_username = editTextUsername.getText().toString();
                    s_nama = editTextNama.getText().toString();
                    s_kontak = editTextKontak.getText().toString();
                    s_email = editTextemail.getText().toString();
                    s_alamat = editTextAlamat.getText().toString();
                    s_password = editTextPassword.getText().toString();

                    if (s_username.isEmpty()) {
                        dialog.setMessage("Username belum diisi");
                        dialog.setPositiveButton("Lengkapi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else if (s_nama.isEmpty()) {
                        dialog.setMessage("Nama belum diisi");
                        dialog.setPositiveButton("Lengkapi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else if (s_kontak.isEmpty()) {
                        dialog.setMessage("Nomer Hp belum disi");
                        dialog.setPositiveButton("Lengkapi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else if (s_email.isEmpty()) {
                        dialog.setMessage("Email belum diisi");
                        dialog.setPositiveButton("Lengkapi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else if (s_alamat.isEmpty()) {
                        dialog.setMessage("Alamat belum disi");
                        dialog.setPositiveButton("Lengkapi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else if (s_password.length() <= 7) {
                        dialog.setMessage("Password anda kurang dari 8");
                        dialog.setPositiveButton("Ulang", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else if (spinnerPilihPenyedia.getSelectedItem().toString() == "Pilih Jenis Penyuplai") {
                        dialog.setMessage("Belum memilih jenis penyuplai");
                        dialog.setPositiveButton("Lengkapi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else if (spinner_kab.getSelectedItem().toString() == "Pilih kabupaten") {
                        dialog.setMessage("Anda belum memiih kabupaten");
                        dialog.setPositiveButton("Lengkapi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else if (spinner_kec.getSelectedItem().toString() == "Pilih kecamatan ") {
                        dialog.setMessage("Anda belum memiih kecamatan");
                        dialog.setPositiveButton("Lengkapi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else if (spinnerNama_desa.getSelectedItem().toString() == "Pilih desa") {
                        dialog.setMessage("Anda belum memiih desa");
                        dialog.setPositiveButton("Lengkapi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else {

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_register_suplayer, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String cek = jsonObject.getString("cek");
                                    if (cek.equals("exsits")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DaftarAkunBaru.this);
                                        builder.setMessage("Username yang di gunakan sudah di daftarkan sebelumnya, Apakah anda ingin Login ?");
                                        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                    startActivity(intent);


                                            }
                                        });
                                        builder.setNegativeButton("Akun Baru", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    } else if (cek.equals("success")) {

                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(intent);
                                            auth.createUserWithEmailAndPassword(s_email, s_password).isSuccessful();


                                    } else {
                                        dialog.setMessage("Anda gagal mendaftar");
                                        dialog.setPositiveButton("Ulangi", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();
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
                                map.put("username", s_username);
                                map.put("nama", s_nama);
                                map.put("nomer_hp", s_kontak);
                                map.put("email", s_email);
                                map.put("id_desa", String.valueOf(id_desa));
                                map.put("alamat", s_alamat);
                                map.put("bumdes", String.valueOf(bumdes));
                                map.put("password", s_password);
                                return map;
                            }
                        };
                        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(DaftarAkunBaru.this);
                    builder.setMessage("Data masih kosong silakan lengkapi");
                    builder.setPositiveButton("Lengkapi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }

        });


    }


}
