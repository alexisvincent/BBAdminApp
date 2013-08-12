package gui;

import components.AColor;
import components.AList;
import components.AListItem;
import components.AListModel;
import components.BFooter;
import components.BLabel;
import components.BMenuBar;
import components.BPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.Timer;
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
public class HomeScreen extends BPanel {
    //declare components

    private BMenuBar menubar;
    private HomeScreen.HomeScreenPanel homeScreenPanel;
    private BFooter footer;
    private JComponent logoPane;

    public HomeScreen() {
        //init components
        menubar = new BMenuBar();
        homeScreenPanel = new HomeScreen.HomeScreenPanel();

        footer = new BFooter();
        logoPane = new JComponent() {
            Image logo = BToolkit.getImage("logo");
            double logoEnlargement = 1.5;
            Point pt = new Point(200 - (int) (logo.getWidth(null) * logoEnlargement) / 2, 200 - (int) (logo.getHeight(null) * logoEnlargement) / 2);

            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2d = UIToolkit.getPrettyGraphics(g);
                g2d.setComposite(UIToolkit.makeComposite(50));
                g2d.drawImage(logo, pt.x, pt.y, (int) (logo.getWidth(null) * logoEnlargement), (int) (logo.getHeight(null) * logoEnlargement), this);
            }
        };

        GridBagConstraints gc = new GridBagConstraints();
        this.setLayout(new GridBagLayout());

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 0;
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        this.add(menubar, gc);

        gc.gridy = 1;
        gc.weighty = 1;
        this.add(homeScreenPanel, gc);

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 3;
        gc.weightx = 1;
        gc.weighty = 1;
        this.add(logoPane, gc);

    }

    public HomeScreen.HomeScreenPanel getHomeScreenPanel() {
        return homeScreenPanel;
    }

    public class HomeScreenPanel extends JComponent {

        private GridBagConstraints gc;
        private int panelOpacity;
        private BLabel currentProfileLabel;
        private BLabel editCurrentProfileButton;
        private AList profileStatsList;
        private StatsListModel statsModel;
        private BLabel candidates;
        private BLabel voters;

        public HomeScreenPanel() {

            //variabili
            panelOpacity = 255;

            //setup le variabili
            currentProfileLabel = new BLabel(BBAdminApp.getElectionProfile().getName());
            currentProfileLabel.setPreferredSize(new Dimension(200, 20));
            currentProfileLabel.setLabelColor(AColor.fancyDarkGreen);
            currentProfileLabel.setFont(ResourceManager.getFont("Aeriel").deriveFont(15f));

            editCurrentProfileButton = new BLabel("Edit");
            editCurrentProfileButton.setPreferredSize(new Dimension(40, 20));
            editCurrentProfileButton.setLabelColor(AColor.WHITE);
            editCurrentProfileButton.setFont(ResourceManager.getFont("Sax Mono").deriveFont(16f));
            editCurrentProfileButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    MainFrame.getElectionProfileOverlay().setElectionProfile(BBAdminApp.getElectionProfile());
                    MainFrame.getElectionProfileOverlay().setVisible(true);
                }
            });

            statsModel = new StatsListModel();
            profileStatsList = new AList(statsModel);
            profileStatsList.setPreferredSize(new Dimension(370, 200));
            
            Timer updater = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        updateList();
                    } catch (Exception ex) {
                        System.out.println(e);
                    }
                }
            });
            updater.start();

            currentProfileLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    MainFrame.getProfileSelectionOverlay().setVisible(true);
                }
            });

            candidates = new BLabel("Candidates");
            candidates.setPreferredSize(new Dimension(100, 20));
            candidates.setLabelColor(AColor.fancyDarkBlue);
            candidates.setFont(ResourceManager.getFont("Sax Mono").deriveFont(16f));
            candidates.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    MainFrame.getCandidateOverlay().setVisible(true);
                }
            });

            voters = new BLabel("Voters");
            voters.setPreferredSize(new Dimension(60, 20));
            voters.setLabelColor(AColor.fancyDarkBlue);
            voters.setFont(ResourceManager.getFont("Sax Mono").deriveFont(16f));
            voters.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    MainFrame.getVoterOverlay().setVisible(true);
                }
            });

            //begin adding le variabili
            this.setLayout(new GridBagLayout());
            gc = new GridBagConstraints();
            gc.gridx = 0;
            gc.gridy = 0;
            gc.gridwidth = 1;
            gc.gridheight = 1;
            gc.weightx = 0;
            gc.weighty = 0;
            gc.ipadx = 0;
            gc.ipady = 0;
            gc.insets = new Insets(20, 15, 0, 8);
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.EAST;
            this.add(currentProfileLabel, gc);

            gc.gridx++;
            this.add(editCurrentProfileButton, gc);

            gc.gridx--;
            gc.gridy++;
            gc.gridwidth = 2;
            gc.weighty = 1;
            gc.insets = new Insets(2, 0, 10, 0);
            gc.fill = GridBagConstraints.BOTH;
            this.add(profileStatsList, gc);

            gc.gridy++;
            gc.gridwidth = 1;
            gc.weighty = 0;
            gc.insets = new Insets(2, 15, 15, 0);
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.WEST;
            this.add(candidates, gc);

            gc.gridx++;
            gc.insets = new Insets(2, 0, 15, 15);
            gc.anchor = GridBagConstraints.EAST;
            this.add(voters, gc);
        }

        public void updateList() {

            //getCandidates
            ArrayList<AListItem> items = new ArrayList<>();
            Element rootElement = new Element("Request");
            rootElement.setAttribute("RequestType", "Stats");
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
                    int tally = Integer.parseInt(candidateElement.getAttributeValue("Tally"));
                    double percentage = Double.valueOf(candidateElement.getAttributeValue("Percentage"));
                    String image = candidateElement.getAttributeValue("Image");

                    candidates.add(new Candidate(id, name, info, image, tally, percentage));
                }

                for (Candidate candidate : candidates) {
                    items.add(new StatsListItem(candidate));
                }

                if (items.isEmpty()) {
                    items.add(new StatsListItem(new Candidate("", "No Candidates", "", "")) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2d = UIToolkit.getPrettyGraphics(g);
                            g2d.setPaint(AColor.fancyLightBlue);
                            g2d.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 12, 12);

                            g2d.setPaint(Color.WHITE);
                            g2d.setFont(ResourceManager.getFont("Aeriel").deriveFont(13f));
                            g2d.drawString(getCandidate().getName(), 93, 35);
                        }
                    });
                }

            } else {
                items.add(new StatsListItem(new Candidate("", "Server OFFLINE", "", "")) {
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

            statsModel.setItems(items);
            revalidate();

            currentProfileLabel.setName(BBAdminApp.getElectionProfile().getName());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = UIToolkit.getPrettyGraphics(g);
            g2d.setComposite(UIToolkit.makeComposite(panelOpacity));
        }

        private class StatsListModel extends AListModel {

            public StatsListModel() {
                super();
            }

            @Override
            public void setItems(ArrayList<AListItem> items) {
                if (!items.isEmpty() && items.get(0) instanceof StatsListItem) {
                    super.setItems(items);
                } else {
                    System.out.println("Invalid Items: StatsListItems required");
                }
            }
        }

        private class StatsListItem extends AListItem {

            private Candidate candidate;

            private StatsListItem(Candidate candidate) {
                super(candidate.getName());
                setPreferredSize(new Dimension(0, 60));
                this.candidate = candidate;
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
                g2d.setPaint(AColor.fancyLightBlue);
                g2d.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 12, 12);

                g2d.setPaint(Color.WHITE);
                g2d.setFont(ResourceManager.getFont("Aeriel").deriveFont(13f));
                g2d.drawString(getCandidate().getName(), 15, 20);

                g2d.setFont(ResourceManager.getFont("Sax Mono").deriveFont(14f));
                g2d.setPaint(Color.WHITE);
                g2d.drawString("Tally: ", 15, 43);
                g2d.setFont(ResourceManager.getFont("Aeriel").deriveFont(20f));
                g2d.drawString("" + getCandidate().getTally(), 70, 45);

                g2d.setFont(ResourceManager.getFont("Sax Mono").deriveFont(20f));
                g2d.setPaint(Color.WHITE);
                g2d.drawString(getCandidate().getPercentage() + "%", 130, 45);
            }
        }
    }
}