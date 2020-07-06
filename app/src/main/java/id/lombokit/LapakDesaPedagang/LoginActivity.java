package id.lombokit.LapakDesaPedagang;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.lombokit.LapakDesaPedagang.Root_url.EndPoints;
import id.lombokit.LapakDesaPedagang.Root_url.Url_root;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionAlert;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionManager;
import id.lombokit.LapakDesaPedagang.SessionManager.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {
    Url_root root = new Url_root();
    String slug_login = "login.php";
    String url_login = root.Url + slug_login;
    String url_view_profil = root.Url + "view_profil.php";
    int desa;
    String id_sup;

    SessionManager sessionManager;
    SessionAlert sessionAlert;

    ImageView selebihnya;
    TextView judul;
    TextView textViewUsername, textViewPass;
    TextView login, daftar, lupa_sandi, login_with_google;
    String token;

    String username, password;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    LinearLayout clik_edit;
    FirebaseAuth user;
    View dialogView;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);
        sessionAlert = new SessionAlert(this);
        init();
        eventClik();
    }


    private void init() {

        login = findViewById(R.id.login);
        textViewUsername = findViewById(R.id.username);
        textViewPass = findViewById(R.id.pass);
        username = textViewUsername.getText().toString();
        password = textViewPass.getText().toString();
        lupa_sandi = findViewById(R.id.lupa_pass);
        user = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        //Toast.makeText(getApplicationContext(),""+SharedPrefManager.getInstance(this).getDeviceToken(),Toast.LENGTH_LONG).show();
        token = SharedPrefManager.getInstance(this).getDeviceToken();


    }

    private void eventClik() {

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesLogin();
            }
        });
        lupa_sandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(LoginActivity.this);
                inflater = getLayoutInflater();
                dialogView = inflater.inflate(R.layout.form_email, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setTitle("Form Reset Password");
                final TextView kontak = dialogView.findViewById(R.id.email);


                dialog.setPositiveButton("Reset password", null);
                final AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.setMessage("Mohon tunggu sebentar");
                        progressDialog.show();
                        if (TextUtils.isEmpty(kontak.getText().toString())) {
                            dialog.setMessage("Anda belum memasukan email anda");

                        } else {
                            user.sendPasswordResetEmail(kontak.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Cek inbox email anda", Toast.LENGTH_LONG).show();
                                        alertDialog.dismiss();
                                        progressDialog.dismiss();

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Coba lagi", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }

                                }
                            });
                        }

                    }
                });
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });


            }
        });


    }

    private void prosesLogin() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        id_sup = jsonObject.getString("id_sup");
                        desa = jsonObject.getInt("desa");
                        sessionManager.saveString(SessionManager.SP_IDUSER, id_sup);
                        sessionManager.saveInt(SessionManager.SP_IDDESA, desa);
                        sessionManager.saveBoolean(SessionManager.SP_LOGINED, true);
                        Intent intent = new Intent(getApplicationContext(),DashboardActivity.class);
                        startActivity(intent);

                    } else if (status.equals("filed")) {
                        Toast.makeText(getApplicationContext(), "Data tidak di temukan", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Server not found" + error, Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("username", textViewUsername.getText().toString());
                map.put("password", textViewPass.getText().toString());
                return map;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);

    }


    private void saveTokenDevice() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Device...");
        progressDialog.show();

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_REGISTERED_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    Toast.makeText(LoginActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
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
                map.put("id_supplier", sessionManager.getSpIduser());
                map.put("token", token);
                map.put("desa", String.valueOf(desa));
                return map;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }


}
