package golden.com.checkin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ConfigActivity extends AppCompatActivity {
    SharedPreferences sp;
    String localid;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();

        sp = getApplicationContext().getSharedPreferences("prefs", 0);
        localid= sp.getString("localid","");
((TextView)findViewById(R.id.idname)).setText(sp.getString("nomelocal","")+ "   Ver : "+BuildConfig.VERSION_NAME);        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUsers(localid);
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),checkinActivity.class));
            }
        });
        lv= findViewById(R.id.list);
        getUsers(localid);
    }

        void getUsers(final String localid)
        {
            final List<dispouser> users= new ArrayList<>();
            final String uri= HttpUtils.dispURL + localid;
            utils.toast(getApplicationContext(),"Atualizando informações");
            HttpUtils.postByUrl(HttpUtils.sorturl + localid, new RequestParams(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    JSONObject jo;
                    String sorteadoId ="";
                    try {
                        jo= new JSONObject(new String( responseBody));
                        int limitresg= jo.getInt("limiteResgateSegundos");
                        if(limitresg!=0)
                        {
                            sorteadoId= jo.getJSONArray("sorteados").getJSONObject(0).getString("user_id");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String finalSorteadoId = sorteadoId;
                    HttpUtils.postByUrl(uri,null, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            //utils.log("Sucesooooo "+new String(responseBody));
                            try {
                                JSONObject jsonObj = new JSONObject(new String(responseBody));
                                final JSONArray dipofuts = jsonObj.getJSONArray("disponiveis_futuros");
                                //utils.log("TENHO "+dipofuts.length());
                                //utils.log(uri);
                                String uri2=HttpUtils.SorteiosURL+localid;
                                // CODIGO PARA MOSTRAR O SORTEIO
                                try{
                                if(jsonObj.getJSONArray("proximo_agendamento").length()>0){
                                     JSONObject proxsortarr= jsonObj.getJSONArray("proximo_agendamento").getJSONObject(0);
                                  //  JSONObject proxsortarr = sorteios.getJSONObject(i);
                                    // utils.log(proxsortarr);
                                    String proxsortt = proxsortarr.getString("horario"); //.toString().split("\\s+")[1].substring(0, 5);
                                    String proxhora = proxsortt;
                                    //Debug.Log(proxsortarr[0]["descricao_premio"])
                                    String proxpremio = "";
                                    proxpremio = proxsortarr.getString("descricao_premio");
                                    if (proxsortarr.getString("tipo_premio").equals("VALOR")) {
                                        proxpremio = "R$ " + proxsortarr.getString("valor");
                                    } else if (proxsortarr.getString("tipo_premio").equals("SPIN")) {
                                        proxpremio = "SPIN " + proxsortarr.getString("qtd_spin") + "x";
                                    }

                                    ((TextView)findViewById(R.id.textView2)).setText( proxhora+" "+ proxpremio +System.getProperty("line.separator")+dipofuts.length()+" Disponíveis");

                                }else{
                                    ((TextView)findViewById(R.id.textView2)).setText( "Não há sorteio programado" +System.getProperty("line.separator")+"0 Disponíveis");

                                }
                                }
                                 catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                               /* HttpUtils.get(uri2,new RequestParams(), new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                                        try {
                                            JSONObject json = new JSONObject(new String(responseBody));
                                            JSONArray sorteios= json.getJSONArray("lista_agendamentos");
                                            utils.log("TENHO "+ sorteios.length()+" sorteiros");
                                           // utils.log(uri);
                                            if(sorteios.length()>0){
                                                for(int i=0;i<1;i++)
                                                {
                                                    try {
                                                        JSONObject proxsortarr = sorteios.getJSONObject(i);
                                                       // utils.log(proxsortarr);
                                                        String proxsortt = proxsortarr.getString("horario"); //.toString().split("\\s+")[1].substring(0, 5);
                                                        String proxhora = proxsortt;
                                                        //Debug.Log(proxsortarr[0]["descricao_premio"])
                                                        String proxpremio = "";
                                                        proxpremio = proxsortarr.getString("descricao_premio");
                                                        if (proxsortarr.getString("tipo_premio").equals("VALOR")) {
                                                            proxpremio = "R$ " + proxsortarr.getString("valor");
                                                        } else if (proxsortarr.getString("tipo_premio").equals("SPIN")) {
                                                            proxpremio = "SPIN " + proxsortarr.getString("qtd_spin") + "x";
                                                        }
                                                        String situ= "";
                                                        try {
                                                            situ = proxsortarr.getString("situacao").toLowerCase();
                                                        }catch (Exception ex){}
                                                        ((TextView)findViewById(R.id.textView2)).setText( proxhora+" "+ proxpremio +System.getProperty("line.separator")+dipofuts.length()+" Disponíveis");

                                                    }catch (Exception ex)
                                                    {
                                                        utils.log(ex);
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            utils.toast(getBaseContext(),"Falha ao converter resultado");
                                        }

                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        getUsers(localid);

                                    }
                                });*/


                                for(int i=0;i<dipofuts.length();i++) {
                                    try {
                                        JSONObject jb = (JSONObject) dipofuts.get(i);
                                        long a=0;
                                        if(!jb.getString("ultimo_checkin").equals("")){
                                            Date on = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jb.getString("ultimo_checkin")));
                                            a= (Calendar.getInstance().getTime().getTime()-on.getTime())/ (1000 * 60 ) ;}
                                        long d=0;
                                        if(!jb.getString("penultimo_checkin").equals("")) {
                                            Date ult = ((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jb.getString("penultimo_checkin"))));
                                            d = (Calendar.getInstance().getTime().getTime() - ult.getTime()) / (1000 * 60 * 60 * 24);
                                        }
                                        dispouser dp = new dispouser(jb.getString("name"),a,d,jb.getString("total_chekins"),jb.getString("picture"),jb.getInt("bloqueado"),jb.getString("user_id"));
                                        if(dp.id.equals(finalSorteadoId))
                                        {
                                            dp.premiado=true;
                                            users.add(0,dp);
                                        }else
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
                                    public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                                        final dispouser urs= users.get(i);
                                        String command= urs.cad!=0? "desbloquear":"bloquear";
                                        RequestParams rp = new RequestParams();
                                        rp.add("user_id",urs.id);
                                        rp.add("local_id",localid);
                                        rp.add("command",command);
                                        utils.log("meu rp "+rp +" meu id "+urs.id);
                                        HttpUtils.post(HttpUtils.ActionsURL, rp, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                utils.log("resposta ai "+ new String(responseBody));
                                                urs.cad=urs.cad!=0?0:1;
                                                view.findViewById(R.id.cadeado).setVisibility(view.findViewById(R.id.cadeado).getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);
                                                ((TextView)view.findViewById(R.id.block)).setText(urs.cad+" Blocks");

                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                utils.toast(view.getContext(),"Falha ao efetuar ação");
                                            }
                                        });
                                    }
                                });
                                lv.setLongClickable(true);
                                lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                                        LayoutInflater inflater = getLayoutInflater();
                                        int width = LinearLayout.LayoutParams.MATCH_PARENT;
                                        int height = LinearLayout.LayoutParams.MATCH_PARENT;
                                        boolean focusable = true; // lets taps outside the popup also dismiss it
                                        final PopupWindow popupWindow = new PopupWindow(inflater.inflate(R.layout.dialogalert, null, false), width, height, focusable);

                                        // show the popup window
                                        // which view you pass in doesn't matter, it is only used for the window tolken
                                        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                                        View v= popupWindow.getContentView();
                                        ((TextView)v.findViewById(R.id.username)).setText(users.get(i).name);
                                        ((CircularImageView)v.findViewById(R.id.playimg)).setImageDrawable(((CircularImageView)view.findViewById(R.id.playimg)).getDrawable());
                                       // v.findViewById(R.id.checkinbt).setEnabled(users.get(i).premiado);
                                        popupWindow.getContentView().findViewById(R.id.checkinbt3).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                popupWindow.dismiss();
                                            }
                                        });
                                        popupWindow.getContentView().findViewById(R.id.loginbt).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                RequestParams rp = new RequestParams();
                                                String command= "checkout_usuario";
                                                rp.add("user_id",users.get(i).id);
                                                rp.add("local_id",localid);
                                                rp.add("command",command);
                                                utils.log(rp);
                                                HttpUtils.post(HttpUtils.ActionsURL, rp, new AsyncHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                        utils.toast(lv.getContext(),"Checkout efetuado com sucesso");
                                                        utils.log("Checkout "+ new String(responseBody));
                                                        getUsers(localid);
                                                        popupWindow.dismiss();

                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                        utils.toast(lv.getContext(),"Falha ao efetuar checkout");

                                                    }
                                                });
                                            }
                                        });
                                        popupWindow.getContentView().findViewById(R.id.checkinbt).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                utils.efetuaCheckin(getApplicationContext(),users.get(i).id,localid,findViewById(R.id.refresh),false);
                                                popupWindow.dismiss();
                                            }
                                        });


                                        return true;
                                    }

                                });
                            }


                            catch (JSONException e) {
                                utils.toast(getBaseContext(),"Falha ao obter dados");
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            utils.log("Falhouuu "+error);
                        }
                    });

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    getUsers(localid);
                }
            });

        }
    class useradapter extends BaseAdapter {

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
            ((TextView)v.findViewById(R.id.ontime)).setText("On - "+ user.ontime/60 +"H :"+ user.ontime%60 +" mins");
            ((TextView)v.findViewById(R.id.ultvisi)).setText("Ultima visita - "+user.ultivisita+" dias");
            ((TextView)v.findViewById(R.id.visitas)).setText(user.qntvisit+" Checkin");
            ((TextView)v.findViewById(R.id.block)).setText(user.cad+" Bloqueios");
            if(user.cad!=0)
            {
                v.findViewById(R.id.cadeado).setVisibility(View.VISIBLE);
            }
            new DownloadImageTask((CircularImageView) v.findViewById(R.id.playimg))
                    .execute(HttpUtils.imgplace+user.imagge);
            if(user.premiado)
            {
                v.findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
                ((ImageView)v.findViewById(R.id.bgbarra)).setImageResource(R.drawable.barraazulescura);
            }
            return v;
        }
    }}

