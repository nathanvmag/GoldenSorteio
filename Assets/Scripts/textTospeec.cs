using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using UnityEngine;
using UnityEngine.Networking;
using UnityEngine.UI;
public class textTospeec : MonoBehaviour {
#if UNITY_WEBGL
    [DllImport("__Internal")]
        private static extern void falar(string tx);

#endif
    const string plugname = "golen.com.unity.plugin";
    static AndroidJavaClass jv;
    static AndroidJavaObject instance;
    //public Text hello;
    AndroidJavaClass jc;
    AndroidJavaObject jo;
    // Use this for initialization
    void Start () {
        jv = new AndroidJavaClass(plugname);
        instance = jv.CallStatic<AndroidJavaObject>("getInstance");
        jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
         jo = jc.GetStatic<AndroidJavaObject>("currentActivity");

    }

    // Update is called once per frame
    void Update () {
        /*if(Application.platform== RuntimePlatform.Android)
        hello.text = instance.CallStatic<string>("gettext");*/


    }
   
    public static void talk(string text)

    {
#if UNITY_WEBGL

        falar(text);
#endif
    }
    public  void Talk2 (string tx)
    {
        instance.CallStatic("Talk", tx, jo);
    }

   
}
