package golden.com.checkin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    BootstrapEditText login,senha;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        login= findViewById(R.id.bootstrapEditText2);
        senha = findViewById(R.id.bootstrapEditText);

         sp=getApplicationContext().getSharedPreferences("prefs",0);

        if(sp.contains("login")&&sp.contains("senha"))
        {
            login.setText(sp.getString("login",""));
            senha.setText(sp.getString("senha",""));
            //findViewById(R.id.loginbt).callOnClick();

        }

        findViewById(R.id.loginbt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Login();
            }
        });
    }

    public void Login()
    {
        final String logintx= login.getText().toString();
        final String passtx =senha.getText().toString();
        RequestParams rp= new RequestParams();
        rp.add("login",logintx.trim());
        rp.add("senha",passtx.trim());

        HttpUtils.postByUrl("http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/dev/autentica.php", rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jo = new JSONObject(new String(responseBody));
                    SharedPreferences.Editor edit = sp.edit();
                    if(jo.getString("local_id").equals(""))
                    {
                        utils.toast(getApplicationContext(),"Login ou Usuário incorreto");
                    }
                    else {
                        String tipo = jo.getString("tipo");
                        if(!(tipo.equals("V")||tipo.equals("PR")||tipo.equals("C"))){
                        edit.putString("login",logintx);
                        edit.putString("senha",passtx);
                        edit.putString("localid",jo.getString("local_id"));
                        edit.putString("nomelocal",jo.getString("nome_local"));
                        edit.putString("tipo",jo.getString("tipo"));
                        edit.commit();
                        startActivity(new Intent(getApplicationContext(),checkinActivity.class));
                    }
                    else utils.toast(getApplicationContext(),"Desculpe mas você não possui acesso ao sistema");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                utils.toast(getApplicationContext(),"Falha ao logar");
            }
        });
    }

}
