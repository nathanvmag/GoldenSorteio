package golden.com.checkin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapEditText;
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

public class nextsorteios extends AppCompatActivity {
    ListView lv ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nextsorteios);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        final SharedPreferences sp = getApplicationContext().getSharedPreferences("prefs", 0);
        final String localid= sp.getString("localid","");

        findViewById(R.id.backbt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
         ((TextView)findViewById(R.id.idname)).setText(sp.getString("nomelocal","")+ "   Ver : "+BuildConfig.VERSION_NAME);        lv= findViewById(R.id.list);
        findViewById(R.id.refresh2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSorteios(localid);
            }
        });
        getSorteios(localid);
    }
    void getSorteios(final String localid){
        utils.toast(getApplicationContext(),"Obtendo sorteios");
        final List<nextsorteio> nx= new ArrayList<>();
        final String uri=HttpUtils.SorteiosURL+localid;
        final List<nextsorteio> bloqs=new ArrayList<>();

        final List<nextsorteio> blosqamanha= new ArrayList<>();
        final List<nextsorteio> nxamanha= new ArrayList<>();
        final int newdayweek=Calendar.getInstance().get(Calendar.DAY_OF_WEEK)==7?1:Calendar.getInstance().get(Calendar.DAY_OF_WEEK)+1;

        HttpUtils.get(uri,new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                nextsortadpter adp= new nextsortadpter(nx);
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    JSONArray sorteios= json.getJSONArray("agendamentos");
                    utils.log("TENHO "+ sorteios.length()+" sorteiros");
                    utils.log(uri);
                    for(int i=0;i<sorteios.length();i++)
                    {
                        try {
                            JSONObject proxsortarr = sorteios.getJSONObject(i);
                            utils.log(proxsortarr);
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
                            boolean situa =situ.equals("inativo");
                            String semana = proxsortarr.getString("dias_semana");
                            int dayweek=0;
                            utils.log(semana);
                            if(!semana.equals(""))
                            {
                                try{
                                dayweek=Integer.parseInt(semana.substring(2))-1;
                            }catch (Exception ex){}
                            }else
                            dayweek=7;


                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date dt=format.parse(proxsortarr.getString("date_time"));
                            Calendar calender = Calendar.getInstance();
                            calender.setTime(dt);
                            String data= calender.get(Calendar.DAY_OF_MONTH)+"/"+(calender.get(Calendar.MONTH)>9?calender.get(Calendar.MONTH):"0"+calender.get(Calendar.MONTH))+"/"+calender.get(Calendar.YEAR);
                           // utils.log(dayweek+ " "+ newdayweek+" "+ Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
                            if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)==dayweek+1) {

                                int hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                                int minutes = Calendar.getInstance().get(Calendar.MINUTE);
                                int minhahora= Integer.parseInt( proxhora.split(":")[0]);
                                int meuminuto= Integer.parseInt(proxhora.split(":")[1]);
                                boolean passou = (hora>minhahora) || (hora==minhahora&& minutes>meuminuto);

                                utils.log(hora+" "+ minhahora+"     "+minutes+ " "+ meuminuto+ "   "+passou);

                                if (situa)
                                    bloqs.add(new nextsorteio(proxpremio, proxhora, proxsortarr.getString("id"), localid, situa, proxsortarr.getString("tipo_premio"), dayweek, data,true,passou));
                                else
                                    nx.add(new nextsorteio(proxpremio, proxhora, proxsortarr.getString("id"), localid, situa, proxsortarr.getString("tipo_premio"), dayweek, data,true,passou));
                            }

                            if(dayweek==newdayweek-1) {
                                if (situa)
                                    blosqamanha.add(new nextsorteio(proxpremio, proxhora, proxsortarr.getString("id"), localid, situa, proxsortarr.getString("tipo_premio"), dayweek, data,false,false));
                                else
                                    nxamanha.add(new nextsorteio(proxpremio, proxhora, proxsortarr.getString("id"), localid, situa, proxsortarr.getString("tipo_premio"), dayweek, data,false,false));
                            }

                        }catch (Exception ex)
                        {
                            utils.log(ex);
                        }
                    }nx.addAll(bloqs);
                    nx.addAll(nxamanha);
                    nx.addAll(blosqamanha);




                lv.setAdapter(adp);
                lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, final long l) {
                        final nextsorteio ntx= nx.get(i);

                            LayoutInflater inflater = getLayoutInflater();
                            int width = LinearLayout.LayoutParams.MATCH_PARENT;
                            int height = LinearLayout.LayoutParams.MATCH_PARENT;
                            boolean focusable = true; // lets taps outside the popup also dismiss it
                            final PopupWindow popupWindow = new PopupWindow(inflater.inflate(R.layout.valuealert, null, false), width, height, focusable);

                            // show the popup window
                            // which view you pass in doesn't matter, it is only used for the window tolken
                            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                            final View v= popupWindow.getContentView();
                            ((BootstrapEditText)v.findViewById(R.id.horariochange)).setText(ntx.date);
                            if(ntx.tipo.equals("VALOR")) {
                                ((BootstrapEditText)v.findViewById(R.id.newvalue)).setInputType(InputType.TYPE_CLASS_NUMBER);
                            }
                            ((BootstrapEditText)v.findViewById(R.id.newvalue)).setText(ntx.name.replace("R$ ","").replace(',','.'));
                            v.findViewById(R.id.checkinbt3).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    popupWindow.dismiss();
                                }
                            });
                            v.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String newvalue= String.valueOf(((BootstrapEditText)v.findViewById(R.id.newvalue)).getText());
                                    if(ntx.tipo.equals("VALOR")){
                                   if(!newvalue.equals("")){
                                       if(newvalue.contains(".")||newvalue.contains(","))
                                    {
                                        newvalue= newvalue.replace('.',',');
                                    }
                                    else {
                                        newvalue+=",00";
                                       }}}
                                       final RequestParams rp = new RequestParams();
                                            rp.add("command","valor_sorteio");
                                            rp.add("local_id",localid);
                                            rp.add("reg_id",ntx.id);
                                            rp.add("valor",newvalue);
                                            rp.add("tipo",ntx.tipo);
                                            utils.log(ntx.tipo);
                                            HttpUtils.postByUrl(HttpUtils.ActionsURL, rp, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            utils.toast(getApplicationContext(),"Sucesso ao editar sorteio");
                                            getSorteios(localid);
                                            popupWindow.dismiss();
                                            utils.log(new String(responseBody));
                                            utils.log(rp);
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            utils.toast(getApplicationContext(),"Falha ao editar sorteio");
                                            popupWindow.dismiss();
                                        }
                                    });
                                            String newhorario =((BootstrapEditText)v.findViewById(R.id.horariochange)).getText().toString();
                                            if(newhorario.length()==5)
                                            {
                                                if(newhorario.split(":").length==2)
                                                {
                                                  String[] a= newhorario.split(":");
                                                  if(a[0].length()==2&&a[1].length()==2)
                                                  {
                                                      int a0= Integer.parseInt(a[0]);
                                                      int a1= Integer.parseInt(a[1]);
                                                      if(a0<24&&a1<60)
                                                      {
                                                          RequestParams rps = new RequestParams();
                                                          rps.add("command","horario_sorteio");
                                                          rps.add("local_id",localid);
                                                          rps.add("reg_id",ntx.id);
                                                          rps.add("horario",newhorario);
                                                          HttpUtils.postByUrl(HttpUtils.ActionsURL, rps, new AsyncHttpResponseHandler() {
                                                              @Override
                                                              public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                                                              }

                                                              @Override
                                                              public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                  utils.toast(getApplicationContext(),"Falha ao editar sorteio");
                                                                  popupWindow.dismiss();
                                                              }
                                                          });

                                                      }
                                                      else utils.toast(getApplicationContext(),"Digite um Horário correto");

                                                  }
                                                  else utils.toast(getApplicationContext(),"Digite um Horário correto");

                                                }
                                                else utils.toast(getApplicationContext(),"Digite um Horário correto");

                                            }
                                            else utils.toast(getApplicationContext(),"Digite um Horário correto");


                                       RequestParams rps= new RequestParams();
                                   }

                            });

                        return false;
                    }
                });
                /*
                try{
                   if(json.getJSONArray("proximo_agendamento").length()>0){
                        JSONObject proxsortarr= json.getJSONArray("proximo_agendamento").getJSONObject(0);
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

                        ((TextView)findViewById(R.id.textView2)).setText( proxhora+ " "+ proxpremio +System.getProperty("line.separator"));


                    }
                    else {
                        ((TextView)findViewById(R.id.textView2)).setText( "Não há proxímo sorteio" +System.getProperty("line.separator"));

                    }
                }
                catch (JSONException e1) {
                    e1.printStackTrace();
                }*/}
                catch (JSONException e) {
                    utils.toast(getBaseContext(),"Falha ao converter resultado");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                utils.toast(getApplication().getApplicationContext(),"Falha ao obter sorteios do servidor");
                getSorteios(localid);
            }
        });

    }

}
class nextsortadpter extends BaseAdapter{

    public List<nextsorteio> list;
    public nextsortadpter(List<nextsorteio>lt)
    {
        list=lt;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final nextsorteio sort = list.get(i);
        View v= ((Activity)viewGroup.getContext()) .getLayoutInflater().inflate(R.layout.listsorteio,viewGroup,false);
        ((TextView)v.findViewById(R.id.sorteioname)).setText(sort.getresume());
        ((TextView)v.findViewById(R.id.semanaday)).setText(sort.dia);

        ((ImageView)v.findViewById(R.id.playpause)).setImageResource(sort.cancelado?R.drawable.pause:R.drawable.play);
        ((TextView)v.findViewById(R.id.sorteioname)).setTextColor(sort.cancelado? v.getResources().getColor(R.color.bootstrap_gray_light):v.getResources().getColor(R.color.bootstrap_brand_secondary_fill));
        if(sort.pass)            ((ImageView)v.findViewById(R.id.barrabg)).setImageResource(R.drawable.barraazul);

        if(!sort.dodia) {
            v.findViewById(R.id.instant).setVisibility(View.INVISIBLE);
            ((ImageView)v.findViewById(R.id.barrabg)).setImageResource(R.drawable.barraamarela);

        }
        if(sort.cancelado) ((ImageView)v.findViewById(R.id.barrabg)).setImageResource(R.drawable.barravermelha);

        v.findViewById(R.id.playpause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                RequestParams rp = new RequestParams();

                rp.add("reg_id",sort.id);
                rp.add("local_id",sort.localid);
                rp.add("command",list.get(i).cancelado?"desbloquear_sorteio":"bloquear_sorteio" );

                HttpUtils.post(HttpUtils.ActionsURL, rp, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(view.getContext() instanceof Activity)
                        {
                            utils.log("sucesso "+ new String(responseBody));
                            ((nextsorteios) view.getContext()).getSorteios(sort.localid);
                           // list.get(i).cancelado=! list.get(i).cancelado;
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        utils.toast(view.getContext(),"Falha ao Pausar sorteio avulso");
                    }
                });
            }
        });
        ((ImageView)v.findViewById(R.id.instant)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                RequestParams rp = new RequestParams();
                                rp.add("reg_id",sort.id);

                                rp.add("local_id",sort.localid);
                                rp.add("command","sorteio_avulso");

                                HttpUtils.post(HttpUtils.ActionsURL, rp, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        if(view.getContext() instanceof Activity)
                                        {
                                            utils.log("sucesso avulso"+ new String(responseBody));
                                            ((nextsorteios) view.getContext()).getSorteios(sort.localid);
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        utils.toast(view.getContext(),"Falha ao criar sorteio avulso");
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Você tem certeza que deseja sortear um(a) "+sort.name+" agora ?").setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener).show();

            }
        });
        return v;
    }
}
