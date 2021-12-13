import org.json.simple.JSONObject;

public class MainTest {

   public static void main(String[] args) {
    
      //String host = "127.0.0.1";
      int port = 2345; //might change that
      ServeurHTTP ts = null;
      ClientHTTP cs = null;

      try {
         ts = new ServeurHTTP(port);
         System.out.println("serveur sur le port : " + port);

         cs = new ClientHTTP(port);
         System.out.println("Client initialisé.");
         System.out.println();

         //Test to 
         JSONObject result  = cs.getProjects();
         System.out.println("result of getProjects :");
         System.out.println(result.get("Recommanded"));
         System.out.println();

         result  = cs.searchProject("culture");
         System.out.println("result of searchProject :");
         System.out.println(result.get("0"));
         System.out.println();

         System.exit(0); // closing server
      } catch (Exception e)   {
         e.printStackTrace();
         System.exit(0); // closing server
      }
      
      
      
   }
}