package id.lombokit.LapakDesaPedagang;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.lombokit.LapakDesaPedagang.Models.Categories;
import id.lombokit.LapakDesaPedagang.Models.ModelKategori;
import id.lombokit.LapakDesaPedagang.Models.Products;
import id.lombokit.LapakDesaPedagang.Notifikasi.NotificationActivity;
import id.lombokit.LapakDesaPedagang.RecycleAdapter.Recycle_adapterViewBarangInput;
import id.lombokit.LapakDesaPedagang.Root_url.EndPoints;
import id.lombokit.LapakDesaPedagang.Root_url.Url_root;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionManager;

public class AddBarang extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        Recycle_adapterViewBarangInput.onDeleteBarang, Recycle_adapterViewBarangInput.onEditBarang {
    Url_root url_root = new Url_root();
    String url_add_barang = url_root.Url + "add_barang.php";
    String url_view_barang_input = url_root.Url + "view_barang_input.php";
    String url_view_kategori = url_root.Url + "view_kategori.php";
    String url_delete_barang = url_root.Url + "delete_barang.php";
    List<ModelKategori> modelKategoris = new ArrayList<>();
    List<String> listKoategori = new ArrayList<>();

    private String[] liststok = {"Pilih Status", "Kosong", "Tersedia"};
    private String[] listsatuan = {"Pilih Satuan", "Ikat", "Tre", "Kg", "Lainnya"};
    Spinner spinnerStatus;
    Spinner spinnerSatuan;
    Spinner spinnerKategori;


    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_IMAGE_EDIT = 2;
    Recycle_adapterViewBarangInput adapter;
    private Categories categories;
    RecyclerView recyclerView;

    LinearLayout lay_satuan, layout_satuan_lain;
    EditText editTextNama, editTextharga, editTextStok, editTextKet, editTextSatuan;
    TextView textViewJudul, badge_value;
    Button cari_gambar;
    int id_kategori;
    String s_nama_barang, s_harga, s_stok, s_ket, s_status;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton add_barang;
    private Bitmap bitmap;
    ImageView gambar_place_tambah, gambar_place_edit, back, imageViewNotif;
    SessionManager sessionManager;
    int id_barang;
    String nama_barang;
    String gambar;
    String harga;
    int stok;

    String satuan;
    String status, keterangan;

    AlertDialog.Builder dialog, dialog_tambah, dialog_edit;
    LayoutInflater inflater_tambah, inflater_edit;
    View dialogView, dialog_view_tambah, dialog_view_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_barang);
        init();
        event();
        eventSwipe();
        object();
        viewBadge();


    }

    //inisialisai widget
    private void init() {
        add_barang = findViewById(R.id.add);
        recyclerView = findViewById(R.id.list_barang);
        swipeRefreshLayout = findViewById(R.id.swipe);
        sessionManager = new SessionManager(this);
        textViewJudul = findViewById(R.id.title);
        textViewJudul.setText("Tambah barang");
        back = findViewById(R.id.back);
        imageViewNotif = findViewById(R.id.notif);
        badge_value = findViewById(R.id.count_notif);


        //inisialisasi widget dialog input
        inflater_tambah = getLayoutInflater();
        dialog_tambah = new AlertDialog.Builder(this);
        dialog_view_tambah = inflater_tambah.inflate(R.layout.form_add_barang, null);

        //inisialisai widget edit input
        inflater_edit = getLayoutInflater();
        dialog_edit = new AlertDialog.Builder(this);
        dialog_view_edit = inflater_edit.inflate(R.layout.form_add_barang, null);


    }

    private void event() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            }
        });
        add_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBarangToDb();
            }
        });

        imageViewNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });

    }

    //add barang ke database
    private void saveBarangToDb() {
        if (dialog_view_tambah != null) {
            ViewGroup parent = (ViewGroup) dialog_view_tambah.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }

        dialog_tambah.setView(dialog_view_tambah);
        dialog_tambah.setCancelable(false);
        dialog_tambah.setIcon(R.mipmap.ic_launcher);
        dialog_tambah.setTitle("Form input barang ");

        spinnerStatus = dialog_view_tambah.findViewById(R.id.pilih_status);
        spinnerSatuan = dialog_view_tambah.findViewById(R.id.pilih_satuan);
        spinnerKategori = dialog_view_tambah.findViewById(R.id.pilih_kategori);
        editTextNama = dialog_view_tambah.findViewById(R.id.nama_barang);
        editTextharga = dialog_view_tambah.findViewById(R.id.harga);
        editTextKet = dialog_view_tambah.findViewById(R.id.ket);
        editTextStok = dialog_view_tambah.findViewById(R.id.stok);
        editTextSatuan = dialog_view_tambah.findViewById(R.id.satuan);
        layout_satuan_lain = dialog_view_tambah.findViewById(R.id.layout_satuan);
        lay_satuan = dialog_view_tambah.findViewById(R.id.lay_satuan);

        gambar_place_tambah = dialog_view_tambah.findViewById(R.id.gambar);
        cari_gambar = dialog_view_tambah.findViewById(R.id.cari);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spiner_item, liststok);
        spinnerStatus.setAdapter(adapter);
        getDataKategori();
        final ArrayAdapter<String> adapterSatuan = new ArrayAdapter<>(getApplicationContext(), R.layout.spiner_item, listsatuan);
        spinnerSatuan.setAdapter(adapterSatuan);

        cari_gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(PICK_IMAGE_REQUEST);
            }
        });
        dialog_tambah.setPositiveButton("Simpan", null);
        dialog_tambah.setNegativeButton("Cancel", null);
        final AlertDialog alertDialog = dialog_tambah.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_nama_barang = editTextNama.getText().toString();
                s_harga = editTextharga.getText().toString();
                s_stok = editTextStok.getText().toString();
                s_ket = editTextKet.getText().toString();

                if (s_nama_barang.isEmpty()) {
                    editTextNama.setError("kosong");
                } else if (s_harga.isEmpty()) {
                    editTextharga.setError("kosong");
                } else if (s_stok.isEmpty()) {
                    editTextStok.setError("kosong");
                } else if (s_ket.isEmpty()) {
                    editTextKet.setError("kosong");
                } else if (editTextSatuan.getText().toString().isEmpty() && lay_satuan.getVisibility() == View.GONE) {
                    editTextSatuan.setText("Kosong");
                } else if (spinnerStatus.getSelectedItem() == "Pilih Status") {
                    Toast.makeText(getApplicationContext(), "Belum memilih status", Toast.LENGTH_LONG).show();
                } else if (spinnerSatuan.getSelectedItem() == "Pilih Satuan") {
                    Toast.makeText(getApplicationContext(), "Belum memilih satuan", Toast.LENGTH_LONG).show();
                } else if (spinnerKategori.getSelectedItem() == "Pilih Kategori") {
                    Toast.makeText(getApplicationContext(), "Belum memilih Kategori", Toast.LENGTH_LONG).show();
                } else {
                    alertDialog.dismiss();
                    uploadBitmap(bitmap);

                }
            }
        });

    }


    private void uploadBitmap(final Bitmap bitmap) {
        swipeRefreshLayout.setRefreshing(true);
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.INSERT_BARANG,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if (obj.getString("status").equals("sukses")) {
                                swipeRefreshLayout.setRefreshing(false);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nama_barang", s_nama_barang);
                params.put("keterangan", s_ket);
                params.put("harga", s_harga);
                params.put("stok", s_stok);
                params.put("status", spinnerStatus.getSelectedItem().toString());
                if (lay_satuan.getVisibility() == View.GONE) {
                    params.put("satuan", editTextSatuan.getText().toString());
                } else {
                    params.put("satuan", spinnerSatuan.getSelectedItem().toString());
                }
                params.put("slug", s_nama_barang.replace(" ", "-") + ".html");
                params.put("id_kategori", String.valueOf(id_kategori));
                params.put("id_suplayer", sessionManager.getSpIduser());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("gambar", new DataPart(s_nama_barang.replace(" ", "-") + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }


        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    //edit barang
    @Override
    public void editItem() {
        if (dialog_view_edit != null) {
            ViewGroup parent = (ViewGroup) dialog_view_edit.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }

        dialog_edit.setView(dialog_view_edit);
        dialog_edit.setCancelable(true);
        dialog_edit.setIcon(R.mipmap.ic_launcher);
        dialog_edit.setTitle("Form edit barang ");
        spinnerStatus = dialog_view_edit.findViewById(R.id.pilih_status);
        spinnerSatuan = dialog_view_edit.findViewById(R.id.pilih_satuan);
        spinnerKategori = dialog_view_edit.findViewById(R.id.pilih_kategori);
        editTextNama = dialog_view_edit.findViewById(R.id.nama_barang);
        editTextharga = dialog_view_edit.findViewById(R.id.harga);
        editTextKet = dialog_view_edit.findViewById(R.id.ket);
        editTextStok = dialog_view_edit.findViewById(R.id.stok);
        editTextSatuan = dialog_view_edit.findViewById(R.id.satuan);
        layout_satuan_lain = dialog_view_edit.findViewById(R.id.layout_satuan);
        lay_satuan = dialog_view_edit.findViewById(R.id.lay_satuan);
        gambar_place_edit = dialog_view_edit.findViewById(R.id.gambar);
        editTextNama.setText(nama_barang);
        editTextharga.setText(harga);
        editTextStok.setText("" + stok);
        editTextKet.setText(keterangan);


        cari_gambar = dialog_view_edit.findViewById(R.id.cari);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spiner_item, liststok);
        spinnerStatus.setAdapter(adapter);
        getDataKategori();
        final ArrayAdapter<String> adapterSatuan = new ArrayAdapter<>(getApplicationContext(), R.layout.spiner_item, listsatuan);
        spinnerSatuan.setAdapter(adapterSatuan);


        cari_gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(PICK_IMAGE_EDIT);
            }
        });
        dialog_edit.setPositiveButton("Update", null);
        AlertDialog alertDialog = dialog_edit.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_nama_barang = editTextNama.getText().toString();
                s_harga = editTextharga.getText().toString();
                s_stok = editTextStok.getText().toString();
                s_ket = editTextKet.getText().toString();
                swipeRefreshLayout.setRefreshing(true);
                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, EndPoints.INSERT_BARANG,
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                try {
                                    JSONObject obj = new JSONObject(new String(response.data));
                                    String status =  obj.getString("status");
                                    if (status.equals("sukses")){
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(getIntent());
                                        overridePendingTransition(0, 0);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {


                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("nama_barang", s_nama_barang);
                        params.put("keterangan", s_ket);
                        params.put("harga", s_harga);
                        params.put("stok", s_stok);
                        params.put("status", spinnerStatus.getSelectedItem().toString());
                        if (lay_satuan.getVisibility() == View.GONE) {
                            params.put("satuan", editTextSatuan.getText().toString());
                        } else {
                            params.put("satuan", spinnerSatuan.getSelectedItem().toString());
                        }

                        params.put("slug", s_nama_barang.replace(" ", "-") + ".html");
                        params.put("id_kategori", String.valueOf(id_kategori));
                        params.put("id_suplayer", sessionManager.getSpIduser());
                        return params;
                    }

                    @Override
                    protected Map<String, DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();
                        params.put("pic", new DataPart(s_nama_barang.replace(" ", "-") + ".jpg", getFileDataFromDrawable(bitmap)));
                        return params;
                    }
                };

                //adding the request to volley
                Volley.newRequestQueue(AddBarang.this).add(volleyMultipartRequest);
            }
        });
    }

    //hapus barang
    @Override
    public void deleteItem(final int id_barang) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete_barang, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.contains("File deleted")) {
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
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
                map.put("id_barang", String.valueOf(id_barang));
                map.put("gambar", gambar);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void getDataKategori() {
        listKoategori.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_view_kategori, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listKoategori.add(0, "Pilih Kategori");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        int id = data.getInt("id_kategori");
                        String kategori = data.getString("kategori");
                        ModelKategori modelKategori = new ModelKategori(
                                id,
                                kategori
                        );
                        modelKategoris.add(modelKategori);
                    }
                    for (int i = 0; i < modelKategoris.size(); i++) {
                        listKoategori.add(modelKategoris.get(i).getKategori());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.spiner_item, listKoategori);
                    spinnerKategori.setAdapter(spinnerAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();

            }
        });
        Volley.newRequestQueue(this).add(stringRequest);

        spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinnerKategori.getSelectedItem().toString().equals("Pilih Kategori")) {
                    id_kategori = modelKategoris.get(position - 1).getId_kategori();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSatuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinnerSatuan.getSelectedItem().toString().equals("Pilih Satuan")) {
                    if (spinnerSatuan.getSelectedItem().toString().equals("Lainnya")) {
                        lay_satuan.setVisibility(View.GONE);
                        layout_satuan_lain.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // ubah bitmat ke string
    private String imagetoString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageType = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageType, Base64.DEFAULT);
    }

    //swipe refresh
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
                loadDataBarang();


            }
        });

    }

    //inisialisai objek
    private void object() {
        categories = new Categories();
        categories.productsArrayList = new ArrayList<>();

        adapter = new Recycle_adapterViewBarangInput(getApplicationContext(), categories, AddBarang.this, AddBarang.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onRefresh() {
        loadDataBarang();
    }

    //load barang
    private void loadDataBarang() {


        swipeRefreshLayout.setRefreshing(true);
        if (categories.productsArrayList != null) {
            categories.productsArrayList.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_view_barang_input, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            id_barang = data.getInt("id_barang");
                            nama_barang = data.getString("nama_barang");
                            gambar = data.getString("gambar");
                            harga = data.getString("harga");
                            stok = data.getInt("stok");
                            satuan = data.getString("satuan");
                            status = data.getString("status");
                            keterangan = data.getString("keterangan");

                            Products products = new Products(
                                    id_barang,
                                    nama_barang,
                                    gambar,
                                    harga,
                                    stok,
                                    satuan,
                                    status,
                                    keterangan

                            );
                            categories.productsArrayList.add(products);
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
                    swipeRefreshLayout.setRefreshing(false);

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

    //view badge notifikasi
    private void viewBadge() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_BADGE_NOTIFIKASI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        int badge = data.getInt("badge");
                        badge_value.setText("" + badge);
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
        Volley.newRequestQueue(this).add(stringRequest);
    }

    //cari gambar
    private void showFileChooser(int pick_image) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), pick_image);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                gambar_place_tambah.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_EDIT && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                gambar_place_edit.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


