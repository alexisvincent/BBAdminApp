package gui;

import components.AColor;
import components.AComponent;
import components.AList;
import components.AListItem;
import components.AListModel;
import components.APopup;
import components.AScrollPane;
import components.BButton;
import components.BLabel;
import components.BTextArea;
import components.BTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import main.BBAdminApp;
import networking.ASocket;
import networking.Request;
import networking.Responce;
import objects.Candidate;
import org.jdom2.Document;
import org.jdom2.Element;
import toolkit.BToolkit;
import toolkit.ResourceManager;
import toolkit.UIToolkit;

/**
 *
 * @author alexisvincent
 */
public class CandidateOverlay extends AComponent {

    private GridBagConstraints gc;
    private CandidateOverlay.CandidatePane candidatePane;
    private AComponent overlayFrame;
    private AScrollPane candidateScrollPane;

    public CandidateOverlay() {

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                candidatePane.nameField.setText("");
                candidatePane.infoField.setText("");
                candidatePane.currentCandidate = null;

                updateList();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                MainFrame.getHomeScreen().getHomeScreenPanel().updateList();
            }
        });

        candidatePane = new CandidateOverlay.CandidatePane();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                CandidateOverlay.this.setVisible(false);
            }
        });

        candidateScrollPane = new AScrollPane(candidatePane);

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
        overlayFrame.add(candidateScrollPane, gc);

        this.setLayout(new GridBagLayout());
        setGridBagDefaults();
        this.add(overlayFrame, gc);
    }

    public void updateList() {
        //getCandidates
        ArrayList<AListItem> items = new ArrayList<>();
        Element rootElement = new Element("Request");
        rootElement.setAttribute("RequestType", "Candidates");
        rootElement.setAttribute("From", "AdminApp");

        Document document = new Document(rootElement);
        ASocket socket = BBAdminApp.getNetworkingClient().getSocket();
        Request request = new Request(document, socket);
        Responce responce = null;

        try {
            responce = socket.postRequest(request);
        } catch (Exception e) {
        }


        if (responce != null && responce.getResponceCode().equals("200")) {

            rootElement = responce.getRootElement();
            ArrayList<Candidate> candidates = new ArrayList<>();
            int candidateCount = Integer.parseInt(rootElement.getAttributeValue("CandidatesCount"));

            for (int i = 1; i < candidateCount + 1; i++) {
                Element candidateElement = rootElement.getChild("Candidate" + i);
                String id = candidateElement.getAttributeValue("ID");
                String name = candidateElement.getAttributeValue("Name");
                String info = candidateElement.getAttributeValue("Info");
                String image = candidateElement.getAttributeValue("Image");

                candidates.add(new Candidate(id, name, info, image));
            }

            for (Candidate candidate : candidates) {
                items.add(candidatePane.new CandidateListItem(candidate));
            }

            if (items.isEmpty()) {
                items.add(candidatePane.new CandidateListItem(new Candidate()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        this.removeAll();
                        Graphics2D g2d = UIToolkit.getPrettyGraphics(g);
                        g2d.setPaint(AColor.fancyLightBlue);
                        g2d.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 12, 12);

                        g2d.setPaint(Color.WHITE);
                        g2d.setFont(ResourceManager.getFont("Sax Mono").deriveFont(13f));
                        g2d.drawString("No Candidates", 77, 15);
                    }
                });
            }

        } else {
            items.add(candidatePane.new CandidateListItem(new Candidate("", "Server OFFLINE", "", "")) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = UIToolkit.getPrettyGraphics(g);
                    g2d.setPaint(AColor.fancyLightBlue);
                    g2d.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 12, 12);

                    g2d.setPaint(Color.WHITE);
                    g2d.setFont(ResourceManager.getFont("Aeriel").deriveFont(13f));
                    g2d.drawString(getCandidate().getName(), 80, 35);
                }
            });
            System.out.println("Could not get Candidates from server");
        }

        candidatePane.candidateList.setItems(items);

        if (!candidatePane.candidateList.getItems().isEmpty()) {
            candidatePane.candidateList.getItems().get(0).setSelected(true);
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

    private class CandidatePane extends AComponent {

        private BLabel headingLabel;
        private CandidateOverlay.CandidatePane.CandidateList candidateList;
        private BLabel nameLabel;
        private BLabel infoLabel;
        private BTextField nameField;
        private Image image;
        private BTextArea infoField;
        private BButton newButton;
        private BButton saveButton;
        private Candidate currentCandidate;

        public CandidatePane() {

            headingLabel = new BLabel("Candidates");
            headingLabel.setPreferredSize(new Dimension(160, 20));
            headingLabel.setFont(ResourceManager.getFont("Aeriel").deriveFont(18f));
            headingLabel.setLabelColor(AColor.fancyDarkGreen);

            candidateList = new CandidateOverlay.CandidatePane.CandidateList(new CandidateOverlay.CandidatePane.CandidateListModel());
            candidateList.setPreferredSize(new Dimension(0, 120));

            nameLabel = new BLabel("Name");
            nameLabel.setPreferredSize(new Dimension(60, 15));
            nameLabel.setFont(ResourceManager.getFont("Sax Mono").deriveFont(15f));

            infoLabel = new BLabel("Info");
            infoLabel.setPreferredSize(new Dimension(60, 15));
            infoLabel.setFont(ResourceManager.getFont("Sax Mono").deriveFont(15f));

            nameField = new BTextField();
            nameField.setPreferredSize(new Dimension(200, 30));
            nameField.setFont(ResourceManager.getFont("Sax Mono").deriveFont(15f));

            infoField = new BTextArea();
            infoField.setPreferredSize(new Dimension(0, 120));
            infoField.setFont(ResourceManager.getFont("Sax Mono").deriveFont(15f));

            newButton = new BButton("Add New");
            newButton.setPreferredSize(new Dimension(100, 30));
            newButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (BToolkit.checkComponentCompletion(CandidatePane.this)) {
                        Element rootElement = new Element("Request");
                        rootElement.setAttribute("RequestType", "AddCandidate");
                        rootElement.setAttribute("From", "AdminApp");
                        rootElement.setAttribute("CandidateName", nameField.getText());
                        rootElement.setAttribute("CandidateInfo", infoField.getText());

                        Document document = new Document(rootElement);
                        ASocket socket = BBAdminApp.getNetworkingClient().getSocket();
                        Request request = new Request(document, socket);
                        socket.postRequest(request);

                        CandidateOverlay.this.updateList();
                        nameField.setText("");
                        infoField.setText("");
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

                    if (BToolkit.checkComponentCompletion(CandidatePane.this) && currentCandidate != null) {
                        Element rootElement = new Element("Request");
                        rootElement.setAttribute("RequestType", "UpdateCandidate");
                        rootElement.setAttribute("From", "AdminApp");
                        rootElement.setAttribute("CandidateName", nameField.getText());
                        rootElement.setAttribute("CandidateInfo", infoField.getText());
                        rootElement.setAttribute("CandidateID", currentCandidate.getId());

                        Document document = new Document(rootElement);
                        ASocket socket = BBAdminApp.getNetworkingClient().getSocket();
                        Request request = new Request(document, socket);
                        socket.postRequest(request);

                        CandidateOverlay.this.updateList();
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
            this.add(candidateList, gc);

            gc.fill = GridBagConstraints.NONE;
            gc.insets = new Insets(5, 15, 5, 15);
            gc.anchor = GridBagConstraints.SOUTHWEST;
            gc.gridy++;
            this.add(nameLabel, gc);

            gc.gridy++;
            gc.anchor = GridBagConstraints.WEST;
            this.add(nameField, gc);

            gc.gridy++;
            gc.anchor = GridBagConstraints.SOUTHWEST;
            this.add(infoLabel, gc);

            gc.gridy++;
            gc.anchor = GridBagConstraints.CENTER;
            gc.fill = GridBagConstraints.HORIZONTAL;
            this.add(infoField, gc);

            gc.gridy++;
            gc.gridwidth = 1;
            gc.fill = GridBagConstraints.NONE;
            this.add(newButton, gc);

            gc.gridx++;
            this.add(saveButton, gc);
        }

        public void setCandidate(Candidate candidate) {
            nameField.setText(candidate.getName());
            infoField.setText(candidate.getInfo());

            currentCandidate = candidate;
        }

        private class CandidateList extends AList {

            public CandidateList(AListModel model) {
                super(model);
            }
        }

        private class CandidateListModel extends AListModel {

            public CandidateListModel() {
                super();
            }

            @Override
            public void setItems(ArrayList<AListItem> items) {
                if (!items.isEmpty() && items.get(0) instanceof CandidateOverlay.CandidatePane.CandidateListItem) {
                    super.setItems(items);
                } else {
                    System.out.println("Invalid Items: CandidateListItems required");
                }
            }
        }

        private class CandidateListItem extends AListItem {

            private Candidate candidate;
            private BButton removeButton;

            public CandidateListItem(Candidate candidate) {
                super(candidate.getName());
                this.candidate = candidate;
                this.setPreferredSize(new Dimension(0, 25));

                this.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        CandidateOverlay.CandidatePane.this.setCandidate(getCandidate());
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
                        rootElement.setAttribute("RequestType", "RemoveCandidate");
                        rootElement.setAttribute("From", "AdminApp");
                        rootElement.setAttribute("CandidateID", getCandidate().getId());

                        Document document = new Document(rootElement);
                        ASocket socket = BBAdminApp.getNetworkingClient().getSocket();
                        Request request = new Request(document, socket);
                        socket.postRequest(request);

                        CandidateOverlay.this.updateList();
                        nameField.setText("");
                        infoField.setText("");

                    }
                });

                this.setLayout(
                        new GridBagLayout());
                setGridBagDefaults();
                gc.fill = GridBagConstraints.NONE;
                gc.anchor = GridBagConstraints.EAST;

                this.add(removeButton, gc);
            }

            public Candidate getCandidate() {
                return candidate;
            }

            public void setCandidate(Candidate candidate) {
                this.candidate = candidate;
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
                if (getCandidate().getName().length() > 22) {
                    g2d.drawString(getCandidate().getName().substring(0, 20) + "...", 12, 15);
                } else {
                    g2d.drawString(getCandidate().getName(), 12, 15);
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
