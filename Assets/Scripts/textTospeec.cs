using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using UnityEngine;
using UnityEngine.Networking;

public class textTospeec : MonoBehaviour {
    [DllImport("__Internal")]
    private static extern void falar(string tx);

    // Use this for initialization
    void Start () {
	}
	
	// Update is called once per frame
	void Update () {
		
	}
   
    public static void talk(string text)
    {
        falar(text);
        
    }

   
}
