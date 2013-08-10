package gui;

import components.AComponent;
import components.BButton;
import components.BFormattedTextField;
import components.BLabel;
import components.BPasswordField;
import components.BTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import main.BBAdminApp;
import networking.ElectionProfile;
import networking.Server;
import toolkit.ResourceManager;
import toolkit.UIToolkit;

/**
 *
 * @author alexisvincent
 */
public class ElectionProfileOverlay extends AComponent {

    private GridBagConstraints gc;
    private ElectionProfilePane electionProfilePane;

    public ElectionProfileOverlay() {

        electionProfilePane = new ElectionProfilePane();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                ElectionProfileOverlay.this.setVisible(false);
            }
        });

        this.setLayout(new GridBagLayout());
        setGridBagDefaults();
        this.add(electionProfilePane, gc);
    }

    private void setGridBagDefaults() {
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.CENTER;
    }

    public void setElectionProfile(ElectionProfile electionProfile) {
        electionProfilePane.setElectionProfile(electionProfile);
    }

    private class ElectionProfilePane extends AComponent {

        private BLabel infoLabel;
        private BLabel nameLabel;
        private BLabel passwordLabel;
        private BLabel serverAddressLabel;
        private BLabel serverPortLabel;
        private BTextField nameTextField;
        private BPasswordField passwordField;
        private BTextField serverAddressTextField;
        private BFormattedTextField serverPortTextField;
        private BButton saveButton;
        private BButton deleteButton;
        private ElectionProfile currentProfile;

        public ElectionProfilePane() {
            this.setPreferredSize(new Dimension(300, 220));

            infoLabel = new BLabel("Please complete all fields. :)");
            infoLabel.setPreferredSize(new Dimension(250, 20));
            infoLabel.setFont(ResourceManager.getFont("Sax Mono", 14));

            nameLabel = new BLabel("Name");
            nameLabel.setPreferredSize(new Dimension(120, 20));
            nameLabel.setFont(ResourceManager.getFont("Sax Mono", 14));

            passwordLabel = new BLabel("Password");
            passwordLabel.setPreferredSize(new Dimension(120, 20));
            passwordLabel.setFont(ResourceManager.getFont("Sax Mono", 14));

            serverAddressLabel = new BLabel("Server Address");
            serverAddressLabel.setPreferredSize(new Dimension(120, 20));
            serverAddressLabel.setFont(ResourceManager.getFont("Sax Mono", 14));

            serverPortLabel = new BLabel("Server Port");
            serverPortLabel.setPreferredSize(new Dimension(120, 20));
            serverPortLabel.setFont(ResourceManager.getFont("Sax Mono", 14));

            nameTextField = new BTextField();
            nameTextField.setPreferredSize(new Dimension(120, 30));
            nameTextField.setFont(ResourceManager.getFont("Sax Mono", 14));

            passwordField = new BPasswordField();
            passwordField.setPreferredSize(new Dimension(120, 30));
            passwordField.setFont(ResourceManager.getFont("Sax Mono", 14));

            serverAddressTextField = new BTextField();
            serverAddressTextField.setPreferredSize(new Dimension(120, 30));
            serverAddressTextField.setFont(ResourceManager.getFont("Sax Mono", 14));

            try {
                serverPortTextField = new BFormattedTextField(new MaskFormatter("#####"));
                serverPortTextField.setFocusLostBehavior(JFormattedTextField.COMMIT);
                serverPortTextField.setPreferredSize(new Dimension(120, 30));
                serverPortTextField.setFont(ResourceManager.getFont("Sax Mono", 14));
            } catch (ParseException ex) {
                Logger.getLogger(ElectionProfileOverlay.class.getName()).log(Level.SEVERE, null, ex);
            }

            saveButton = new BButton("SAVE");
            saveButton.setPreferredSize(new Dimension(100, 30));
            saveButton.setFont(ResourceManager.getFont("Sax Mono", 14));
            saveButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    
                    Server server = new Server(nameTextField.getText(), serverAddressTextField.getText(), Integer.parseInt(serverPortTextField.getText()));
                    currentProfile.setName(nameTextField.getText());
                    currentProfile.setServer(server);
                    
                    currentProfile.deleteFile();
                    currentProfile.setFile(new File("./" + currentProfile.getName() + "/blueballot.conf"));
                    currentProfile.updateFile();
                    BBAdminApp.getProfileEngine().updateProfileList();
                    BBAdminApp.setElectionProfile(currentProfile);
                    MainFrame.getProfileSelectionOverlay().updateList();
                    MainFrame.getProfileSelectionOverlay().setSelectedProfile(currentProfile);
                    MainFrame.getHomeScreen().getHomeScreenPanel().updateList();
                    MainFrame.getElectionProfileOverlay().setVisible(false);
                }
            });
            
            deleteButton = new BButton("DELETE");
            deleteButton.setPreferredSize(new Dimension(100, 30));
            deleteButton.setFont(ResourceManager.getFont("Sax Mono", 14));
            deleteButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    
                    currentProfile.deleteFile();
                    BBAdminApp.getProfileEngine().updateProfileList();
                    BBAdminApp.setElectionProfile(BBAdminApp.getProfileEngine().getFirstProfile());
                    MainFrame.getProfileSelectionOverlay().updateList();
                    MainFrame.getProfileSelectionOverlay().setSelectedProfile(BBAdminApp.getProfileEngine().getFirstProfile());
                    MainFrame.getHomeScreen().getHomeScreenPanel().updateList();
                    MainFrame.getElectionProfileOverlay().setVisible(false);
                }
            });

            this.setLayout(new GridBagLayout());
            setGridBagDefaults();

            gc.anchor = GridBagConstraints.SOUTH;
            gc.gridwidth++;
            this.add(infoLabel, gc);

            gc.anchor = GridBagConstraints.SOUTH;
            gc.gridy++;
            gc.gridwidth--;
            this.add(nameLabel, gc);

            gc.anchor = GridBagConstraints.SOUTH;
            gc.gridx++;
            this.add(passwordLabel, gc);

            gc.anchor = GridBagConstraints.NORTH;
            gc.gridy++;
            gc.gridx--;
            this.add(nameTextField, gc);

            gc.anchor = GridBagConstraints.NORTH;
            gc.gridx++;
            this.add(passwordField, gc);

            gc.anchor = GridBagConstraints.SOUTH;
            gc.gridy++;
            gc.gridx--;
            this.add(serverAddressLabel, gc);

            gc.anchor = GridBagConstraints.SOUTH;
            gc.gridx++;
            this.add(serverPortLabel, gc);

            gc.anchor = GridBagConstraints.NORTH;
            gc.gridy++;
            gc.gridx--;
            this.add(serverAddressTextField, gc);

            gc.anchor = GridBagConstraints.NORTH;
            gc.gridx++;
            this.add(serverPortTextField, gc);

            gc.anchor = GridBagConstraints.CENTER;
            gc.gridy++;
            gc.gridx--;
            this.add(saveButton, gc);
            
            gc.anchor = GridBagConstraints.CENTER;
            gc.gridx++;
            this.add(deleteButton, gc);
        }

        public void setElectionProfile(ElectionProfile electionProfile) {
            nameTextField.setText(electionProfile.getName());
            serverAddressTextField.setText(electionProfile.getServer().getServerAddress());
            serverPortTextField.setText("" + electionProfile.getServer().getServerPort());
            currentProfile = electionProfile;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = UIToolkit.getPrettyGraphics(g);
            g2d.setPaint(new Color(34, 34, 34, 255));
            g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 15, 15);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = UIToolkit.getPrettyGraphics(g);

        g2d.setPaint(new Color(34, 34, 34, 150));
        g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 15, 15);
    }
}
