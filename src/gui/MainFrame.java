package gui;

import components.AFrame;

/**
 *
 * @author alexisvincent
 */
public class MainFrame extends AFrame {

    private static HomeScreen homeScreen;
    private static ProfileSelectionOverlay profileSelectionOverlay;

    public MainFrame() {
        super();

        //new instances
        homeScreen = new HomeScreen();
        profileSelectionOverlay = new ProfileSelectionOverlay();
        profileSelectionOverlay.setVisible(false);

        //configure this damn FRAME O.o
        this.setResizable(false);
        this.setSize(400, 500);

        //begin adding components
        addPaneltoDefaultLayer(homeScreen);
        addPaneltoPaletteLayer(profileSelectionOverlay);

        //starting animation
    }
    
    public static HomeScreen getHomeScreen() {
        return homeScreen;
    }

    public static ProfileSelectionOverlay getProfileSelectionOverlay() {
        return profileSelectionOverlay;
    }

}
