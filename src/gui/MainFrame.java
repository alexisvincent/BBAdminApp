package gui;

import components.AFrame;

/**
 *
 * @author alexisvincent
 */
public class MainFrame extends AFrame {

    private static HomeScreen homeScreen;
    private static ProfileSelectionOverlay profileSelectionOverlay;
    private static ElectionProfileOverlay electionProfileOverlay;

    public MainFrame() {
        super();

        //new instances
        homeScreen = new HomeScreen();
        profileSelectionOverlay = new ProfileSelectionOverlay();
        profileSelectionOverlay.setVisible(false);
        
        electionProfileOverlay = new ElectionProfileOverlay();
        electionProfileOverlay.setVisible(false);

        //configure this damn FRAME O.o
        this.setResizable(false);
        this.setSize(400, 500);

        //begin adding components
        addPaneltoDefaultLayer(homeScreen);
        addPaneltoPaletteLayer(profileSelectionOverlay);
        addPaneltoPaletteLayer(electionProfileOverlay);

        //starting animation
    }
    
    public static HomeScreen getHomeScreen() {
        return homeScreen;
    }

    public static ProfileSelectionOverlay getProfileSelectionOverlay() {
        return profileSelectionOverlay;
    }

    public static ElectionProfileOverlay getElectionProfileOverlay() {
        return electionProfileOverlay;
    }

}
