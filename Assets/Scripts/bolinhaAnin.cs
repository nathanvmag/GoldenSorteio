using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class bolinhaAnin : MonoBehaviour {
    public bool storteando = false;
    Animator anim;
    int directionRot = 1;
    public float multiplier,multiplier2=10;
    float z = 0;
	// Use this for initialization
	void Start () {
        anim = GetComponent<Animator>();
	}
	
	// Update is called once per frame
	void Update () {

        if (storteando)
        {
            transform.Rotate(new Vector3(0, 0, -108*multiplier2 * Time.deltaTime));
        }
        else
        {

            if (z > 19.5f || z < -19.5f)
                Camera.main.GetComponent<screenshake>().shakeDuration = 0.2f;
            z = Mathf.PingPong(Time.time * multiplier, 40) - 20;
            transform.rotation = Quaternion.Euler(0, 0, z);


        }
    }
    public void startSortio()
    {
        

    }
    public void gettext()
    {

    }

}
