import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import java.util.logging.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientHTTP {
    private static Logger logger = Logger.getLogger("com.foo.client");
    private URL url; // url of server
    private HttpURLConnection con = null; // To send a request

    public ClientHTTP(int port) {
        logger.setLevel(Level.FINE);
        try {
            String siteWeb = "http://localhost:" + port + "/";

            url = new URL(siteWeb);
            logger.fine("Authority : " + url.getAuthority());
            logger.fine("Default port : " + url.getDefaultPort());
            logger.fine("Host : " + url.getHost());
            logger.fine("Port : " + url.getPort());
            logger.fine("Protocol : " + url.getProtocol());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject getProjects() 
            throws MalformedURLException, IOException, ParseException {
        return makeRequest(0, "none");
    }
    
    public JSONObject searchProject(String title) 
            throws MalformedURLException, IOException, ParseException {
        return makeRequest(1, title);
    }

    private JSONObject makeRequest(int actionId, String param)
            throws MalformedURLException, IOException, ParseException {
        String line, result = null;
        
        param = URLEncoder.encode(param, "UTF-8");

        logger.fine("[Cli] " + url +"?s=" + actionId + "/"+ param);
        con = (HttpURLConnection) new URL(url +"?s=" + actionId + "/"+ param).openConnection(); 
        //Stock le flux
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        
        // ecriture du flux 
        while ((line = rd.readLine()) != null) {
            result = line + "\n";
        }  

        //only keeping the Json and parsing 
        result = result.split("²")[1];
        Object obj = JSONValue.parse(result);
        
        return (JSONObject)obj;
    }
}
    /*
   public static String makeSearch(String url, String search){

      //Une petite pause
      try {
         Thread.sleep(5000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      
      String resultat = "";
      HttpURLConnection con = null;
      try {
         
         //Cette classe permet d'encoder les caractères spéciaux  
         //pour qu'ils soient interprétables dans une URL
         //Nous devons fournir la chaîne à encoder et le type d'encodage, ici UTF-8
         String recherche = URLEncoder.encode("q", "UTF-8") + "=";
         recherche += URLEncoder.encode(search, "UTF-8");
         
         //Nous nous connectons, via un objet HTTPUrlConnection
         //à la nouvelle URL, la recherche se faisant en GET, 

         //System.out.println("URL de recherche : " + url + "?" + recherche);
         con = (HttpURLConnection) new URL(url + "?" + recherche).openConnection();
         
         //Comme la recherche précédente, nous récupérons un flux que nous allons stocker
         BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
         String line;
         while ((line = rd.readLine()) != null) {
            resultat += line + "\n";
         }         
      } catch (MalformedURLException e) {
         e.printStackTrace();
      } catch (ProtocolException e) {
         e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }

      return resultat;
   }   
}*/
/*     // Exceptions
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } */