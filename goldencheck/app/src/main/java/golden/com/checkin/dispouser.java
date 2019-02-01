package golden.com.checkin;

import android.provider.ContactsContract;

import java.util.Date;

public class dispouser {
    public String name,imagge;
    public long ontime, ultivisita;
    public int qntvisit;
    public int cad;
    public dispouser(String n, long on, long ult,int visi,String img,int c)
    {
        name= n;
        ontime=on;
        ultivisita=ult;
        qntvisit= visi;
        imagge= img;
        cad= c;
    }
}
