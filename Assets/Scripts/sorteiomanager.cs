using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using SimpleJSON;
using UnityEngine.UI;
using UnityEngine.Video;

public class sorteiomanager : MonoBehaviour {
    AudioSource audio;
    public AudioClip roulete, aplause,corneta,contagem,cornetinhas,swope,lose, jackpotwin,rodajackpot;
    public Sprite rouleteimage,winnerImg,luckImage;
    public GameObject bolaK, bolaP, bolaJ;
    public VideoPlayer vp;
    public RawImage rw;
    datamanager data;
    GameObject hd;
    Color[] oldcolors;
    // Use this for initialization
    void Start () {
        audio = Camera.main.GetComponent<AudioSource>();
        data = GetComponent<datamanager>();
         hd = GameObject.Find("hidethings");
        oldcolors = new Color[3] { bolaK.GetComponent<SpriteRenderer>().color, bolaP.GetComponent<SpriteRenderer>().color, bolaJ.GetComponent<SpriteRenderer>().color };


    }

    // Update is called once per frame
    void Update () {
		
	}
    public void Startsorteio(JSONNode json,bool b,float t)
    {
        
        StartCoroutine(startSorteio( json, b,t));

    }
    public void startJackpotvoid(JSONNode j, bool g)
    {
        StartCoroutine(startJackpot(j,g));
    }
    public IEnumerator startJackpot(JSONNode json, bool ganhou)
    {
        GameObject.Find("temposort").GetComponent<Text>().enabled = false;
        if(ganhou)
        {
            //StartCoroutine(preloadvideo());
        }
        data.sorteandojackpot = true;
        Debug.Log("olaa");
        data.sorteando = true;
        float animator = 0;
        hd.SetActive(false);

        GameObject spin = GameObject.Find("Spin");
        data.oqtasort.transform.parent.gameObject.SetActive(false);
        data.oqganhou.transform.parent.gameObject.SetActive(false);
        Vector3 localposi = spin.transform.position;
        JSONArray vencedor = (JSONArray)json["sorteados"];
        WWW downimage = new WWW(data.imgplace + vencedor[0]["picture"]);
        yield return downimage;
        data.winnerImage.sprite = Sprite.Create(downimage.texture,
           new Rect(0, 0, downimage.texture.width, downimage.texture.height), Vector2.zero);
        GameObject.Find("winnerImage").transform.localPosition = new Vector3(-1.5f, -1.5f, 0);

        while (animator < 1)
        {
            spin.transform.position = Vector3.Lerp(spin.transform.position, Vector3.zero, animator);
            spin.transform.localScale = Vector3.Lerp(spin.transform.localScale, new Vector3(1.5f, 1.5f, 1), animator);
            animator += Time.deltaTime;
            yield return new WaitForSeconds(Time.deltaTime / 2);


        }
        animator = 0;
        Vector3[] oldbolaposis = new Vector3[3] { bolaK.transform.localPosition, bolaP.transform.localPosition, bolaJ.transform.localPosition };
        Vector3[] newbolaposis = new Vector3[3] { bolaK.transform.localPosition - new Vector3(1,0,0), bolaP.transform.localPosition + new Vector3(1,0,0), bolaJ.transform.localPosition + new Vector3(0,1,0) };

        audio.PlayOneShot(swope);
        while (animator<1)
        {
            bolaK.transform.localPosition = Vector3.Lerp(bolaK.transform.localPosition, newbolaposis[0], animator);
            bolaP.transform.localPosition = Vector3.Lerp(bolaP.transform.localPosition, newbolaposis[1], animator);
            bolaJ.transform.localPosition = Vector3.Lerp(bolaJ.transform.localPosition, newbolaposis[2], animator);
            animator += Time.deltaTime;

            yield return new WaitForSeconds(Time.deltaTime / 2);

        }
        GameObject bolaslat = GameObject.Find("bolaslateraiss");

        tryspeak(vencedor[0]["fala_pre_extra"]);
        yield return new WaitForSeconds(3);
       
        Camera.main.GetComponent<screenshake>().shakeDuration = 16.8f;
        audio.PlayOneShot(rodajackpot);
        yield return new WaitForSeconds(2);
        bolaslat.GetComponent<bolinhaAnin>().storteando = true;
       
        StartCoroutine(colorfyimages(ganhou,oldcolors));
        //StartCoroutine(losespeed());
        StartCoroutine(backfront(newbolaposis,oldbolaposis));
        yield return new WaitForSeconds(7);
        SpriteRenderer[] bolassprite = new SpriteRenderer[3] { bolaJ.GetComponent<SpriteRenderer>(), bolaK.GetComponent<SpriteRenderer>(), bolaP.GetComponent<SpriteRenderer>() };
        bolaslat.GetComponent<bolinhaAnin>().storteando = false;
        if (ganhou)
        {

          Color randcolor = oldcolors[Random.Range(0, oldcolors.Length)];
          foreach (SpriteRenderer sp in bolassprite) sp.color = randcolor;
           
            yield return new WaitForSeconds(3);
            
            rw.transform.parent.gameObject.SetActive(true);
            //vp.frame = 1;
            //vp.Play();
            audio.PlayOneShot(jackpotwin);
            yield return new WaitForSeconds(3);
            rw.transform.GetChild(1).gameObject.SetActive(true);
            StartCoroutine( lbyl(rw.transform.GetChild(1).GetComponent<Text>(), json["jackpot"]["jackpot"]));
            yield return new WaitForSeconds(12);
            vp.Stop();
            rw.transform.parent.gameObject.SetActive(false);

            rw.transform.GetChild(1).gameObject.SetActive(false);


        }
        else
        {

                Color randcolor = oldcolors[Random.Range(0, oldcolors.Length)];
                Color randcolor2 = oldcolors[Random.Range(0, oldcolors.Length)];
                while (randcolor == randcolor2) randcolor2 = oldcolors[Random.Range(0, oldcolors.Length)];
                bolassprite[0].color = randcolor;
                bolassprite[2].color = randcolor;
                bolassprite[1].color = randcolor2;
                audio.PlayOneShot(lose);
            yield return new WaitForSeconds(4);
        }
        animator = 0;
        while (animator < 1)
        {
            spin.transform.position = Vector3.Lerp(spin.transform.position, localposi, animator);
            spin.transform.localScale = Vector3.Lerp(spin.transform.localScale, new Vector3(1f, 1f, 1), animator);
            animator += Time.deltaTime;
            yield return new WaitForSeconds(Time.deltaTime / 2);
        }
        data.sorteando = false;
        Debug.Log("acabouuu");
        yield return new WaitForSeconds(2);
        animator = 0;

        audio.PlayOneShot(swope);
        while (animator < 1)
        {
            bolaK.transform.localPosition = Vector3.Lerp(bolaK.transform.localPosition, oldbolaposis[0], animator);
            bolaP.transform.localPosition = Vector3.Lerp(bolaP.transform.localPosition, oldbolaposis[1], animator);
            bolaJ.transform.localPosition = Vector3.Lerp(bolaJ.transform.localPosition, oldbolaposis[2], animator);
            animator += Time.deltaTime;
            yield return new WaitForSeconds(Time.deltaTime / 2);

        }

        GameObject.Find("temposort").GetComponent<Text>().enabled = true;
        data.sorteandojackpot = false;
        
    }
    IEnumerator lbyl(Text tx, string texto)
    {
        string temp = "";
        for(int i=0;i<texto.Length;i++)
        {
            temp += texto[i];
            tx.text = temp;
            yield return new WaitForSeconds(0.2f);
        }
    }
    IEnumerator preloadvideo()
    {
        vp.url = "http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/dev/tv/back1.mp4";
        vp.Prepare();

        WaitForSeconds waitForSeconds = new WaitForSeconds(Time.deltaTime);
        while (!vp.isPrepared)
        {
            Debug.Log("carregando video");
            yield return waitForSeconds;
        }
        //carre.enabled = false;
        rw.texture = vp.texture;
        rw.color = Color.white;
    }
    IEnumerator backfront(Vector3[] newposi,Vector3[] oldbolaposis)
    {
        GameObject bolaslat = GameObject.Find("bolaslateraiss");

        float animator = 0;
        while (bolaslat.GetComponent<bolinhaAnin>().storteando)
        {
            animator = Mathf.PingPong(Time.time, 1);

            bolaK.transform.localPosition = Vector3.Lerp(newposi[0], oldbolaposis[0], animator);
            bolaP.transform.localPosition = Vector3.Lerp(newposi[1], oldbolaposis[1], animator);
            bolaJ.transform.localPosition = Vector3.Lerp(newposi[2], oldbolaposis[2], animator);
            yield return new WaitForSeconds(Time.deltaTime/2);

        }
        animator = 0;
        while (animator < 1 )
        {
            bolaK.transform.localPosition = Vector3.Lerp(bolaK.transform.localPosition, newposi[0], animator);
            bolaP.transform.localPosition = Vector3.Lerp(bolaP.transform.localPosition, newposi[1], animator);
            bolaJ.transform.localPosition = Vector3.Lerp(bolaJ.transform.localPosition, newposi[2], animator);
            animator += Time.deltaTime;

            yield return new WaitForSeconds(Time.deltaTime / 2);

        }
        bolaslat.GetComponent<bolinhaAnin>().multiplier2 = 10;

    }
    IEnumerator losespeed()
    {
        GameObject bolaslat = GameObject.Find("bolaslateraiss");
        bolaslat.GetComponent<bolinhaAnin>().multiplier2 = 4;

        float animator = 0;
        while(animator<1&& bolaslat.GetComponent<bolinhaAnin>().storteando)
        {
            bolaslat.GetComponent<bolinhaAnin>().multiplier2 = Mathf.Lerp(bolaslat.GetComponent<bolinhaAnin>().multiplier2, 0, animator);
            Debug.Log(bolaslat.GetComponent<bolinhaAnin>().multiplier2+" "+ animator);
            animator += Time.deltaTime/15;
            
            yield return new WaitForSeconds( Time.deltaTime * 15);

        }
    }
    IEnumerator colorfyimages(bool ganhou,Color[] oldcores)

    {
        GameObject bolaslat = GameObject.Find("bolaslateraiss");
        SpriteRenderer[] bolassprite= new SpriteRenderer[3] { bolaJ.GetComponent<SpriteRenderer>(), bolaK.GetComponent<SpriteRenderer>(), bolaP.GetComponent<SpriteRenderer>() };
        while (bolaslat.GetComponent<bolinhaAnin>().storteando)
        {
            foreach (SpriteRenderer sp in bolassprite) sp.color = oldcores[Random.Range(0, oldcores.Length)];
            yield return new WaitForSeconds(Time.deltaTime * 8);

        }
       
    }
    public IEnumerator startSorteio(JSONNode json,bool principal,float tempprox)
    {
        // corneta voz, contagem, 
        data.sorteando = true;
        Debug.Log("vai sortear");



        if (!principal)
        {
            float resu = tempprox;
            while (resu > 0)
            {
                resu -= Time.deltaTime;
                Debug.Log("esperando " + resu);
                yield return new WaitForSeconds(Time.deltaTime);
            }
            Debug.Log("hey");

            WWW www2 = new WWW(datamanager.serverURL);
            yield return www2;

           WWW www = new WWW(datamanager.jsonurl);
            yield return www;
            json = JSON.Parse(www.text);

            Debug.Log("hey 2");
            if (int.Parse(json["proximoSorteioSegundos"]) > 0)
            {
                data.sorteando = false;
                yield break;

            }

        } else
        {
          /*  WWW www2 = new WWW(datamanager.serverURL);
            yield return www2;

            WWW www = new WWW(datamanager.jsonurl);
            yield return www;

            string response = www.text;
            json = JSON.Parse(response);*/
        }
        JSONArray vencedor;
        if (principal)
        {
            audio.PlayOneShot(corneta);
            yield return new WaitForSeconds(3);
            audio.PlayOneShot(contagem);
            yield return new WaitForSeconds(1);
            vencedor = (JSONArray)json["sorteados"];
            tryspeak(vencedor[0]["fala_pre_sorteio"]);

            yield return new WaitForSeconds(13);
        }
        else
        {
            audio.PlayOneShot(corneta);
            yield return new WaitForSeconds(4);
            vencedor = (JSONArray)json["sorteados"];

            tryspeak(vencedor[0]["fala_pre_sorteio"]);
            yield return new WaitForSeconds(2);
        }

        WWW downimage = new WWW(data.imgplace + vencedor[0]["picture"]);
        yield return downimage;
        
        /*
         www2 = new WWW(datamanager.serverURL);
        yield return www2;

        www = new WWW(datamanager.jsonurl);
       yield return www;

        response = www.text;
       json = JSON.Parse(response);*/
     
        


        float animator = 0;
        hd.SetActive(false);

        GameObject spin = GameObject.Find("Spin");
        data.oqtasort.transform.parent.gameObject.SetActive(false);
        data.oqganhou.transform.parent.gameObject.SetActive(false);
        Vector3 localposi = spin.transform.position;

        while (animator<1)
        {
            spin.transform.position = Vector3.Lerp(spin.transform.position, Vector3.zero, animator);
            spin.transform.localScale = Vector3.Lerp(spin.transform.localScale, new Vector3(1.5f, 1.5f, 1), animator);
            animator +=  Time.deltaTime;
            yield return new WaitForSeconds( Time.deltaTime/2);

        }
        data.winnerImage.sprite = null;
        data.winnerImage.transform.localPosition = Vector3.zero;
        data.winnerImage.enabled = true;
        data.timerest.GetComponent<Text>().enabled = false;

        //yield return new WaitForSeconds(3);

        Camera.main.GetComponent<screenshake>().shakeDuration = 16.8f;
        GameObject bolaslat = GameObject.Find("bolaslateraiss");
        bolaslat.GetComponent<bolinhaAnin>().storteando = true;
        audio.PlayOneShot(roulete);
        data.winnerImage.sprite = rouleteimage;

        StartCoroutine(spinImage());
        /*
        WWW www3 = new WWW(datamanager.serverURL);
        yield return www3;

        www = new WWW(datamanager.jsonurl);
        yield return www;

        response = www.text;
        json = JSON.Parse(response);
        */
        yield return new WaitForSeconds(10);
        bolaslat.GetComponent<bolinhaAnin>().storteando = false;

        animator = 0;
        GameObject.Find("winnerImage").transform.localPosition = new Vector3(-1.5f, -1.5f, 0);

        data.winnerImage.sprite = Sprite.Create(downimage.texture,
            new Rect(0, 0, downimage.texture.width, downimage.texture.height), Vector2.zero);
        GameObject.Find("winnerImage").transform.localPosition = new Vector3(-1.5f, -1.5f, 0);
        Camera.main.GetComponent<screenshake>().shakeDuration = 14f;

        audio.PlayOneShot(cornetinhas);
        yield return new WaitForSeconds(3);
        audio.PlayOneShot(aplause);
        yield return new WaitForSeconds(3);

        while (animator < 1)
        {
            spin.transform.position = Vector3.Lerp(spin.transform.position, localposi, animator);
            spin.transform.localScale = Vector3.Lerp(spin.transform.localScale, new Vector3(1f, 1f, 1), animator);
            animator += Time.deltaTime;
            yield return new WaitForSeconds(Time.deltaTime / 2);
        }
        data.sorteando = false;
        data.oqtasort.transform.parent.gameObject.SetActive(true);
        data.oqganhou.transform.parent.gameObject.SetActive(true);
        data.oqganhou.text = vencedor[0]["name"];
        data.oqtasort.text = vencedor[0]["premio"]["descricao_premio"];


            

        yield return new WaitForSeconds(7);
        tryspeak(vencedor[0]["fala_pos_sorteio"]);


        while (bolaslat.GetComponent<bolinhaAnin>().multiplier2 > 0)
        {
            bolaslat.GetComponent<bolinhaAnin>().multiplier2 -= 1.04f * Time.deltaTime;

            yield return new WaitForSeconds(Time.deltaTime);
        }


        // hd.SetActive(true);
        bolaslat.GetComponent<bolinhaAnin>().multiplier2 = 10;

    }
    void tryspeak(string text)
    {
        try
        {
            textTospeec.talk(text);
        }
        catch
        {
            Debug.Log("Tentou falar " + text + " e falhou");
        }
    }
    IEnumerator spinImage()
    {
        GameObject bolaslat = GameObject.Find("bolaslateraiss");
        data.winnerImage.sprite = rouleteimage;
        data.winnerImage.transform.position = new Vector3(0, -19, 0);
        GameObject.Find("winnerImage").GetComponent<SpriteRenderer>().sprite = rouleteimage;

        float y = -19;
        float mp = 1;

        while (bolaslat.GetComponent<bolinhaAnin>().storteando)
        {
            y += 216 * Time.deltaTime/mp;
            if (y > 19) y = -19;
            data.winnerImage.transform.position = new Vector3(0, y, 0);
            mp += Time.deltaTime / 6;
            yield return new WaitForSeconds(Time.deltaTime);

        }
        GameObject.Find("winnerImage").transform.localPosition = new Vector3(-1.5f, -1.5f, 0);

    }
}
