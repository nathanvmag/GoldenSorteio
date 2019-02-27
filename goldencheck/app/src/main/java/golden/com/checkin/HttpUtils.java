package golden.com.checkin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cz.msebera.android.httpclient.entity.mime.Header;

public class HttpUtils {
    //private static final String BASE_URL = loginPage.basesite;
    public static final String dispURL="http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/dev/endpoint_view.php?local_id=";
    public static final String sorturl="http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/endpoint.php?origem=view&local_id=";
    public static final String ActionsURL= "http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/dev/acoes.php";
    public static final String Consulta ="http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/dev/consulta.php";
    public static final String SorteiosURL="http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/dev/acoes.php?command=todos_agendamentos&local_id=";
    //?command=desbloquear&local_id=dff9e4b2c4a31743&user_id=1356016e-9d36-46ca-99f0-b8c107f28446

    public static String imgplace = "http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/upload/";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient clientSync= new SyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        client.addHeader("Content-Type","application/x-www-form-urlencoded;");

        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }
    public static void postByUrlSync(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        clientSync.post(url, params, responseHandler);
    }
    public static void postWithHeaders( String url, RequestParams rp, AsyncHttpResponseHandler response)
    {
        client.addHeader("Ocp-Apim-Subscription-Key","56373045e6934fda9b476d18af577a1e");
        client.addHeader("Content-Type","application/json");
        client.post(url,rp,response);

    }
    public static void postespecial (Context ctx,String url) {
    }


    private static String getAbsoluteUrl(String relativeUrl) {
        return //BASE_URL
        "" + relativeUrl;
    }
}