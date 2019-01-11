using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;
public class localmanager : MonoBehaviour {

    public GameObject menumang;
    public AudioSource ad;
    public Slider slide;
    public InputField ip;
    void Awake()
    {
        try
        {
            Uri myUri = new Uri(Application.absoluteURL);
            string param1 = System.Web.HttpUtility.ParseQueryString(myUri.Query).Get("localid");
            Debug.Log("LocalID "+param1);
            if(!string.IsNullOrEmpty(param1))
            PlayerPrefs.SetString("localid", param1);
        }
        catch
        {
            Debug.Log("exeppption");
        }
        try
        {
            if (!PlayerPrefs.HasKey("localid"))
            {
                menumang.SetActive(true);
            }
            else
            {
                datamanager.localid = PlayerPrefs.GetString("localid");
            }
            if (PlayerPrefs.HasKey("volume")) ad.volume = PlayerPrefs.GetFloat("volume");
        }
        catch (System.Exception e)
        {
            Debug.Log("Deu o erro no local manageger " + e);
        }

    }
	// Use this for initialization
	void Start () {
        slide.value = ad.volume;
        StartCoroutine(reload()); ;
		
	}
	
	// Update is called once per frame
	void Update () {

	}
    public void volumechange()
    {
        PlayerPrefs.SetFloat("volume", slide.value);
        PlayerPrefs.Save();
        ad.volume = slide.value;

    }
    public void savezone()
    {
        if (!string.IsNullOrEmpty(ip.text))
        {
            datamanager.localid = ip.text;
            PlayerPrefs.SetString("localid", datamanager.localid);
            PlayerPrefs.Save();
            GetComponent<datamanager>().st();
        }
        openmenu(false);


    }
    public void openmenu(bool opn)
    {
        slide.value = ad.volume;

        menumang.SetActive(opn);
    }
    IEnumerator reload ()
    {
        System.TimeSpan tp = new System.TimeSpan();
        tp.Add(System.TimeSpan.FromMinutes(10));
        Debug.Log("Vou esperar " + 10*60+" para resetar ");
        yield return new WaitForSeconds(800);
        datamanager dt = GetComponent<datamanager>();
        if (dt.psint < 30 && dt.psint > 0 || dt.sorteando || dt.sorteandojackpot)
        {
            Debug.Log("vou esepera");
            yield return new WaitForSeconds(100);
        }
        Debug.Log("vou resetar");
        SceneManager.LoadScene(0, LoadSceneMode.Single);
    }
}
