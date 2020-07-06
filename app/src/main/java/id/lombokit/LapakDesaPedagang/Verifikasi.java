package id.lombokit.LapakDesaPedagang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import id.lombokit.LapakDesaPedagang.Root_url.EndPoints;
import id.lombokit.LapakDesaPedagang.Root_url.Url_root;
import id.lombokit.LapakDesaPedagang.SessionManager.SessionManager;
import id.lombokit.LapakDesaPedagang.SessionManager.SharedPrefManager;

public class Verifikasi extends AppCompatActivity {
    Url_root url_root = new Url_root();
    String url_view_profil=url_root.Url+"view_profil.php";
    SessionManager sessionManager;
    String nik;
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    PhoneAuthCredential phoneAuth;
    private String mVerifikasi;

    EditText get_code;
    Button verifikasi, btn_resend;
    String no_hp;
    CountDownTimer countDownTimer;
    TextView value_count;
    String id_sup;
    int id_desa_c,id_desa;
    String token;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        init();
        loadData();
        countDown();
        evenClik();
        verifkasiCode();


    }
    private void init() {
        sessionManager = new SessionManager(this);
        nik = sessionManager.getSpNikwali();
        get_code = findViewById(R.id.code_v);
        verifikasi = findViewById(R.id.verifikasi);
        auth = FirebaseAuth.getInstance();
        value_count = findViewById(R.id.count);
        btn_resend = findViewById(R.id.resend);
        id_desa = getIntent().getIntExtra("id_desa",0);

        id_sup = getIntent().getStringExtra("id_sup");
        token = SharedPrefManager.getInstance(this).getDeviceToken();
    }
  private void loadData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_view_profil, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        no_hp = jsonObject.getString("kontak");
                        Toast.makeText(getApplicationContext(),""+no_hp,Toast.LENGTH_LONG).show();
                        sendVerifikasi(no_hp);
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
                map.put("id_suplayer", id_sup);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void countDown() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String format = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                value_count.setText(format);
            }

            @Override
            public void onFinish() {
                String value_code = get_code.getText().toString();
                if (get_code.getText().toString().matches("")) {
                    btn_resend.setVisibility(View.VISIBLE);
                    value_count.setVisibility(View.GONE);
                } else {
                    value_count.setVisibility(View.GONE);
                }
            }
        }.start();
    }

    private void verifkasiCode() {
        verifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value_code = get_code.getText().toString().trim();
                if (value_code.isEmpty() || value_code.length() < 6) {
                    get_code.setError("input code");
                } else {
                    verifyVerificationCode(value_code);
                }
            }
        });
    }

    private void evenClik() {
        btn_resend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resendVerificationCode(no_hp);

                Toast.makeText(getApplicationContext(), "" + no_hp, Toast.LENGTH_LONG).show();
                value_count.setVisibility(View.VISIBLE);
                countDownTimer = new CountDownTimer(60000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        String format = String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                        value_count.setText(format);
                    }

                    @Override
                    public void onFinish() {
                        String value_code = get_code.getText().toString();
                        if (get_code.getText().toString().matches("")) {
                            btn_resend.setVisibility(View.VISIBLE);
                            value_count.setVisibility(View.GONE);
                        } else {
                            value_count.setVisibility(View.GONE);
                        }
                    }
                }.start();
            }
        });
    }

    private void sendVerifikasi(String no_hp) {
        setupCallback();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+62" + no_hp,
                60,
                TimeUnit.SECONDS,
                this,
                callbacks
        );
    }

    private void resendVerificationCode(String phoneNumber) {
        setupCallback();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+62" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                callbacks,         // OnVerificationStateChangedCallbacks
                resendToken);             // ForceResendingToken from callbacks
    }

    private void setupCallback() {
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerifikasi = s;
                resendToken = forceResendingToken;
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    get_code.setText(code);
                } else {
                    get_code.setError("kode kosong");
                    get_code.requestFocus();
                }

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Verifikasi.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerifikasi, code);
        signVerifikasi(credential);
    }

    private void signVerifikasi(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sessionManager.saveString(SessionManager.SP_IDUSER, id_sup);
                    sessionManager.saveInt(SessionManager.SP_IDDESA, id_desa);
                    sessionManager.saveBoolean(SessionManager.SP_LOGINED, true);
                    saveTokenDevice();
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);

                }

            }
        });

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
                    Toast.makeText(Verifikasi.this, object.getString("message"), Toast.LENGTH_LONG).show();
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
                map.put("id_supplier",sessionManager.getSpIduser());
                map.put("token", token);
                map.put("desa",String.valueOf(sessionManager.getSpIddesa()));
                return map;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            sessionManager.saveString(SessionManager.SP_IDUSER, "");
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
