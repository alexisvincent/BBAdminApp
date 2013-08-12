package gui;

import components.AColor;
import components.AComponent;
import components.AList;
import components.AListItem;
import components.AListModel;
import components.APopup;
import components.AScrollPane;
import components.BButton;
import components.BFormattedTextField;
import components.BLabel;
import components.BTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;
import main.BBAdminApp;
import networking.ASocket;
import networking.Request;
import networking.Responce;
import objects.Address;
import objects.Voter;
import org.jdom2.Document;
import org.jdom2.Element;
import toolkit.BToolkit;
import toolkit.ResourceManager;
import toolkit.UIToolkit;

/**
 *
 * @author alexisvincent
 */
public class VoterOverlay extends AComponent {

    private GridBagConstraints gc;
    private VoterOverlay.VoterPane voterPane;
    private AComponent overlayFrame;
    private AScrollPane voterScrollPane;

    public VoterOverlay() {

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                voterPane.clearFields();
                voterPane.currentVoter = null;

                updateList();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                MainFrame.getHomeScreen().getHomeScreenPanel().updateList();
            }
        });

        voterPane = new VoterOverlay.VoterPane();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                VoterOverlay.this.setVisible(false);
            }
        });

        voterScrollPane = new AScrollPane(voterPane);

        overlayFrame = new AComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = UIToolkit.getPrettyGraphics(g);
                g2d.setPaint(AColor.fancyDarkGrey);
                g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 15, 15);
            }
        };

        overlayFrame.setPreferredSize(new Dimension(300, 450));
        overlayFrame.setLayout(new GridBagLayout());
        setGridBagDefaults();
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(5, 5, 5, 5);
        overlayFrame.add(voterScrollPane, gc);

        this.setLayout(new GridBagLayout());
        setGridBagDefaults();
        this.add(overlayFrame, gc);
    }

    public void updateList() {
        //getCandidates
        ArrayList<AListItem> items = new ArrayList<>();
        Element rootElement = new Element("Request");
        rootElement.setAttribute("RequestType", "Voters");
        rootElement.setAttribute("From", "AdminApp");

        Document document = new Document(rootElement);
        ASocket socket = BBAdminApp.getNetworkingClient().getSocket();
        Request request = new Request(document, socket);
        Responce responce = null;

        try {
            responce = socket.postRequest(request);
        } catch (Exception e) {
            System.out.println("Posting request failed");
        }

        if (responce != null && responce.getResponceCode().equals("200")) {

            rootElement = responce.getRootElement();
            ArrayList<Voter> voters = new ArrayList<>();
            int voterCount = Integer.parseInt(rootElement.getAttributeValue("VotersCount"));

            for (int i = 1; i < voterCount + 1; i++) {
                Element voterElement = rootElement.getChild("Voter" + i);
                String firstName = voterElement.getAttributeValue("FirstName");
                String middelNames = voterElement.getAttributeValue("MiddleNames");
                String lastName = voterElement.getAttributeValue("LastName");
                String idNumber = voterElement.getAttributeValue("IDNumber");
                String voterID = voterElement.getAttributeValue("VoterID");
                String encryptionKey = voterElement.getAttributeValue("EncryptionKey");

                String addressLine1 = voterElement.getAttributeValue("AddressLine1");
                String addressLine2 = voterElement.getAttributeValue("AddressLine2");
                String city = voterElement.getAttributeValue("City");
                String suburb = voterElement.getAttributeValue("Suburb");
                String province = voterElement.getAttributeValue("Province");

                Address address = new Address(addressLine1, addressLine2, suburb, city, province);

                voters.add(new Voter(idNumber, firstName, middelNames, lastName, voterID, encryptionKey, address));
            }

            for (Voter voter : voters) {
                items.add(voterPane.new VoterListItem(voter));
            }

            if (items.isEmpty()) {
                items.add(voterPane.new VoterListItem(new Voter()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        this.removeAll();
                        Graphics2D g2d = UIToolkit.getPrettyGraphics(g);
                        g2d.setPaint(AColor.fancyLightBlue);
                        g2d.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 12, 12);

                        g2d.setPaint(Color.WHITE);
                        g2d.setFont(ResourceManager.getFont("Sax Mono").deriveFont(13f));
                        g2d.drawString("No Voters", 77, 15);
                    }
                });
            }

        } else {
            items.add(voterPane.new VoterListItem(new Voter("Server OFFLINE", "", "", "", "", "", null)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = UIToolkit.getPrettyGraphics(g);
                    g2d.setPaint(AColor.fancyLightBlue);
                    g2d.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 12, 12);

                    g2d.setPaint(Color.WHITE);
                    g2d.setFont(ResourceManager.getFont("Aeriel").deriveFont(13f));
                    g2d.drawString(getVoter().getFirstName(), 80, 35);
                }
            });
            System.out.println("Could not get Voters from server");
        }

        voterPane.voterList.setItems(items);

        if (!voterPane.voterList.getItems().isEmpty()) {
            voterPane.voterList.getItems().get(0).setSelected(true);
        }

        revalidate();

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

    private class VoterPane extends AComponent {

        private BLabel headingLabel;
        private VoterOverlay.VoterPane.VoterList voterList;
        private BLabel firstNameLabel;
        private BLabel middleNamesLabel;
        private BLabel lastNameLabel;
        private BLabel idNumberLabel;
        private BTextField firstNameField;
        private BTextField middleNamesField;
        private BTextField lastNameField;
        private BFormattedTextField idNumberField;
        private BButton newButton;
        private BButton saveButton;
        private Voter currentVoter;

        public VoterPane() {

            headingLabel = new BLabel("Voters");
            headingLabel.setPreferredSize(new Dimension(100, 20));
            headingLabel.setFont(ResourceManager.getFont("Aeriel").deriveFont(18f));
            headingLabel.setLabelColor(AColor.fancyDarkGreen);

            voterList = new VoterOverlay.VoterPane.VoterList(new VoterOverlay.VoterPane.VoterListModel());
            voterList.setPreferredSize(new Dimension(0, 120));

            firstNameLabel = new BLabel("First Name");
            firstNameLabel.setPreferredSize(new Dimension(100, 15));
            firstNameLabel.setFont(ResourceManager.getFont("Sax Mono").deriveFont(15f));

            middleNamesLabel = new BLabel("Middle Names");
            middleNamesLabel.setPreferredSize(new Dimension(100, 15));
            middleNamesLabel.setFont(ResourceManager.getFont("Sax Mono").deriveFont(15f));

            lastNameLabel = new BLabel("Last Name");
            lastNameLabel.setPreferredSize(new Dimension(100, 15));
            lastNameLabel.setFont(ResourceManager.getFont("Sax Mono").deriveFont(15f));

            idNumberLabel = new BLabel("ID Number");
            idNumberLabel.setPreferredSize(new Dimension(100, 15));
            idNumberLabel.setFont(ResourceManager.getFont("Sax Mono").deriveFont(15f));

            firstNameField = new BTextField();
            firstNameField.setPreferredSize(new Dimension(120, 30));
            firstNameField.setFont(ResourceManager.getFont("Sax Mono").deriveFont(15f));

            middleNamesField = new BTextField();
            middleNamesField.setPreferredSize(new Dimension(120, 30));
            middleNamesField.setFont(ResourceManager.getFont("Sax Mono").deriveFont(15f));

            lastNameField = new BTextField();
            lastNameField.setPreferredSize(new Dimension(120, 30));
            lastNameField.setFont(ResourceManager.getFont("Sax Mono").deriveFont(15f));

            try {
                idNumberField = new BFormattedTextField(new MaskFormatter("#############"));
                idNumberField.setPreferredSize(new Dimension(120, 30));
                idNumberField.setFont(ResourceManager.getFont("Sax Mono").deriveFont(15f));
            } catch (ParseException ex) {
                Logger.getLogger(VoterOverlay.class.getName()).log(Level.SEVERE, null, ex);
            }

            clearFields();

            newButton = new BButton("Add New");
            newButton.setPreferredSize(new Dimension(100, 30));
            newButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (BToolkit.checkComponentCompletion(VoterPane.this)) {
                        Element rootElement = new Element("Request");
                        rootElement.setAttribute("RequestType", "AddVoter");
                        rootElement.setAttribute("From", "AdminApp");
                        rootElement.setAttribute("IDNumber", idNumberField.getText());
                        rootElement.setAttribute("FirstName", firstNameField.getText());
                        rootElement.setAttribute("MiddleNames", middleNamesField.getText());
                        rootElement.setAttribute("LastName", lastNameField.getText());

                        Document document = new Document(rootElement);
                        ASocket socket = BBAdminApp.getNetworkingClient().getSocket();
                        Request request = new Request(document, socket);
                        socket.postRequest(request);

                        VoterOverlay.this.updateList();
                        clearFields();
                    } else {
                        new APopup("Please fill out all fields");
                    }

                }
            });

            saveButton = new BButton("Save");
            saveButton.setPreferredSize(new Dimension(70, 30));
            saveButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {

                    if (BToolkit.checkComponentCompletion(VoterPane.this) && currentVoter != null) {
                        Element rootElement = new Element("Request");
                        rootElement.setAttribute("RequestType", "UpdateVoter");
                        rootElement.setAttribute("From", "AdminApp");
                        rootElement.setAttribute("IDNumber", idNumberField.getText());
                        rootElement.setAttribute("FirstName", firstNameField.getText());
                        rootElement.setAttribute("MiddleNames", middleNamesField.getText());
                        rootElement.setAttribute("LastName", lastNameField.getText());
                        rootElement.setAttribute("VotersID", currentVoter.getVotersID());

                        Document document = new Document(rootElement);
                        ASocket socket = BBAdminApp.getNetworkingClient().getSocket();
                        Request request = new Request(document, socket);
                        socket.postRequest(request);

                        VoterOverlay.this.updateList();
                    } else {
                        new APopup("Please fill out all fields");
                    }
                }
            });

            setGridBagDefaults();
            this.setLayout(new GridBagLayout());
            gc.gridwidth = 2;
            gc.fill = GridBagConstraints.NONE;
            gc.insets = new Insets(5, 5, 0, 5);
            gc.anchor = GridBagConstraints.CENTER;
            this.add(headingLabel, gc);

            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.gridy++;
            this.add(voterList, gc);

            gc.fill = GridBagConstraints.NONE;
            gc.insets = new Insets(5, 15, 5, 15);
            gc.anchor = GridBagConstraints.SOUTHWEST;
            gc.gridy++;
            this.add(firstNameLabel, gc);

            gc.gridx++;
            gc.anchor = GridBagConstraints.SOUTH;
            this.add(middleNamesLabel, gc);

            gc.gridy++;
            gc.gridx--;
            gc.anchor = GridBagConstraints.WEST;
            this.add(firstNameField, gc);

            gc.gridx++;
            gc.anchor = GridBagConstraints.CENTER;
            this.add(middleNamesField, gc);

            gc.gridy++;
            gc.gridx--;
            gc.anchor = GridBagConstraints.SOUTHWEST;
            this.add(lastNameLabel, gc);

            gc.gridy++;
            gc.anchor = GridBagConstraints.CENTER;
            gc.fill = GridBagConstraints.HORIZONTAL;
            this.add(lastNameField, gc);

            gc.gridy++;
            gc.anchor = GridBagConstraints.CENTER;
            gc.fill = GridBagConstraints.HORIZONTAL;
            this.add(idNumberLabel, gc);

            gc.gridy++;
            gc.anchor = GridBagConstraints.CENTER;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weighty = 2;
            this.add(idNumberField, gc);

            gc.gridy++;
            gc.gridwidth = 1;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.NONE;
            this.add(newButton, gc);

            gc.gridx++;
            this.add(saveButton, gc);
        }

        public void clearFields() {
            firstNameField.setText("");
            middleNamesField.setText("");
            lastNameField.setText("");
            idNumberField.setText("");
        }

        public void setVoter(Voter voter) {
            firstNameField.setText(voter.getFirstName());
            middleNamesField.setText(voter.getMiddleNames());
            lastNameField.setText(voter.getSurname());
            idNumberField.setText(voter.getIdNumber());

            currentVoter = voter;
        }

        private class VoterList extends AList {

            public VoterList(AListModel model) {
                super(model);
            }
        }

        private class VoterListModel extends AListModel {

            public VoterListModel() {
                super();
            }

            @Override
            public void setItems(ArrayList<AListItem> items) {
                if (!items.isEmpty() && items.get(0) instanceof VoterOverlay.VoterPane.VoterListItem) {
                    super.setItems(items);
                } else {
                    System.out.println("Invalid Items: VoterListItems required");
                }
            }
        }

        private class VoterListItem extends AListItem {

            private Voter voter;
            private BButton removeButton;

            public VoterListItem(Voter voter) {
                super(voter.getFirstName() + " " + voter.getSurname());
                this.voter = voter;
                this.setPreferredSize(new Dimension(0, 25));

                this.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        VoterOverlay.VoterPane.this.setVoter(getVoter());
                    }
                });

                removeButton = new BButton("remove") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = UIToolkit.getPrettyGraphics(g);
                        if (this.isFocus()) {
                            g2d.setPaint(AColor.fancyDarkGreen);
                        } else {
                            g2d.setPaint(AColor.fancyDarkBlue);
                        }
                        g2d.drawString("remove", 2, 12);
                    }
                };
                removeButton.setPreferredSize(new Dimension(55, 20));
                removeButton.setFont(ResourceManager.getFont("Sax Mono").deriveFont(13f));

                removeButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {

                        Element rootElement = new Element("Request");
                        rootElement.setAttribute("RequestType", "RemoveVoter");
                        rootElement.setAttribute("From", "AdminApp");
                        rootElement.setAttribute("VoterID", getVoter().getVotersID());

                        Document document = new Document(rootElement);
                        ASocket socket = BBAdminApp.getNetworkingClient().getSocket();
                        Request request = new Request(document, socket);
                        socket.postRequest(request);

                        VoterOverlay.this.updateList();
                        firstNameField.setText("");
                        middleNamesField.setText("");

                    }
                });

                this.setLayout(
                        new GridBagLayout());
                setGridBagDefaults();
                gc.fill = GridBagConstraints.NONE;
                gc.anchor = GridBagConstraints.EAST;

                this.add(removeButton, gc);
            }

            public Voter getVoter() {
                return voter;
            }

            public void setVoter(Voter voter) {
                this.voter = voter;
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = UIToolkit.getPrettyGraphics(g);
                if (isFocus()) {
                    g2d.setPaint(AColor.fancyLightGreen);
                } else {
                    g2d.setPaint(AColor.fancyLightBlue);
                }
                g2d.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 12, 12);

                g2d.setPaint(Color.WHITE);
                g2d.setFont(ResourceManager.getFont("Sax Mono").deriveFont(13f));
                if ((getVoter().getFirstName() + " " + getVoter().getSurname()).length() > 22) {
                    g2d.drawString((getVoter().getFirstName() + " " + getVoter().getSurname()).substring(0, 20) + "...", 12, 15);
                } else {
                    g2d.drawString((getVoter().getFirstName() + " " + getVoter().getSurname()), 12, 15);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = UIToolkit.getPrettyGraphics(g);

        g2d.setPaint(new Color(34, 34, 34, 150));
        g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 15, 15);
    }
}
