package golden.com.checkin;

import android.provider.ContactsContract;

import java.util.Date;

public class dispouser {
    public String name,imagge;
    public long ontime, ultivisita;
    public String qntvisit;
    public int cad;
    public String id;
    public boolean premiado;
    public dispouser(String n, long on, long ult,String visi,String img,int c,String i)
    {
        name= n;
        ontime=on;
        ultivisita=ult;
        qntvisit= visi;
        imagge= img;
        cad= c;
        id=i;
    }

    @Override
    public boolean equals(Object obj) {
        dispouser tocomp= (dispouser)obj;
        return tocomp.id.equals(this.id)&&tocomp.imagge.equals(this.imagge);
    }
}
