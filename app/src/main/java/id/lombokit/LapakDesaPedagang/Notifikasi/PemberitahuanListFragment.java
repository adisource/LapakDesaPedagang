package id.lombokit.LapakDesaPedagang.Notifikasi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import id.lombokit.LapakDesaPedagang.Models.ListNotifikasi;
import id.lombokit.LapakDesaPedagang.Models.Notifikasi;
import id.lombokit.LapakDesaPedagang.Pesanan.UserPemesan;
import id.lombokit.LapakDesaPedagang.R;
import id.lombokit.LapakDesaPedagang.Root_url.EndPoints;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionManager;


public class PemberitahuanListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SessionManager sessionManager;

    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecycleAdapter_Notifikasi mAdapter;

    private ListNotifikasi listNotifikasi;
    private View view;

    RelativeLayout massage_kosong;


    Animation startAnimation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_pemberitahuan, container, false);
        startAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        initComponent(view);
        eventSwipe();
        object();

        return view;

    }


    private void initComponent(View view) {
        sessionManager = new SessionManager(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        massage_kosong = view.findViewById(R.id.massage_kosong);

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
        listNotifikasi = new ListNotifikasi();
        listNotifikasi.notifikasiArrayList = new ArrayList<>();
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
        if (listNotifikasi.notifikasiArrayList != null) {
            listNotifikasi.notifikasiArrayList.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_VIEW_NOTIFIKASI, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                   /* if (response.contains("kosong")){
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.massage, null);
                        ImageView gambar = view.findViewById(R.id.image);
                        gambar.setImageResource(R.drawable.ic_notifications_black_24dp);
                        TextView msg = view.findViewById(R.id.msg);
                        msg.setText("Pemberitahuan kosong");
                        massage_kosong.addView(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        recyclerView.setVisibility(View.GONE);
                    }*/
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            int id_pemberitahuan = data.getInt("id_pemberitahuan");
                            String tgl = data.getString("tgl");
                            String jam = data.getString("jam");
                            String judul = data.getString("judul");
                            String isi = data.getString("isi");
                            String from = data.getString("from_notif");

                            Notifikasi notifikasi = new Notifikasi(
                                    id_pemberitahuan,
                                    tgl,
                                    jam,
                                    judul,
                                    isi,
                                    from
                            );

                            listNotifikasi.notifikasiArrayList.add(notifikasi);
                            //Toast.makeText(getContext(),""+nama,Toast.LENGTH_SHORT).show();
                        }
                        mAdapter = new RecycleAdapter_Notifikasi(getContext(), listNotifikasi);
                        recyclerView.setAdapter(mAdapter);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(),""+error,Toast.LENGTH_LONG).show();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("id_sup", sessionManager.getSpIduser());
                    return map;
                }
            };

            Volley.newRequestQueue(getContext()).add(stringRequest);
        }
    }

    public class RecycleAdapter_Notifikasi extends RecyclerView.Adapter<RecycleAdapter_Notifikasi.MyViewHolder> {

        Context context;
        private ListNotifikasi listNotifikasi;


        public class MyViewHolder extends RecyclerView.ViewHolder {


            TextView judul;
            TextView isi;
            TextView jam;
            TextView quantityTxt;
            ImageView imageViewDelete;


            public MyViewHolder(View view) {
                super(view);

                judul = (TextView) view.findViewById(R.id.judul);
                isi = (TextView) view.findViewById(R.id.isi);
                jam = view.findViewById(R.id.jam);
                imageViewDelete = view.findViewById(R.id.delete);
            }
        }


        public RecycleAdapter_Notifikasi(Context context, ListNotifikasi listNotifikasi) {
            this.listNotifikasi = listNotifikasi;
            this.context = context;
        }

        @Override
        public RecycleAdapter_Notifikasi.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_notify, parent, false);

            return new RecycleAdapter_Notifikasi.MyViewHolder(itemView);
        }



        @Override
        public void onBindViewHolder(final RecycleAdapter_Notifikasi.MyViewHolder holder, final int position) {
            holder.judul.setText(listNotifikasi.getNotifikasiArrayList().get(position).getJudul());
            holder.isi.setText(listNotifikasi.getNotifikasiArrayList().get(position).getIsi());
            holder.jam.setText(listNotifikasi.getNotifikasiArrayList().get(position).getJam());
            holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Hapus notifikasi ini");
                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_DELETE_NOTIFIKASI, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    getActivity().finish();
                                    getActivity().overridePendingTransition(0, 0);
                                    getActivity().startActivity(getActivity().getIntent());
                                    getActivity().overridePendingTransition(0, 0);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> map = new HashMap<>();
                                    map.put("id_notif",String.valueOf(listNotifikasi.notifikasiArrayList.get(position).getId_pemberitahuan()));
                                    map.put("id_sup",sessionManager.getSpIduser());
                                    return map;
                                }
                            };
                            Volley.newRequestQueue(getContext()).add(stringRequest);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listNotifikasi.notifikasiArrayList.get(position).getFrom().equals("user")){
                        startActivity(new Intent(getContext(), UserPemesan.class));
                    }
                }
            });





        }

        @Override
        public int getItemCount() {
            return listNotifikasi.notifikasiArrayList.size();
        }

    }



}
