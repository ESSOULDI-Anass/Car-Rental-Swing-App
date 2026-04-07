package location_voiture;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class AgenceSwingApp extends JFrame {

    // ── Palette (style AgenceGUI) ─────────────────────────────────
    static final Color C_BG        = new Color(12, 15, 25);
    static final Color C_CARD      = new Color(20, 24, 38);
    static final Color C_CARD2     = new Color(26, 31, 50);
    static final Color C_ACCENT    = new Color(230, 57, 70);    // rouge vif
    static final Color C_ACCENT2   = new Color(255, 120, 30);   // orange
    static final Color C_BLUE      = new Color(70, 130, 255);
    static final Color C_BLUE_L    = new Color(110, 165, 255);
    static final Color C_TEAL      = new Color(40, 200, 170);
    static final Color C_GOLD      = new Color(255, 196, 0);
    static final Color C_RED       = new Color(230, 57, 70);
    static final Color C_GREEN     = new Color(46, 213, 115);
    static final Color C_TEXT      = new Color(245, 245, 255);
    static final Color C_MUTED     = new Color(140, 150, 175);
    static final Color C_BORDER    = new Color(45, 52, 80);
    static final Font  F_TITLE     = new Font("Georgia",  Font.BOLD,  26);
    static final Font  F_SECTION   = new Font("Segoe UI", Font.BOLD,  14);
    static final Font  F_BODY      = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font  F_MONO      = new Font("Consolas", Font.PLAIN, 12);

    // ── Chemins images (mêmes que AgenceGUI) ─────────────────────
    static final String IMG_DIR = "./src/img/";

    // ── Modèle ────────────────────────────────────────────────────
    Agence agence = new Agence();
    List<String> carImages = new ArrayList<>();

    // ── Composants partagés ───────────────────────────────────────
    DefaultTableModel voitureModel, locationModel, rechercheModel;
    JTable voitureTable, locationTable, rechercheTable;
    JLabel statusBar;
    JPanel gridcars;
    Runnable FillCb;
    
    private JPanel statsHeader;  // Panel contenant les chips
    private JPanel chipTotal, chipLouees, chipDisponibles;  // Références aux chips

    // Compteurs dashboard (sidebar)
    JLabel lblTotalVoitures, lblVoituresLibres, lblVoituresLouees, lblTotalClients;

    // ─────────────────────────────────────────────────────────────
    public AgenceSwingApp() {
        super(" AutoElite — Agence de Location");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 780);
        setMinimumSize(new Dimension(950, 640));
        setLocationRelativeTo(null);
        setIconImage(loadImg(IMG_DIR + "pngimg.com - parking_PNG32.png",32,32));

        initDemoData();
        setContentPane(buildRoot());
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════
    //  DONNÉES DÉMO
    // ══════════════════════════════════════════════════════════════
    void initDemoData() {
        // Voitures + images associées (ordre identique)
        addVoiture("Tesla",    "Model X",   2023, 250, "pngimg.com-tesla_car_PNG48.png");
        addVoiture("Renault",  "Kadjar",    2020,  85, "pngimg.com-renault_PNG70.png");
        addVoiture("Renault",  "Mégane GT", 2009,  75, "pngimg.com-renault_PNG44.png");
        addVoiture("Toyota",   "RAV4",      2018, 110, "pngimg.com-toyota_PNG1915.png");
        addVoiture("Toyota",   "Avalon",    2019, 130, "pngimg.com-toyota_PNG1919.png");
        addVoiture("Mercedes", "Classe G",  2022, 350, "pngimg.com-mercedes_PNG80166.png");
        addVoiture("Peugeot",  "308",       2021,  95, "pngimg.com-peugeot_PNG34662.png");

        Client c1 = new Client("M.",  "Alaoui", "Yassine", "AB123");
        Client c2 = new Client("Mme", "Benali", "Sara",    "CD456");
        try {
            agence.loueVoiture(c1, agence.getVoitures().get(0));
            agence.loueVoiture(c2, agence.getVoitures().get(3));
        } catch (Exception ignored) {}
    }

    void addVoiture(String marque, String modele, int annee, int prix, String imgFile) {
        agence.ajouterVoiture(new Voiture(marque, modele, annee, prix));
        carImages.add(IMG_DIR + imgFile);
    }

    // ══════════════════════════════════════════════════════════════
    //  ROOT
    // ══════════════════════════════════════════════════════════════
    JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 30, 70, 60), 0, 200, new Color(0, 0, 0, 0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), 200);
                g2.dispose();
            }
        };
        root.setOpaque(true);
        root.add(buildHeader(),    BorderLayout.NORTH);
        root.add(buildCenter(),    BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
        return root;
    }

    // ══════════════════════════════════════════════════════════════
    //  HEADER  (style AgenceGUI : Georgia bold + accent rouge)
    // ══════════════════════════════════════════════════════════════
    JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(18, 22, 40), getWidth(), 0, new Color(30, 20, 50));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Barre accent rouge en bas
                g2.setColor(C_ACCENT);
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);
                g2.dispose();
            }
        };
        h.setOpaque(false);
        h.setPreferredSize(new Dimension(0, 107));
        h.setBorder(new EmptyBorder(16, 28, 16, 28));

        // ── Logo gauche ──
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        logoPanel.setOpaque(false);
        logoPanel.add(makeCarIcon());          // icône voiture dessinée

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("AutoElite  •  Agence de Location");
        title.setFont(F_TITLE);               // Georgia Bold comme AgenceGUI
        title.setForeground(C_TEXT);

        JLabel sub = new JLabel("Système de gestion — Université Hassan II Mohammedia");
        sub.setFont(F_BODY);
        sub.setForeground(C_MUTED);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(sub);
        logoPanel.add(textPanel);
        h.add(logoPanel, BorderLayout.WEST);

        // ── Chips stats droite (style AgenceGUI) ──
     // ── Chips stats droite (style AgenceGUI) ──
        statsHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 4));
        statsHeader.setOpaque(false);
        
        // Création et stockage des chips
        
        chipTotal = statChip("🚘", "7", "Voitures", C_ACCENT);
        chipLouees = statChip("🔑", "2", "Louées", C_GOLD);
        chipDisponibles = statChip("✅", "5", "Disponibles", C_GREEN);
        
        statsHeader.add(chipTotal);
        statsHeader.add(chipLouees);
        statsHeader.add(chipDisponibles);
        
        h.add(statsHeader, BorderLayout.EAST);
        return h;
    }
    
 // Fonction de mise à jour des chips
    void updateHeaderStats() {
        int total = agence.getVoitures().size();
        int louees = (int) agence.getVoitures().stream().filter(v -> agence.estLoue(v)).count();
        int disponibles = total - louees;
        
        updateStatChip(chipTotal, String.valueOf(total));
        updateStatChip(chipLouees, String.valueOf(louees));
        updateStatChip(chipDisponibles, String.valueOf(disponibles));
    }

    // Met à jour la valeur d'un chip
    void updateStatChip(JPanel chip, String newValue) {
        // Le chip a un GridLayout(3,1) : ligne0=emoji, ligne1=valeur, ligne2=label
        Component[] components = chip.getComponents();
        if (components.length >= 2 && components[1] instanceof JLabel) {
            ((JLabel) components[1]).setText(newValue);
        }
    }

    /** Chip stat carré comme dans AgenceGUI */
    JPanel statChip(String emoji, String value, String label, Color color) {
        JPanel chip = new JPanel(new GridLayout(3, 1)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        chip.setOpaque(false);
        chip.setBorder(new EmptyBorder(8, 14, 8, 14));
        chip.setPreferredSize(new Dimension(90, 70));

        JLabel emojiL = new JLabel(emoji, SwingConstants.CENTER);
        emojiL.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));

        JLabel valL = new JLabel(value, SwingConstants.CENTER);
        valL.setFont(new Font("Georgia", Font.BOLD, 20));
        valL.setForeground(color);

        JLabel lblL = new JLabel(label, SwingConstants.CENTER);
        lblL.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblL.setForeground(C_MUTED);

        chip.add(emojiL); chip.add(valL); chip.add(lblL);
        return chip;
    }

    // ══════════════════════════════════════════════════════════════
    //  CENTRE  (sidebar + onglets)
    // ══════════════════════════════════════════════════════════════
    JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(buildSidebar(), BorderLayout.WEST);
        center.add(buildTabs(),    BorderLayout.CENTER);
        return center;
    }

    // ══════════════════════════════════════════════════════════════
    //  SIDEBAR  (style AgenceGUI)
    // ══════════════════════════════════════════════════════════════
    JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_CARD2);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(C_BORDER);
                g2.fillRect(getWidth()-1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 14, 20, 14));

        // ── Section TABLEAU DE BORD ──
        sidebar.add(sideSection("TABLEAU DE BORD"));
        sidebar.add(Box.createVerticalStrut(12));

        lblTotalVoitures  = makeStat("🚗", "Total voitures", "0", C_BLUE);
        lblVoituresLibres = makeStat("✅", "Disponibles",    "0", C_GREEN);
        lblVoituresLouees = makeStat("🔑", "Louées",         "0", C_GOLD);
        lblTotalClients   = makeStat("👤", "Clients actifs", "0", C_TEAL);

        sidebar.add(lblTotalVoitures);  sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(lblVoituresLibres); sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(lblVoituresLouees); sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(lblTotalClients);   sidebar.add(Box.createVerticalStrut(20));

        // ── Section ACTIONS RAPIDES (boutons icônes comme AgenceGUI) ──
        sidebar.add(sideSection("ACTIONS RAPIDES"));
        sidebar.add(Box.createVerticalStrut(10));
        
        ImageIcon key = new ImageIcon("./src/img/key-set.png");
        ImageIcon retur = new ImageIcon("./src/img/dos.png");
        ImageIcon plus = new ImageIcon("./src/img/signe-plus-dans-un-cercle-noir.png");
        
        ImageIcon supr = new ImageIcon("./src/img/corbeille.png");
        
        JButton btnLouer   = sideButton("    Louer une voiture",   C_GREEN,key);
        JButton btnRendre  = sideButton("     Rendre une voiture",  C_GOLD,retur);
        JButton btnAjouter = sideButton("    Ajouter voiture",     C_ACCENT2,plus);
        JButton btnsupprimer = sideButton("    Supprimer voiture", C_RED,supr);

        sidebar.add(btnLouer);   sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnRendre);  sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnAjouter); sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnsupprimer); sidebar.add(Box.createVerticalGlue());

        // Légende
        JLabel legend = new JLabel("<html><font color='#8C96AF' size='2'>Données mises à jour<br>en temps réel</font></html>");
        legend.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(legend);

        updateDashboard();

        // Listeners boutons rapides
        btnLouer.addActionListener(e -> {
            // Basculer sur l'onglet Locations
            Container tp = sidebar.getParent();
            if (tp instanceof JPanel) {
                Component c = ((BorderLayout) ((JPanel) tp).getLayout()).getLayoutComponent(BorderLayout.CENTER);
                if (c instanceof JTabbedPane) ((JTabbedPane) c).setSelectedIndex(1);
            }
        });
        btnRendre.addActionListener(e -> {
            Container tp = sidebar.getParent();
            if (tp instanceof JPanel) {
                Component c = ((BorderLayout) ((JPanel) tp).getLayout()).getLayoutComponent(BorderLayout.CENTER);
                if (c instanceof JTabbedPane) ((JTabbedPane) c).setSelectedIndex(1);
            }
        });
        btnAjouter.addActionListener(e -> {
            Container tp = sidebar.getParent();
            if (tp instanceof JPanel) {
                Component c = ((BorderLayout) ((JPanel) tp).getLayout()).getLayoutComponent(BorderLayout.CENTER);
                if (c instanceof JTabbedPane) ((JTabbedPane) c).setSelectedIndex(3);
            }
        });
        
        btnsupprimer.addActionListener(e -> showSupprimerDialog());

        return sidebar;
    }

    JLabel sideSection(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(C_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    /** Bouton sidebar style AgenceGUI */
    JButton sideButton(String text, Color bg,Icon icone) {
        JButton b = new JButton(text,icone) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isRollover() ? bg.brighter() :
                             getModel().isPressed()  ? bg.darker()   : bg;
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Stat card sidebar ──
    JLabel makeStat(String emoji, String label, String val, Color accent) {
        JPanel card = new JPanel(new BorderLayout(8, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(190, 56));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(new EmptyBorder(8, 12, 8, 8));

        JLabel lbl = new JLabel(emoji + "  " + label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(C_MUTED);

        JLabel num = new JLabel(val);
        num.setFont(new Font("Georgia", Font.BOLD, 22));  // Georgia comme AgenceGUI
        num.setForeground(accent);

        card.add(lbl, BorderLayout.NORTH);
        card.add(num, BorderLayout.CENTER);

        JLabel wrapper = new JLabel() {
            { setLayout(new BorderLayout()); add(card); setOpaque(false);
              setMaximumSize(new Dimension(190, 60)); setAlignmentX(Component.LEFT_ALIGNMENT); }
        };
        wrapper.putClientProperty("numLabel", num);
        return wrapper;
    }

    void updateDashboard() {
        int total  = agence.getVoitures().size();
        int louees = agence.getLocations().size();
        int libres = total - louees;
        setStatVal(lblTotalVoitures,  total);
        setStatVal(lblVoituresLibres, libres);
        setStatVal(lblVoituresLouees, louees);
        setStatVal(lblTotalClients,   louees);
    }

    void setStatVal(JLabel wrapper, int val) {
        Object lbl = wrapper.getClientProperty("numLabel");
        if (lbl instanceof JLabel) ((JLabel) lbl).setText(String.valueOf(val));
    }

    // ══════════════════════════════════════════════════════════════
    //  ONGLETS  (structure identique au fichier 1)
    // ══════════════════════════════════════════════════════════════
    JTabbedPane buildTabs() {
        JTabbedPane tp = new JTabbedPane(JTabbedPane.TOP);
        tp.setBackground(C_BG);
        tp.setForeground(C_TEXT);
        tp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("TabbedPane.selected",            C_CARD2);
        UIManager.put("TabbedPane.background",          C_BG);
        UIManager.put("TabbedPane.foreground",          C_TEXT);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        tp.addTab("    Voitures  ",  buildVoituresTab());
        tp.addTab("    Locations  ", buildLocationsTab());
        tp.addTab("    Recherche  ", buildRechercheTab());
        tp.addTab("    Ajouter    ", buildAjouterTab());
        return tp;
    }
    
    // ──────────────────────────────────────────────────────────────
    //  DIALOGS
    // ──────────────────────────────────────────────────────────────
    private JDialog styledDialog(String title, int w, int h) {
        JDialog dlg = new JDialog(this, title, true);
        dlg.setSize(w, h);
        dlg.setLocationRelativeTo(this);
        dlg.setBackground(C_BG);
        dlg.getContentPane().setBackground(C_BG);
        dlg.setUndecorated(false);
        return dlg;
    }
    
    private void showSupprimerDialog() {
        JDialog dlg = styledDialog(" Supprimer une voiture", 580, 310); 
        dlg.setIconImage(new ImageIcon("./src/img/voiture.png").getImage());

     // Table cachée pour conserver la logique de sélection
        String[] cols = {"Marque", "Modèle", "Année", "Prix/jour (DH)", "Statut"};
        voitureModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        voitureTable = buildStyledTable(voitureModel);

        refreshVoitureCards(gridcars);

        // Bouton supprimer (basé sur table cachée)
        JButton btnDel = makeButton("  Supprimer voiture sélectionnée", C_RED);
        btnDel.addActionListener(e -> {
            int row = voitureTable.getSelectedRow();
            if (row < 0) { showError("Cliquez d'abord sur une carte puis utilisez la table ci-dessous."); return; }
            Voiture v = agence.getVoitures().get(row);
            if (agence.estLoue(v)) { showError("Impossible : cette voiture est actuellement louée."); return; }
            int conf = JOptionPane.showConfirmDialog(this,
                "Supprimer " + v + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                int idx = agence.getVoitures().indexOf(v);
                if (idx >= 0 && idx < carImages.size()) carImages.remove(idx);
                agence.supprimerVoiture(v);
                refreshAll(gridcars);
                FillCb.run();
                updateHeaderStats();
                showSuccess("Voiture supprimée.");
            }
        });

        // Table compacte en bas (garde la logique de sélection)
        JScrollPane tableScroll = styledScroll(voitureTable);
        tableScroll.setPreferredSize(new Dimension(0, 160));


        JPanel bottom = darkPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnDel);
        
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        
        p.add(tableScroll,  BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        dlg.add(p);
        dlg.setVisible(true);
    }
    

    
    // ──────────────────────────────────────────────────────────────
    //  ONGLET 1 : VOITURES  (cards visuelles style AgenceGUI)
    // ──────────────────────────────────────────────────────────────
    JPanel buildVoituresTab() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        p.add(sectionTitle("  Parc automobile", "Toutes les voitures — cliquez sur une carte pour la sélectionner"), BorderLayout.NORTH);

        // Grille de cards voitures (style AgenceGUI)
        JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 16, 16)){
            @Override
            public Dimension getPreferredSize() {
                // Force le recalcul de la largeur visible avant de calculer la hauteur
                int width = getParent() != null ? getParent().getWidth() : super.getPreferredSize().width;
                Dimension d = super.getPreferredSize();
                return new Dimension(width, d.height);
            }
        };
        
        gridcars = grid;
        grid.setOpaque(false);

        // Table cachée pour conserver la logique de sélection
        String[] cols = {"Marque", "Modèle", "Année", "Prix/jour (DH)", "Statut"};
        voitureModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        voitureTable = buildStyledTable(voitureModel);

        JScrollPane cardScroll = new JScrollPane(grid);
        cardScroll.setOpaque(false);
        cardScroll.getViewport().setOpaque(false);
        cardScroll.setBorder(null);
        cardScroll.getVerticalScrollBar().setUnitIncrement(16);
        cardScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        refreshVoitureCards(grid);

        // Bouton supprimer (basé sur table cachée)
        JButton btnDel = makeButton(" Supprimer voiture sélectionnée", C_RED);
        btnDel.addActionListener(e -> {
            int row = voitureTable.getSelectedRow();
            if (row < 0) { showError("Cliquez d'abord sur une carte puis utilisez la table ci-dessous."); return; }
            Voiture v = agence.getVoitures().get(row);
            if (agence.estLoue(v)) { showError("Impossible : cette voiture est actuellement louée."); return; }
            int conf = JOptionPane.showConfirmDialog(this,
                "Supprimer " + v + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                int idx = agence.getVoitures().indexOf(v);
                if (idx >= 0 && idx < carImages.size()) carImages.remove(idx);
                agence.supprimerVoiture(v);
                refreshAll(grid);
                showSuccess("Voiture supprimée.");
            }
        });

        // Table compacte en bas (garde la logique de sélection)
        JScrollPane tableScroll = styledScroll(voitureTable);
        tableScroll.setPreferredSize(new Dimension(0, 160));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cardScroll, tableScroll);
        split.setDividerLocation(380);
        split.setOpaque(false);
        split.setBorder(null);
        split.setBackground(C_BG);

        JPanel bottom = darkPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnDel);

        p.add(cardScroll,  BorderLayout.CENTER);
        //p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    /** Construit les cards visuelles style AgenceGUI */
    void refreshVoitureCards(JPanel grid) {
        grid.removeAll();
        List<Voiture> voitures = agence.getVoitures();
        for (int i = 0; i < voitures.size(); i++) {
            grid.add(buildCarCard(voitures.get(i), i));
        }
        grid.revalidate();
        grid.repaint();
        refreshVoitureTable();
    }

    /** Carte voiture individuelle (style AgenceGUI) */
    JPanel buildCarCard(Voiture v, int imgIndex) {
        boolean loue = agence.estLoue(v);
        Color accentColor = loue ? C_ACCENT : C_GREEN;

        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                // Barre latérale accent
                GradientPaint gp = new GradientPaint(0, 0, accentColor, 0, getHeight(), accentColor.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(230, 280));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        // ── Image voiture ──
        JLabel imgLabel = new JLabel("🚗", SwingConstants.CENTER);
        imgLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        imgLabel.setPreferredSize(new Dimension(190, 120));
        if (imgIndex < carImages.size()) {
            try {
                File f = new File(carImages.get(imgIndex));
                if (f.exists()) {
                    BufferedImage img = ImageIO.read(f);
                    Image scaled = img.getScaledInstance(190, 110, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(scaled));
                    imgLabel.setText("");
                }
            } catch (Exception ignored) {}
        }

        JPanel imgContainer = new JPanel(new BorderLayout());
        imgContainer.setOpaque(false);
        imgContainer.add(imgLabel, BorderLayout.CENTER);

        // Badge statut
        JLabel badge = new JLabel(loue ? "LOUÉE" : "DISPONIBLE", SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 9));
        badge.setForeground(accentColor);
        badge.setOpaque(true);
        badge.setBackground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        imgContainer.add(badge, BorderLayout.SOUTH);
        card.add(imgContainer, BorderLayout.CENTER);

        // ── Infos ──
        JPanel info = new JPanel(new GridLayout(4, 1, 0, 2));
        info.setOpaque(false);

        JLabel nameLabel = new JLabel(v.getMarque() + " " + v.getModele());
        nameLabel.setFont(new Font("Georgia", Font.BOLD, 13));
        nameLabel.setForeground(C_TEXT);

        JLabel anneeLabel = new JLabel("Année : " + v.getAnnee());
        anneeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        anneeLabel.setForeground(C_MUTED);

        JLabel prixLabel = new JLabel(v.getPrixJournee() + " DH / jour");
        prixLabel.setFont(new Font("Georgia", Font.BOLD, 15));
        prixLabel.setForeground(C_GOLD);

        JButton btn = new JButton(loue ? "Indisponible" : "Louer") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = loue ? new Color(60, 20, 20) :
                           (getModel().isRollover() ? C_GREEN.brighter() : new Color(20, 60, 35));
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(loue ? C_ACCENT : C_GREEN);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);  btn.setEnabled(!loue);
        btn.setCursor(loue ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        
        info.add(nameLabel); info.add(anneeLabel); info.add(prixLabel); info.add(btn);
        card.add(info, BorderLayout.SOUTH);

        // Hover
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(new CompoundBorder(new LineBorder(accentColor, 1, true), new EmptyBorder(12, 14, 12, 14)));
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(new EmptyBorder(12, 14, 12, 14));
            }
        });

        // Clic → sélection dans la table cachée
        int finalI = imgIndex;
        /*card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (finalI < voitureTable.getRowCount())
                    voitureTable.setRowSelectionInterval(finalI, finalI);
            }
        });*/

        return card;
    }

    void refreshVoitureTable() {
        voitureModel.setRowCount(0);
        for (Voiture v : agence.getVoitures()) {
            voitureModel.addRow(new Object[]{
                v.getMarque(), v.getModele(), v.getAnnee(),
                v.getPrixJournee() + " DH",
                agence.estLoue(v) ? "🔑 Louée" : "✅ Libre"
            });
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  ONGLET 2 : LOCATIONS  (logique identique, style AgenceGUI)
    // ──────────────────────────────────────────────────────────────
    JPanel buildLocationsTab() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        p.add(sectionTitle(" Gestion des locations", "Location & restitution de véhicules"), BorderLayout.NORTH);

        // ── Formulaire nouvelle location ──
        JPanel formPanel = darkCard();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_BORDER), "  Nouvelle location  ",
            TitledBorder.LEFT, TitledBorder.TOP, F_SECTION, C_ACCENT));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField fCiv  = styledField("M. / Mme / Mlle");
        JTextField fNom  = styledField("Nom");
        JTextField fPren = styledField("Prénom");
        JTextField fCin  = styledField("N° CIN");

        JComboBox<String> cbVoiture = new JComboBox<>();
        styleCombo(cbVoiture);

        gbc.gridx=0; gbc.gridy=0; formPanel.add(fieldLabel("Civilité"), gbc);
        gbc.gridx=1; formPanel.add(fCiv, gbc);
        gbc.gridx=2; formPanel.add(fieldLabel("Nom"), gbc);
        gbc.gridx=3; formPanel.add(fNom, gbc);

        gbc.gridx=0; gbc.gridy=1; formPanel.add(fieldLabel("Prénom"), gbc);
        gbc.gridx=1; formPanel.add(fPren, gbc);
        gbc.gridx=2; formPanel.add(fieldLabel("CIN"), gbc);
        gbc.gridx=3; formPanel.add(fCin, gbc);

        gbc.gridx=0; gbc.gridy=2; formPanel.add(fieldLabel("Voiture"), gbc);
        gbc.gridx=1; gbc.gridwidth=3; formPanel.add(cbVoiture, gbc);
        gbc.gridwidth=1;

        JButton btnLouer = makeButton("  Louer cette voiture", C_GREEN);
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=4;
        gbc.fill=GridBagConstraints.NONE; gbc.anchor=GridBagConstraints.CENTER;
        formPanel.add(btnLouer, gbc);

        Runnable fillCombo = () -> {
            cbVoiture.removeAllItems();
            for (Voiture v : agence.getVoitures())
                if (!agence.estLoue(v)) cbVoiture.addItem(v.toString());
        };
        
        FillCb = fillCombo;
        fillCombo.run();

        btnLouer.addActionListener(e -> {
            String civ  = fCiv.getText().trim();
            String nom  = fNom.getText().trim();
            String pren = fPren.getText().trim();
            String cin  = fCin.getText().trim();
            if (nom.isEmpty() || cin.isEmpty()) { showError("Nom et CIN obligatoires."); return; }
            if (cbVoiture.getItemCount() == 0)  { showError("Aucune voiture disponible."); return; }
            int idx = cbVoiture.getSelectedIndex();
            int freeCount = 0; Voiture choix = null;
            for (Voiture v : agence.getVoitures()) {
                if (!agence.estLoue(v)) {
                    if (freeCount == idx) { choix = v; break; }
                    freeCount++;
                }
            }
            if (choix == null) { showError("Voiture introuvable."); return; }
            Client client = new Client(civ.isEmpty() ? "M." : civ, nom, pren, cin);
            try {
                agence.loueVoiture(client, choix);
                fCiv.setText(""); fNom.setText(""); fPren.setText(""); fCin.setText("");
                fillCombo.run();
                refreshAll(null);
                refreshVoitureCards(gridcars);
                updateHeaderStats();
                showSuccess("Location enregistrée : " + client.getNom() + " → " + choix.getModele());
            } catch (VoitureDejaBoueeException ex)   { showError("Voiture déjà louée : " + ex.getMessage()); }
              catch (VoitureInexistanteException ex) { showError("Voiture inexistante : " + ex.getMessage()); }
        });

        // ── Table locations ──
        String[] cols = {"Client", "CIN", "Voiture", "Marque", "Prix/jour"};
        locationModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        locationTable = buildStyledTable(locationModel);
        refreshLocationTable();

        JButton btnRendre = makeButton(" Rendre la voiture", C_GOLD);
        btnRendre.addActionListener(e -> {
            int row = locationTable.getSelectedRow();
            if (row < 0) { showError("Sélectionnez une ligne."); return; }
            List<Client> clients = agence.getClients();
            if (row >= clients.size()) return;
            Client c = clients.get(row);
            agence.rendVoiture(c);
            fillCombo.run();
            refreshAll(null);
            updateHeaderStats();
            refreshVoitureCards(gridcars);
            showSuccess("Voiture rendue par " + c.getNom() + ".");
        });

        JPanel tablePanel = darkPanel(new BorderLayout(0, 8));
        tablePanel.add(sectionTitle("📋  Locations en cours", "Triées alphabétiquement (TreeMap)"), BorderLayout.NORTH);
        tablePanel.add(styledScroll(locationTable), BorderLayout.CENTER);
        JPanel btnP = darkPanel(new FlowLayout(FlowLayout.RIGHT));
        btnP.add(btnRendre);
        tablePanel.add(btnP, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        split.setDividerLocation(230); split.setOpaque(false);
        split.setBorder(null); split.setBackground(C_BG);

        p.add(split, BorderLayout.CENTER);
        return p;
    }

    void refreshLocationTable() {
        locationModel.setRowCount(0);
        for (Map.Entry<Client, Voiture> e : agence.getLocations().entrySet()) {
            Client c = e.getKey(); Voiture v = e.getValue();
            locationModel.addRow(new Object[]{
                c.getCivilite() + " " + c.getNom() + " " + c.getPrenom(),
                c.getCin(), v.getModele(), v.getMarque(), v.getPrixJournee() + " DH"
            });
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  ONGLET 3 : RECHERCHE  (logique identique, style AgenceGUI)
    // ──────────────────────────────────────────────────────────────
    JPanel buildRechercheTab() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        p.add(sectionTitle(" Recherche multi-critères", "CritereMarque + CritereAnnee + CriterePrix → InterCritere"), BorderLayout.NORTH);

        JPanel form = darkCard();
        form.setLayout(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_BORDER), "  Filtres de recherche  ",
            TitledBorder.LEFT, TitledBorder.TOP, F_SECTION, C_TEAL));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        JCheckBox chkMarque = styledCheck("Marque");
        JCheckBox chkAnnee  = styledCheck("Année");
        JCheckBox chkPrix   = styledCheck("Prix max (DH)");

        JTextField fMarque = styledField("ex: Renault");
        JTextField fAnnee  = styledField("ex: 2009");
        JTextField fPrix   = styledField("ex: 100");
        fMarque.setEnabled(false); fAnnee.setEnabled(false); fPrix.setEnabled(false);

        chkMarque.addActionListener(e -> fMarque.setEnabled(chkMarque.isSelected()));
        chkAnnee.addActionListener(e  -> fAnnee.setEnabled(chkAnnee.isSelected()));
        chkPrix.addActionListener(e   -> fPrix.setEnabled(chkPrix.isSelected()));

        g.gridx=0; g.gridy=0; g.weightx=0.2; form.add(chkMarque, g);
        g.gridx=1; g.weightx=0.8; form.add(fMarque, g);
        g.gridx=2; g.weightx=0.2; form.add(chkAnnee, g);
        g.gridx=3; g.weightx=0.8; form.add(fAnnee, g);

        g.gridx=0; g.gridy=1; g.weightx=0.2; form.add(chkPrix, g);
        g.gridx=1; g.weightx=0.8; form.add(fPrix, g);

        JButton btnSearch = makeButton("  Rechercher",    C_TEAL);
        JButton btnReset  = makeButton("  Réinitialiser", C_MUTED);
        g.gridx=2; g.gridy=1; g.gridwidth=1; g.fill=GridBagConstraints.NONE; form.add(btnSearch, g);
        g.gridx=3; form.add(btnReset, g);

        String[] cols = {"Marque", "Modèle", "Année", "Prix/jour", "Statut"};
        rechercheModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        rechercheTable = buildStyledTable(rechercheModel);

        JLabel lblCount = new JLabel("  Aucune recherche effectuée");
        lblCount.setFont(F_BODY); lblCount.setForeground(C_MUTED);

        btnSearch.addActionListener(e -> {
            InterCritere ic = new InterCritere();
            if (chkMarque.isSelected() && !fMarque.getText().isBlank())
                ic.addCritere(new CritereMarque(fMarque.getText().trim()));
            if (chkAnnee.isSelected() && !fAnnee.getText().isBlank()) {
                try { ic.addCritere(new CritereAnnee(Integer.parseInt(fAnnee.getText().trim()))); }
                catch (NumberFormatException ex) { showError("Année invalide."); return; }
            }
            if (chkPrix.isSelected() && !fPrix.getText().isBlank()) {
                try { ic.addCritere(new CriterePrix(Integer.parseInt(fPrix.getText().trim()))); }
                catch (NumberFormatException ex) { showError("Prix invalide."); return; }
            }
            List<Voiture> res = agence.selectionne(ic);
            rechercheModel.setRowCount(0);
            for (Voiture v : res) {
                rechercheModel.addRow(new Object[]{
                    v.getMarque(), v.getModele(), v.getAnnee(),
                    v.getPrixJournee() + " DH",
                    agence.estLoue(v) ? "🔑 Louée" : "✅ Libre"
                });
            }
            lblCount.setText("  " + res.size() + " résultat(s) trouvé(s)");
            lblCount.setForeground(res.isEmpty() ? C_RED : C_TEAL);
        });

        btnReset.addActionListener(e -> {
            chkMarque.setSelected(false); chkAnnee.setSelected(false); chkPrix.setSelected(false);
            fMarque.setText(""); fAnnee.setText(""); fPrix.setText("");
            fMarque.setEnabled(false); fAnnee.setEnabled(false); fPrix.setEnabled(false);
            rechercheModel.setRowCount(0);
            lblCount.setText("  Aucune recherche effectuée");
            lblCount.setForeground(C_MUTED);
        });

        JPanel resultPanel = darkPanel(new BorderLayout(0, 8));
        resultPanel.add(lblCount, BorderLayout.NORTH);
        resultPanel.add(styledScroll(rechercheTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, resultPanel);
        split.setDividerLocation(180);
        split.setOpaque(false); split.setBorder(null); split.setBackground(C_BG);

        p.add(split, BorderLayout.CENTER);
        return p;
    }

    // ──────────────────────────────────────────────────────────────
    //  ONGLET 4 : AJOUTER  (logique identique, style AgenceGUI)
    // ──────────────────────────────────────────────────────────────
    JPanel buildAjouterTab() {
        JPanel p = darkPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        p.add(sectionTitle("  Ajouter une voiture", "Enregistrer un nouveau véhicule dans le parc"), BorderLayout.NORTH);

        JPanel form = darkCard();
        form.setLayout(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_BORDER), "  Nouveau véhicule  ",
            TitledBorder.LEFT, TitledBorder.TOP, F_SECTION, C_GOLD));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 12, 10, 12);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fMarque = styledField("ex: Renault");
        JTextField fModele = styledField("ex: Clio");
        JTextField fAnnee  = styledField("ex: 2023");
        JTextField fPrix   = styledField("Prix par jour en DH");

        JLabel preview = new JLabel("  ← Remplissez les champs");
        preview.setFont(new Font("Consolas", Font.ITALIC, 13));
        preview.setForeground(C_MUTED);

        ActionListener upd = ev -> {
            preview.setText("  Aperçu : " + fMarque.getText().trim() + " " + fModele.getText().trim()
                + " (" + fAnnee.getText().trim() + ") — " + fPrix.getText().trim() + " DH/j");
            preview.setForeground(C_GOLD);
        };
        for (JTextField f : new JTextField[]{fMarque, fModele, fAnnee, fPrix})
            f.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e)  { upd.actionPerformed(null); }
                public void removeUpdate(javax.swing.event.DocumentEvent e)  { upd.actionPerformed(null); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) {}
            });

        g.gridx=0; g.gridy=0; g.weightx=0.3; form.add(fieldLabel("Marque *"),       g);
        g.gridx=1; g.weightx=0.7; form.add(fMarque, g);
        g.gridx=2; g.weightx=0.3; form.add(fieldLabel("Modèle *"),       g);
        g.gridx=3; g.weightx=0.7; form.add(fModele, g);

        g.gridx=0; g.gridy=1; g.weightx=0.3; form.add(fieldLabel("Année *"),        g);
        g.gridx=1; g.weightx=0.7; form.add(fAnnee, g);
        g.gridx=2; g.weightx=0.3; form.add(fieldLabel("Prix/jour (DH) *"), g);
        g.gridx=3; g.weightx=0.7; form.add(fPrix, g);

        g.gridx=0; g.gridy=2; g.gridwidth=4; g.fill=GridBagConstraints.HORIZONTAL;
        form.add(preview, g);

        JButton btnAjouter = makeButton("  Ajouter au parc", C_GOLD);
        JButton btnVider   = makeButton(" Vider",           C_MUTED);
        JPanel btns = darkPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btns.add(btnVider); btns.add(btnAjouter);
        g.gridy=3; form.add(btns, g);

        btnAjouter.addActionListener(e -> {
            String m = fMarque.getText().trim(), mo = fModele.getText().trim();
            String a = fAnnee.getText().trim(),  pr = fPrix.getText().trim();
            if (m.isEmpty() || mo.isEmpty() || a.isEmpty() || pr.isEmpty()) {
                showError("Tous les champs sont obligatoires."); return;
            }
            try {
                int annee = Integer.parseInt(a), prix = Integer.parseInt(pr);
                if (annee < 1980 || annee > 2030) { showError("Année invalide (1980–2030)."); return; }
                if (prix <= 0) { showError("Le prix doit être positif."); return; }
                agence.ajouterVoiture(new Voiture(m, mo, annee, prix));
                carImages.add(""); // pas d'image pour les nouvelles voitures
                fMarque.setText(""); fModele.setText(""); fAnnee.setText(""); fPrix.setText("");
                refreshAll(null);
                refreshVoitureCards(gridcars);
                FillCb.run();
                updateHeaderStats();
                showSuccess("✅ Voiture ajoutée : " + m + " " + mo);
            } catch (NumberFormatException ex) { showError("Année et prix doivent être des nombres."); }
        });
        btnVider.addActionListener(e -> {
            fMarque.setText(""); fModele.setText(""); fAnnee.setText(""); fPrix.setText("");
        });

        // Chips marques rapides
        JPanel chips = darkPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        chips.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_BORDER), "  Marques rapides  ",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.PLAIN, 11), C_MUTED));
        for (String marque : new String[]{"Renault","Peugeot","BMW","Mercedes","Toyota","Dacia","Volkswagen","Audi","Ford","Tesla"}) {
            JButton chip = new JButton(marque);
            chip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            chip.setBackground(C_CARD2); chip.setForeground(C_TEXT);
            chip.setBorder(new CompoundBorder(new LineBorder(C_BORDER, 1, true), new EmptyBorder(4, 10, 4, 10)));
            chip.setFocusPainted(false);
            chip.setCursor(new Cursor(Cursor.HAND_CURSOR));
            chip.addActionListener(e -> fMarque.setText(marque));
            chip.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { chip.setBackground(C_ACCENT); }
                public void mouseExited(MouseEvent e)  { chip.setBackground(C_CARD2); }
            });
            chips.add(chip);
        }

        JPanel content = darkPanel(new BorderLayout(0, 14));
        content.add(form,  BorderLayout.NORTH);
        content.add(chips, BorderLayout.CENTER);
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════
    //  STATUS BAR  (style AgenceGUI)
    // ══════════════════════════════════════════════════════════════
    JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_CARD2);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, C_BORDER),
            new EmptyBorder(8, 20, 8, 20)));

        statusBar = new JLabel("  Prêt");
        statusBar.setFont(F_MONO);
        statusBar.setForeground(C_MUTED);

        JLabel right = new JLabel("AutoElite  •  Java + Swing  •  Collections (TreeMap, ArrayList)  ");
        right.setFont(F_MONO);
        right.setForeground(new Color(60, 70, 100));

        bar.add(statusBar, BorderLayout.WEST);
        bar.add(right,     BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════════════════════════
    //  REFRESH GLOBAL
    // ══════════════════════════════════════════════════════════════
    /**
     * @param cardsGrid le JPanel de l'onglet Voitures (null si non visible)
     */
    void refreshAll(JPanel cardsGrid) {
        if (cardsGrid != null) refreshVoitureCards(cardsGrid);
        else refreshVoitureTable();
        refreshLocationTable();
        updateDashboard();
        repaint(); // rafraîchit les chips du header
    }

    // ══════════════════════════════════════════════════════════════
    //  HELPERS UI
    // ══════════════════════════════════════════════════════════════
    JPanel darkPanel(LayoutManager lm) {
        JPanel p = new JPanel(lm); p.setOpaque(false); return p;
    }

    JPanel darkCard() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(C_BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
    }

    JPanel sectionTitle(String title, String sub) {
        JPanel p = darkPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(0, 0, 12, 0));
        JLabel l1 = new JLabel(title);
        l1.setFont(new Font("Georgia", Font.BOLD, 17));  // Georgia comme AgenceGUI
        l1.setForeground(C_TEXT);
        JLabel l2 = new JLabel(sub);
        l2.setFont(F_BODY); l2.setForeground(C_MUTED);
        p.add(l1, BorderLayout.NORTH);
        p.add(l2, BorderLayout.CENTER);
        return p;
    }

    JLabel fieldLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(C_MUTED);
        return l;
    }

    JTextField styledField(String placeholder) {
        JTextField tf = new JTextField(14) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_CARD2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        tf.setFont(F_BODY); tf.setForeground(C_TEXT);
        tf.setBackground(C_CARD2); tf.setCaretColor(C_ACCENT);
        tf.setBorder(new CompoundBorder(new LineBorder(C_BORDER, 1, true), new EmptyBorder(6, 10, 6, 10)));
        tf.setOpaque(false);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) { tf.setText(""); tf.setForeground(C_TEXT); }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) { tf.setText(placeholder); tf.setForeground(C_MUTED); }
            }
        });
        tf.setText(placeholder); tf.setForeground(C_MUTED);
        return tf;
    }

    JButton makeButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            boolean hover = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? accent.brighter() : accent.darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE); btn.setFocusPainted(false);
        btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 36));
        return btn;
    }

    JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? C_CARD : C_CARD2);
                c.setForeground(C_TEXT);
                if (isRowSelected(row)) c.setBackground(new Color(C_ACCENT.getRed(), C_ACCENT.getGreen(), C_ACCENT.getBlue(), 80));
                return c;
            }
        };
        table.setFont(F_BODY); table.setForeground(C_TEXT); table.setBackground(C_CARD);
        table.setSelectionBackground(new Color(C_ACCENT.getRed(), C_ACCENT.getGreen(), C_ACCENT.getBlue(), 80));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(C_BORDER); table.setRowHeight(32);
        table.setShowGrid(true); table.setIntercellSpacing(new Dimension(1, 1));
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(18, 22, 45));
        header.setForeground(C_MUTED);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(new MatteBorder(0, 0, 1, 0, C_BORDER));
        return table;
    }

    JScrollPane styledScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(C_CARD); sp.getViewport().setBackground(C_CARD);
        sp.setBorder(new LineBorder(C_BORDER, 1, true));
        return sp;
    }

    void styleCombo(JComboBox<String> cb) {
        cb.setFont(F_BODY); cb.setBackground(C_CARD2); cb.setForeground(C_TEXT);
        cb.setBorder(new CompoundBorder(new LineBorder(C_BORDER, 1, true), new EmptyBorder(4, 8, 4, 8)));
    }

    JCheckBox styledCheck(String label) {
        JCheckBox cb = new JCheckBox(label);
        cb.setFont(F_BODY); cb.setForeground(C_TEXT); cb.setOpaque(false);
        return cb;
    }

    void showSuccess(String msg) {
        statusBar.setText("  ✅  " + msg); statusBar.setForeground(C_GREEN);
        Timer t = new Timer(4000, e -> { statusBar.setText("  Prêt"); statusBar.setForeground(C_MUTED); });
        t.setRepeats(false); t.start();
    }

    void showError(String msg) {
        statusBar.setText("  ❌  " + msg); statusBar.setForeground(C_RED);
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
        Timer t = new Timer(5000, e -> { statusBar.setText("  Prêt"); statusBar.setForeground(C_MUTED); });
        t.setRepeats(false); t.start();
    }

    // ══════════════════════════════════════════════════════════════
    //  ICÔNE VOITURE DESSINÉE  (conservée du fichier 1)
    // ══════════════════════════════════════════════════════════════
    JLabel makeCarIcon() {
        int W = 72, H = 52;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Carrosserie
        g.setColor(C_ACCENT);
        g.fillRoundRect(4, 28, 64, 16, 8, 8);
        // Toit
        int[] xp = {16, 22, 50, 56}; int[] yp = {28, 14, 14, 28};
        g.fillPolygon(xp, yp, 4);
        // Vitre
        g.setColor(new Color(200, 220, 255, 180));
        int[] xv = {19, 24, 48, 53}; int[] yv = {27, 17, 17, 27};
        g.fillPolygon(xv, yv, 4);
        // Roues
        g.setColor(new Color(20, 20, 30));
        g.fillOval(10, 36, 16, 16); g.fillOval(46, 36, 16, 16);
        g.setColor(C_MUTED);
        g.fillOval(14, 40, 8,  8);  g.fillOval(50, 40, 8,  8);
        // Feux
        g.setColor(C_GOLD);  g.fillRoundRect(60, 30, 6, 5, 3, 3);
        g.setColor(C_GREEN); g.fillRoundRect(6,  30, 6, 5, 3, 3);
        g.dispose();
        return new JLabel(new ImageIcon(img));
    }

    // ══════════════════════════════════════════════════════════════
    //  HELPER IMAGE
    // ══════════════════════════════════════════════════════════════
    Image loadImg(String path, int w, int h) {
        try {
            File f = new File(path);
            if (f.exists()) {
                BufferedImage img = ImageIO.read(f);
                return img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            }
        } catch (Exception ignored) {}
        return null;
    }

    // ══════════════════════════════════════════════════════════════
    //  WRAPLAYOUT  (copié de AgenceGUI pour la grille de cards)
    // ══════════════════════════════════════════════════════════════
    static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

        @Override public Dimension preferredLayoutSize(Container target) { return layoutSize(target, true); }
        @Override public Dimension minimumLayoutSize(Container target)   { return layoutSize(target, false); }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);
                int width = 0, height = insets.top + insets.bottom + vgap * 2;
                int rowWidth = 0, rowHeight = 0;
                for (int i = 0; i < target.getComponentCount(); i++) {
                    Component m = target.getComponent(i);
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if (rowWidth + d.width > maxWidth) {
                            height += rowHeight + vgap;
                            rowWidth = 0; rowHeight = 0;
                        }
                        if (rowWidth != 0) rowWidth += hgap;
                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                height += rowHeight;
                width = Math.max(width, rowWidth);
                return new Dimension(width + insets.left + insets.right + hgap * 2, height + vgap);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
       
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        // Personnaliser Nimbus
                        UIManager.put("control",          C_BG);
                        UIManager.put("nimbusBase",       C_CARD);
                        UIManager.put("nimbusBlueGrey",   C_CARD2);
                        UIManager.put("text",             C_TEXT);
                        break;
                    }
                }
            } catch (Exception ignored) {}
            SwingUtilities.invokeLater(AgenceSwingApp::new);
        }
}