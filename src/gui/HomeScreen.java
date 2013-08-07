package gui;

import components.AList;
import components.AListItem;
import components.AListModel;
import components.BButton;
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
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JComponent;
import main.BBAdminApp;
import networking.ASocket;
import networking.Request;
import networking.Responce;
import objects.Candidate;
import org.jdom2.Document;
import org.jdom2.Element;
import toolkit.BToolkit;

/**
 *
 * @author alexisvincent
 */
public class HomeScreen extends BPanel {
    //declare components

    private BMenuBar menubar;
    private HomeScreen.HomeScreenPanel statsScreenPanel;
    private BFooter footer;
    private JComponent logoPane;

    public HomeScreen() {
        //init components
        menubar = new BMenuBar();
        statsScreenPanel = new HomeScreen.HomeScreenPanel();
        footer = new BFooter();
        logoPane = new JComponent() {
            Image logo = BToolkit.getImage("logo");
            double logoEnlargement = 1.5;
            Point pt = new Point(200 - (int) (logo.getWidth(null) * logoEnlargement) / 2, 200 - (int) (logo.getHeight(null) * logoEnlargement) / 2);

            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(BToolkit.makeComposite(50));
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g2d.drawImage(logo, pt.x, pt.y, (int) (logo.getWidth(null) * logoEnlargement), (int) (logo.getHeight(null) * logoEnlargement), this);
            }
        };
        //set ResultScreen properties
        //add components to ResultScreen
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
        this.add(statsScreenPanel, gc);

        gc.gridy = 2;
        gc.weighty = 0;
        //this.add(footer, gc);

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 3;
        gc.weightx = 1;
        gc.weighty = 1;
        this.add(logoPane, gc);

    }

    public HomeScreen.HomeScreenPanel getHomeScreenPanel() {
        return statsScreenPanel;
    }

    public class HomeScreenPanel extends JComponent {

        private GridBagConstraints gc;
        private int panelOpacity;
        private BLabel currentProfileLabel;
        private BLabel editCurrentProfileButton;
        private AList profileStatsList;
        private StatsListModel statsModel;

        public HomeScreenPanel() {

            //variabili
            panelOpacity = 255;

            //setup le variabili
            currentProfileLabel = new BLabel(BBAdminApp.getElectionProfile().getName());
            currentProfileLabel.setPreferredSize(new Dimension(100, 20));
            editCurrentProfileButton = new BLabel("EDIT");
            editCurrentProfileButton.setPreferredSize(new Dimension(40, 20));
            statsModel = new StatsListModel();
            profileStatsList = new AList(statsModel);
            profileStatsList.setPreferredSize(new Dimension(350, 300));

            currentProfileLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    MainFrame.getProfileSelectionOverlay().setVisible(true);
                }
            });
            
            updateList();

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
            gc.insets = new Insets(0, 0, 0, 0);
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.EAST;
            this.add(currentProfileLabel, gc);

            gc.gridy++;
            this.add(editCurrentProfileButton, gc);

            gc.gridy++;
            gc.weighty = 1;
            gc.insets = new Insets(0, 0, 30, 0);
            gc.fill = GridBagConstraints.VERTICAL;
            this.add(profileStatsList, gc);


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
            Responce responce = socket.postRequest(request);

            if (responce.getResponceCode().equals("200")) {

                rootElement = responce.getRootElement();
                ArrayList<Candidate> candidates = new ArrayList<>();
                int candidateCount = Integer.parseInt(rootElement.getAttributeValue("CandidatesCount"));

                for (int i = 1; i < candidateCount + 1; i++) {
                    Element candidateElement = rootElement.getChild("Candidate" + i);
                    String id = candidateElement.getAttributeValue("ID");
                    String name = candidateElement.getAttributeValue("Name");
                    String info = candidateElement.getAttributeValue("Info");
                    int tally = Integer.parseInt(candidateElement.getAttributeValue("Tally"));
                    int percentage = Integer.parseInt(candidateElement.getAttributeValue("Percentage"));
                    Image image = null;

                    candidates.add(new Candidate(id, name, info, image, tally, percentage));
                }

                for (Candidate candidate : candidates) {
                    items.add(new StatsListItem(candidate));
                }

            } else {
                System.out.println("Could not get Candidates from server");
            }

            statsModel.setItems(items);
            currentProfileLabel.setName(BBAdminApp.getElectionProfile().getName());
        }

        public void animate(String action) {
            switch (action) {
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(BToolkit.makeComposite(panelOpacity));
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
                Graphics2D g2d = (Graphics2D) g;

                g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 12, 12);
                g2d.setPaint(Color.RED);
                g2d.drawString(getCandidate().getName(), 15, 15);
                g2d.setPaint(Color.RED);
                g2d.drawString("Tally: " + getCandidate().getTally(), 15, 30);
                g2d.setPaint(Color.RED);
                g2d.drawString(getCandidate().getPercentage() + "%", 100, 30);
            }
        }
    }
}