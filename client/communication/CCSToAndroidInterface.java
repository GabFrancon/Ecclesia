package android.communication;

import org.json.simple.JSONObject; // a include 

/**
 * Interface Permettant la communication avec le serveur depuis l'application
 */
public interface CCSToAndroidInterface {

    /**
     * Ads a project to the list of downloaded Project
     * Is used when a friend shares a project with the user
     * @param project the shared project (JSON project object)
     * @param friendID the user ID of the friend
     */
    void addProject(JSONObject project, int friendID);

}
