package golden.com.checkin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences sp =getApplicationContext().getSharedPreferences("prefs",0);
        if(sp.contains("localid")){
            gofoward();
        }
        findViewById(R.id.entrabt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if(   ((EditText)findViewById(R.id.editText)).getText().length()==4)
             {
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("localid",((EditText)findViewById(R.id.editText)).getText().toString());
                edit.commit();
                gofoward();
             }
            }
        });
    }
    public void gofoward()
    {
        startActivity(new Intent(this,checkinActivity.class));
    }

}
