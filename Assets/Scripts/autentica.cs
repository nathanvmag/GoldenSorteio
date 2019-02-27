using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.Networking;
using UnityEngine.UI;
using SimpleJSON;
using UnityEngine.SceneManagement;

public class autentica : MonoBehaviour
{
    public InputField login, pass;
    public Text feedback;
    // Start is called before the first frame update
    void Start()
    {
        if(PlayerPrefs.HasKey("localid")&&PlayerPrefs.HasKey("login")&&PlayerPrefs.HasKey("pass"))
        {
            login.text = PlayerPrefs.GetString("login");
            pass.text = PlayerPrefs.GetString("pass");
            Logar();
        }
    }

    // Update is called once per frame
    void Update()
    {
        
    }
    public void Logar()
    {
        StartCoroutine(log());
    }
    IEnumerator log()
    {
        UnityWebRequest www = UnityWebRequest.Get("http://ec2-18-228-130-49.sa-east-1.compute.amazonaws.com/dev/autentica.php?login="+login.text+"&senha="+pass.text);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            // Show results as text
            Debug.Log(www.downloadHandler.text);

            // Or retrieve results as binary data
            byte[] results = www.downloadHandler.data;
            string resu =Encoding.UTF8.GetString(results);
            JSONNode jo = JSON.Parse(resu);
            try
            {
                string s = jo["local_id"];
                if (string.IsNullOrEmpty(s)) throw new System.Exception();
                PlayerPrefs.SetString("localid", s);
                PlayerPrefs.SetString("login", login.text);
                PlayerPrefs.SetString("pass", pass.text);
                PlayerPrefs.Save();
                SceneManager.LoadScene(1);
            }
            catch
            {
                feedback.text = "Erro ao logar email ou senha incorretos";
            }
        }
    }
}
