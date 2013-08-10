package main;

import components.BMenuBar;
import gui.MainFrame;
import gui.SplashScreen;
import java.util.ArrayList;
import networking.ElectionProfile;
import networking.NetworkingClient;
import settingsEngine.ProfileEngine;

/**
 *
 * @author alexisvincent
 */
public class BBAdminApp {

    private SplashScreen splashScreen;
    private static BBAdminApp INSTANCE;
    private static MainFrame mainFrame;
    //Engines
    private static ProfileEngine profileEngine;
    private static NetworkingClient networkingClient;
    private static ElectionProfile electionProfile;

    public BBAdminApp() {
        splashScreen = new SplashScreen();
        
        profileEngine = new ProfileEngine();
        setElectionProfile(profileEngine.getFirstProfile());
        
        mainFrame = new MainFrame();
        splashScreen.setVisible(false);
        BMenuBar.setMainFrame(mainFrame);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public static ProfileEngine getProfileEngine() {
        return profileEngine;
    }

    public static ElectionProfile getElectionProfile() {
        return electionProfile;
    }
    
    public static ArrayList<ElectionProfile> getElectionProfiles() {
        return profileEngine.getElectionProfiles();
    }

    public static void setElectionProfile(ElectionProfile electionProfile) {
        BBAdminApp.electionProfile = electionProfile;
        BBAdminApp.networkingClient = new NetworkingClient(electionProfile.getServer());
    }

    public static BBAdminApp getINSTANCE() {
        return INSTANCE;
    }

    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static void setMainFrame(MainFrame mainFrame) {
        BBAdminApp.mainFrame = mainFrame;
    }

    public static NetworkingClient getNetworkingClient() {
        return networkingClient;
    }

    public static void setNetworkingClient(NetworkingClient networkingClient) {
        BBAdminApp.networkingClient = networkingClient;
    }
    

    public static void main(String[] args) {
        INSTANCE = new BBAdminApp();
    }
}
