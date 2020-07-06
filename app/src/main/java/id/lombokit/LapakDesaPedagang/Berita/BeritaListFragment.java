package id.lombokit.LapakDesaPedagang.Berita;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import id.lombokit.LapakDesaPedagang.Models.Berita;
import id.lombokit.LapakDesaPedagang.Models.List_berita;
import id.lombokit.LapakDesaPedagang.R;
import id.lombokit.LapakDesaPedagang.Root_url.Url_root;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionManager;


public class BeritaListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    Url_root root_url = new Url_root();
    String url_desa = root_url.Url + "/tentang_desa.php";
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecycleAdapter_AddProduct mAdapter;
    SessionManager sessionManager;
    private List_berita list_berita;
    public String desa_user;

    private View view;


    RelativeLayout layout_berita_kosong;
    Animation startAnimation;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_berita, container, false);
        startAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        initComponent(view);
        eventSwipe();
        object();
        return view;

    }


    private void initComponent(View view) {
        sessionManager = new SessionManager(getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        layout_berita_kosong = view.findViewById(R.id.layout_berita_kosong);
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
        list_berita = new List_berita();
        list_berita.beritaArrayList = new ArrayList<>();

        mAdapter = new RecycleAdapter_AddProduct(getActivity(), list_berita);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public void onRefresh() {
        loadData();

    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        if (list_berita.beritaArrayList != null) {
            list_berita.beritaArrayList.clear();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_desa, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.contains("Belum ada berita")) {
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.massage, null);
                        ImageView gambar = view.findViewById(R.id.image);
                        gambar.setImageResource(R.drawable.ic_interface);
                        TextView msg = view.findViewById(R.id.msg);
                        msg.setText("Mohon maaf belum ada berita");
                        layout_berita_kosong.addView(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        recyclerView.setVisibility(View.GONE);
                    }
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            String judul = data.getString("judul");
                            String berita = data.getString("berita");
                            String time = data.getString("time");
                            Berita dBerita = new Berita();
                            dBerita.setJudul(judul);
                            dBerita.setIsi_berita(berita);
                            dBerita.setJam_post(time);
                            list_berita.beritaArrayList.add(dBerita);
                        }
                        recyclerView.setAdapter(mAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "" + error, Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("desa", desa_user);
                    return map;
                }
            };
            Volley.newRequestQueue(getContext()).add(stringRequest);
        }
    }


    public class RecycleAdapter_AddProduct extends RecyclerView.Adapter<RecycleAdapter_AddProduct.MyViewHolder> {

        Context context;
        boolean showingFirst = true;
        private List_berita list_berita;


        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView image;
            TextView title;
            TextView isi_berita;


            public MyViewHolder(View view) {
                super(view);
                image = (ImageView) view.findViewById(R.id.image);
                title = (TextView) view.findViewById(R.id.title);
                isi_berita = (TextView) view.findViewById(R.id.isi_berita);

            }
        }


        public RecycleAdapter_AddProduct(Context context, List_berita list_berita) {
            this.list_berita = list_berita;
            this.context = context;
        }

        @Override
        public RecycleAdapter_AddProduct.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_article, parent, false);

            return new RecycleAdapter_AddProduct.MyViewHolder(itemView);
        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(final RecycleAdapter_AddProduct.MyViewHolder holder, final int position) {
//            Products movie = productsList.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("ind", "" + position);
                    Intent i = new Intent(getContext(), DetailList_Berita.class);
                    i.putExtra("image", list_berita.getBeritaArrayList().get(position).getImage());
                    i.putExtra("judul", list_berita.getBeritaArrayList().get(position).getJudul());
                    i.putExtra("isi_berita", list_berita.getBeritaArrayList().get(position).getIsi_berita());
                    i.putExtra("jam_post", list_berita.getBeritaArrayList().get(position).getJam_post());
                    startActivity(i);
                }
            });

            holder.title.setText(list_berita.getBeritaArrayList().get(position).getJudul());
            holder.isi_berita.setText(list_berita.getBeritaArrayList().get(position).getIsi_berita().substring(0, 100) + "............");
            holder.image.setImageResource(R.drawable.berita_kominfo);
        }

        @Override
        public int getItemCount() {
            return list_berita.getBeritaArrayList().size();
        }

    }


}

