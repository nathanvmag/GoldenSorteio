package golen.com.unity;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.w3c.dom.Text;

import java.util.Locale;

public class plugin {
    private static final plugin ourInstance = new plugin();

    public static plugin getInstance() {
        return ourInstance;
    }

    private plugin() {
        Log.d("eai", "plugin: ABRIUUU");
    }
    public static String gettext()
    {
        Log.d("eai", "gettext: to fazendo fazzz");
        return "blalbla";
    }
    static TextToSpeech tts;
    public static void Talk(final String text, Activity at)
    {
        Context ctx= at;
         tts = new TextToSpeech(ctx, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i== TextToSpeech.SUCCESS)
                {
                    Log.d("eai", "Talk: tentou falarrrrrr");
                    speakar(text);

                    Log.d("eai", "Talk: falouuuuu "+ text);
                }
                else  Log.d("eai", "Talk: FALHOU SEM STATUS ");
            }
        });

    }
    public static void speakar (String tx)
    {
        if(tts!=null)
        {
            tts.setLanguage(new Locale("pt","BR"));
            tts.speak(tx,TextToSpeech.QUEUE_ADD,null);
        }
    }


}
