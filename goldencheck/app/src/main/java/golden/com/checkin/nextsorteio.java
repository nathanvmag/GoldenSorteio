package golden.com.checkin;

public class nextsorteio {
    public String name,date,id,localid,tipo;
    boolean cancelado,dodia,pass;
    public String dia;
    String[] diasdasemana = new String[]{"Domingo","Segunda-Feira","Terça-Feira","Quarta-Feira","Quinta-Feira","Sexta-Feira","Sabado","Sem dia"};
    public nextsorteio(String n, String dt,String i,String lid ,boolean b,String t,int semanday,String datecomp ,boolean dd,boolean p)
    {
        name= n;
        date= dt;
        id=i;
        localid=lid;
        cancelado=b;
        tipo=t;
        dia=diasdasemana[semanday];
        dodia=dd;
        pass=p;
    }
    public String getresume ()
    {
        return date+ " - "+name;
    }

}
