package gui;

import components.AComponent;
import components.AList;
import components.AListItem;
import components.AListModel;
import components.BButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import main.BBAdminApp;
import networking.ElectionProfile;

/**
 *
 * @author alexisvincent
 */
public class ProfileSelectionOverlay extends AComponent {

    private ArrayList<ElectionProfile> electionProfiles;
    private AList profileList;
    private ProfileListModel model;
    
    private BButton newButton;
    private GridBagConstraints gc;

    public ProfileSelectionOverlay() {
        model = new ProfileListModel();
        profileList = new AList(model);
        profileList.setPreferredSize(new Dimension(200, 200));
        updateList();
        
        newButton = new BButton("NEW");
        newButton.setPreferredSize(new Dimension(100, 30));
        newButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                ProfileSelectionOverlay.this.setVisible(false);
            }
        });
        
        this.setLayout(new GridBagLayout());
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.CENTER;
        this.add(profileList, gc);

        gc.gridy++;
        this.add(newButton, gc);
    }

    public void updateList() {
        
        ArrayList<AListItem> items = new ArrayList<>();
        electionProfiles = BBAdminApp.getElectionProfiles();
        
        for (ElectionProfile electionProfile : electionProfiles) {
            items.add(new ProfileListItem(electionProfile));
        }
        
        model.setItems(items);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(new Color(34, 34, 34, 200));
        g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 15, 15);

        g2d.setPaint(new Color(34, 34, 34, 255));
        g2d.fillRoundRect(145, 80, 210, 248, 15, 15);
    }

    private class ProfileListModel extends AListModel {

        public ProfileListModel() {
            super();
        }

        @Override
        public void setItems(ArrayList<AListItem> items) {
            if (!items.isEmpty() && items.get(0) instanceof ProfileListItem) {
                super.setItems(items);
            } else {
                System.out.println("Invalid Items: ProfileListItems required");
            }
        }
    }

    private class ProfileListItem extends AListItem {

        ElectionProfile electionProfile;

        public ProfileListItem(ElectionProfile electionProfile) {
            super(electionProfile.getName());
            this.electionProfile = electionProfile;
            
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    BBAdminApp.setElectionProfile(getElectionProfile());
                    MainFrame.getHomeScreen().getHomeScreenPanel().updateList();
                    MainFrame.getProfileSelectionOverlay().setVisible(false);
                }
            });
        }

        public ElectionProfile getElectionProfile() {
            return electionProfile;
        }

        public void setElectionProfile(ElectionProfile electionProfile) {
            this.electionProfile = electionProfile;
        }
    }
}
