package golden.com.checkin;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.InputStreamEntity;

public class register extends AppCompatActivity {

    ImageView userimage;
    BootstrapEditText nome,observa;
    BootstrapButton save, saveoculto;
    SharedPreferences sp;
    String localid;
    JSONObject imagedataJson;
    String mCurrentPhotoPath;
    Bitmap facefind;
    Uri imageUri;
    final int REQUEST_TAKE_PHOTO =1888;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sp = getApplicationContext().getSharedPreferences("prefs", 0);
        localid= sp.getString("localid","");
((TextView)findViewById(R.id.idname)).setText(sp.getString("nomelocal","")+ "   Ver : "+BuildConfig.VERSION_NAME);
        userimage =((ImageView)findViewById(R.id.personimage));
        nome= findViewById(R.id.nomeinput);
        observa = findViewById(R.id.obsinput);
        findViewById(R.id.backbt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),checkinActivity.class));
            }
        });
        findViewById(R.id.capimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1888);*/
                dispatchTakePictureIntent();
            }
        });
        save= findViewById(R.id.pescbt);
        findViewById(R.id.pescbt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!nome.getText().toString().equals(""))
                Save(false);
                else utils.toast(view.getContext(),"Por favor preencha o nome");

            }
        });
        saveoculto= findViewById(R.id.efetuacheck);
        findViewById(R.id.efetuacheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!nome.getText().toString().equals(""))
                    Save(true);
                else utils.toast(view.getContext(),"Por favor preencha o nome");
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"golden.com.checkin.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    void Save(Boolean oculto)
    {
        ((BootstrapButton)findViewById(R.id.capimage)).setEnabled(false);

       ((BootstrapButton)findViewById(R.id.capimage)).setText("Enviando...");
        save.setEnabled(false);
        saveoculto.setEnabled(false);

        BitmapDrawable drawable = (BitmapDrawable) userimage.getDrawable();
        Bitmap bitmap = facefind;//drawable.getBitmap();
        String encodedImage = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
        String checkinmode= oculto?"oculto":"comum";
        RequestParams rp = new RequestParams();
        String faceid= "" ;
        try {
          faceid = imagedataJson.getString("faceId");
    } catch (JSONException e) {
        e.printStackTrace();
    }
        final String json="{\"codigo_cupom\":" +
                "\"jgEM\",\"action\":\"save_user\",\"app_origem\":\"roleta\",\"local_id\":\""+localid+"\"," +
                "\"user_id\":\""+generateString()+"\",\"user_picture\":\""+encodedImage.trim()+"\",\"user_info\":{\"face_id\":\""+faceid+"\",\"images\":[{\"attributes\":{\"age\":18,\"asian\":0.04346,\"black\":0.00446,\"gender\":{\"femaleConfidence\":0.00007,\"maleConfidence\":0.99993,\"type\":\"M\"},\"glasses\":\"None\",\"hispanic\":0.09603,\"lips\":\"Together\",\"other\":0.04559,\"white\":0.81046},\"transaction\":{\"confidence\":0.99959,\"eyeDistance\":99,\"face_id\":\""+faceid+"\",\"gallery_name\":\"dff9e4b2c4a31743\",\"height\":219,\"image_id\":1,\"pitch\":-28,\"quality\":1.08136,\"roll\":2,\"status\":\"success\",\"subject_id\":\"20180629212347\",\"timestamp\":\"1530318232392\",\"topLeftX\":292,\"topLeftY\":222,\"width\":219,\"yaw\":-7}}]},\"user_location\":{\"accuracy\":16.983999252319336,\"altitude\":null,\"altitudeAccuracy\":null,\"heading\":null,\"latitude\":-30.1029586,\"longitude\":-51.3172465,\"speed\":null},\"user_emotion\":{},\"texto\":\"\"," +
                "\"name\":\""+nome.getText()+"\",\"phone\":\""+observa.getText()+"\",\"checkin_mode\":\""+checkinmode+"\"}";
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
                    utils.toast(getApplication(),"Cadastrado com sucesso " );//+new String(responseBody));

                    startActivity(new Intent(getApplicationContext(),checkinActivity.class));
                }
                else utils.toast(getApplication(),"Falha ao efetuar cadastro" );//+new String(responseBody));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                utils.toast(getApplication(),"Falha ao cadastrar "+ error);
                save.setEnabled(true);
                saveoculto.setEnabled(true);

            }
        });
    }

    void SearchFace(final int tenta, final Bitmap bip)
    {
        utils.toast(getApplicationContext(),"Procurando face");
        utils.log("Comecei a procurar face ");
        imagedataJson=null;
        save.setEnabled(false);
        saveoculto.setEnabled(false);
        String url= "https://brazilsouth.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&returnFaceAttributes=age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise";
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = url;
            JSONObject jsonBody = new JSONObject();
            userimage.buildLayer();
            BitmapDrawable drawable = (BitmapDrawable) userimage.getDrawable();
            final Bitmap photo = (Bitmap)drawable.getBitmap();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bArray = bos.toByteArray();


            final byte[] requestBody = bArray; //jsonBody.toString();
            utils.log("Fiz o request no server");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    utils.log( response);
                    utils.log("Recebi a respota ");
                    try {
                        JSONArray json = new JSONArray(response);
                        if(json.length()>0)
                        {
                           JSONObject jo =  json.getJSONObject(0);
                           utils.log(jo);
                           JSONObject facerect= jo.getJSONObject("faceRectangle");

                            Bitmap resized = Bitmap.createBitmap(photo,facerect.getInt("left"),facerect.getInt("top"),facerect.getInt("width"),facerect.getInt("height"));
                            resized= Bitmap.createScaledBitmap(resized,300,300,false);

                            facefind= resized;
                            //userimage.setImageBitmap(resized);
                            save.setEnabled(true);
                            saveoculto.setEnabled(true);
                            imagedataJson= jo;
                            userimage.setVisibility(View.VISIBLE);
                            ((BootstrapButton)findViewById(R.id.capimage)).setEnabled(true);

                            ((BootstrapButton)findViewById(R.id.capimage)).setText("Trocar Imagem");


                        }
                        else
                        {
                            //if(tenta==3) {
                                utils.log("Não rostos");
                                utils.toast(getApplicationContext(), "Não foi dectado rosto nesta foto por favor envie outra foto ou rotacione a imagem");
                                userimage.setImageResource(R.drawable.person);
                                userimage.setVisibility(View.VISIBLE);
                            ((BootstrapButton)findViewById(R.id.capimage)).setEnabled(true);

                            ((BootstrapButton)findViewById(R.id.capimage)).setText("Capturar Imagem ");


                           /* }
                            else{Bitmap rotated= rotate(bip);
                            userimage.setImageBitmap(rotated);
                            SearchFace(tenta+1,rotated);}*/


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY"," deu erro aqui "+ error.toString());
                    utils.log("Não rostos");
                    utils.toast(getApplicationContext(), "Não foi dectado rosto nesta foto por favor envie outra foto ou rotacione a imagem");
                    userimage.setImageResource(R.drawable.person);
                    userimage.setVisibility(View.VISIBLE);
                    ((BootstrapButton)findViewById(R.id.capimage)).setEnabled(true);

                    ((BootstrapButton)findViewById(R.id.capimage)).setText("Capturar Imagem ");

                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/octet-stream";
                }



                /*@Override
                public byte[] getBody() throws AuthFailureError {
                    try {

                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }*/

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return requestBody;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  client = new HashMap<String, String>();
                    client.put("Ocp-Apim-Subscription-Key","56373045e6934fda9b476d18af577a1e");
                  //  client.put("Content-Type","application/json");

                    return client;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = new String(response.data);
                        // can get more details such as response.headers
                    }

                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }







    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* utils.log("Saiu da cameras");
        if (requestCode == 1888 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Matrix mx = new Matrix();
            mx.postRotate(-90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), mx, true);
            ((BootstrapButton)findViewById(R.id.capimage)).setText("Trocar Imagem");
            userimage.setVisibility(View.INVISIBLE);
            userimage.setImageBitmap(photo);
            SearchFace();

            save.setEnabled(true);
            saveoculto.setEnabled(true);



        }*/
        utils.log("sai da cameraaaa");

        super.onActivityResult(requestCode, resultCode, data);
        utils.log("Sai da camera ne ");
        try {

                    if (resultCode == RESULT_OK) {
                        File file = new File(mCurrentPhotoPath);

                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getApplicationContext().getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {

                            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);

                            Bitmap rotatedBitmap = bitmap;
                            switch(orientation) {

                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotateImage(bitmap, 90);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotateImage(bitmap, 180);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotateImage(bitmap, 270);
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotatedBitmap = bitmap;
                            }
                            bitmap= rotatedBitmap;
                            ((BootstrapButton)findViewById(R.id.capimage)).setText("Processando aguarde... ");
                            ((BootstrapButton)findViewById(R.id.capimage)).setEnabled(false);

                            //userimage.setVisibility(View.INVISIBLE);
                            bitmap= Bitmap.createScaledBitmap(bitmap,500,500,false);
                            userimage.setImageBitmap(bitmap);

                            SearchFace(0,bitmap);
                        }
                    }



        } catch (Exception error) {
            error.printStackTrace();
        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
        public Bitmap rotate(Bitmap b)
        {
            Matrix mx = new Matrix();
            mx.postRotate(-90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), mx, true);
            return rotatedBitmap;
        }



        public static void main(String[] args) {
            System.out.println(generateString());
        }

        public static String generateString() {
            String uuid = UUID.randomUUID().toString();
            return  uuid;
        }


}
