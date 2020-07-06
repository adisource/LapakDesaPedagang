package id.lombokit.LapakDesaPedagang;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.lombokit.LapakDesaPedagang.Notifikasi.NotificationActivity;
import id.lombokit.LapakDesaPedagang.Root_url.EndPoints;
import id.lombokit.LapakDesaPedagang.Root_url.Url_root;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    SessionManager sessionManager;
    Url_root root = new Url_root();
    String slug_profil = "view_profil.php";
    String slug_update_username_password = "update_username_password.php";
    String slug_update_alamat = "update_alamat_suplayer.php";
    String slug_update_kontak = "update_kontak_suplayer.php";
    String url_profil = root.Url + slug_profil;
    String url_username_password = root.Url + slug_update_username_password;
    String url_update_alamat = root.Url + slug_update_alamat;
    String url_update_kontak = root.Url + slug_update_kontak;

    String pass;
    String username;
    String email;
    String kontak;
    String alamat;
    String kabupaten;
    String kecamatan;
    String nama_desa;
    String photo;


    ImageView kembali, pemberitahuan;
    TextView judul,
            textViewUsername,
            textViewKontak,
            textViewEmail,
            textViewNamaToko,
            textViewKab,
            textViewKec,
            textViewDesa,
            textViewAlamat,
            textViewPass,
            textViewEdit_username_pass, textVieweditAlamat, textViewEditKontak,badge_value;
    EditText editTextusername, editTextpassword, editTextAlamat, editTextKontak_NoHp, editTextEmail;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    LinearLayout clik_edit;
    View dialogView;
    ImageView place_image;
    Bitmap bitmap;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sessionManager = new SessionManager(this);
        init();
        getProfil();
        eventClick();
        eventEdit();
        viewBadge();
    }


    private void init() {
        kembali = findViewById(R.id.back);
        judul = findViewById(R.id.title);
        pemberitahuan = findViewById(R.id.notif);
        textViewUsername = findViewById(R.id.username);
        textViewKontak = findViewById(R.id.no_hp);
        textViewEmail = findViewById(R.id.email);
        //textViewKab = findViewById(R.id.kab);
        textViewKec = findViewById(R.id.kec);
        textViewDesa = findViewById(R.id.desa);
        textViewAlamat = findViewById(R.id.alamat);
        textViewPass = findViewById(R.id.password);
        textViewEdit_username_pass = findViewById(R.id.edit_username_pass);
        textViewEditKontak = findViewById(R.id.editkontak);
        textVieweditAlamat = findViewById(R.id.editalamat);
        clik_edit = findViewById(R.id.clik_add);
        place_image = findViewById(R.id.place_image);
        badge_value = findViewById(R.id.count_notif);

    }

    private void getProfil() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_profil, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        pass = data.getString("password");
                        username = data.getString("username");
                        email = data.getString("email");
                        kontak = data.getString("kontak");
                        alamat = data.getString("alamat");
                        kabupaten = data.getString("nama");
                        kecamatan = data.getString("nama_kec");
                        nama_desa = data.getString("nama_desa");
                        photo = data.getString("photo");

                        textViewUsername.setText(username);
                        textViewKontak.setText(kontak);
                        textViewEmail.setText(email);
//                        textViewKab.setText(kabupaten);
                        // textViewKec.setText(kecamatan);
                        textViewDesa.setText("Desa " + nama_desa + "\n kec." + kecamatan);
                        textViewAlamat.setText(alamat);
                        textViewPass.setText(pass);
                        if (photo.equals("")){
                            place_image.setImageResource(R.drawable.store);
                        }else{
                            Glide.with(getApplicationContext()).load(root.root+"upload/suplayer/"+photo).into(place_image);
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

    private void eventClick() {
        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            }
        });

        pemberitahuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });
        judul.setText((CharSequence) "Profil");

        clik_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImage();
            }
        });
    }

    private void eventEdit() {
        textViewEdit_username_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(ProfileActivity.this);
                inflater = getLayoutInflater();
                dialogView = inflater.inflate(R.layout.form_edit_username_pass, null);
                dialog.setView(dialogView);
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setTitle("Edit username dan password");
                editTextusername = dialogView.findViewById(R.id.username);
                editTextpassword = dialogView.findViewById(R.id.password);
                editTextusername.setText(username);
                editTextpassword.setText(pass);
                dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_username_password, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.contains("success")) {
                                    Toast.makeText(getApplicationContext(), "Berhasil di update", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Gagal update", Toast.LENGTH_LONG).show();
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
                                map.put("username", editTextusername.getText().toString());
                                map.put("password", editTextpassword.getText().toString());
                                return map;
                            }
                        };
                        Volley.newRequestQueue(ProfileActivity.this).add(stringRequest);

                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();


            }

        });
        textVieweditAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(ProfileActivity.this);
                inflater = getLayoutInflater();
                dialogView = inflater.inflate(R.layout.form_edit_alamat, null);
                dialog.setView(dialogView);
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setTitle("Edit alamat lengkap");
                editTextAlamat = dialogView.findViewById(R.id.alamat);
                editTextAlamat.setText(alamat);

                dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_update_alamat, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.contains("success")) {
                                    Toast.makeText(getApplicationContext(), "Berhasil di update", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Gagal update", Toast.LENGTH_LONG).show();
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
                                map.put("alamat", editTextAlamat.getText().toString());
                                return map;
                            }
                        };
                        Volley.newRequestQueue(ProfileActivity.this).add(stringRequest);

                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        textViewEditKontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(ProfileActivity.this);
                inflater = getLayoutInflater();
                dialogView = inflater.inflate(R.layout.form_edit_kontak, null);
                dialog.setView(dialogView);
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setTitle("Edit Kontak");
                editTextKontak_NoHp = dialogView.findViewById(R.id.nomer_hp);
                editTextEmail = dialogView.findViewById(R.id.email);
                editTextKontak_NoHp.setText(kontak);
                editTextEmail.setText(email);
                dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_update_kontak, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.contains("success")) {
                                    Toast.makeText(getApplicationContext(), "Berhasil di update", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Gagal update", Toast.LENGTH_LONG).show();
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
                                map.put("kontak", editTextKontak_NoHp.getText().toString());
                                map.put("email", editTextEmail.getText().toString());
                                return map;
                            }
                        };
                        Volley.newRequestQueue(ProfileActivity.this).add(stringRequest);

                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void editImage() {
        chooseFile();
    }

    private void chooseFile() {
        try {
            Intent intent = new Intent();
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 100);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "error" + e, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                place_image.setImageBitmap(scaled);
                updateBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void updateBitmap(final Bitmap bitmap) {
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPDATE_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", sessionManager.getSpIduser());
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
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
