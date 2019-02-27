using System.Collections;
using UnityEngine;
using SimpleJSON;
using UnityEngine.UI;
using UnityEngine.Video;
using System.Globalization;
using UnityEngine.EventSystems;
using System.Collections.Generic;

public class datamanager : MonoBehaviour {
    public static string localid = "";
      public static string jsonurl = "http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/endpoint.php?local_id=fac12aa3a25a00eb&origem=view";
     public static string serverURL = "http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/endpoint.php?local_id=fac12aa3a25a00eb&origem=server";
     public string imgplace = "http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/upload/";
    string lastFetch = "";
    int whatshow = 0,lastup=-1;
    string[] whatshowstext = new string[4] { "Vencedores", "Sorteios", "", "Prêmios" };
    public GameObject userinfGameObj,userinf2,utiparent, bemvindogameob;
    public Text wtshowtx;
    public AudioClip swipe,bemvindclip;
    public Sprite[] infsbgs;
    public Sprite noWinner;
    JSONArray ult10,lastdispo,last10sort, lastpremios;
    public float psint, limiresgint;
    public VideoPlayer vp;
    public RawImage rw;
    public Text proxpremio,proxhorapremio,tmr,textmr,oqtasort,oqganhou,nozone,carre,jackpottx,basenome;
    public GameObject timerest,timemaxresg,dispos,dispoinfo,contentdispo;
    bool temsorteio = false;
    public bool sorteando;
    public SpriteRenderer winnerImage;
    int status = 0;
    Vector3 localposi;
    float animator = 0;
    public GameObject ultimos, fundonext;
    public Font newfont;
    bool noInternet;
    string oldwinner = "";
    GameObject spin;
    GameObject hd ;
    public GameObject nointernet;
    public bool sorteandojackpot;
    string lastuserjackpot;
    bool wait,showdispo,mostravence;
    JSONNode ultimo;
    List<falados> faladoslist;
    // Use this for l
    void Start () {
        Application.runInBackground = true;
        noInternet = false;
        sorteando = false;
        sorteandojackpot = false;
        wait = false;
        faladoslist = new List<falados>();
        // if (PlayerPrefs.HasKey("localid")) localid = PlayerPrefs.GetString("localid");
        fixtexts();

        if (!string.IsNullOrEmpty(localid))
        {
            st();

        }
        //StartCoroutine(playvideo());
        StartCoroutine(texts());
        


    }
    void fixtexts()
    {
        var texts = GameObject.FindObjectsOfType<Text>();
        foreach (Object ob in texts)
        {
            Text tx = (Text)ob;
            
                tx.resizeTextForBestFit = true;
                tx.resizeTextMaxSize = 300;
                tx.resizeTextMinSize = 1;
                tx.verticalOverflow = VerticalWrapMode.Truncate;
                if(tx.font!=newfont)
                tx.font = newfont;
            
        }
    }IEnumerator texts()
    {
        while(true)
        {
            fixtexts();
            yield return new WaitForSeconds(1);
        }
    }
    public void st()
    {

        jsonurl = "http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/endpoint.php?local_id=" + localid + "&origem=view";
        serverURL = "http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/endpoint.php?local_id=" + localid + "&origem=server";

        sorteando = false;
        ult10 = new JSONArray();
        lastdispo = new JSONArray();
        last10sort = new JSONArray();
        lastpremios = new JSONArray();
        winnerImage = GameObject.Find("winnerImage").GetComponent<SpriteRenderer>();
        InvokeRepeating("stdate", 0.1f, 5f);

        StartCoroutine(counter());
        timerest = GameObject.Find("temposort");
        timemaxresg = GameObject.Find("timemaxresg");
        //StartCoroutine(textToSpeachAPI.Traduzir("Olá tudo bem nathan ?"));
        spin = GameObject.Find("Spin");
        hd = GameObject.Find("hidethings");
        localposi = spin.transform.position;

    }
    void stdate()
    {

        //Debug.Log("Chamou aqui "+wait);
        if (!wait)
        {

            StartCoroutine(getDates());
        }
    }
    // Update is called once per frame
    void Update () {
        if (!string.IsNullOrEmpty(localid))
        {
            nozone.enabled = false;

            if (limiresgint > 0)
            {
                timemaxresg.GetComponent<Text>().text = hourManager((int)limiresgint + "");
                limiresgint -= Time.deltaTime;

            }

            if (psint > 0)
            {
                timerest.GetComponent<Text>().text = hourManager((int)psint + "");
                psint -= Time.deltaTime;

            }
           
        }
        else nozone.enabled = true;

        nointernet.SetActive(noInternet);
        if(sorteandojackpot)
        {
            jackpottx.transform.parent.parent.gameObject.SetActive(true);
        }
        else if(sorteando)
        {
            jackpottx.transform.parent.parent.gameObject.SetActive(false);

        }


    }
    IEnumerator playvideo()
    {
        //string filePath = System.IO.Path.Combine(Application.streamingAssetsPath, "video.mp4");
       
        vp.url = "http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/dev/tv/back1.mp4";
        vp.Prepare();

        WaitForSeconds waitForSeconds = new WaitForSeconds(1);
        while (!vp.isPrepared)
        {
           
            yield return waitForSeconds;
        }
        //carre.enabled = false;
        rw.texture = vp.texture;
        rw.color = Color.white;
        vp.Play();
    }

   
    IEnumerator getDates()
    {
        while(true)
        {
           // Debug.Log("clocou");
            wait = true;  
            WWW www2 = new WWW(serverURL);
            yield return www2;
            if (!string.IsNullOrEmpty(www2.error))
            {
                noInternet = true;
                continue;
            }
            noInternet = false;
            WWW www = new WWW(jsonurl);
            yield return www;
            if (!string.IsNullOrEmpty(www.error)) { 
                noInternet = true;
            continue;
         }
            noInternet = false;
        string response = www.text;
                var json = JSON.Parse(response);
                string prsseg;
                string limitresg;
            if (lastFetch != response)
            {

                try
                {
                    lastFetch = response;
                    //Debug.Log(response);
                    json = JSON.Parse(response);
                    prsseg = json["proximoSorteioSegundos"].ToString();
                    limitresg = json["limiteResgateSegundos"].ToString();

                }
                catch (System.Exception e)
                {
                    Debug.Log("Deu o erro " + e);
                    break;
                }
            
                JSONArray vencedor = (JSONArray)json["sorteados"];

                if (!sorteando)
                {

                    //sorttx.GetComponent<Text>().text = "Tempo para proximo sorteio:";
                    if (prsseg != "0")
                    {
                        psint = int.Parse(prsseg);
                        temsorteio = true;
                    }
                        // GameObject.Find("minrest").GetComponent<Text>().text = hourManager(prsseg);
                  
                        // sorttx.GetComponent<Text>().text = "Tempo limite para resgate:";
                        
                        limiresgint = int.Parse(limitresg);
                       
                        // GameObject.Find("minrest").GetComponent<Text>().text = hourManager(limitresg);
                    
                }


                JSONArray proxsortarr = (JSONArray)json["proximos_sorteios"];
                JSONNode jackpot = json["jackpot"];

                CultureInfo provider = CultureInfo.InvariantCulture;
                int timelimitresg=9999;
               
                basenome.text = json["nome_local"];

                try
                {
                    timelimitresg = getseconds(System.DateTime.ParseExact(json["tempo_resgate"], "HH:mm", provider));
                    
                }
                catch (System.Exception e)
                {
                    Debug.Log("Deu o erro " + e);
                }
                if (!sorteando)
                {
                    if (!sorteando && psint <= 20&&psint>0)// && proxsortarr.Count > 0 )//&& int.Parse(limitresg) <= timelimitresg.Second)
                    {
                       

                        try
                        {
                            {
                                Debug.Log("REALIZA SORTEIO");
                                
                                GetComponent<sorteiomanager>().Startsorteio(JSON.Parse(lastFetch), true,0);
                                sorteando = true;

                            }
                        }
                        catch (System.Exception e)
                        {
                            Debug.Log("Deu o erro " + e);
                        }
                    }
                    /*
                    if(!sorteando&& vencedor[0]["situacao"] == "TICKET_RESGATADO" &&prsseg=="0"&&limiresgint>0)
                    {
                        Debug.Log("Sorteio multiplo resgatado");
                        GetComponent<sorteiomanager>().Startsorteio(JSON.Parse(json), false, limiresgint+2);

                    }
                    */

                    if ((limiresgint <= 10 && limiresgint > 0)&&int.Parse(prsseg)==0) //&& (json["tipo"] == "EXTRA"))
                    {
                        Debug.Log("tenta Efetua sorteio extra "+ vencedor[0]["date_time_sorteio"]+ "  " + json["sorteio_extra"] +" "+ (json["sorteio_extra"] == "SIM"));
                        
                            string st = vencedor[0]["date_time_sorteio"].ToString().Substring(1, 15);
                            Debug.Log(st);
                            System.DateTime dt = System.DateTime.Now;
                            System.DateTime now = System.DateTime.Now;

                        try
                        {
                            dt = System.DateTime.ParseExact(st, "yyyy-MM-ddHH:mm", provider);
                            now = System.DateTime.ParseExact(json["dataHoraServidor"], "yyyy-MM-dd HH:mm:ss", provider);
                        }
                        catch(System.Exception e)
                        {
                            Debug.Log("Deu o erro " + e);

                        }
                            Debug.Log(json["sorteio_extra"] +" "+ json["tipo"] + "  " + (dt - now).TotalMinutes);

                            if ((dt - now).TotalMinutes <= 1&& ( (json["tipo"] == "EXTRA")|| json["sorteio_extra"]=="SIM"))
                            {
                                // Debug.Log("ERA P SORTEA EXTRA ");
                                www = new WWW(jsonurl);
                                yield return www;
                                if (!string.IsNullOrEmpty(www.error))
                                {
                                    noInternet = true;
                                    continue;
                            }
                            json = JSON.Parse(www.text);
                            if ( int.Parse(json["proximoSorteioSegundos"]) <=0)
                            {

                                GetComponent<sorteiomanager>().Startsorteio(JSON.Parse(json), false, limiresgint);
                            }
                            else Debug.Log("saiu protecao");

                            }
                        
                    }
                    if (!sorteando&& limiresgint> 0 &&int.Parse(prsseg)==0&& limiresgint < timelimitresg)
                    {
                        winnerImage.enabled = true;
                        timerest.GetComponent<Text>().enabled = false;
                        if (oldwinner != vencedor[0]["picture"])
                        {
                            oldwinner = vencedor[0]["picture"];
                           
                                WWW downimage = new WWW(imgplace + vencedor[0]["picture"]);
                                yield return downimage;
                                if (!string.IsNullOrEmpty(downimage.error))
                                    continue;
                                winnerImage.sprite = Sprite.Create(downimage.texture,
                                new Rect(0, 0, downimage.texture.width, downimage.texture.height), Vector2.zero);
                                winnerImage.transform.localPosition = new Vector3(-1.5f, -1.5f, 0);
                            
                        }
                        tmr.enabled = true;
                        textmr.enabled = true;
                        ultimos.SetActive(false);
                        fundonext.SetActive(false);
                        
                        oqtasort.transform.parent.gameObject.SetActive(true);
                        oqganhou.transform.parent.gameObject.SetActive(true);
                        oqganhou.text = vencedor[0]["name"];
                        oqtasort.text = vencedor[0]["premio"]["descricao_premio"];
                        if (vencedor[0]["premio"]["tipo_premio"] == "VALOR") 
                        {
                            oqtasort.text = "R$ "+ vencedor[0]["premio"]["valor"];
                       }
                       else  if (vencedor[0]["premio"]["tipo_premio"] == "SPIN")
                        {
                            oqtasort.text = "SPIN " + vencedor[0]["premio"]["qtd_spin"]+"x";

                        }
                        hd.SetActive(true);
                        jackpottx.transform.parent.parent.gameObject.SetActive(false);
                        mostravence = true;

                    }
                    else if(!sorteando)
                    {
                        winnerImage.sprite = noWinner;
                        winnerImage.gameObject.transform.localPosition = Vector3.zero;
                        mostravence = false;
                        timerest.GetComponent<Text>().enabled = true;
                        tmr.enabled = false;
                        textmr.enabled = false;
                        if(!showdispo)
                        ultimos.SetActive(true);
                        fundonext.SetActive(true);
                        oqtasort.transform.parent.gameObject.SetActive(false);
                        oqganhou.transform.parent.gameObject.SetActive(false);
                        hd.SetActive(true);
                        jackpot = json["jackpot"];
                        if (jackpot["situacao_jackpot"] == "ATIVO" )
                        {
                            jackpottx.transform.parent.parent.gameObject.SetActive(true);
                            jackpottx.text = jackpot["jackpot"];
                        }
                        else jackpottx.transform.parent.parent.gameObject.SetActive(false);


                    }

                }
                string proxsortt="";

                if (proxsortarr.Count<=0)
                {
                    proxhorapremio.text = "00:00" ;
                    proxpremio.text = "Sem sorteio";
                }
                else
                {

                    proxsortt =  proxsortarr[0]["horario"] .ToString().Split(' ')[1].Substring(0, 5);
                    proxhorapremio.text = proxsortt;
                    //Debug.Log(proxsortarr[0]["descricao_premio"]);

                    proxpremio.text = proxsortarr[0]["descricao_premio"];
                    if (proxsortarr[0]["tipo_premio"] == "VALOR")
                    {
                        proxpremio.text = "R$ " + proxsortarr[0]["valor"];
                    }
                    else if (proxsortarr[0]["tipo_premio"] == "SPIN")
                    {
                        proxpremio.text = "SPIN " + proxsortarr[0]["qtd_spin"] + "x";
                    }
                }                
                
              

                JSONArray ultimos10 = (JSONArray)json["ultimos10"];
                JSONArray disponiveis = (JSONArray)json["disponiveis"];
                JSONArray ultimos10sorts = (JSONArray)json["ultimos10Sorteados"];
                JSONArray premios = (JSONArray)json["premios_disponiveis"];
               // disponiveis = ultimos10;
                //Debug.Log("aqui "+ ultimos.ToString()+ "  "+ ultimos10sorts.ToString());
                if(disponiveis.Count>0)
                {
                    if (ultimo == null)
                        ultimo = disponiveis[0];
                    else if (ultimo["user_id"] != disponiveis[0]["user_id"])
                    {
                        ultimo = disponiveis[0];

                        falados fd = new falados(ultimo["user_id"], System.DateTime.Now);
                        Debug.Log("ultimo trocou");
                        if(!sorteando&&(psint>40||limiresgint>40))
                        {
                            if (falados.possofalar(faladoslist, fd)&&GetComponent<localmanager>().falabemvindo.isOn)
                            {

                                StartCoroutine(playbenvindo(ultimo["name"]));
                                faladoslist.Add(fd);
                            }
                            else Debug.Log("Nao posso falar pq ja falei");
                        }
                    }
                }
                if (!sorteando)
                {
                    if(ultimos10.Count>0 &&vencedor.Count>0)
                    {
                        if (jackpot["situacao_jackpot"] == "ATIVO"|| jackpot["jackpot_agendamento"] == "SIM")
                        {
                            //Debug.Log("situ jackpot "+vencedor[0]["situacao"] + " ultimo id " + ultimos10[0]["user_id"] + " vence id " + vencedor[0]["user_id"] + "  lastjack " + jackpot["jackpot_agendamento"]);
                            if (vencedor[0]["situacao"] == "TICKET_RESGATADO" && ultimos10[0]["user_id"] == vencedor[0]["user_id"] && !sorteandojackpot)
                            {
                                if (lastuserjackpot != vencedor[0]["user_id"])
                                {
                                    bool ganha = jackpot["jackpot_agendamento"] == "SIM";
                                    lastuserjackpot = vencedor[0]["user_id"];
                                    try
                                    {
                                        System.DateTime resgtime = System.DateTime.ParseExact(vencedor[0]["hora_resgate"], "yyyy-MM-dd HH:mm:ss", provider);
                                        float minutes = Mathf.Abs((float)(System.DateTime.Now - resgtime).TotalSeconds);
                                        if (!sorteandojackpot &&minutes<150) 
                                        {
                                            Debug.Log("Sorteou Jackpot");
                                            GetComponent<sorteiomanager>().startJackpotvoid(JSON.Parse(lastFetch), ganha);
                                        }
                                    }
                                    catch (System.Exception e)
                                    {
                                        Debug.Log("Deu o erro " + e);

                                    }
                                }
                            }
                        }
                    }
                    if (showdispo&&!sorteando&&!sorteandojackpot&&!mostravence)
                    {
                        //userinfGameObj.SetActive(false);
                        ultimos.SetActive(false);
                        dispos.SetActive(true);
                        Debug.Log("mostra dispo ai " + ultimos.activeSelf);
                        JSONArray disparray = (JSONArray)json["disponiveis"];
                        GameObject.Find("title").GetComponent<Text>().text = disparray.Count + " Disponíveis";
                        if (contentdispo.transform.childCount != disparray.Count)
                        {
                            foreach (Transform t in contentdispo.transform) Destroy(t.gameObject);
                            contentdispo.GetComponent<RectTransform>().sizeDelta =new Vector2(contentdispo.GetComponent<RectTransform>().sizeDelta.x, disparray.Count * 100 + disparray * 10);
                            for (int i = 0; i < disparray.Count; i++)
                            {
                                WWW imgwww = new WWW(imgplace + disparray[i]["picture"]);
                                yield return imgwww;

                                var infuser = Instantiate(dispoinfo, contentdispo.transform);
                                infuser.transform.Find("nomeuser").GetComponent<Text>().text = disparray[i]["name"];
                                infuser.transform.Find("arg").GetChild(0).GetChild(0).GetComponent<Image>().sprite =
                                           Sprite.Create(imgwww.texture, new Rect(0, 0, imgwww.texture.width, imgwww.texture.height), new Vector2(0, 0));
                                infuser.transform.Find("barrabg").GetComponent<Image>().sprite = infsbgs[Random.Range(0, 4)];
                                EventTrigger.Entry entry = new EventTrigger.Entry();
                                entry.eventID = EventTriggerType.PointerUp;
                                Image locked = infuser.transform.Find("locked").gameObject.GetComponent<Image>();
                                entry.callback.AddListener((eventData) => { locked.enabled = !locked.enabled; Debug.Log("tranca"); });

                                
                                infuser.GetComponent<EventTrigger>().triggers.Add(entry);
                            }
                        }
                    }
                    else
                    {
                        dispos.SetActive(false);
                        if(ultimos10!=null &&ult10!=null)
                        if ((ultimos10.ToString() != ult10.ToString() || lastup != 0) && whatshow == 0)
                        {
                            lastup = 0;
                            //Camera.main.GetComponent<AudioSource>().PlayOneShot(swipe);
                            ult10 = ultimos10;
                            wtshowtx.text = "";
                            foreach (Transform t in utiparent.transform)
                            {
                                if (t.gameObject.tag == "userinf") Destroy(t.gameObject);
                            }

                            int much = ult10.Count > 6 ? 6 : ult10.Count;
                            for (int i = 0; i < much; i++)
                            {


                                WWW imgwww = new WWW(imgplace + ult10[i]["picture"]);
                                yield return imgwww;
                                if (!string.IsNullOrEmpty(imgwww.error))
                                    continue;

                                var infuser = Instantiate(userinfGameObj, utiparent.transform);

                                infuser.GetComponent<LayoutElement>().minHeight = 40 * Screen.height / 750;
                                infuser.transform.Find("username").GetComponent<Text>().text = ult10[i]["name"];


                                infuser.transform.Find("brancofund").Find("mask").Find("userimage").GetComponent<Image>().sprite =
                                    Sprite.Create(imgwww.texture, new Rect(0, 0, imgwww.texture.width, imgwww.texture.height), new Vector2(0, 0));
                                infuser.transform.Find("barrabg").GetComponent<Image>().sprite = infsbgs[Random.Range(0, 4)];
                                // if (i == 5) infuser.SetActive(false);
                                infuser.SetActive(true);
                                if (i == 4) break;
                            }
                            if (much != 0)
                                wtshowtx.text = whatshowstext[whatshow];
                            else wtshowtx.text = "";

                            if (much == 0) whatshow++;


                        }
                        else if ((lastpremios.ToString() != premios.ToString() || lastup != 1) && whatshow == 1)
                        {
                            lastpremios = premios;
                            wtshowtx.text = "";
                            //Camera.main.GetComponent<AudioSource>().PlayOneShot(swipe);
                            foreach (Transform t in utiparent.transform)
                            {
                                if (t.gameObject.tag == "userinf") Destroy(t.gameObject);
                            }

                            JSONArray proxsort = (JSONArray)json["proximos_sorteios"];
                            int much = proxsort.Count > 6 ? 6 : proxsort.Count;
                            for (int i = 0; i < much; i++)
                            {
                                try
                                {
                                    var hr = proxsort[i]["horario"].ToString().Split(' ')[1].Substring(0, 5);
                                    if (hr == proxsortt) continue;

                                    var infuser = Instantiate(userinf2, utiparent.transform);
                                    string ptx = proxsort[i]["descricao_premio"];
                                    if (proxsort[i]["tipo_premio"] == "VALOR")
                                    {
                                        ptx = "R$ " + proxsort[i]["valor"];
                                    }
                                    else if (proxsort[i]["tipo_premio"] == "SPIN")
                                    {
                                        ptx = "SPIN " + proxsort[i]["qtd_spin"] + "x";
                                    }

                                    infuser.transform.Find("username").GetComponent<Text>().text = hr + " - " + ptx;



                                    infuser.transform.Find("barrabg").GetComponent<Image>().sprite = infsbgs[Random.Range(0, 4)];
                                }
                                catch (System.Exception e)
                                {
                                    Debug.Log("Deu o erro " + e);

                                }
                            }
                            if(much>1)
                            wtshowtx.text = whatshowstext[whatshow];
                            if (much == 0) whatshow++;

                            lastup = 1;

                        }
                        

                        else if ((last10sort.ToString() != disponiveis.ToString() || lastup != 2) && whatshow == 2)
                        {
                            last10sort = disponiveis;
                            wtshowtx.text = "";
                            //Camera.main.GetComponent<AudioSource>().PlayOneShot(swipe);
                            foreach (Transform t in utiparent.transform)
                            {
                                if (t.gameObject.tag == "userinf") Destroy(t.gameObject);
                            }

                            int much = last10sort.Count > 6 ? 6 : last10sort.Count;
                            much = much > 1 ? 1 : much;
                            for (int i = 0; i < much; i++)
                            {
                                WWW imgwww = new WWW(imgplace + last10sort[i]["picture"]);
                                yield return imgwww;
                                if (!string.IsNullOrEmpty(imgwww.error))
                                    continue;
                                var infuser = Instantiate(bemvindogameob, utiparent.transform);
                                infuser.transform.Find("username").GetComponent<Text>().text = last10sort[i]["name"];

                                infuser.transform.Find("Image").Find("brancofund").Find("mask").Find("userimage").GetComponent<Image>().sprite = Sprite.Create(imgwww.texture, new Rect(0, 0, imgwww.texture.width, imgwww.texture.height), new Vector2(0, 0));
                                //infuser.transform.Find("barrabg").GetComponent<Image>().sprite = infsbgs[Random.Range(0, 4)];
                                // if (i == 5) infuser.SetActive(false);
                                infuser.SetActive(true);
                                if (i == 4) break;
                            }
                            lastup = 2;
                            if (much != 0)
                                wtshowtx.text = whatshowstext[whatshow];
                            else wtshowtx.text = "";
                            if (much == 0) whatshow++;


                        }
                        else if ((lastpremios.ToString() != premios.ToString() || lastup != 3) && whatshow == 3)
                        {
                            lastpremios = premios;
                            wtshowtx.text = "";
                            // Camera.main.GetComponent<AudioSource>().PlayOneShot(swipe);
                            foreach (Transform t in utiparent.transform)
                            {
                                if (t.gameObject.tag == "userinf") Destroy(t.gameObject);
                            }
                            //wtshowtx.text = whatshowstext[whatshow];


                            int much = lastpremios.Count > 6 ? 6 : lastpremios.Count;
                            for (int i = 0; i < much; i++)
                            {
                                var infuser = Instantiate(userinf2, utiparent.transform);

                                infuser.transform.Find("username").GetComponent<Text>().text = lastpremios[i]["titulo"] + "      " + lastpremios[i]["pontos"] + " pts";




                                infuser.transform.Find("barrabg").GetComponent<Image>().sprite = infsbgs[Random.Range(0, 4)];

                            }
                            lastup = 3;
                            if (much != 0)
                                wtshowtx.text = whatshowstext[whatshow];
                            else wtshowtx.text = "";
                            if (much == 0) whatshow = 0;

                        }
                    }
                }
            }


            break;
            //yield return new WaitForSeconds(2);
        }
        wait = false;
    }
    public void showdisp()
    {if (!sorteando && !sorteandojackpot && !mostravence)
        {
            showdispo = !showdispo;
            Debug.Log("hey");
        }
    }
    public void soteia(bool a )
    {
        GetComponent<sorteiomanager>().startJackpotvoid(JSON.Parse(lastFetch), a);
    }
    IEnumerator playbenvindo(string nome)
    {
       Camera.main.GetComponent<AudioSource>().PlayOneShot(bemvindclip);
        yield return new WaitForSeconds(2);
        GetComponent<sorteiomanager>().tryspeak("Bem vindo... " + nome + "... chequím efetuado... boa sorte!!!");


    }
    IEnumerator counter()
    {
        int ct = 0;
        while(true)
        {
            yield return new WaitForSeconds(10);
            ct++;
            whatshow = ct % 4;
           /// whatshow = whatshow == 2 ? 3 : whatshow;
            if (whatshow == 0) ct = 0;
        }

    }
    int getseconds(System.DateTime dt)
    {
        int resu = dt.Second + (dt.Hour * 3600) + dt.Minute * 60;
        return resu;
    }
    string hourManager(string segs)
    {
        int seg = int.Parse(segs);
        int min = seg / 60;
        int hour = min / 60;
        if (hour > 0) min = min % 60;
        seg = seg % 60;
        return  (hour>0?hour+":":"")+( min>0?min + ":":"") + (seg>9?seg+"":"0"+seg);
    }
}
public class falados
{
    public string id;
    System.DateTime when;
    public falados (string i,System.DateTime dt)
    {
        id = i;
        when = dt;
    }
    public static bool  possofalar(List<falados> faladoslist,falados fd)
    {
        if(faladoslist.Contains(fd))
        {
            double hrs = (fd.when - faladoslist.Find(x => x.Equals(fd)).when).TotalHours;
            Debug.Log("horas é " + hrs);
            return hrs >= 8;
        }
        return true;

    }
    public override bool Equals(object obj)
    {
        return id == ((falados)obj).id;
    }
}
