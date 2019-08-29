package golden.com.checkin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import golden.com.checkin.BuildConfig;


import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class checkinActivity extends AppCompatActivity {
    SharedPreferences sp;
    ListView lv;
   // List<dispouser> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        getSupportActionBar().hide();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final SharedPreferences sp = getApplicationContext().getSharedPreferences("prefs", 0);
        if (!sp.contains("localid")) {
            goback();
        }
        findViewById(R.id.proxsort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), nextsorteios.class));
            }
        });
        final String localid = sp.getString("localid", "");
        final String tipo = sp.getString("tipo","");
        if(!(tipo.equals("P")||tipo.equals("G")))
        {
            findViewById(R.id.config).setVisibility(View.INVISIBLE);
            findViewById(R.id.proxsort).setVisibility(View.INVISIBLE);

        }
        ((TextView)findViewById(R.id.idname)).setText(sp.getString("nomelocal","")+ " LocalId:"+getlast(4,localid)+"   Ver : "+BuildConfig.VERSION_NAME);
        findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), register.class));
            }
        });
        findViewById(R.id.efetuacheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.MATCH_PARENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(inflater.inflate(R.layout.poplay, null, false), width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                popupWindow.getContentView().findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });
                View v=  ( popupWindow.getContentView().findViewById(R.id.nomeinput));

                v.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                v.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));

                popupWindow.getContentView().findViewById(R.id.pescbt).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        final View vr = view;
                        String name = ((BootstrapEditText) popupWindow.getContentView().findViewById(R.id.nomeinput)).getText().toString();
                        utils.toast(view.getContext(), "Pesquisando " + name);
                        RequestParams rp = new RequestParams();
                        rp.add("command", "buscar_usuario");
                        rp.add("nome", name);
                        rp.add("local_id",localid);
                        HttpUtils.post(HttpUtils.Consulta, rp, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    final List<pesqUser> pesqlist = new ArrayList<>();
                                    utils.log(new String(responseBody));
                                    JSONArray objects = new JSONObject(new String(responseBody)).getJSONArray("nomes");
                                    for (int i = 0; i < objects.length(); i++) {
                                        JSONObject jo = (JSONObject) objects.get(i);
                                        pesqlist.add(new pesqUser(jo.getString("name"), jo.getString("picture"), jo.getString("user_id")));

                                    }
                                    pesquisaAdapter adp = new pesquisaAdapter(pesqlist);
                                    ((ListView) popupWindow.getContentView().findViewById(R.id.lview)).setAdapter(adp);
                                    ((ListView) popupWindow.getContentView().findViewById(R.id.lview)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            utils.efetuaCheckin(getApplicationContext(),pesqlist.get(i).userid,localid,findViewById(R.id.refresh),((CheckBox)popupWindow.getContentView().findViewById(R.id.ocultobox)).isChecked());
                                            popupWindow.dismiss();
                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                utils.toast(popupWindow.getContentView().getContext(), "Falha ao pesquisar");
                            }
                        });
                    }
                });

            }
        });
        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUsers(localid);
            }
        });
        findViewById(R.id.config).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ConfigActivity.class));
            }
        });
        lv = findViewById(R.id.list);
        getUsers(localid);
    }
    public String getlast(int a,String myString) {
        if(myString.length() > a)
            return myString.substring(myString.length()-a);
        else
            return myString;
    }
        void getUsers(final String localid)
        {
            RequestParams rp = new RequestParams();
            rp.add("command", "lista_checkins");
            rp.add("local_id",localid);
            HttpUtils.post(HttpUtils.Consulta, rp, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        final List<pesqUser> pesqlist = new ArrayList<>();
                        utils.log(new String(responseBody));
                        JSONArray objects = new JSONObject(new String(responseBody)).getJSONArray("lista_checkin");
                        for (int i = 0; i < objects.length(); i++) {
                            JSONObject jo = (JSONObject) objects.get(i);
                            pesqlist.add(new pesqUser(jo.getString("user_name"), jo.getString("picture"), jo.getString("user_id")));

                        }
                        pesquisaAdapter adp = new pesquisaAdapter(pesqlist);
                        lv.setAdapter(adp);
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
                                ((TextView)v.findViewById(R.id.username)).setText(pesqlist.get(i).nome);
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
                                        rp.add("user_id",pesqlist.get(i).userid);
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
                                        utils.efetuaCheckin(getApplicationContext(),pesqlist.get(i).userid,localid,findViewById(R.id.refresh),false);
                                        popupWindow.dismiss();
                                    }
                                });


                                return true;
                            }

                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    utils.toast(getApplicationContext(),"Falha ao obter usuários");
                }
            });

        }




    void goback() {
        sp.edit().clear().commit();
        startActivity(new Intent(this, MainActivity.class));
    }
}

class checkinpeopleadp extends  BaseAdapter{
    List<pesqUser>list;
    public checkinpeopleadp(List<pesqUser> l)
    {
        list=l;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        pesqUser user = list.get(i);
        View v= ((Activity)viewGroup.getContext()) .getLayoutInflater().inflate(R.layout.pesqlayout,viewGroup,false);
        ((TextView)v.findViewById(R.id.username)).setText(user.nome);
        utils.log(HttpUtils.imgplace+user.photourl);
        if(!user.photourl.equals(""))
            new DownloadImageTask(((CircularImageView)v.findViewById(R.id.playimg))).execute(HttpUtils.imgplace+user.photourl);
        return v;
    }
}
class pesquisaAdapter extends BaseAdapter{
    List<pesqUser> list;
    public pesquisaAdapter(List<pesqUser>l)
    {
        list=l;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        pesqUser user = list.get(i);
        View v= ((Activity)viewGroup.getContext()) .getLayoutInflater().inflate(R.layout.pesqlayout,viewGroup,false);
        ((TextView)v.findViewById(R.id.username)).setText(user.nome);
        utils.log(HttpUtils.imgplace+user.photourl);
        if(!user.photourl.equals(""))
        new DownloadImageTask(((CircularImageView)v.findViewById(R.id.playimg))).execute(HttpUtils.imgplace+user.photourl);
        return v;
    }
}
