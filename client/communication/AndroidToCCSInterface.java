package android.communication;

import org.json.simple.JSONObject; // a include 

/**
 * Interface allowing comunication from the app to the server
 */
public interface AndroidToCCSInterface {
    
    /**
     * Searches for a project in database
     * @param userID the ID of the user
     * @param title the string of character to be found in database
     * @return a list of corresponding projects (JSON project object)
     */
    JSONObject searchProject(int userID, String title);

    /**
     * Gets new projects and new info on saved projects from server 
     * @param userID the ID of the user
     * @param position last known position of user (GPS)
     * @return A list of new projects (JSON project object)
     */
    JSONObject getProjects(int userID, GPS position); // GPS class temporary


    /**
     * Gets new friends from server (facebook)
     * @param userID the ID of the user 
     * @return a list of all new friends (simplified JSON profile object)
     */
    JSONObject getFriends(int userID);

    /**
     * Updates the profile when user preferences change
     * @param userProfile profile of user (simplified JSON profile object)
     * @return true if all went well, false if error occured
     */
    boolean updateProfile(JSONObject userProfile);

    /**
     * Notifies Server that user reacted to a project (like/dislike/save)
     * @param userID The ID of the user
     * @param projectID The ID of the liked project 
     * @param interest The interest of the user (0 = dislike, 1 = save, 2 = like)
     * @return true if all went well, false if error occured
     */
    boolean updateInterest(int userID, int projectID, int interest);

    /**
     * Sends a specific project to a friend
     * @param userID The ID of the user
     * @param friendID the user ID of the targeted friend
     * @param projectID the ID of the project to be shared
     * @return true if all went well, false if error occured
     */
    boolean sendToFriends(int friendID, int projectID);

}   