package id.lombokit.LapakDesaPedagang.Pesanan;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.lombokit.LapakDesaPedagang.Models.Categories;
import id.lombokit.LapakDesaPedagang.Models.Products;
import id.lombokit.LapakDesaPedagang.R;
import id.lombokit.LapakDesaPedagang.Root_url.Url_root;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class DaftarBarangStatusSukses extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    Url_root root = new Url_root();
    String slug_viewProduct = "view_product_status_sukses.php";
    String slug_proses_barang = "proses_barang.php";
    String url_viewProduct = root.Url + slug_viewProduct;

    String url_proses = root.Url + slug_proses_barang;
    String url_update_status = root.Url + "update_status.php";
    String url_push_noatifikasi = root.Url + "notifikasi_belanja.php";


    private Categories categories;
    private RecyclerView recyclerView;
    private Recycle_adapterStatusTunda adapter;
    SessionManager sessionManager;
    RelativeLayout layout_belanja_user, layout_cart;

    TextView textViewJudul, textViewValueSum;
    Button btn_proses;
    SwipeRefreshLayout swipeRefreshLayout;
    String desa, v_time;
    int id_user;


    public DaftarBarangStatusSukses() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daftar_barang_status_sukses, container, false);
        id_user = getArguments().getInt("id_user");
        desa = getArguments().getString("desa");

        init(view);
        eventSwipe();
        object();
        eventClick();
        return view;
    }

    private void init(View view) {
        sessionManager = new SessionManager(getContext());
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        recyclerView = view.findViewById(R.id.list_barang);


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
                loadDataBarang();
            }
        });

    }

    private void object() {

        sessionManager = new SessionManager(getContext());
        categories = new Categories();
        categories.productsArrayList = new ArrayList<>();
        adapter = new Recycle_adapterStatusTunda(getContext(), categories);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void eventClick() {
        getActivity().findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UserPemesan.class));
            }
        });
    }

    @Override
    public void onRefresh() {
        loadDataBarang();
    }

    private void loadDataBarang() {
        swipeRefreshLayout.setRefreshing(true);
        if (categories.productsArrayList != null) {
            categories.productsArrayList.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_viewProduct, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            int id_barang = data.getInt("id_barang");
                            String nama_barang = data.getString("nama_barang");
                            String gambar = data.getString("gambar");
                            String harga = data.getString("harga");
                            String qty = data.getString("qty");
                            String total = data.getString("total");
                            String jam = data.getString("jam");
                            String status = data.getString("status");
                            String get_token_device_user = data.getString("token");
                            int ongkir = data.getInt("ongkir");
                            String kode_order = data.getString("kode_order");
                            Products products = new Products(
                                    id_barang,
                                    nama_barang,
                                    gambar,
                                    harga,
                                    qty,
                                    total,
                                    jam,
                                    status,
                                    get_token_device_user,
                                    ongkir,
                                    kode_order

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

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("desa", desa);
                    map.put("id", String.valueOf(id_user));
                    map.put("id_sup", sessionManager.getSpIduser());
                    return map;
                }
            };
            Volley.newRequestQueue(getContext()).add(stringRequest);
        }

    }


    public class Recycle_adapterStatusTunda extends RecyclerView.Adapter<Recycle_adapterStatusTunda.My_viewHolder> {
        private Context context;
        private Categories categories;

        public Recycle_adapterStatusTunda(Context context, Categories categories) {
            this.context = context;
            this.categories = categories;
        }

        @NonNull
        @Override
        public Recycle_adapterStatusTunda.My_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_product, parent, false);
            return new Recycle_adapterStatusTunda.My_viewHolder(viewItem);
        }

        public class My_viewHolder extends RecyclerView.ViewHolder {

            ImageView imageViewImage;
            TextView textViewTitle;
            TextView textViewHarga;
            TextView textViewQuantityTxt;
            TextView textViewTotalharga;
            TextView textViewOngkir;
            TextView textViewJam;
            RadioGroup radioGroup;
            RadioButton radioButtonProses, radioButtonTolak;
            LinearLayout layout_red, layout_yellow, layout_green;
            Button btn_tolak, btn_proses, btn_selesaikan;


            public My_viewHolder(@NonNull View itemView) {
                super(itemView);
                textViewTitle = itemView.findViewById(R.id.nama_barang);
                textViewHarga = itemView.findViewById(R.id.price);
                textViewQuantityTxt = itemView.findViewById(R.id.quatity);
                textViewTotalharga = itemView.findViewById(R.id.totaL_harga);
                textViewOngkir = itemView.findViewById(R.id.ongkir);
                imageViewImage = itemView.findViewById(R.id.image);
                textViewJam = itemView.findViewById(R.id.value_tglpesanan);
                btn_tolak = itemView.findViewById(R.id.tolak);
                btn_proses = itemView.findViewById(R.id.proses);
                btn_selesaikan = itemView.findViewById(R.id.selesaikan);
                layout_red = itemView.findViewById(R.id.red);
                layout_green = itemView.findViewById(R.id.green);
                layout_yellow = itemView.findViewById(R.id.yellow);


            }
        }

        @Override
        public void onBindViewHolder(@NonNull final Recycle_adapterStatusTunda.My_viewHolder holder, final int position) {
            Locale localeID = new Locale("in", "ID");
            NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(localeID);
            int c_harga = Integer.parseInt(categories.getProductsArrayList().get(position).getHarga());
            int c_totalH = Integer.parseInt(categories.getProductsArrayList().get(position).getTotal());
            int ongkir = categories.getProductsArrayList().get(position).getOngkir();

            holder.textViewTitle.setText(categories.getProductsArrayList().get(position).getNama_barang());
            holder.textViewHarga.setText(rupiahFormat.format((double) c_harga));
            holder.textViewQuantityTxt.setText(categories.getProductsArrayList().get(position).getQty());
            holder.textViewTotalharga.setText(rupiahFormat.format((double) c_totalH));
            holder.textViewOngkir.setText(rupiahFormat.format((double) ongkir));
            holder.textViewJam.setText(categories.getProductsArrayList().get(position).getJam());
            Glide.with(getContext()).load(root.root + "/upload/barang/" + categories.getProductsArrayList().get(position).getgambar()).into(holder.imageViewImage);

            final int parse = Integer.parseInt(categories.getProductsArrayList().get(position).getStatus());
            if (parse == 2) {
                holder.btn_proses.setVisibility(View.GONE);
                holder.btn_tolak.setVisibility(View.GONE);

                holder.layout_green.setVisibility(View.VISIBLE);
                holder.layout_red.setVisibility(View.GONE);

            }


        }

        @Override
        public int getItemCount() {
            return categories.productsArrayList.size();
        }
    }

    private void push_notifikasi(final String s, final String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_push_noatifikasi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("token", token);
                map.put("title", "Pesan dari suplayer");
                map.put("body", s);
                return map;
            }
        };
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private void update_status(final int id_barang) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateNow = new SimpleDateFormat("yyyy/MM/dd h:m:s ");
        v_time = dateNow.format(calendar.getTime());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_update_status, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("success")) {
                    startActivity(new Intent(getContext(), UserPemesan.class));
                    Toast.makeText(getContext(), "Barang berhasil proses", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Barang gagal proses", Toast.LENGTH_LONG).show();
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
                map.put("id", String.valueOf(id_user));
                map.put("id_barang", String.valueOf(id_barang));
                return map;
            }
        };
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    public void onResume() {

        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Intent intent = new Intent(getContext(), UserPemesan.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    return true;

                }

                return false;
            }
        });
    }


}
