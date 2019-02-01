package golden.com.checkin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;


import com.github.siyamed.shapeimageview.CircularImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bikecycle.com.bikecycle.HttpUtils;
import cz.msebera.android.httpclient.Header;

public class checkinActivity extends AppCompatActivity {
    SharedPreferences sp;
    ListView lv ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        getSupportActionBar().hide();
        final SharedPreferences sp = getApplicationContext().getSharedPreferences("prefs", 0);
        if (!sp.contains("localid")) {
            goback();
        }
        String localid= sp.getString("localid","");
        lv= findViewById(R.id.list);
         final List<dispouser>users= new ArrayList<>();

        bikecycle.com.bikecycle.HttpUtils.postByUrl(HttpUtils.dispURL + localid,null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        utils.log("Sucesooooo "+new String(responseBody));
                        try {
                            JSONObject jsonObj = new JSONObject(new String(responseBody));
                            JSONArray dipofuts = jsonObj.getJSONArray("disponiveis_futuros");
                            utils.log("TENHO "+dipofuts.length());
                            for(int i=0;i<dipofuts.length();i++) {
                                try {
                                    JSONObject jb = (JSONObject) dipofuts.get(i);
                                    Date on = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jb.getString("ultimo_checkin")));
                                          long a= (Calendar.getInstance().getTime().getTime()-on.getTime())/ (1000 * 60 ) ;

                                    dispouser dp = new dispouser(jb.getString("name"),a,(a/3600000>=24?a/86400000:a/3600000),50,jb.getString("picture"),jb.getInt("bloqueado"));
                                    users.add(dp);
                                }
                                catch (Exception ex)
                                {
                                    utils.log("Falhou nas data "+ ex);
                                }

                            }

                            useradapter at = new useradapter(users);
                            lv.setAdapter(at);
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    view.findViewById(R.id.cadeado).setVisibility(view.findViewById(R.id.cadeado).getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);
                                }
                            });
                        } catch (JSONException e) {
                            utils.toast(getBaseContext(),"Falha ao obter dados");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        utils.log("Falhouuu "+error);
                    }
                });



    }
    void goback()
    {
        sp.edit().clear().commit();
        startActivity(new Intent(this,MainActivity.class));
    }


}
class useradapter extends BaseAdapter{

    private final List<dispouser> lista;
    public useradapter(List<dispouser> list)
    {
        lista= list;
    }
    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int i) {
        return lista.get(i);

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        dispouser user = lista.get(i);
        View v= ((Activity)viewGroup.getContext()) .getLayoutInflater().inflate(R.layout.listlayout,viewGroup,false);
        ((TextView)v.findViewById(R.id.username)).setText(user.name);
        ((TextView)v.findViewById(R.id.ontime)).setText("On - "+ user.ontime+" mins");
        ((TextView)v.findViewById(R.id.ultvisi)).setText("Ultima visita - "+user.ultivisita+" dias");
        ((TextView)v.findViewById(R.id.visitas)).setText(user.qntvisit+" Visitas");
        if(user.cad!=0)
        {
            v.findViewById(R.id.cadeado).setVisibility(View.VISIBLE);
        }
        new DownloadImageTask((CircularImageView) v.findViewById(R.id.playimg))
                .execute(HttpUtils.imgplace+user.imagge);
                return v;
    }
}
