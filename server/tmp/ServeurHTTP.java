
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.json.simple.JSONObject;
 
public class ServeurHTTP {
  private HttpServer server;

  public ServeurHTTP(int port) throws IOException {
    InetSocketAddress addr = new InetSocketAddress(port);
    server = HttpServer.create(addr, 0);
 
    server.createContext("/", new Gestionnaire());
    server.setExecutor(Executors.newCachedThreadPool());
    server.start();
    System.out.println("Le serveur en ecoute sur le port: "+addr.getPort());
  }
  
  public void stop(){
    server.stop(0);
  }
}
 
class Gestionnaire implements HttpHandler {
 
  public void handle(HttpExchange exchange) throws IOException {
    String methodeRequete = exchange.getRequestMethod();
    if (methodeRequete.equalsIgnoreCase("GET")) {
      Headers reponseEntete = exchange.getResponseHeaders();
      reponseEntete.set("Content-Type", "text/plain");
      reponseEntete.set("Access-Control-Allow-Origin", "*");
      exchange.sendResponseHeaders(200, 0);
 
      OutputStream reponse = exchange.getResponseBody();
      Headers requeteEntete = exchange.getRequestHeaders();
      Set<String> keySet = requeteEntete.keySet();
      Iterator<String> iter = keySet.iterator();
      while (iter.hasNext()) {
        String key = iter.next();
        List values = requeteEntete.get(key);
        String s = key + " = " + values.toString() + "";
        reponse.write(s.getBytes());
      }

      //process request (isolate actionId and param)
      String param = exchange.getRequestURI().toString().split("=")[1]; //leaving only params
      int actionId = Integer.parseInt(param.split("/")[0]);
      param = param.split("/")[1];
      
      // formating response
      JSONObject obj = new JSONObject();

      //exemples of project (TEMPORARY)
      JSONObject ex1 = new JSONObject();
      JSONObject ex2 = new JSONObject();
      ex1.put("ID", 10);
      ex1.put("DESC", "Ce concert est super");
      ex1.put("TYPE", "Concert");
      ex1.put("TITRE", "Concerto pour piano n°1 de Beethoven");

      ex2.put("ID", 25);
      ex2.put("DESC", "Une exposition révolutionaire");
      ex2.put("TYPE", "Exposition");
      ex2.put("TITRE", "Anticorps au Palais de Tokyo");

      switch(actionId) {
        case 0: // getProjects
          /*obj.put("Local",BDD.scanSurroundingArea());
          obj.put("Recommanded",Classif.getRecommandedProjects));
          break;
          */

          obj.put("Local",ex1);
          obj.put("Recommanded",ex2);

          break;

        case 1: // searchProject
          /*obj = BDD.searchProject(param);
          break;
          */

          obj.put(0,ex1);
          obj.put(1,ex2);
          break;

        case 2: // getFriends
          /*obj = BDD.friendsList();
          break;
          */



        case 3: // updateProfile
          /*
          BDD.updateProfile(param);
          break;
          */
        case 4: // updateInterest
          /*
          BDD.addLike(param);
          break;
          */
        case 5: // sendToFriend
          //TODO 
          

          break;
        default :
          obj.put("actionId not recognized", true);
          break;
      }
      
      //writting 
      reponse.write(("²"+obj.toString()).getBytes());
      reponse.close();
    }
  }
}


