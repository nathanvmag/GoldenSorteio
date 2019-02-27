package golden.com.checkin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class utils {
    public static void log(Object tolog)
    {
        Log.d("BikeCycle",tolog.toString());
    }
    public static void toast(Context ctx, String text){
        Toast.makeText(ctx,text,Toast.LENGTH_LONG).show();}
        public static void efetuaCheckin(final Context ctx, String id, String localid, final View v,boolean oculto )
        {
            String modo= oculto?"oculto":"normal";
            final String json="{\"codigo_cupom\":" +
                    "\"jgEM\",\"action\":\"checkin\",\"app_origem\":\"roleta\",\"local_id\":\""+localid+"\"," +
                    "\"user_id\":\""+id+"\",\"user_picture\":\"\",\"user_info\":{\"face_id\":\"\",\"images\":[{\"attributes\":{\"age\":18,\"asian\":0.04346,\"black\":0.00446,\"gender\":{\"femaleConfidence\":0.00007,\"maleConfidence\":0.99993,\"type\":\"M\"},\"glasses\":\"None\",\"hispanic\":0.09603,\"lips\":\"Together\",\"other\":0.04559,\"white\":0.81046},\"transaction\":{\"confidence\":0.99959,\"eyeDistance\":99,\"face_id\":\"\",\"gallery_name\":\"dff9e4b2c4a31743\",\"height\":219,\"image_id\":1,\"pitch\":-28,\"quality\":1.08136,\"roll\":2,\"status\":\"success\",\"subject_id\":\"20180629212347\",\"timestamp\":\"1530318232392\",\"topLeftX\":292,\"topLeftY\":222,\"width\":219,\"yaw\":-7}}]},\"user_location\":{\"accuracy\":16.983999252319336,\"altitude\":null,\"altitudeAccuracy\":null,\"heading\":null,\"latitude\":-30.1029586,\"longitude\":-51.3172465,\"speed\":null},\"user_emotion\":{},\"texto\":\"\"," +
                    "\"name\":\"\",\"phone\":\"\",\"checkin_mode\":\""+modo+"\"}";
            RequestParams rp = new RequestParams();
            rp.add("json",json);
            final String url= "http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/engine.php";
            HttpUtils.post(url,rp,new  AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    utils.log("Minha url "+url);
                    utils.log(new String(responseBody));
                    utils.log("meu json "+json);
                    if(new String(responseBody).contains("success"))
                    {
                        utils.toast(ctx,"Checkin realizado com sucesso " );//+new String(responseBody));
                        v.callOnClick();
                     //   ctx.startActivity(new Intent(ctx,checkinActivity.class));
                    }
                    else utils.toast(ctx,"Falha ao efetuar checkin" );//+new String(responseBody));

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    utils.toast(ctx,"Falha ao realizar checkin "+ error);

                }
            });

        }

}
