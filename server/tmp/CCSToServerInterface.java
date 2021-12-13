package communication;

import org.json.simple.JSONObject; // a include 

public interface CCSToServerInterface {

    /**
     * Searches for a project in database
     * (Classification)
     * @param userID the ID of the user
     * @param title the string of character to be found in database
     * @return a list of corresponding projects (JSON project object)
     */
    JSONObject searchProject(int userID, String title);

    /**
     * Gets new projects from server 
     * (Classification)
     * @param userID the ID of the user
     * @param position last known position of user (GPS)
     * @return A list of new projects (JSON project object)
     */
    JSONObject getProjects(int userID, int position); // GPS class temporary

    /**
     * Gets new friends from server (facebook)
     * (Social)
     * @param userID the ID of the user 
     * @return a list of all new friends (simplified JSON profile object)
     */
    JSONObject getFriends(int userID);

    /**
     * Updates the profile when user preferences change (or when user reacted to a project)
     * (Classification)
     * @param userProfile profile of user (simplified JSON profile object)
     * @return true if all went well, false if error occured
     */
    boolean updateProfile(JSONObject userProfile);

    /**
     * Adds +1 like on a given project
     * (BDD)
     * @param projectID The ID of the liked project 
     * @return true if all went well, false if error occured
     */
    boolean addLike(int projectID);

    /**
     * Gets ip adress of a user
     * (BDD)
     * @param userID the ID of the user
     * @return a string containing adress
     */
    String getUserAdress(int userID);

    /**
     * gets a specific project for a given ID
     * (BDD)
     * @param projectID the project ID
     * @return the project Object
     */
    JSONObject getSpecificProject(int projectID);

}