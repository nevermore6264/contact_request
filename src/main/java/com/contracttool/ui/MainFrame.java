/*
 * Decompiled with CFR 0.152.
 */
package com.contracttool.ui;

import com.contracttool.AppPaths;
import com.contracttool.model.ContractInfo;
import com.contracttool.model.CustomerRecord;
import com.contracttool.model.ExportDoc;
import com.contracttool.model.SavedData;
import com.contracttool.service.BrandColorStore;
import com.contracttool.service.CustomerStore;
import com.contracttool.service.DataStore;
import com.contracttool.service.WordExportService;
import com.contracttool.ui.UiTheme;
import com.contracttool.util.ContractNumberUtil;
import com.contracttool.util.NumberUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.nio.file.Path;
import java.time.Year;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableColumnModelListener;
import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class MainFrame
extends JFrame {
    private static final int ROW_COUNT = 30;
    private static final int DEFAULT_PRODUCT_COUNT = 18;
    private static final int MIN_ROW_HEIGHT = 36;
    /** Mẫu số thành tiền phải nhìn đủ (kèm padding), không cần kéo ngang. */
    private static final String AMOUNT_WIDTH_SAMPLE = "45.976.541.123";
    /** Cột phải — gói Báo phát (checkbox riêng, không đồng bộ cột trái). */
    private static final EnumSet<ExportDoc> BAO_PHAT_DOCS = EnumSet.of(ExportDoc.BAO_PHAT, ExportDoc.BAO_GIA_1, ExportDoc.BAO_GIA_2, ExportDoc.BAO_GIA_CUNG, ExportDoc.DE_NGHI_TT, ExportDoc.NT_BG);
    /** Phần chỉ thuộc gói Hợp đồng trên cột trái. */
    private static final EnumSet<ExportDoc> HOP_DONG_EXTRA_DOCS = EnumSet.of(ExportDoc.HOP_DONG, ExportDoc.THANH_LY, ExportDoc.BBGN, ExportDoc.MAU_08A, ExportDoc.DOI_CHIEU);
    /** Đủ 11 loại trên cột trái. */
    private static final EnumSet<ExportDoc> ALL_DOCS;
    static {
        EnumSet<ExportDoc> all = EnumSet.copyOf(BAO_PHAT_DOCS);
        all.addAll(HOP_DONG_EXTRA_DOCS);
        ALL_DOCS = all;
    }
    private static final int[] DEFAULT_SOUTH = new int[]{73000, 75000, 86000, 39500, 39500, 39500, 73000, 75000, 86000, 89000, 39500, 39500, 39500, 46500, 39500, 39500, 110000, 39500};
    private static final int[] DEFAULT_NORTH = new int[]{73000, 75000, 86000, 39500, 39500, 39500, 73000, 75000, 86000, 89000, 39500, 39500, 39500, 46500, 39500, 39500, 110000, 39500};
    private static final String[] DEFAULT_CONTENTS = new String[]{"Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh ma t\u00fay t\u1ed5ng h\u1ee3p 4 trong 1\n- Morphine/ Heroin (MOP/OPI)\n- C\u1ea7n sa (THC)\n- Ma t\u00fay \u0111\u00e1 (MET)\n- Thu\u1ed1c l\u1eafc (ECSTASY/MDMA)\nH\u1ed9p 25 Test\nModel: 74716", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh ma t\u00fay t\u1ed5ng h\u1ee3p 5 trong 1 \n- Morphine/ Heroin (MOP/OPI)\n- C\u1ea7n sa (THC)\n- Ma t\u00fay \u0111\u00e1 (MET)\n- Thu\u1ed1c l\u1eafc (ECSTASY/MDMA)\n- Ketamine (KET)\nH\u1ed9p 25 Test\nModel: 75025", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh ma t\u00fay t\u1ed5ng h\u1ee3p 6 trong 1 \n- Morphine/ Heroin (MOP/OPI)\n- C\u1ea7n sa (THC)\n- Ma t\u00fay \u0111\u00e1 (MET)\n- Thu\u1ed1c l\u1eafc (MDMA)\n- Ketamine (KET)\n- Cocaine (COC)\nH\u1ed9p 25 Test\nModel: 76005", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh C\u1ecf M\u1ef9 (K2)\nH\u1ed9p 50 Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh Morphine (MOP)\nH\u1ed9p 50 Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh Methamphetamine Ma t\u00fay \u0111\u00e1 (MET)\nH\u1ed9p 50 Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111inh t\u00ednh ma t\u00fay t\u1ed5ng h\u1ee3p 4.1\n- Morphine/ Heroin (MOP)\n- C\u1ea7n sa (THC)\n- Ma t\u00fay \u0111\u00e1 (MET)\n- Thu\u1ed1c l\u1eafc (MDMA)\nH\u1ed9p 25 Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111inh t\u00ednh ma t\u00fay t\u1ed5ng h\u1ee3p 5.1\n- Morphine/ Heroin (MOP)\n- C\u1ea7n sa (THC)\n- Ma t\u00fay \u0111\u00e1 (MET)\n- Thu\u1ed1c l\u1eafc (MDMA)\n- Ketamine (KET)\nH\u1ed9p 25 Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111inh t\u00ednh ma t\u00fay t\u1ed5ng h\u1ee3p 6.1\n- Morphine/Heroin (OPI)\n- C\u1ea7n sa (THC)\n- Ma t\u00fay \u0111\u00e1 (MET)\n- Thu\u1ed1c l\u1eafc (MDMA)\n- Amphetamine (AMP)(AMP)\n- Ketamine (KET)\nH\u1ed9p 25 Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111inh t\u00ednh ma t\u00fay t\u1ed5ng h\u1ee3p 7.1\n- Codein (COD)\n- Heroin (HER)\n- Ketamine (KET)\n- Amphetamine (AMP) \n- C\u1ecf M\u1ef9 (K2)\n- C\u1ea7n sa (THC)\n- Morphine (MOP)\nH\u1ed9p 25 Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh C\u1ea7n sa t\u1ed5ng h\u1ee3p K2\nH\u1ed9p 50Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh Morphine\nH\u1ed9p 50Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh Methamphetamine\nH\u1ed9p 50Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh Ketamine (KET)\nH\u1ed9p 40 Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh C\u1ea7n sa (THC/Marijuana)\nH\u1ed9p 50 Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh Thu\u1ed1c l\u1eafc (ECSTASY/MDMA)\nH\u1ed9p 50 Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh ma t\u00fay t\u1ed5ng h\u1ee3p 5.1 (m\u1eabu n\u01b0\u1edbc b\u1ecdt)\nH\u1ed9p 25 Test", "Que th\u1eed x\u00e9t nghi\u1ec7m \u0111\u1ecbnh t\u00ednh Amphetamine (AMP)\nH\u1ed9p 50 Test"};
    private static final String[] DEFAULT_CODES = new String[]{"74716\nCHEMTRUE", "75025\nCHEMTRUE", "76005\nCHEMTRUE", "K2\nCHEMTRUE", "MOP\nCHEMTRUE", "MET\nCHEMTRUE", "4.1\nASSURE", "5.1\nASSURE", "6.1\nASSURE", "7.1\nASSURE", "K2\nASSURE", "MOP\nASSURE", "MET\nASSURE", "KET\nASSURE", "THC\nASSURE", "MDMA\nASSURE", "5.1\nASSURE", "AMP\nASSURE"};
    private final ProductTableModel tableModel = new ProductTableModel();
    private final JTable table = new JTable(this.tableModel);
    private final TableRowSorter<ProductTableModel> productSorter = new TableRowSorter<ProductTableModel>(this.tableModel);
    private JScrollPane tableScroll;
    private final JTextArea detailArea = new JTextArea();
    private final JLabel grandTotalTop = new JLabel("0");
    private final JLabel folderLabel = new JLabel("Th\u01b0 m\u1ee5c g\u1ed1c: (ch\u01b0a ch\u1ecdn)  |  Th\u01b0 m\u1ee5c xu\u1ea5t: (ch\u01b0a ch\u1ecdn)");
    private final DataStore dataStore = new DataStore(AppPaths.savedProducts());
    private final CustomerStore customerStore = new CustomerStore(AppPaths.customerHistory());
    private final BrandColorStore brandColors = new BrandColorStore();
    private final WordExportService wordService = new WordExportService();
    private Path inputFolder;
    private Path outputFolder;
    private boolean southPrices = true;
    private boolean syncingContractNo;
    private boolean syncingDetail;
    private boolean syncingTemplates;
    private boolean hidingZeroQtyProducts;
    private JButton btnToggleZeroProducts;
    private int wheelAnchorRow;
    private boolean wheelAnchorValid;
    private boolean wheelProgrammaticScroll;
    private long wheelLastStepMs;
    private long wheelIgnoreAdjustUntilMs;
    private double wheelPreciseBudget;
    private final Map<ExportDoc, JCheckBox> leftTemplateChecks = new EnumMap<ExportDoc, JCheckBox>(ExportDoc.class);
    private final Map<ExportDoc, JCheckBox> rightTemplateChecks = new EnumMap<ExportDoc, JCheckBox>(ExportDoc.class);
    private final MultiLineRenderer contentHeightProbe = new MultiLineRenderer();
    private final JToggleButton presetBaoPhat = new JToggleButton("B\u00c1O PH\u00c1T");
    private final JToggleButton presetHopDongBaoPhat = new JToggleButton("H\u1ee2P \u0110\u1ed2NG + B\u00c1O PH\u00c1T");
    private final JRadioButton radioSouth = new JRadioButton("NAM", true);
    private final JRadioButton radioNorth = new JRadioButton("B\u1eaeC");
    private final JTextField contractNo = new JTextField();
    private final JTextArea partyA = new JTextArea(3, 40);
    private final JTextArea address = new JTextArea(2, 40);
    private final JTextField phone = new JTextField();
    private final JTextArea account1 = new JTextArea(2, 28);
    private final JTextArea account2 = new JTextArea(2, 28);
    private final JTextField taxCode = new JTextField(10);
    private final JTextField budgetCode = new JTextField();
    private final JTextField rep1 = new JTextField();
    private final JTextField rep2 = new JTextField();
    private final JTextField title1 = new JTextField();
    private final JTextField title2 = new JTextField();
    private final JTextArea partyC = new JTextArea(3, 40);
    private final JTextArea addressC = new JTextArea(2, 40);
    private final JTextField phoneC = new JTextField();
    private final JTextArea accountC = new JTextArea(2, 28);
    private final JTextArea accountC2 = new JTextArea(2, 28);
    private final JTextField taxCodeC = new JTextField();
    private final JTextField budgetCodeC = new JTextField();
    private final JTextField repC = new JTextField();
    private final JTextField repC2 = new JTextField();
    private final JTextField titleC = new JTextField();
    private final JTextField titleC2 = new JTextField();
    private final JToggleButton tabTwoParty = new JToggleButton("2 B\u00caN");
    private final JToggleButton tabThreeParty = new JToggleButton("3 B\u00caN");
    private final JToggleButton tabSideA = new JToggleButton("B\u00caN A (MUA)");
    private final JToggleButton tabSideC = new JToggleButton("B\u00caN C (NH\u1eacN)");
    private JPanel infoForm;
    private JPanel partyABlock;
    private JPanel partyCBlock;
    private JPanel sideTabs;
    private final java.awt.CardLayout partyCards = new java.awt.CardLayout();
    private final JPanel partyHolder = new JPanel(this.partyCards);
    private int partyLabelWidth;
    private final JTextField year = new JTextField(String.valueOf(Year.now().getValue()));

    public MainFrame() {
        super(" ");
        this.setDefaultCloseOperation(3);
        this.setMinimumSize(new Dimension(1480, 900));
        this.setExtendedState(6);
        this.initDefaults();
        this.buildUi();
        this.bindEvents();
        this.loadSavedData();
        this.refreshTotals();
        this.resizeRows();
    }

    private void initDefaults() {
        int i = 0;
        while (i < ROW_COUNT) {
            if (i < DEFAULT_PRODUCT_COUNT) {
                this.tableModel.setValueAt(DEFAULT_CONTENTS[i], i, 1);
                this.tableModel.setValueAt(DEFAULT_CODES[i], i, 2);
                this.tableModel.setValueAt("TEST", i, 3);
                this.tableModel.setValueAt("0", i, 4);
                this.tableModel.setValueAt(NumberUtil.formatDot(DEFAULT_SOUTH[i]), i, 5);
            } else {
                this.tableModel.setValueAt("", i, 1);
                this.tableModel.setValueAt("", i, 2);
                this.tableModel.setValueAt("", i, 3);
                this.tableModel.setValueAt("0", i, 4);
                this.tableModel.setValueAt("0", i, 5);
            }
            ++i;
        }
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(6, 4));
        root.setBackground(UiTheme.BG);
        root.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
        this.setContentPane(root);
        root.add((Component)this.buildTopBar(), "North");
        root.add((Component)this.buildCenter(), "Center");
        root.add((Component)this.buildBottomBar(), "South");
    }

    private JPanel buildTopBar() {
        JPanel top = UiTheme.cardPanel();
        top.setLayout(new BorderLayout(8, 0));
        JPanel left = new JPanel(new FlowLayout(0, 8, 4));
        left.setOpaque(false);
        JLabel regionLabel = new JLabel("CH\u1eccN KHU V\u1ef0C");
        regionLabel.setFont(UiTheme.FONT_BOLD.deriveFont(14.0f));
        this.radioSouth.setFont(UiTheme.FONT_SIZE20);
        this.radioSouth.setForeground(UiTheme.NAM_RED);
        this.radioSouth.setOpaque(false);
        this.radioNorth.setFont(UiTheme.FONT_SIZE20);
        this.radioNorth.setForeground(UiTheme.BAC_GREEN);
        this.radioNorth.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        group.add(this.radioSouth);
        group.add(this.radioNorth);
        this.radioSouth.addActionListener(e -> this.switchRegion(true));
        this.radioNorth.addActionListener(e -> this.switchRegion(false));
        JButton btnIn = this.bigAction("CH\u1eccN TH\u01af M\u1ee4C G\u1ed0C");
        this.btnToggleZeroProducts = this.bigAction("L\u1eccC");
        this.btnToggleZeroProducts.setToolTipText("\u1ea8n s\u1ea3n ph\u1ea9m thi\u1ebfu s\u1ed1 l\u01b0\u1ee3ng ho\u1eb7c \u0111\u01a1n gi\u00e1. B\u1ea5m l\u1ea1i \u0111\u1ec3 hi\u1ec7n t\u1ea5t c\u1ea3.");
        this.btnToggleZeroProducts.addActionListener(e -> this.toggleZeroQtyProducts());
        JButton btnOut = this.bigAction("CH\u1eccN TH\u01af M\u1ee4C XU\u1ea4T");
        JButton btnDone = this.bigAction("HO\u00c0N TH\u00c0NH");
        left.add(regionLabel);
        left.add(this.radioNorth);
        left.add(this.radioSouth);
        left.add(MainFrame.redArrow());
        left.add(btnIn);
        left.add(MainFrame.redArrow());
        left.add(this.btnToggleZeroProducts);
        left.add(MainFrame.redArrow());
        left.add(btnOut);
        left.add(MainFrame.redArrow());
        left.add(btnDone);
        JPanel totalPanel = new JPanel(new FlowLayout(2, 10, 4));
        totalPanel.setOpaque(false);
        JLabel totalCap = new JLabel("T\u1ed4NG S\u1ed0 TI\u1ec0N");
        totalCap.setFont(UiTheme.FONT_BOLD.deriveFont(12.0f));
        totalCap.setForeground(Color.BLACK);
        this.grandTotalTop.setFont(UiTheme.FONT_BOLD.deriveFont(24.0f));
        this.grandTotalTop.setForeground(UiTheme.NAM_RED);
        totalPanel.add(totalCap);
        totalPanel.add(this.grandTotalTop);
        top.add((Component)left, "West");
        top.add((Component)totalPanel, "East");
        btnIn.addActionListener(e -> this.chooseFolder(true));
        btnOut.addActionListener(e -> this.chooseFolder(false));
        btnDone.addActionListener(e -> this.completeAll());
        return top;
    }

    private JButton bigAction(String text) {
        JButton b = UiTheme.actionButton(text, UiTheme.BTN_TEAL);
        b.setFont(UiTheme.FONT_SIZE20);
        b.setMargin(new Insets(8, 14, 8, 14));
        return b;
    }

    private static JComponent redArrow() {
        JPanel arrow = new JPanel(){

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UiTheme.NAM_RED);
                int w = this.getWidth();
                int h = this.getHeight();
                int cy = h / 2;
                int[] x = new int[]{4, w - 14, w - 14, w - 2, w - 14, w - 14, 4};
                int[] y = new int[]{cy - 5, cy - 5, cy - 11, cy, cy + 11, cy + 5, cy + 5};
                g2.fillPolygon(x, y, x.length);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(36, 28);
            }
        };
        arrow.setOpaque(false);
        return arrow;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setOpaque(false);
        this.setupTable();
        this.tableScroll = new JScrollPane(this.table);
        this.tableScroll.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER));
        this.tableScroll.getViewport().setBackground(Color.WHITE);
        this.tableScroll.setVerticalScrollBarPolicy(20);
        this.tableScroll.setHorizontalScrollBarPolicy(31);
        this.installProductWheelScroll(this.tableScroll);
        this.detailArea.setFont(UiTheme.FONT);
        this.detailArea.setLineWrap(true);
        this.detailArea.setWrapStyleWord(true);
        this.detailArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("S\u1eeda n\u1ed9i dung d\u00f2ng \u0111ang ch\u1ecdn"), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        JScrollPane detailScroll = new JScrollPane(this.detailArea);
        detailScroll.setPreferredSize(new Dimension(260, 0));
        JPanel midRight = new JPanel(new BorderLayout(6, 6));
        midRight.setOpaque(false);
        midRight.add((Component)detailScroll, "Center");
        JPanel saveZone = new JPanel(new FlowLayout(1, 8, 10));
        saveZone.setOpaque(true);
        saveZone.setBackground(new Color(16317180));
        saveZone.setBorder(BorderFactory.createDashedBorder(UiTheme.BORDER, 3.0f, 4.0f));
        JButton btnSaveData = UiTheme.actionButton("SAVE DATA");
        btnSaveData.setFont(UiTheme.FONT_SIZE20);
        btnSaveData.addActionListener(e -> this.saveProductData());
        JButton btnBrandColor = UiTheme.actionButton("COLOR");
        btnBrandColor.setFont(UiTheme.FONT_SIZE20);
        btnBrandColor.setBackground(new Color(8141549));
        btnBrandColor.setToolTipText("\u0110\u1eb7t m\u00e0u ch\u1eef h\u00e3ng \u1edf c\u1ed9t M\u00c3 CODE (CHEMTRUE, ASSURE\u2026)");
        btnBrandColor.addActionListener(e -> this.showBrandColorDialog());
        saveZone.add(btnSaveData);
        saveZone.add(btnBrandColor);
        midRight.add((Component)saveZone, "South");
        midRight.setPreferredSize(new Dimension(270, 0));
        JPanel mid = new JPanel(new BorderLayout(8, 0));
        mid.setOpaque(false);
        mid.add((Component)this.tableScroll, "Center");
        mid.add((Component)midRight, "East");
        center.add((Component)mid, "Center");
        center.add((Component)this.buildCustomerPanel(), "East");
        return center;
    }

    private void setupTable() {
        this.table.setFont(UiTheme.FONT);
        this.table.setSelectionMode(0);
        this.table.setFillsViewportHeight(true);
        // Co giãn theo khung — không bắt kéo ngang mới thấy hết thành tiền.
        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.table.setShowGrid(true);
        this.table.setGridColor(new Color(13751771));
        this.table.setSelectionBackground(UiTheme.ROW_SELECTED);
        this.table.setSelectionForeground(UiTheme.TEXT);
        this.table.setSurrendersFocusOnKeystroke(true);
        this.table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.table.putClientProperty("Table.selectionInactiveBackground", UiTheme.ROW_SELECTED);
        this.table.putClientProperty("Table.selectionInactiveForeground", UiTheme.TEXT);
        this.table.setRowSorter(this.productSorter);
        int c = 0;
        while (c < this.tableModel.getColumnCount()) {
            this.productSorter.setSortable(c, false);
            ++c;
        }
        JTableHeader header = this.table.getTableHeader();
        header.setFont(UiTheme.FONT_BOLD);
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel)super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(0);
                label.setOpaque(true);
                label.setBackground(UiTheme.HEADER_COLORS[column % UiTheme.HEADER_COLORS.length]);
                label.setForeground(Color.WHITE);
                label.setFont(UiTheme.FONT_BOLD);
                label.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
                return label;
            }
        });
        TableColumnModel cols = this.table.getColumnModel();
        this.applyProductColumnWidths();
        cols.getColumn(1).setCellRenderer(new MultiLineRenderer());
        cols.getColumn(1).setCellEditor(new ContentCellEditor());
        cols.getColumn(2).setCellRenderer(new CodeCellRenderer());
        cols.getColumn(2).setCellEditor(new CodeCellEditor());
        DefaultTableCellRenderer bold20 = this.boldCenterRenderer(UiTheme.FONT_SIZE20, Color.BLACK);
        int[] nArray = new int[]{3, 4, 5};
        int n = nArray.length;
        int n2 = 0;
        while (n2 < n) {
            int i2 = nArray[n2];
            cols.getColumn(i2).setCellRenderer(bold20);
            ++n2;
        }
        cols.getColumn(0).setCellRenderer(this.boldCenterRenderer(UiTheme.FONT_BOLD, Color.BLACK));
        // Thành tiền: luôn giữa + padding hai bên — tránh số sát mép nhìn như bị cắt chữ số.
        cols.getColumn(6).setCellRenderer(this.amountCenterRenderer(UiTheme.FONT_SIZE20, Color.BLACK));
        cols.getColumn(3).setCellEditor(new UppercaseCellEditor());
        cols.getColumn(4).setCellEditor(new AmountCellEditor());
        cols.getColumn(5).setCellEditor(new AmountCellEditor());
        cols.addColumnModelListener(new TableColumnModelListener(){

            @Override
            public void columnAdded(javax.swing.event.TableColumnModelEvent e) {
            }

            @Override
            public void columnRemoved(javax.swing.event.TableColumnModelEvent e) {
            }

            @Override
            public void columnMoved(javax.swing.event.TableColumnModelEvent e) {
            }

            @Override
            public void columnMarginChanged(ChangeEvent e) {
                SwingUtilities.invokeLater(MainFrame.this::resizeRows);
            }

            @Override
            public void columnSelectionChanged(javax.swing.event.ListSelectionEvent e) {
            }
        });
        this.table.addComponentListener(new ComponentAdapter(){

            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(MainFrame.this::resizeRows);
            }
        });
        this.installTableTabTraversal();
    }

    private void applyProductColumnWidths() {
        FontMetrics fm = this.table.getFontMetrics(UiTheme.FONT_SIZE20);
        int pad = 28 + 28;
        int amountW = Math.max(fm.stringWidth(AMOUNT_WIDTH_SAMPLE), fm.stringWidth("999.999.999.999")) + pad + 12;
        TableColumnModel cols = this.table.getColumnModel();
        // Thu hẹp nội dung; thành tiền đủ rộng cho 45.976.541.123 + khoảng trống hai bên.
        int[] prefs = new int[]{42, 280, 118, 72, 88, 100, amountW};
        int[] mins = new int[]{36, 160, 90, 56, 70, 80, amountW};
        int i = 0;
        while (i < prefs.length) {
            cols.getColumn(i).setMinWidth(mins[i]);
            cols.getColumn(i).setPreferredWidth(prefs[i]);
            ++i;
        }
        cols.getColumn(6).setMaxWidth(Integer.MAX_VALUE);
        cols.getColumn(1).setMaxWidth(Integer.MAX_VALUE);
    }

    /** Tab/Shift+Tab luôn chuyển ô — kể cả khi đang sửa JTextArea (nội dung / mã code). */
    private void installTableTabTraversal() {
        InputMap im = this.table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = this.table.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "contract-tab-next");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), "contract-tab-prev");
        am.put("contract-tab-next", new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.tabThroughTable(false);
            }
        });
        am.put("contract-tab-prev", new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.tabThroughTable(true);
            }
        });
    }

    private void tabThroughTable(boolean backward) {
        int row;
        int col;
        if (this.table.isEditing()) {
            row = this.table.getEditingRow();
            col = this.table.getEditingColumn();
            TableCellEditor ed = this.table.getCellEditor();
            if (ed != null && !ed.stopCellEditing()) {
                return;
            }
        } else {
            row = this.table.getSelectedRow();
            col = this.table.getSelectedColumn();
        }
        if (row < 0) {
            row = 0;
        }
        if (col < 0) {
            col = 0;
        }
        final int fromRow = row;
        final int fromCol = col;
        SwingUtilities.invokeLater(() -> MainFrame.moveTableEdit(this.table, fromRow, fromCol, backward));
    }

    private static void bindEditorTabKeys(JTextArea area, AbstractCellEditor editor, java.util.function.Supplier<JTable> tableRef, java.util.function.IntSupplier rowRef, java.util.function.IntSupplier colRef) {
        area.setFocusTraversalKeysEnabled(false);
        InputMap im = area.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = area.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "editor-tab-next");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), "editor-tab-prev");
        am.put("editor-tab-next", new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
                JTable table = tableRef.get();
                int row = rowRef.getAsInt();
                int col = colRef.getAsInt();
                if (editor.stopCellEditing()) {
                    SwingUtilities.invokeLater(() -> MainFrame.moveTableEdit(table, row, col, false));
                }
            }
        });
        am.put("editor-tab-prev", new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
                JTable table = tableRef.get();
                int row = rowRef.getAsInt();
                int col = colRef.getAsInt();
                if (editor.stopCellEditing()) {
                    SwingUtilities.invokeLater(() -> MainFrame.moveTableEdit(table, row, col, true));
                }
            }
        });
    }

    private DefaultTableCellRenderer boldCenterRenderer(final Font font, final Color fg) {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                c.setFont(font);
                if (isSelected) {
                    c.setBackground(UiTheme.ROW_SELECTED);
                    c.setForeground(fg);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(fg);
                }
                return c;
            }
        };
        r.setHorizontalAlignment(SwingConstants.CENTER);
        r.setOpaque(true);
        return r;
    }

    private DefaultTableCellRenderer amountCenterRenderer(final Font font, final Color fg) {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel)super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                label.setFont(font);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(2, 28, 2, 28));
                if (isSelected) {
                    label.setBackground(UiTheme.ROW_SELECTED);
                    label.setForeground(fg);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(fg);
                }
                return label;
            }
        };
        r.setHorizontalAlignment(SwingConstants.CENTER);
        r.setOpaque(true);
        return r;
    }

    private void showBrandColorDialog() {
        // Đọc lại từ F: (không tự thêm lại hãng đã xóa / hãng đang có trên bảng SP).
        this.brandColors.reload();
        JDialog dialog = new JDialog(this, "C\u00c0I \u0110\u1eb6T M\u00c0U THEO H\u00c3NG", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(UiTheme.BG);
        JLabel hint = new JLabel("<html>Ch\u1ecdn m\u00e0u ch\u1eef <b>d\u00f2ng 2</b> c\u1ed9t M\u00c3 CODE.<br/>C\u00f3 th\u1ec3 <b>th\u00eam</b> ho\u1eb7c <b>x\u00f3a</b> h\u00e3ng (vd. g\u00f5 nh\u1ea7m) \u2014 l\u01b0u v\u00e0o file.</html>");
        hint.setBorder(BorderFactory.createEmptyBorder(12, 14, 4, 14));
        hint.setFont(UiTheme.FONT);
        Path colorPath = this.brandColors.path().toAbsolutePath();
        boolean onShared = AppPaths.usingSharedDrive();
        String pathHtml = onShared
                ? "<html><b>File:</b> " + colorPath + "</html>"
                : "<html><b>File \u0111ang d\u00f9ng (T\u1ea0M C:):</b> " + colorPath
                        + "<br/><font color='#B00020'><b>Ch\u01b0a map \u1ed5 F:</b> " + AppPaths.sharedDir()
                        + "<br/>Map F: xong m\u1edf l\u1ea1i tool \u2014 m\u1eddi x\u00f3a/l\u01b0u m\u1edbi \u0111\u00fang \u1ed5 chung.</font></html>";
        JLabel pathLabel = new JLabel(pathHtml);
        pathLabel.setFont(UiTheme.FONT.deriveFont(11.0f));
        pathLabel.setForeground(onShared ? UiTheme.MUTED : new Color(0xB00020));
        pathLabel.setBorder(BorderFactory.createEmptyBorder(0, 14, 6, 14));
        JPanel addRow = new JPanel(new FlowLayout(0, 8, 4));
        addRow.setOpaque(false);
        addRow.setBorder(BorderFactory.createEmptyBorder(0, 10, 4, 10));
        JLabel addLbl = new JLabel("TH\u00caM H\u00c3NG M\u1edaI");
        addLbl.setFont(UiTheme.FONT_BOLD);
        JTextField newBrand = new JTextField(16);
        newBrand.setFont(UiTheme.FONT_BOLD);
        ((AbstractDocument)newBrand.getDocument()).setDocumentFilter(MainFrame.uppercaseFilter());
        JButton btnAdd = UiTheme.actionButton("TH\u00caM");
        btnAdd.setFont(UiTheme.FONT_BOLD.deriveFont(13.0f));
        addRow.add(addLbl);
        addRow.add(newBrand);
        addRow.add(btnAdd);
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add((Component)hint, "North");
        JPanel northMid = new JPanel(new BorderLayout());
        northMid.setOpaque(false);
        northMid.add((Component)pathLabel, "North");
        northMid.add((Component)addRow, "Center");
        north.add((Component)northMid, "Center");
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, 1));
        list.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        list.setOpaque(false);
        Runnable[] refreshList = new Runnable[1];
        refreshList[0] = () -> {
            list.removeAll();
            for (Map.Entry<String, Color> e : this.brandColors.all().entrySet()) {
                String brand = e.getKey();
                list.add(this.buildBrandColorRow(brand, e.getValue(), () -> {
                    int confirm = JOptionPane.showConfirmDialog(dialog, "X\u00f3a h\u00e3ng \"" + brand + "\" kh\u1ecfi danh s\u00e1ch m\u00e0u?", "X\u00e1c nh\u1eadn x\u00f3a", 0);
                    if (confirm != 0) {
                        return;
                    }
                    this.brandColors.removeBrand(brand);
                    this.table.repaint();
                    refreshList[0].run();
                }));
                list.add(Box.createVerticalStrut(8));
            }
            list.revalidate();
            list.repaint();
        };
        refreshList[0].run();
        btnAdd.addActionListener(ev -> {
            String name = BrandColorStore.normalize(newBrand.getText());
            if (name.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Nh\u1eadp t\u00ean h\u00e3ng (vd. CHEMTRUE, ASSURE\u2026).", "Thi\u1ebfu t\u00ean", 2);
                return;
            }
            boolean existed = this.brandColors.all().containsKey(name);
            this.brandColors.ensureBrand(name);
            this.brandColors.save();
            newBrand.setText("");
            refreshList[0].run();
            this.table.repaint();
            if (existed) {
                JOptionPane.showMessageDialog(dialog, "H\u00e3ng \"" + name + "\" \u0111\u00e3 c\u00f3 \u2014 k\u00e9o xu\u1ed1ng \u0111\u1ec3 \u0111\u1ed5i m\u00e0u.", "\u0110\u00e3 c\u00f3", 1);
            }
        });
        newBrand.addActionListener(ev -> btnAdd.doClick());
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UiTheme.BG);
        scroll.setPreferredSize(new Dimension(520, 360));
        JButton saveBtn = UiTheme.actionButton("L\u01afU FILE");
        saveBtn.setFont(UiTheme.FONT_BOLD.deriveFont(14.0f));
        saveBtn.addActionListener(ev -> {
            boolean ok = this.brandColors.save();
            this.table.repaint();
            Path path = this.brandColors.path().toAbsolutePath();
            if (ok) {
                String where = AppPaths.usingSharedDrive()
                        ? "\u0110\u00e3 l\u01b0u \u1ed5 data chung c\u00f4ng ty:\n" + path
                        : "Kh\u00f4ng ghi \u0111\u01b0\u1ee3c \u1ed5 F: \u2014 \u0111\u00e3 l\u01b0u t\u1ea1m local:\n" + path
                                + "\n\nH\u00e3y map \u1ed5 F:\\TOOL - HOP DONG\\ContractToolData \u0111\u1ec3 c\u1ea3 c\u00f4ng ty d\u00f9ng chung.";
                JOptionPane.showMessageDialog(dialog, "\u0110\u00c3 L\u01afU TH\u00c0NH C\u00d4NG\n\n" + where, "L\u01afU B\u1ea2NG M\u00c0U", 1);
            } else {
                JOptionPane.showMessageDialog(dialog, "Kh\u00f4ng ghi \u0111\u01b0\u1ee3c file:\n" + path, "L\u1ed7i", 0);
            }
        });
        JButton close = UiTheme.actionButton("XONG");
        close.setFont(UiTheme.FONT_BOLD.deriveFont(14.0f));
        close.addActionListener(ev -> {
            this.brandColors.save();
            this.table.repaint();
            dialog.dispose();
        });
        JPanel south = new JPanel(new FlowLayout(2, 12, 10));
        south.setOpaque(false);
        south.add(saveBtn);
        south.add(close);
        dialog.add((Component)north, "North");
        dialog.add((Component)scroll, "Center");
        dialog.add((Component)south, "South");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel buildBrandColorRow(String brand, Color current, Runnable onDelete) {
        JPanel row = new JPanel(new BorderLayout(10, 6));
        row.setOpaque(true);
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UiTheme.BORDER), BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));
        JLabel name = new JLabel(brand);
        name.setFont(UiTheme.FONT_BOLD.deriveFont(16.0f));
        name.setForeground(current);
        JPanel swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(40, 24));
        swatch.setOpaque(true);
        swatch.setBackground(current);
        swatch.setBorder(BorderFactory.createLineBorder(new Color(3359061)));
        JButton btnDelete = UiTheme.actionButton("X\u00d3A", UiTheme.NAM_RED);
        btnDelete.setFont(UiTheme.FONT_BOLD.deriveFont(12.0f));
        btnDelete.setMargin(new Insets(4, 10, 4, 10));
        btnDelete.setToolTipText("X\u00f3a h\u00e3ng n\u00e0y kh\u1ecfi danh s\u00e1ch m\u00e0u");
        btnDelete.addActionListener(e -> onDelete.run());
        JPanel right = new JPanel(new FlowLayout(2, 8, 0));
        right.setOpaque(false);
        right.add(swatch);
        right.add(btnDelete);
        JPanel head = new JPanel(new BorderLayout(8, 0));
        head.setOpaque(false);
        head.add((Component)name, "West");
        head.add((Component)right, "East");
        JPanel palette = new JPanel(new FlowLayout(0, 4, 2));
        palette.setOpaque(false);
        Color[] colorArray = BrandColorStore.THEME_SWATCHES;
        int n = BrandColorStore.THEME_SWATCHES.length;
        int n2 = 0;
        while (n2 < n) {
            Color c = colorArray[n2];
            JButton chip = this.makeColorChip(c, () -> {
                this.brandColors.setColor(brand, c);
                name.setForeground(c);
                swatch.setBackground(c);
                this.table.repaint();
            });
            palette.add(chip);
            ++n2;
        }
        row.add((Component)head, "North");
        row.add((Component)palette, "Center");
        return row;
    }

    private JButton makeColorChip(Color color, Runnable onPick) {
        JButton chip = new JButton(){

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(26, 26);
            }
        };
        chip.setOpaque(true);
        chip.setBackground(color);
        chip.setBorder(BorderFactory.createLineBorder(new Color(9741240)));
        chip.setFocusPainted(false);
        chip.setContentAreaFilled(true);
        chip.setToolTipText(BrandColorStore.toHex(color));
        chip.addActionListener(e -> onPick.run());
        return chip;
    }

    private JPanel buildCustomerPanel() {
        JPanel wrap = UiTheme.cardPanel();
        wrap.setLayout(new BorderLayout(4, 4));
        wrap.setPreferredSize(new Dimension(420, 0));
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, 1));
        JPanel mstRow = new JPanel(new FlowLayout(0, 6, 2));
        mstRow.setOpaque(false);
        mstRow.setAlignmentX(0.0f);
        JLabel mstLabel = new JLabel("M\u00c3 S\u1ed0 THU\u1ebe");
        mstLabel.setFont(UiTheme.FONT_BOLD);
        this.taxCode.setColumns(10);
        this.taxCode.setPreferredSize(new Dimension(110, 28));
        mstRow.add(mstLabel);
        mstRow.add(this.taxCode);
        JButton btnSearch = UiTheme.actionButton("SEARCH");
        btnSearch.setFont(UiTheme.FONT_SIZE20);
        btnSearch.setAlignmentX(0.0f);
        btnSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSearch.setPreferredSize(new Dimension(280, 42));
        JButton btnSaveCustomer = UiTheme.actionButton("SAVE CUSTOMER");
        btnSaveCustomer.setFont(UiTheme.FONT_SIZE20);
        btnSaveCustomer.setAlignmentX(0.0f);
        btnSaveCustomer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSaveCustomer.setPreferredSize(new Dimension(280, 42));
        btnSearch.addActionListener(e -> this.searchCustomer());
        btnSaveCustomer.addActionListener(e -> this.saveCustomerData());
        header.add(mstRow);
        header.add(Box.createVerticalStrut(4));
        header.add(btnSearch);
        header.add(Box.createVerticalStrut(6));
        header.add(btnSaveCustomer);
        header.setPreferredSize(new Dimension(300, 140));
        header.setMinimumSize(new Dimension(300, 140));
        wrap.add((Component)header, "North");
        wrap.add((Component)this.buildInfoForm(), "Center");
        return wrap;
    }

    private JPanel buildInfoForm() {
        JPanel info = new JPanel(new GridBagLayout());
        info.setOpaque(false);
        this.infoForm = info;
        this.partyLabelWidth = this.measurePartyLabelWidth();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 3, 2, 3);
        c.fill = 2;
        int y = 0;
        y = this.addToggleRow(info, c, y, this.tabTwoParty, this.tabThreeParty, true);
        c.gridx = 0;
        c.gridy = y++;
        c.weightx = 0.0;
        c.gridwidth = 1;
        JLabel lbHd = new JLabel("S\u1ed0 H\u1ee2P \u0110\u1ed2NG");
        lbHd.setFont(UiTheme.FONT_BOLD);
        this.pinPartyLabel(lbHd);
        info.add((Component)lbHd, c);
        c.gridx = 1;
        c.weightx = 1.0;
        this.contractNo.setFont(UiTheme.FONT);
        info.add((Component)this.contractNo, c);
        y = this.addToggleRow(info, c, y, this.tabSideA, this.tabSideC, false);
        this.sideTabs = (JPanel)info.getComponent(info.getComponentCount() - 1);
        this.partyABlock = this.buildPartyABlock();
        this.partyCBlock = this.buildPartyCBlock();
        this.partyHolder.setOpaque(false);
        this.partyHolder.add((Component)this.partyABlock, "A");
        this.partyHolder.add((Component)this.partyCBlock, "C");
        c.gridx = 0;
        c.gridy = y++;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = 2;
        c.anchor = 11;
        info.add((Component)this.partyHolder, c);
        c.gridwidth = 1;
        c.anchor = 10;
        this.year.setEditable(false);
        this.year.setHorizontalAlignment(0);
        this.year.setForeground(UiTheme.NAM_RED);
        Object[][] yearRow = new Object[][]{{"N\u0102M", this.year}};
        this.addInfoFields(info, c, y, yearRow);
        this.year.setFont(UiTheme.FONT_SIZE20);
        this.pinPartyField(this.year);
        MainFrame.addUppercase(this.contractNo, this.title1, this.title2, this.titleC, this.titleC2);
        MainFrame.addUppercaseDoc(this.partyA);
        MainFrame.addUppercaseDoc(this.address);
        MainFrame.addUppercaseDoc(this.account1);
        MainFrame.addUppercaseDoc(this.account2);
        MainFrame.addUppercaseDoc(this.partyC);
        MainFrame.addUppercaseDoc(this.addressC);
        MainFrame.addUppercaseDoc(this.accountC);
        MainFrame.addUppercaseDoc(this.accountC2);
        MainFrame.useTabToChangeFocus(this.partyA, this.address, this.account1, this.account2, this.partyC, this.addressC, this.accountC, this.accountC2);
        MainFrame.addDigitsOnly(this.taxCode, 15);
        MainFrame.addDigitsOnly(this.phone, 15);
        MainFrame.addDigitsOnly(this.budgetCode, 30);
        MainFrame.addDigitsOnly(this.phoneC, 15);
        MainFrame.addDigitsOnly(this.taxCodeC, 15);
        MainFrame.addDigitsOnly(this.budgetCodeC, 30);
        MainFrame.addRepFormat(this.rep1, this.rep2, this.repC, this.repC2);
        this.tabTwoParty.addActionListener(e -> {
            if (this.tabTwoParty.isSelected()) {
                this.applyPartyMode();
            }
        });
        this.tabThreeParty.addActionListener(e -> {
            if (this.tabThreeParty.isSelected()) {
                this.applyPartyMode();
            }
        });
        this.tabSideA.addActionListener(e -> {
            if (this.tabSideA.isSelected()) {
                this.applyPartyMode();
            }
        });
        this.tabSideC.addActionListener(e -> {
            if (this.tabSideC.isSelected()) {
                this.applyPartyMode();
            }
        });
        this.applyPartyMode();
        return info;
    }

    /** Hợp đồng 2 bên: chỉ Bên A. Hợp đồng 3 bên: chuyển tab để nhập Bên A hoặc Bên C. Bên B cố định trong mẫu. */
    private void applyPartyMode() {
        boolean three = this.tabThreeParty.isSelected();
        if (this.sideTabs != null) {
            this.sideTabs.setVisible(three);
        }
        boolean showC = three && this.tabSideC.isSelected();
        this.partyCards.show(this.partyHolder, showC ? "C" : "A");
        if (this.infoForm != null) {
            this.infoForm.revalidate();
            this.infoForm.repaint();
        }
    }

    private int addToggleRow(JPanel info, GridBagConstraints c, int y, JToggleButton left, JToggleButton right, boolean selectLeft) {
        ButtonGroup group = new ButtonGroup();
        group.add(left);
        group.add(right);
        this.stylePreset(left);
        this.stylePreset(right);
        if (selectLeft) {
            left.setSelected(true);
        } else {
            left.setSelected(true);
        }
        JPanel tabs = new JPanel(new java.awt.GridLayout(1, 2, 6, 0));
        tabs.setOpaque(false);
        tabs.add(left);
        tabs.add(right);
        c.gridx = 0;
        c.gridy = y++;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = 2;
        c.insets = new Insets(6, 3, 4, 3);
        info.add((Component)tabs, c);
        c.gridwidth = 1;
        c.insets = new Insets(2, 3, 2, 3);
        return y;
    }

    private JPanel buildPartyABlock() {
        JPanel block = new JPanel(new GridBagLayout());
        block.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 3, 2, 3);
        c.fill = 2;
        int y = 0;
        y = this.addAutoGrowArea(block, y, "B\u00caN A (MUA)", this.partyA, 4, "V\u00ed d\u1ee5: V\u0102N PH\u00d2NG H\u1ed8I \u0110\u1ed2NG NH\u00c2N D\u00c2N & \u1ee6Y BAN NH\u00c2N D\u00c2N X\u00c3 TR\u01af\u01a0NG NGUY\u1ec4N HO\u00c0NG MINH H\u01af\u01a0NG", true);
        y = this.addAutoGrowArea(block, y, "\u0110\u1ecaA CH\u1ec8", this.address, 4, "Xu\u1ed1ng d\u00f2ng \u0111\u1ec3 xem \u0111\u1ee7 \u0111\u1ecba ch\u1ec9, d\u1ec5 b\u1eaft l\u1ed7i ch\u00ednh t\u1ea3", false);
        c.gridx = 0;
        c.gridy = y++;
        c.weightx = 0.0;
        c.gridwidth = 1;
        JLabel lbPhone = new JLabel("\u0110I\u1ec6N THO\u1ea0I");
        lbPhone.setFont(UiTheme.FONT_BOLD);
        this.pinPartyLabel(lbPhone);
        block.add((Component)lbPhone, c);
        c.gridx = 1;
        c.weightx = 1.0;
        this.phone.setFont(UiTheme.FONT);
        this.pinPartyField(this.phone);
        block.add((Component)this.phone, c);
        y = this.addAutoGrowArea(block, y, "T\u00c0I KHO\u1ea2N #1", this.account1, 3, "Nh\u1eadp \u0111\u1ee7, g\u1ed3m c\u1ea3 KHU V\u1ef0C IX; xu\u1ea5t file s\u1ebd n\u1eb1m sau d\u1ea5u :", false);
        y = this.addAutoGrowArea(block, y, "T\u00c0I KHO\u1ea2N #2", this.account2, 3, null, false);
        Object[][] rows = new Object[][]{{"M\u00c3 QHNS", this.budgetCode}, {"\u0110\u1ea0I DI\u1ec6N #1", this.rep1}, {"\u0110\u1ea0I DI\u1ec6N #2", this.rep2}, {"CH\u1ee8C V\u1ee4 #1", this.title1}, {"CH\u1ee8C V\u1ee4 #2", this.title2}};
        this.addInfoFields(block, c, y, rows);
        return block;
    }

    private JPanel buildPartyCBlock() {
        JPanel block = new JPanel(new GridBagLayout());
        block.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 3, 2, 3);
        c.fill = 2;
        int y = 0;
        y = this.addAutoGrowArea(block, y, "B\u00caN C (NH\u1eacN)", this.partyC, 4, "B\u00ean nh\u1eadn. B\u00ean B gi\u1eef nguy\u00ean trong m\u1eabu Word.", true);
        y = this.addAutoGrowArea(block, y, "\u0110\u1ecaA CH\u1ec8", this.addressC, 4, null, false);
        c.gridx = 0;
        c.gridy = y++;
        c.weightx = 0.0;
        c.gridwidth = 1;
        JLabel lbPhone = new JLabel("\u0110I\u1ec6N THO\u1ea0I");
        lbPhone.setFont(UiTheme.FONT_BOLD);
        this.pinPartyLabel(lbPhone);
        block.add((Component)lbPhone, c);
        c.gridx = 1;
        c.weightx = 1.0;
        this.phoneC.setFont(UiTheme.FONT);
        this.pinPartyField(this.phoneC);
        block.add((Component)this.phoneC, c);
        y = this.addAutoGrowArea(block, y, "T\u00c0I KHO\u1ea2N #1", this.accountC, 3, null, false);
        y = this.addAutoGrowArea(block, y, "T\u00c0I KHO\u1ea2N #2", this.accountC2, 3, null, false);
        c.gridx = 0;
        c.gridy = y++;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.fill = 2;
        JLabel lbTax = new JLabel("M\u00c3 S\u1ed0 THU\u1ebe");
        lbTax.setFont(UiTheme.FONT_BOLD);
        this.pinPartyLabel(lbTax);
        block.add((Component)lbTax, c);
        c.gridx = 1;
        c.weightx = 1.0;
        this.taxCodeC.setFont(UiTheme.FONT);
        this.pinPartyField(this.taxCodeC);
        block.add((Component)this.taxCodeC, c);
        Object[][] rows = new Object[][]{{"M\u00c3 QHNS", this.budgetCodeC}, {"\u0110\u1ea0I DI\u1ec6N #1", this.repC}, {"\u0110\u1ea0I DI\u1ec6N #2", this.repC2}, {"CH\u1ee8C V\u1ee4 #1", this.titleC}, {"CH\u1ee8C V\u1ee4 #2", this.titleC2}};
        this.addInfoFields(block, c, y, rows);
        return block;
    }

    private int addInfoFields(JPanel info, GridBagConstraints c, int y, Object[][] rows) {
        int n = 0;
        while (n < rows.length) {
            Object[] row = rows[n];
            c.gridx = 0;
            c.gridy = y++;
            c.weightx = 0.0;
            c.gridwidth = 1;
            c.fill = 2;
            JLabel label = new JLabel((String)row[0]);
            label.setFont(UiTheme.FONT_BOLD);
            this.pinPartyLabel(label);
            info.add((Component)label, c);
            c.gridx = 1;
            c.weightx = 1.0;
            JTextField field = (JTextField)row[1];
            field.setFont(UiTheme.FONT);
            this.pinPartyField(field);
            info.add((Component)field, c);
            ++n;
        }
        return y;
    }

    private static void useTabToChangeFocus(JTextArea ... areas) {
        Set<KeyStroke> forward = Set.of(KeyStroke.getKeyStroke(9, 0));
        Set<KeyStroke> backward = Set.of(KeyStroke.getKeyStroke(9, 64));
        JTextArea[] jTextAreaArray = areas;
        int n = areas.length;
        int n2 = 0;
        while (n2 < n) {
            JTextArea area = jTextAreaArray[n2];
            area.setFocusTraversalKeys(0, forward);
            area.setFocusTraversalKeys(1, backward);
            ++n2;
        }
    }

    private int addAutoGrowArea(JPanel info, int y, String labelText, final JTextArea area, final int maxRows, String tip, boolean bold) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 3, 2, 3);
        c.fill = 2;
        c.anchor = 18;
        c.gridx = 0;
        c.gridy = y;
        c.gridwidth = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.ipady = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(UiTheme.FONT_BOLD);
        this.pinPartyLabel(label);
        info.add((Component)label, c);
        area.setFont(bold ? UiTheme.FONT_BOLD.deriveFont(13.0f) : UiTheme.FONT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setRows(1);
        area.setBorder(UIManager.getBorder("TextField.border"));
        if (area.getBorder() == null) {
            area.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UiTheme.BORDER), BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        }
        if (tip != null) {
            area.setToolTipText(tip);
        }
        final JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add((Component)area, "Center");
        this.applyAutoGrowHeight(area, wrap, maxRows);
        DocumentListener grow = MainFrame.simpleListener(() -> this.applyAutoGrowHeight(area, wrap, maxRows));
        area.getDocument().addDocumentListener(grow);
        area.addComponentListener(new ComponentAdapter(){

            @Override
            public void componentResized(ComponentEvent e) {
                MainFrame.this.applyAutoGrowHeight(area, wrap, maxRows);
            }
        });
        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = 2;
        info.add((Component)wrap, c);
        return y + 1;
    }

    /** Cùng bề ngang cột nhãn để ô nhập Bên A và Bên C thẳng hàng. */
    private int measurePartyLabelWidth() {
        FontMetrics fm = this.getFontMetrics(UiTheme.FONT_BOLD);
        String[] labels = new String[]{
            "S\u1ed0 H\u1ee2P \u0110\u1ed2NG",
            "B\u00caN A (MUA)",
            "B\u00caN C (NH\u1eacN)",
            "\u0110\u1ecaA CH\u1ec8",
            "\u0110I\u1ec6N THO\u1ea0I",
            "T\u00c0I KHO\u1ea2N #1",
            "T\u00c0I KHO\u1ea2N #2",
            "M\u00c3 S\u1ed0 THU\u1ebe",
            "M\u00c3 QHNS",
            "\u0110\u1ea0I DI\u1ec6N #1",
            "\u0110\u1ea0I DI\u1ec6N #2",
            "CH\u1ee8C V\u1ee4 #1",
            "CH\u1ee8C V\u1ee4 #2",
            "N\u0102M"
        };
        int w = 0;
        int i = 0;
        while (i < labels.length) {
            w = Math.max(w, fm.stringWidth(labels[i]));
            ++i;
        }
        return w + 6;
    }

    private void pinPartyLabel(JLabel label) {
        int h = Math.max(label.getPreferredSize().height, 22);
        Dimension d = new Dimension(this.partyLabelWidth, h);
        label.setPreferredSize(d);
        label.setMinimumSize(d);
        label.setHorizontalAlignment(2);
    }

    private static void pinPartyField(JTextField field) {
        Dimension pref = field.getPreferredSize();
        int h = Math.max(30, pref.height);
        field.setPreferredSize(new Dimension(Math.max(80, pref.width), h));
    }

    private void applyAutoGrowHeight(JTextArea area, JComponent host, int maxRows) {
        SwingUtilities.invokeLater(() -> {
            String text;
            int lines = 1;
            try {
                int len = area.getDocument().getLength();
                if (len > 0 && area.getWidth() > 20) {
                    Rectangle2D r0 = area.modelToView2D(0);
                    Rectangle2D r1 = area.modelToView2D(len);
                    if (r0 != null && r1 != null) {
                        int lineH = Math.max(1, area.getFontMetrics(area.getFont()).getHeight());
                        lines = (int)Math.round((r1.getY() - r0.getY()) / (double)lineH) + 1;
                    }
                }
                if ((text = area.getText()) != null) {
                    lines = Math.max(lines, text.split("\n", -1).length);
                }
            }
            catch (Exception ignored) {
                text = area.getText();
                lines = text == null || text.isBlank() ? 1 : text.split("\n", -1).length;
            }
            lines = Math.max(1, Math.min(maxRows, lines));
            FontMetrics fm = area.getFontMetrics(area.getFont());
            int lineH = Math.max(18, fm.getHeight());
            Insets insets = area.getInsets();
            int pad = (insets != null ? insets.top + insets.bottom : 8) + 8;
            int h = lines * lineH + pad;
            if (lines == 1) {
                h = Math.max(30, h);
            }
            if (area.getRows() != lines || area.getPreferredSize().height != h) {
                area.setRows(lines);
                int viewH = lines >= 2 ? h + lineH / 3 : h;
                Dimension d = new Dimension(200, viewH);
                area.setPreferredSize(d);
                host.setPreferredSize(d);
                host.setMinimumSize(new Dimension(120, viewH));
                host.revalidate();
                Container p = host.getParent();
                if (p != null) {
                    p.revalidate();
                    p.repaint();
                }
            }
        });
    }

    private JPanel buildBottomBar() {
        JPanel bottom = UiTheme.cardPanel();
        bottom.setLayout(new BorderLayout(4, 2));
        bottom.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UiTheme.BORDER, 1, true), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        this.stylePreset(this.presetHopDongBaoPhat);
        this.stylePreset(this.presetBaoPhat);
        this.presetHopDongBaoPhat.addActionListener(e -> this.applyPresetHopDongBaoPhat());
        this.presetBaoPhat.addActionListener(e -> this.applyPresetBaoPhat());
        // Trái / phải: checkbox độc lập (cùng tên cũng không kéo tick bên kia). Xuất = hợp các mục đang tick.
        ExportDoc[] leftCol1 = new ExportDoc[]{ExportDoc.HOP_DONG, ExportDoc.THANH_LY, ExportDoc.MAU_08A, ExportDoc.DE_NGHI_TT, ExportDoc.BAO_GIA_1, ExportDoc.BAO_GIA_CUNG};
        ExportDoc[] leftCol2 = new ExportDoc[]{ExportDoc.NT_BG, ExportDoc.BBGN, ExportDoc.DOI_CHIEU, ExportDoc.BAO_PHAT, ExportDoc.BAO_GIA_2};
        ExportDoc[] rightCol1 = new ExportDoc[]{ExportDoc.BAO_PHAT, ExportDoc.DE_NGHI_TT, ExportDoc.NT_BG};
        ExportDoc[] rightCol2 = new ExportDoc[]{ExportDoc.BAO_GIA_CUNG, ExportDoc.BAO_GIA_1, ExportDoc.BAO_GIA_2};
        JPanel leftChecks = this.buildCheckGrid(new ExportDoc[][]{leftCol1, leftCol2}, this.leftTemplateChecks, true);
        JPanel rightChecks = this.buildCheckGrid(new ExportDoc[][]{rightCol1, rightCol2}, this.rightTemplateChecks, false);
        JPanel leftSide = this.buildPresetColumn(this.presetHopDongBaoPhat, leftChecks);
        JPanel rightSide = this.buildPresetColumn(this.presetBaoPhat, rightChecks);
        JSeparator divider = new JSeparator(1);
        divider.setForeground(new Color(13358561));
        JPanel columns = new JPanel(new GridBagLayout());
        columns.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.fill = 1;
        gc.weighty = 1.0;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.gridx = 0;
        gc.weightx = 0.58;
        gc.insets = new Insets(0, 2, 0, 8);
        columns.add((Component)leftSide, gc);
        gc.gridx = 1;
        gc.weightx = 0.0;
        gc.fill = 3;
        gc.insets = new Insets(2, 0, 2, 0);
        columns.add((Component)divider, gc);
        gc.gridx = 2;
        gc.weightx = 0.42;
        gc.fill = 1;
        gc.insets = new Insets(0, 8, 0, 2);
        columns.add((Component)rightSide, gc);
        JPanel mid = new JPanel(new BorderLayout(4, 1));
        mid.setOpaque(false);
        mid.add((Component)columns, "Center");
        this.folderLabel.setForeground(UiTheme.MUTED);
        this.folderLabel.setFont(UiTheme.FONT.deriveFont(11.0f));
        mid.add((Component)this.folderLabel, "South");
        bottom.add((Component)mid, "Center");
        bottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 168));
        // Mặc định: chỉ gói trái (11 mục). Cột phải tắt — không tick cả 2 bên lúc mở app.
        this.syncingTemplates = true;
        try {
            this.setDocsSelected(this.leftTemplateChecks, ALL_DOCS, true);
            this.setDocsSelected(this.rightTemplateChecks, BAO_PHAT_DOCS, false);
            this.presetHopDongBaoPhat.setSelected(true);
            this.presetBaoPhat.setSelected(false);
            this.updatePresetLook(this.presetHopDongBaoPhat);
            this.updatePresetLook(this.presetBaoPhat);
        }
        finally {
            this.syncingTemplates = false;
        }
        return bottom;
    }

    private JPanel buildCheckGrid(ExportDoc[][] cols, Map<ExportDoc, JCheckBox> store, boolean defaultSelected) {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.fill = 1;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.weighty = 1.0;
        int c = 0;
        while (c < cols.length) {
            JPanel col = this.colPanel();
            int r = 0;
            while (r < cols[c].length) {
                col.add(this.makeTemplateCheck(cols[c][r], store, defaultSelected));
                ++r;
            }
            gc.gridx = c;
            gc.weightx = 1.0;
            gc.insets = new Insets(0, c == 0 ? 0 : 10, 0, 0);
            grid.add((Component)col, gc);
            ++c;
        }
        return grid;
    }

    private JPanel buildPresetColumn(JToggleButton presetBtn, JPanel checks) {
        JPanel side = new JPanel(new BorderLayout(0, 3));
        side.setOpaque(false);
        side.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        JPanel head = new JPanel(new FlowLayout(0, 0, 0));
        head.setOpaque(false);
        presetBtn.setAlignmentX(0.0f);
        head.add(presetBtn);
        side.add((Component)head, "North");
        side.add((Component)checks, "Center");
        return side;
    }

    private JPanel colPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, 1));
        p.setOpaque(false);
        return p;
    }

    private void stylePreset(JToggleButton btn) {
        btn.setOpaque(true);
        btn.setFont(UiTheme.FONT_BOLD.deriveFont(12.0f));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setMargin(new Insets(4, 10, 4, 10));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        this.updatePresetLook(btn);
        btn.addChangeListener(e -> this.updatePresetLook(btn));
    }

    private void updatePresetLook(JToggleButton btn) {
        if (btn.isSelected()) {
            btn.setBackground(new Color(745302));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(14870768));
            btn.setForeground(UiTheme.TEXT);
        }
    }

    private JCheckBox makeTemplateCheck(ExportDoc doc, Map<ExportDoc, JCheckBox> store, boolean defaultSelected) {
        JCheckBox existing = store.get((Object)doc);
        if (existing != null) {
            return existing;
        }
        JCheckBox cb = new JCheckBox(doc.getLabel(), defaultSelected);
        cb.setOpaque(false);
        cb.setFont(UiTheme.FONT_BOLD.deriveFont(12.0f));
        cb.setMargin(new Insets(0, 2, 0, 2));
        cb.setAlignmentX(0.0f);
        cb.addActionListener(e -> this.onTemplateManualChange());
        store.put(doc, cb);
        return cb;
    }

    private void setDocsSelected(Map<ExportDoc, JCheckBox> store, Set<ExportDoc> docs, boolean selected) {
        for (ExportDoc doc : docs) {
            JCheckBox cb = store.get((Object)doc);
            if (cb != null) {
                cb.setSelected(selected);
            }
        }
    }

    private boolean allDocsSelected(Map<ExportDoc, JCheckBox> store, Set<ExportDoc> docs) {
        for (ExportDoc doc : docs) {
            JCheckBox cb = store.get((Object)doc);
            if (cb == null || !cb.isSelected()) {
                return false;
            }
        }
        return true;
    }

    private boolean anyDocsSelected(Map<ExportDoc, JCheckBox> store, Set<ExportDoc> docs) {
        for (ExportDoc doc : docs) {
            JCheckBox cb = store.get((Object)doc);
            if (cb != null && cb.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private void syncPresetButtonsFromChecks() {
        if (this.syncingTemplates) {
            return;
        }
        boolean full = this.allDocsSelected(this.leftTemplateChecks, ALL_DOCS);
        boolean baoPhatOnly = this.allDocsSelected(this.rightTemplateChecks, BAO_PHAT_DOCS)
            && !this.anyDocsSelected(this.leftTemplateChecks, ALL_DOCS);
        this.syncingTemplates = true;
        try {
            this.presetHopDongBaoPhat.setSelected(full);
            this.presetBaoPhat.setSelected(baoPhatOnly);
            this.updatePresetLook(this.presetHopDongBaoPhat);
            this.updatePresetLook(this.presetBaoPhat);
        }
        finally {
            this.syncingTemplates = false;
        }
    }

    private void onTemplateManualChange() {
        if (this.syncingTemplates) {
            return;
        }
        this.syncPresetButtonsFromChecks();
    }

    private void applyPresetBaoPhat() {
        if (this.syncingTemplates) {
            return;
        }
        this.syncingTemplates = true;
        try {
            boolean on = this.presetBaoPhat.isSelected();
            this.presetHopDongBaoPhat.setSelected(false);
            // Chỉ cột phải. Bật thì tắt hết cột trái — 2 bên không cùng sáng.
            this.setDocsSelected(this.rightTemplateChecks, BAO_PHAT_DOCS, on);
            this.setDocsSelected(this.leftTemplateChecks, ALL_DOCS, false);
            this.updatePresetLook(this.presetBaoPhat);
            this.updatePresetLook(this.presetHopDongBaoPhat);
        }
        finally {
            this.syncingTemplates = false;
        }
    }

    private void applyPresetHopDongBaoPhat() {
        if (this.syncingTemplates) {
            return;
        }
        this.syncingTemplates = true;
        try {
            boolean on = this.presetHopDongBaoPhat.isSelected();
            this.presetBaoPhat.setSelected(false);
            // Chỉ cột trái. Không tick cột phải.
            this.setDocsSelected(this.leftTemplateChecks, ALL_DOCS, on);
            this.setDocsSelected(this.rightTemplateChecks, BAO_PHAT_DOCS, false);
            this.updatePresetLook(this.presetBaoPhat);
            this.updatePresetLook(this.presetHopDongBaoPhat);
        }
        finally {
            this.syncingTemplates = false;
        }
    }

    private void bindEvents() {
        this.table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.syncDetailFromSelection();
            }
        });
        this.detailArea.getDocument().addDocumentListener(MainFrame.simpleListener(this::pushDetailToTable));
        this.tableModel.addTableModelListener(e -> {
            if (e.getType() != 0) {
                return;
            }
            int col = e.getColumn();
            if (col == 4 || col == 5 || col == -1) {
                SwingUtilities.invokeLater(() -> {
                    if (this.table.isEditing() && (this.table.getEditingColumn() == 4 || this.table.getEditingColumn() == 5)) {
                        this.refreshTotalsQuiet();
                    } else {
                        this.refreshTotals();
                    }
                });
            }
            if (col == 1 || col == 2 || col == -1) {
                SwingUtilities.invokeLater(this::resizeRows);
            }
            if (this.hidingZeroQtyProducts && (col == 4 || col == 5 || col == -1)) {
                SwingUtilities.invokeLater(this::applyProductFilter);
            }
        });
        this.partyA.getDocument().addDocumentListener(MainFrame.simpleListener(this::autoFillContractNo));
        if (this.table.getRowCount() > 0) {
            this.table.setRowSelectionInterval(0, 0);
            this.syncDetailFromSelection();
        }
    }

    private void autoFillContractNo() {
        if (this.syncingContractNo) {
            return;
        }
        this.syncingContractNo = true;
        this.contractNo.setText(ContractNumberUtil.fromPartyName(this.partyA.getText()));
        this.syncingContractNo = false;
    }

    private void syncDetailFromSelection() {
        int viewRow = this.table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        int row = this.table.convertRowIndexToModel(viewRow);
        this.syncingDetail = true;
        this.detailArea.setText(String.valueOf(this.tableModel.getValueAt(row, 1)));
        this.detailArea.setCaretPosition(0);
        this.syncingDetail = false;
    }

    private void pushDetailToTable() {
        if (this.syncingDetail) {
            return;
        }
        int viewRow = this.table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        int row = this.table.convertRowIndexToModel(viewRow);
        this.tableModel.setContentDirect(row, this.detailArea.getText());
        this.resizeRows();
    }

    /**
     * Đổi Bắc/Nam: tự lưu data hiện tại trước, rồi chỉ đổi cột đơn giá — không mất nội dung vừa nhập.
     */
    private void switchRegion(boolean toSouth) {
        if (this.southPrices == toSouth) {
            return;
        }
        this.commitDetailAreaToTable();
        try {
            this.persistProductData();
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Kh\u00f4ng l\u01b0u \u0111\u01b0\u1ee3c data tr\u01b0\u1edbc khi \u0111\u1ed5i khu v\u1ef1c:\n" + ex.getMessage()
                            + "\n\n\u0110\u01b0\u1eddng d\u1eabn: " + AppPaths.savedProducts(),
                    "L\u1ed7i l\u01b0u data", 0);
            // khôi phục radio cho khớp trạng thái cũ
            this.radioSouth.setSelected(this.southPrices);
            this.radioNorth.setSelected(!this.southPrices);
            return;
        }
        this.southPrices = toSouth;
        this.applyRegionPricesOnly();
        this.refreshTotals();
    }

    /** Chỉ cập nhật cột đơn giá theo vùng; giữ nguyên nội dung / mã / ĐVT trên bảng. */
    private void applyRegionPricesOnly() {
        SavedData data = this.dataStore.load();
        List<Integer> prices = null;
        if (data != null) {
            prices = this.southPrices ? data.getPricesSouth() : data.getPricesNorth();
        }
        int i = 0;
        while (i < ROW_COUNT) {
            int price;
            if (prices != null && i < prices.size() && prices.get(i) != null) {
                price = prices.get(i);
            } else {
                int[] defaults = this.southPrices ? DEFAULT_SOUTH : DEFAULT_NORTH;
                price = i < defaults.length ? defaults[i] : 0;
            }
            this.tableModel.setValueAt(NumberUtil.formatDot(price), i, 5);
            ++i;
        }
    }

    private void commitDetailAreaToTable() {
        if (this.syncingDetail) {
            return;
        }
        int viewRow = this.table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        int row = this.table.convertRowIndexToModel(viewRow);
        this.tableModel.setContentDirect(row, this.detailArea.getText());
    }

    private void refreshTotals() {
        long grand = this.recomputeAmounts(true);
        this.grandTotalTop.setText(NumberUtil.formatDot(grand));
    }

    private void refreshTotalsQuiet() {
        long grand = this.recomputeAmounts(false);
        this.grandTotalTop.setText(NumberUtil.formatDot(grand));
        this.table.repaint();
    }

    private long recomputeAmounts(boolean fireEvents) {
        long grand = 0L;
        int i = 0;
        while (i < ROW_COUNT) {
            long qty = NumberUtil.parseAmount(String.valueOf(this.tableModel.getValueAt(i, 4)));
            long price = NumberUtil.parseAmount(String.valueOf(this.tableModel.getValueAt(i, 5)));
            long amount = qty * price;
            String text = qty > 0L ? NumberUtil.formatDot(amount) : "";
            if (fireEvents) {
                this.tableModel.setAmount(i, text);
            } else {
                this.tableModel.setAmountQuiet(i, text);
            }
            if (qty > 0L) {
                grand += amount;
            }
            ++i;
        }
        return grand;
    }

    private void installProductWheelScroll(JScrollPane scroll) {
        scroll.setWheelScrollingEnabled(false);
        JScrollBar vbar = scroll.getVerticalScrollBar();
        vbar.setUnitIncrement(1);
        vbar.setBlockIncrement(1);
        MouseWheelListener wheel = this::onProductMouseWheel;
        this.table.addMouseWheelListener(wheel);
        this.table.getTableHeader().addMouseWheelListener(wheel);
        scroll.getViewport().addMouseWheelListener(wheel);
        scroll.addMouseWheelListener(wheel);
        vbar.addMouseWheelListener(wheel);
        vbar.addAdjustmentListener(e -> {
            long now = System.currentTimeMillis();
            if (this.wheelProgrammaticScroll || now < this.wheelIgnoreAdjustUntilMs) {
                return;
            }
            this.wheelAnchorValid = false;
            this.wheelPreciseBudget = 0.0;
        });
    }

    private void onProductMouseWheel(MouseWheelEvent e) {
        e.consume();
        if (this.tableScroll == null || this.table.getRowCount() <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.wheelLastStepMs < 140L) {
            return;
        }
        int notch = e.getWheelRotation();
        if (notch != 0) {
            this.wheelLastStepMs = now;
            this.stepProductScroll(notch > 0 ? 1 : -1);
            return;
        }
        double precise = e.getPreciseWheelRotation();
        if (Math.abs(precise) < 0.01) {
            return;
        }
        this.wheelPreciseBudget += precise;
        if (this.wheelPreciseBudget >= 0.85) {
            this.wheelPreciseBudget = 0.0;
            this.wheelLastStepMs = now;
            this.stepProductScroll(1);
        } else if (this.wheelPreciseBudget <= -0.85) {
            this.wheelPreciseBudget = 0.0;
            this.wheelLastStepMs = now;
            this.stepProductScroll(-1);
        }
    }

    private void stepProductScroll(int dir) {
        if (dir == 0) {
            return;
        }
        if (!this.wheelAnchorValid) {
            this.wheelAnchorRow = this.detectTopProductRow();
            this.wheelAnchorValid = true;
        }
        int target = Math.max(0, Math.min(this.table.getRowCount() - 1, this.wheelAnchorRow + dir));
        this.scrollProductRowToTop(target);
        this.wheelAnchorRow = target;
        this.wheelAnchorValid = true;
    }

    private int detectTopProductRow() {
        int y = this.tableScroll.getViewport().getViewPosition().y;
        int n = this.table.getRowCount();
        if (n <= 0) {
            return 0;
        }
        int best = 0;
        int i = 0;
        while (i < n) {
            Rectangle r = this.table.getCellRect(i, 0, true);
            if (r != null && r.height > 0) {
                if (y < r.y + r.height / 2) {
                    return i;
                }
                best = i;
            }
            ++i;
        }
        return best;
    }

    private void scrollProductRowToTop(int row) {
        if (row < 0 || row >= this.table.getRowCount()) {
            return;
        }
        Rectangle cell = this.table.getCellRect(row, 0, true);
        if (cell == null) {
            return;
        }
        JViewport vp = this.tableScroll.getViewport();
        int maxY = Math.max(0, this.table.getHeight() - vp.getExtentSize().height);
        int y = Math.max(0, Math.min(cell.y, maxY));
        this.wheelProgrammaticScroll = true;
        try {
            this.tableScroll.getVerticalScrollBar().setValue(y);
            vp.setViewPosition(new Point(vp.getViewPosition().x, y));
        }
        finally {
            this.wheelProgrammaticScroll = false;
            this.wheelIgnoreAdjustUntilMs = System.currentTimeMillis() + 80L;
        }
    }

    private void toggleZeroQtyProducts() {
        this.hidingZeroQtyProducts = !this.hidingZeroQtyProducts;
        if (this.btnToggleZeroProducts != null) {
            if (this.hidingZeroQtyProducts) {
                this.btnToggleZeroProducts.setText("B\u1ece L\u1eccC");
                this.btnToggleZeroProducts.setBackground(UiTheme.BTN_TEAL);
                this.btnToggleZeroProducts.setToolTipText("\u0110ang \u1ea9n SP thi\u1ebfu s\u1ed1 l\u01b0\u1ee3ng ho\u1eb7c \u0111\u01a1n gi\u00e1. B\u1ea5m \u0111\u1ec3 hi\u1ec7n l\u1ea1i t\u1ea5t c\u1ea3.");
            } else {
                this.btnToggleZeroProducts.setText("L\u1eccC");
                this.btnToggleZeroProducts.setBackground(UiTheme.BTN_TEAL);
                this.btnToggleZeroProducts.setToolTipText("\u1ea8n s\u1ea3n ph\u1ea9m thi\u1ebfu s\u1ed1 l\u01b0\u1ee3ng ho\u1eb7c \u0111\u01a1n gi\u00e1. B\u1ea5m l\u1ea1i \u0111\u1ec3 hi\u1ec7n t\u1ea5t c\u1ea3.");
            }
        }
        this.applyProductFilter();
        this.resizeRows();
        this.table.revalidate();
        this.table.repaint();
    }

    private void applyProductFilter() {
        if (!this.hidingZeroQtyProducts) {
            this.productSorter.setRowFilter(null);
            this.wheelAnchorValid = false;
            return;
        }
        this.productSorter.setRowFilter(new RowFilter<ProductTableModel, Integer>(){

            @Override
            public boolean include(RowFilter.Entry<? extends ProductTableModel, ? extends Integer> entry) {
                long qty = NumberUtil.parseAmount(String.valueOf(entry.getValue(4)));
                long price = NumberUtil.parseAmount(String.valueOf(entry.getValue(5)));
                return qty > 0L && price > 0L;
            }
        });
        this.wheelAnchorValid = false;
    }

    private void resizeRows() {
        int contentWidth = this.table.getColumnModel().getColumn(1).getWidth();
        if (contentWidth <= 0) {
            contentWidth = this.table.getColumnModel().getColumn(1).getPreferredWidth();
        }
        FontMetrics codeFm = this.table.getFontMetrics(UiTheme.FONT_BOLD.deriveFont(18.0f));
        int codeLineH = Math.max(20, codeFm.getHeight());
        boolean heightChanged = false;
        int viewRow = 0;
        while (viewRow < this.table.getRowCount()) {
            int modelRow = this.table.convertRowIndexToModel(viewRow);
            String text = String.valueOf(this.tableModel.getValueAt(modelRow, 1));
            if (text == null) {
                text = "";
            }
            text = text.replace("\r\n", "\n").replace('\r', '\n');
            String code = MainFrame.normalizeCodeFormat(String.valueOf(this.tableModel.getValueAt(modelRow, 2)));
            int h;
            if (text.isBlank() && code.isBlank()) {
                h = MIN_ROW_HEIGHT;
            } else {
                int contentH = MIN_ROW_HEIGHT;
                if (!text.isBlank()) {
                    Component c = this.contentHeightProbe.getTableCellRendererComponent(this.table, text, false, false, viewRow, 1);
                    c.setSize(contentWidth, Short.MAX_VALUE);
                    contentH = Math.max(MIN_ROW_HEIGHT, c.getPreferredSize().height + 2);
                }
                int codeH = MIN_ROW_HEIGHT;
                if (!code.isBlank()) {
                    int codeLines = code.split("\n", -1).length;
                    codeH = Math.max(MIN_ROW_HEIGHT, codeLines * codeLineH + 10);
                }
                h = Math.max(contentH, codeH);
            }
            if (this.table.getRowHeight(viewRow) != h) {
                this.table.setRowHeight(viewRow, h);
                heightChanged = true;
            }
            ++viewRow;
        }
        if (heightChanged) {
            this.wheelAnchorValid = false;
        }
    }

    static String normalizeCodeFormat(String raw) {
        String brand;
        String type;
        if (raw == null) {
            return "";
        }
        String t = raw.replace("\r\n", "\n").replace('\r', '\n').trim();
        if (t.isEmpty()) {
            return "";
        }
        if (t.contains("\n")) {
            String[] parts = t.split("\n", 2);
            type = parts[0].trim();
            brand = parts.length > 1 ? parts[1].trim() : "";
        } else if (t.contains("\u2013") || t.contains("\u2014") || t.contains(" - ")) {
            String[] parts = t.split("\\s*[\u2013\u2014]\\s*|\\s+-\\s+", 2);
            type = parts[0].trim();
            brand = parts.length > 1 ? parts[1].trim() : "";
        } else {
            int lastSpace = t.lastIndexOf(32);
            if (lastSpace > 0) {
                String maybeBrand = t.substring(lastSpace + 1).trim();
                if (maybeBrand.matches("\\p{L}{2,}")) {
                    type = t.substring(0, lastSpace).trim();
                    brand = maybeBrand;
                } else {
                    type = t;
                    brand = "";
                }
            } else {
                type = t;
                brand = "";
            }
        }
        type = type.toUpperCase(Locale.ROOT);
        brand = BrandColorStore.normalize(brand);
        if (brand.isEmpty()) {
            return type;
        }
        return type + "\n" + brand;
    }

    static String brandOf(String code) {
        String n = MainFrame.normalizeCodeFormat(code);
        if (!n.contains("\n")) {
            return "";
        }
        return n.substring(n.indexOf(10) + 1).trim();
    }

    static String typeOf(String code) {
        String n = MainFrame.normalizeCodeFormat(code);
        int i = n.indexOf(10);
        return i < 0 ? n : n.substring(0, i);
    }

    private void chooseFolder(boolean template) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(1);
        chooser.setDialogTitle(template ? "Ch\u1ecdn th\u01b0 m\u1ee5c g\u1ed1c (file m\u1eabu .docx \u2014 d\u00f9ng folder HOP_DONG_MAU)" : "Ch\u1ecdn th\u01b0 m\u1ee5c xu\u1ea5t");
        if (chooser.showOpenDialog(this) == 0) {
            Path path = chooser.getSelectedFile().toPath();
            if (template) {
                this.inputFolder = path;
            } else {
                this.outputFolder = path;
            }
            this.folderLabel.setText("Th\u01b0 m\u1ee5c g\u1ed1c: " + String.valueOf(this.inputFolder == null ? "(ch\u01b0a ch\u1ecdn)" : this.inputFolder) + "  |  Th\u01b0 m\u1ee5c xu\u1ea5t: " + String.valueOf(this.outputFolder == null ? "(ch\u01b0a ch\u1ecdn)" : this.outputFolder));
        }
    }

    private Set<ExportDoc> selectedTemplates() {
        EnumSet<ExportDoc> set = EnumSet.noneOf(ExportDoc.class);
        for (Map.Entry<ExportDoc, JCheckBox> e : this.leftTemplateChecks.entrySet()) {
            if (e.getValue().isSelected()) {
                set.add(e.getKey());
            }
        }
        for (Map.Entry<ExportDoc, JCheckBox> e : this.rightTemplateChecks.entrySet()) {
            if (e.getValue().isSelected()) {
                set.add(e.getKey());
            }
        }
        return set;
    }

    private void completeAll() {
        try {
            List<WordExportService.LineItem> rows = this.collectRows();
            ContractInfo info = this.collectInfo();
            Set<ExportDoc> types = this.selectedTemplates();
            int exported = this.wordService.export(this.inputFolder, this.outputFolder, rows, info, types);
            try {
                this.wordService.processCungUngFiles(this.outputFolder);
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.wordService.trimTrailingEmptyParagraphs(this.outputFolder);
            List<ExportDoc> missing = this.wordService.findUnmatchedTypes(this.inputFolder, types);
            StringBuilder msg = new StringBuilder();
            msg.append("T\u1ea0O H\u1ee2P \u0110\u1ed2NG TH\u00c0NH C\u00d4NG\n");
            msg.append("\u0110\u00e3 xu\u1ea5t ").append(exported).append("/").append(types.size()).append(" file.");
            if (!missing.isEmpty()) {
                msg.append("\n\nThi\u1ebfu file m\u1eabu trong th\u01b0 m\u1ee5c g\u1ed1c:");
                for (ExportDoc doc : missing) {
                    msg.append("\n- ").append(doc.getLabel());
                }
                msg.append("\n\nTh\u00eam file .docx c\u00f3 t\u00ean ch\u1ee9a \"B\u00e1o Ph\u00e1t\"\n(vd: 8. B\u00e1o Ph\u00e1t.docx) v\u00e0o th\u01b0 m\u1ee5c g\u1ed1c.");
            }
            JOptionPane.showMessageDialog(this, msg.toString(), "HO\u00c0N TH\u00c0NH", missing.isEmpty() ? 1 : 2);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "L\u1ed7i ho\u00e0n th\u00e0nh", 0);
        }
    }

    private List<WordExportService.LineItem> collectRows() {
        ArrayList<WordExportService.LineItem> rows = new ArrayList<WordExportService.LineItem>();
        int i = 0;
        while (i < ROW_COUNT) {
            long qty = NumberUtil.parseAmount(String.valueOf(this.tableModel.getValueAt(i, 4)));
            long price = NumberUtil.parseAmount(String.valueOf(this.tableModel.getValueAt(i, 5)));
            if (qty > 0L) {
                String unit = String.valueOf(this.tableModel.getValueAt(i, 3)).trim();
                String content = String.valueOf(this.tableModel.getValueAt(i, 1)).replace("\r\n", "\n").replace('\r', '\n').replaceAll("\\n+$", "");
                String code = MainFrame.normalizeCodeFormat(String.valueOf(this.tableModel.getValueAt(i, 2)));
                String brand = MainFrame.brandOf(code);
                if (brand.isBlank()) {
                    brand = code.replace('\n', ' ').trim();
                }
                boolean drug = "TEST".equalsIgnoreCase(unit);
                rows.add(new WordExportService.LineItem(content, unit.isEmpty() ? " " : unit, NumberUtil.formatDot(qty), NumberUtil.formatDot(price), NumberUtil.formatDot(qty * price), brand, drug));
            }
            ++i;
        }
        return rows;
    }

    private void searchCustomer() {
        String mst = this.taxCode.getText().trim();
        if (mst.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nh\u1eadp m\u00e3 s\u1ed1 thu\u1ebf tr\u01b0\u1edbc khi t\u00ecm.", "Thi\u1ebfu MST", 2);
            return;
        }
        CustomerRecord rec = this.customerStore.findByTaxCode(mst);
        if (rec == null) {
            JOptionPane.showMessageDialog(this, "Kh\u00f4ng c\u00f3 l\u1ecbch s\u1eed. H\u00e3y nh\u1eadp theo quy tr\u00ecnh r\u1ed3i b\u1ea5m \"SAVE CUSTOMER\".", "Kh\u00f4ng t\u00ecm th\u1ea5y", 1);
            return;
        }
        this.applyCustomer(rec);
        JOptionPane.showMessageDialog(this, "\u0110\u00e3 \u0111\u1ed5 d\u1eef li\u1ec7u kh\u00e1ch h\u00e0ng.", "T\u00ecm th\u1ea5y", 1);
    }

    private void applyCustomer(CustomerRecord rec) {
        this.syncingContractNo = true;
        this.taxCode.setText(MainFrame.nullToEmpty(rec.getTaxCode()));
        this.contractNo.setText(MainFrame.nullToEmpty(rec.getContractNo()));
        this.partyA.setText(MainFrame.nullToEmpty(rec.getPartyA()));
        this.address.setText(MainFrame.nullToEmpty(rec.getAddress()));
        this.phone.setText(MainFrame.nullToEmpty(rec.getPhone()));
        this.account1.setText(MainFrame.nullToEmpty(rec.getAccount1()));
        this.account2.setText(MainFrame.nullToEmpty(rec.getAccount2()));
        this.budgetCode.setText(MainFrame.nullToEmpty(rec.getBudgetCode()));
        this.rep1.setText(MainFrame.formatRepresentative(MainFrame.nullToEmpty(rec.getRepresentative1())));
        this.rep2.setText(MainFrame.formatRepresentative(MainFrame.nullToEmpty(rec.getRepresentative2())));
        this.title1.setText(MainFrame.nullToEmpty(rec.getTitle1()));
        this.title2.setText(MainFrame.nullToEmpty(rec.getTitle2()));
        this.partyC.setText(MainFrame.nullToEmpty(rec.getPartyC()));
        this.addressC.setText(MainFrame.nullToEmpty(rec.getAddressC()));
        this.phoneC.setText(MainFrame.nullToEmpty(rec.getPhoneC()));
        this.accountC.setText(MainFrame.nullToEmpty(rec.getAccountC()));
        this.accountC2.setText(MainFrame.nullToEmpty(rec.getAccountC2()));
        this.taxCodeC.setText(MainFrame.nullToEmpty(rec.getTaxCodeC()));
        this.budgetCodeC.setText(MainFrame.nullToEmpty(rec.getBudgetCodeC()));
        this.repC.setText(MainFrame.formatRepresentative(MainFrame.nullToEmpty(rec.getRepresentativeC())));
        this.repC2.setText(MainFrame.formatRepresentative(MainFrame.nullToEmpty(rec.getRepresentativeC2())));
        this.titleC.setText(MainFrame.nullToEmpty(rec.getTitleC()));
        this.titleC2.setText(MainFrame.nullToEmpty(rec.getTitleC2()));
        if (rec.isThreeParty()) {
            this.tabThreeParty.setSelected(true);
        } else {
            this.tabTwoParty.setSelected(true);
        }
        this.tabSideA.setSelected(true);
        this.applyPartyMode();
        if (rec.getYear() != null && !rec.getYear().isBlank()) {
            this.year.setText(rec.getYear());
        }
        this.syncingContractNo = false;
    }

    private void saveCustomerData() {
        try {
            if (this.taxCode.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("C\u1ea7n c\u00f3 m\u00e3 s\u1ed1 thu\u1ebf \u0111\u1ec3 l\u01b0u kh\u00e1ch h\u00e0ng.");
            }
            CustomerRecord rec = new CustomerRecord();
            rec.setTaxCode(this.taxCode.getText());
            rec.setContractNo(this.contractNo.getText());
            rec.setPartyA(this.partyA.getText());
            rec.setAddress(this.address.getText());
            rec.setPhone(this.phone.getText());
            rec.setAccount1(this.account1.getText());
            rec.setAccount2(this.account2.getText());
            rec.setBudgetCode(this.budgetCode.getText());
            rec.setRepresentative1(this.rep1.getText());
            rec.setRepresentative2(this.rep2.getText());
            rec.setTitle1(this.title1.getText());
            rec.setTitle2(this.title2.getText());
            rec.setThreeParty(this.tabThreeParty.isSelected());
            rec.setPartyC(this.partyC.getText());
            rec.setAddressC(this.addressC.getText());
            rec.setPhoneC(this.phoneC.getText());
            rec.setAccountC(this.accountC.getText());
            rec.setAccountC2(this.accountC2.getText());
            rec.setTaxCodeC(this.taxCodeC.getText());
            rec.setBudgetCodeC(this.budgetCodeC.getText());
            rec.setRepresentativeC(this.repC.getText());
            rec.setRepresentativeC2(this.repC2.getText());
            rec.setTitleC(this.titleC.getText());
            rec.setTitleC2(this.titleC2.getText());
            rec.setYear(this.year.getText());
            rec.setSouthPrices(this.southPrices);
            rec.setContent(new ArrayList<String>());
            rec.setCodes(new ArrayList<String>());
            rec.setUnit(new ArrayList<String>());
            rec.setQuantities(new ArrayList<String>());
            rec.setUnitPrices(new ArrayList<String>());
            this.customerStore.save(rec);
            Path path = AppPaths.customerHistory();
            String where = AppPaths.usingSharedDrive()
                    ? "\u0110\u00e3 l\u01b0u \u1ed5 data chung c\u00f4ng ty:\n" + path
                    : "Kh\u00f4ng ghi \u0111\u01b0\u1ee3c \u1ed5 F: \u2014 \u0111\u00e3 l\u01b0u t\u1ea1m local:\n" + path
                            + "\n\nH\u00e3y map \u1ed5 F:\\TOOL - HOP DONG\\ContractToolData \u0111\u1ec3 c\u1ea3 c\u00f4ng ty d\u00f9ng chung.";
            JOptionPane.showMessageDialog(this, "\u0110\u00c3 L\u01afU TH\u00c0NH C\u00d4NG\n\n" + where, "L\u01afU D\u1eee LI\u1ec6U KH\u00c1CH H\u00c0NG", 1);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage() + "\n\n" + AppPaths.customerHistory(), "L\u1ed7i l\u01b0u kh\u00e1ch h\u00e0ng", 0);
        }
    }

    private void saveProductData() {
        try {
            this.commitDetailAreaToTable();
            Path path = this.persistProductData();
            String where = AppPaths.usingSharedDrive()
                    ? "\u0110\u00e3 l\u01b0u \u1ed5 data chung c\u00f4ng ty:\n" + path
                    : "Kh\u00f4ng ghi \u0111\u01b0\u1ee3c \u1ed5 F: \u2014 \u0111\u00e3 l\u01b0u t\u1ea1m local:\n" + path
                            + "\n\nH\u00e3y map \u1ed5 F:\\TOOL - HOP DONG\\ContractToolData \u0111\u1ec3 c\u1ea3 c\u00f4ng ty d\u00f9ng chung.";
            JOptionPane.showMessageDialog(this, "\u0110\u00c3 L\u01afU TH\u00c0NH C\u00d4NG\n\n" + where, "L\u01afU D\u1eee LI\u1ec6U H\u00c0NG H\u00d3A", 1);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage() + "\n\n" + AppPaths.savedProducts(), "L\u1ed7i l\u01b0u h\u00e0ng h\u00f3a", 0);
        }
    }

    /** Lưu nội dung + mã + ĐVT + giá Bắc/Nam vào 1 file JSON dùng chung. */
    private Path persistProductData() throws Exception {
        SavedData existing = this.dataStore.load();
        List<Integer> oldSouth = existing != null && existing.getPricesSouth() != null ? existing.getPricesSouth() : MainFrame.toList(DEFAULT_SOUTH);
        List<Integer> oldNorth = existing != null && existing.getPricesNorth() != null ? existing.getPricesNorth() : MainFrame.toList(DEFAULT_NORTH);
        SavedData data = new SavedData();
        ArrayList<Integer> south = new ArrayList<Integer>();
        ArrayList<Integer> north = new ArrayList<Integer>();
        ArrayList<String> content = new ArrayList<String>();
        ArrayList<String> units = new ArrayList<String>();
        ArrayList<String> codes = new ArrayList<String>();
        int i = 0;
        while (i < ROW_COUNT) {
            int price = (int)NumberUtil.parseAmount(String.valueOf(this.tableModel.getValueAt(i, 5)));
            int defS = i < DEFAULT_SOUTH.length ? DEFAULT_SOUTH[i] : 0;
            int defN = i < DEFAULT_NORTH.length ? DEFAULT_NORTH[i] : 0;
            if (this.southPrices) {
                south.add(price);
                north.add(i < oldNorth.size() && oldNorth.get(i) != null ? oldNorth.get(i) : defN);
            } else {
                north.add(price);
                south.add(i < oldSouth.size() && oldSouth.get(i) != null ? oldSouth.get(i) : defS);
            }
            content.add(String.valueOf(this.tableModel.getValueAt(i, 1)));
            codes.add(String.valueOf(this.tableModel.getValueAt(i, 2)));
            units.add(String.valueOf(this.tableModel.getValueAt(i, 3)));
            ++i;
        }
        data.setPricesSouth(south);
        data.setPricesNorth(north);
        data.setContent(content);
        data.setUnit(units);
        data.setCodes(codes);
        this.dataStore.save(data);
        return AppPaths.savedProducts();
    }

    private void loadSavedData() {
        SavedData data = this.dataStore.load();
        if (data == null) {
            return;
        }
        List<Integer> prices = this.southPrices ? data.getPricesSouth() : data.getPricesNorth();
        int n = Math.min(ROW_COUNT, Math.max(prices == null ? 0 : prices.size(), data.getContent() == null ? 0 : data.getContent().size()));
        int i = 0;
        while (i < n) {
            if (prices != null && i < prices.size() && prices.get(i) != null) {
                this.tableModel.setValueAt(NumberUtil.formatDot(prices.get(i).intValue()), i, 5);
            }
            if (data.getContent() != null && i < data.getContent().size()) {
                this.tableModel.setValueAt(data.getContent().get(i), i, 1);
            }
            if (data.getCodes() != null && i < data.getCodes().size() && data.getCodes().get(i) != null && !data.getCodes().get(i).isBlank()) {
                this.tableModel.setValueAt(MainFrame.normalizeCodeFormat(data.getCodes().get(i)), i, 2);
            }
            if (data.getUnit() != null && i < data.getUnit().size()) {
                this.tableModel.setValueAt(data.getUnit().get(i), i, 3);
            }
            ++i;
        }
        this.syncDetailFromSelection();
        this.resizeRows();
    }

    private ContractInfo collectInfo() {
        ContractInfo info = new ContractInfo();
        info.setContractNo(this.contractNo.getText());
        info.setPartyA(this.partyA.getText());
        info.setAddress(this.address.getText());
        info.setPhone(this.phone.getText());
        info.setAccount1(this.account1.getText());
        info.setAccount2(this.account2.getText());
        info.setTaxCode(this.taxCode.getText());
        info.setBudgetCode(this.budgetCode.getText());
        info.setRepresentative1(this.rep1.getText());
        info.setRepresentative2(this.rep2.getText());
        info.setTitle1(this.title1.getText());
        info.setTitle2(this.title2.getText());
        boolean three = this.tabThreeParty.isSelected();
        info.setThreeParty(three);
        info.setPartyC(three ? this.partyC.getText() : "");
        info.setAddressC(three ? this.addressC.getText() : "");
        info.setPhoneC(three ? this.phoneC.getText() : "");
        info.setAccountC(three ? this.accountC.getText() : "");
        info.setAccountC2(three ? this.accountC2.getText() : "");
        info.setTaxCodeC(three ? this.taxCodeC.getText() : "");
        info.setBudgetCodeC(three ? this.budgetCodeC.getText() : "");
        info.setRepresentativeC(three ? this.repC.getText() : "");
        info.setRepresentativeC2(three ? this.repC2.getText() : "");
        info.setTitleC(three ? this.titleC.getText() : "");
        info.setTitleC2(three ? this.titleC2.getText() : "");
        info.setYear(this.year.getText());
        return info;
    }

    private static List<Integer> toList(int[] arr) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        int[] nArray = arr;
        int n = arr.length;
        int n2 = 0;
        while (n2 < n) {
            int v = nArray[n2];
            list.add(v);
            ++n2;
        }
        return list;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static DocumentListener simpleListener(final Runnable action) {
        return new DocumentListener(){

            @Override
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        };
    }

    private static void addUppercase(JTextField ... fields) {
        DocumentFilter filter = MainFrame.uppercaseFilter();
        JTextField[] jTextFieldArray = fields;
        int n = fields.length;
        int n2 = 0;
        while (n2 < n) {
            JTextField field = jTextFieldArray[n2];
            ((AbstractDocument)field.getDocument()).setDocumentFilter(filter);
            ++n2;
        }
    }

    private static void addUppercaseDoc(JTextArea area) {
        ((AbstractDocument)area.getDocument()).setDocumentFilter(MainFrame.uppercaseFilter());
    }

    private static DocumentFilter uppercaseFilter() {
        return new DocumentFilter(){

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string != null) {
                    string = string.toUpperCase(Locale.ROOT);
                }
                super.insertString(fb, offset, string, attr);
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null) {
                    text = text.toUpperCase(Locale.ROOT);
                }
                super.replace(fb, offset, length, text, attrs);
            }
        };
    }

    private static void addDigitsOnly(JTextField field, final int maxLen) {
        DocumentFilter filter = new DocumentFilter(){

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null || !string.chars().allMatch(Character::isDigit)) {
                    return;
                }
                if (fb.getDocument().getLength() + string.length() > maxLen) {
                    return;
                }
                super.insertString(fb, offset, string, attr);
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null && !text.chars().allMatch(Character::isDigit)) {
                    return;
                }
                int newLen = fb.getDocument().getLength() - length + (text == null ? 0 : text.length());
                if (newLen > maxLen) {
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        };
        ((AbstractDocument)field.getDocument()).setDocumentFilter(filter);
    }

    private static void addRepFormat(JTextField ... fields) {
        DocumentFilter filter = new DocumentFilter(){
            private boolean filtering;

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                this.replace(fb, offset, 0, string, attr);
            }

            @Override
            public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
                this.replace(fb, offset, length, "", null);
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (this.filtering) {
                    super.replace(fb, offset, length, text, attrs);
                    return;
                }
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String next = cur.substring(0, offset) + (text == null ? "" : text) + cur.substring(offset + length);
                String formatted = MainFrame.formatRepresentative(next);
                this.filtering = true;
                try {
                    super.replace(fb, 0, fb.getDocument().getLength(), formatted, attrs);
                }
                finally {
                    this.filtering = false;
                }
            }
        };
        JTextField[] jTextFieldArray = fields;
        int n = fields.length;
        int n2 = 0;
        while (n2 < n) {
            JTextField field = jTextFieldArray[n2];
            ((AbstractDocument)field.getDocument()).setDocumentFilter(filter);
            ++n2;
        }
    }

    static String formatRepresentative(String raw) {
        if (raw == null) {
            return "";
        }
        boolean keepTrailingSpace = raw.endsWith(" ");
        String t = raw.trim().replaceAll("\\s+", " ");
        if (t.isEmpty()) {
            return "";
        }
        String lower = t.toLowerCase(Locale.ROOT);
        if (lower.startsWith("\u00f4ng") && (lower.length() == 3 || lower.charAt(3) == ' ')) {
            String rest;
            String string = rest = t.length() > 3 ? t.substring(3).trim() : "";
            if (rest.isEmpty()) {
                return keepTrailingSpace || lower.startsWith("\u00f4ng ") ? "\u00f4ng " : "\u00f4ng";
            }
            String out = "\u00f4ng " + rest.toUpperCase(Locale.ROOT);
            return keepTrailingSpace ? out + " " : out;
        }
        if (lower.startsWith("b\u00e0") && (lower.length() == 2 || lower.charAt(2) == ' ')) {
            String rest;
            String string = rest = t.length() > 2 ? t.substring(2).trim() : "";
            if (rest.isEmpty()) {
                return keepTrailingSpace || lower.startsWith("b\u00e0 ") ? "b\u00e0 " : "b\u00e0";
            }
            String out = "b\u00e0 " + rest.toUpperCase(Locale.ROOT);
            return keepTrailingSpace ? out + " " : out;
        }
        return t.toUpperCase(Locale.ROOT);
    }

    private static final class AmountCellEditor
    extends DefaultCellEditor {
        private JTable editingTable;
        private int editingRow = -1;
        private int editingColumn = -1;

        AmountCellEditor() {
            super(new JTextField());
            JTextField field = (JTextField)this.getComponent();
            field.setHorizontalAlignment(SwingConstants.CENTER);
            field.setFont(UiTheme.FONT_SIZE20);
            this.setClickCountToStart(1);
            ((AbstractDocument)field.getDocument()).setDocumentFilter(new DocumentFilter(){

                @Override
                public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                    if (string == null) {
                        return;
                    }
                    string = string.replaceAll("[^0-9]", "");
                    if (string.isEmpty()) {
                        return;
                    }
                    super.insertString(fb, offset, string, attr);
                }

                @Override
                public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    if (text != null) {
                        text = text.replaceAll("[^0-9]", "");
                    }
                    // null/empty replace vẫn cho phép (xóa / chọn hết rồi gõ số mới)
                    super.replace(fb, offset, length, text, attrs);
                }
            });
            // Enter: xuống đúng cột dòng sau và MỞ sẵn editor — tránh mất số đầu (surrendersFocusOnKeystroke).
            field.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "amount-enter-next");
            field.getActionMap().put("amount-enter-next", new AbstractAction(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    final JTable table = AmountCellEditor.this.editingTable;
                    final int row = AmountCellEditor.this.editingRow;
                    final int col = AmountCellEditor.this.editingColumn;
                    if (table == null || row < 0 || col < 0) {
                        AmountCellEditor.this.stopCellEditing();
                        return;
                    }
                    if (!AmountCellEditor.this.stopCellEditing()) {
                        return;
                    }
                    SwingUtilities.invokeLater(() -> AmountCellEditor.openAmountCell(table, row + 1, col));
                }
            });
        }

        private static void openAmountCell(JTable table, int viewRow, int viewCol) {
            if (table == null || viewRow < 0 || viewRow >= table.getRowCount()) {
                return;
            }
            if (viewCol < 0 || viewCol >= table.getColumnCount() || !table.isCellEditable(viewRow, viewCol)) {
                return;
            }
            table.changeSelection(viewRow, viewCol, false, false);
            table.scrollRectToVisible(table.getCellRect(viewRow, viewCol, true));
            if (table.editCellAt(viewRow, viewCol)) {
                Component ed = table.getEditorComponent();
                if (ed instanceof JTextField) {
                    JTextField tf = (JTextField)ed;
                    tf.requestFocusInWindow();
                    tf.selectAll();
                } else if (ed != null) {
                    ed.requestFocusInWindow();
                }
            }
        }

        @Override
        public Object getCellEditorValue() {
            JTextField field = (JTextField)this.getComponent();
            String raw = field.getText();
            if (raw == null || raw.isBlank()) {
                return "0";
            }
            return NumberUtil.formatTyping(raw);
        }

        @Override
        public Component getTableCellEditorComponent(JTable tbl, Object value, boolean isSelected, int row, int column) {
            this.editingTable = tbl;
            this.editingRow = row;
            this.editingColumn = column;
            JTextField field = (JTextField)this.getComponent();
            long n = NumberUtil.parseAmount(value == null ? "" : String.valueOf(value));
            field.setText(n == 0L ? "" : String.valueOf(n));
            // selectAll ngay (không invokeLater) — tránh đua với phím số đầu tiên
            field.selectAll();
            return field;
        }
    }

    private static final class ContentCellEditor
    extends AbstractCellEditor
    implements TableCellEditor {
        private final JTextArea area = new JTextArea();
        private JTable editingTable;
        private int editingRow = -1;
        private int editingColumn = -1;

        ContentCellEditor() {
            this.area.setFont(UiTheme.FONT);
            this.area.setLineWrap(true);
            this.area.setWrapStyleWord(true);
            this.area.setBorder(BorderFactory.createLineBorder(UiTheme.BTN_TEAL, 2));
            MainFrame.bindEditorTabKeys(this.area, this, () -> this.editingTable, () -> this.editingRow, () -> this.editingColumn);
            this.area.addKeyListener(new KeyAdapter(){

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                        ContentCellEditor.this.stopCellEditing();
                        e.consume();
                    }
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            String t = this.area.getText();
            return t == null ? "" : t.replace("\r\n", "\n").replace('\r', '\n');
        }

        @Override
        public Component getTableCellEditorComponent(JTable tbl, Object value, boolean isSelected, int row, int column) {
            this.editingTable = tbl;
            this.editingRow = row;
            this.editingColumn = column;
            String text = value == null ? "" : String.valueOf(value).replace("\r\n", "\n").replace('\r', '\n');
            this.area.setText(text);
            this.area.setCaretPosition(text.length());
            return this.area;
        }
    }

    private static final class UppercaseCellEditor
    extends DefaultCellEditor {
        UppercaseCellEditor() {
            super(new JTextField());
            JTextField field = (JTextField)this.getComponent();
            field.setHorizontalAlignment(0);
            field.setFont(UiTheme.FONT_SIZE20);
            field.setBorder(BorderFactory.createLineBorder(UiTheme.BTN_TEAL, 2));
            MainFrame.addUppercase(field);
        }

        @Override
        public Component getTableCellEditorComponent(JTable tbl, Object value, boolean isSelected, int row, int column) {
            String text = value == null ? "" : String.valueOf(value).toUpperCase(Locale.ROOT);
            return super.getTableCellEditorComponent(tbl, text, isSelected, row, column);
        }

        @Override
        public Object getCellEditorValue() {
            Object v = super.getCellEditorValue();
            return v == null ? "" : String.valueOf(v).toUpperCase(Locale.ROOT);
        }
    }

    private static void moveTableEdit(JTable table, int row, int col, boolean backward) {
        if (table == null || row < 0 || col < 0) {
            return;
        }
        int cols = table.getColumnCount();
        int rows = table.getRowCount();
        int nextCol = col + (backward ? -1 : 1);
        int nextRow = row;
        int guard = cols * rows + 2;
        while (guard-- > 0) {
            if (nextCol < 0) {
                nextCol = cols - 1;
                --nextRow;
            } else if (nextCol >= cols) {
                nextCol = 0;
                ++nextRow;
            }
            if (nextRow < 0 || nextRow >= rows) {
                table.changeSelection(Math.max(0, Math.min(row, rows - 1)), col, false, false);
                return;
            }
            if (table.isCellEditable(nextRow, nextCol)) {
                table.changeSelection(nextRow, nextCol, false, false);
                table.editCellAt(nextRow, nextCol);
                Component ed = table.getEditorComponent();
                if (ed != null) {
                    ed.requestFocusInWindow();
                }
                return;
            }
            nextCol += backward ? -1 : 1;
        }
    }

    private static final class CodeCellEditor
    extends AbstractCellEditor
    implements TableCellEditor {
        private final JTextArea area = new JTextArea(2, 8);
        private JTable editingTable;
        private int editingRow = -1;
        private int editingColumn = -1;

        CodeCellEditor() {
            this.area.setFont(UiTheme.FONT_BOLD.deriveFont(18.0f));
            this.area.setLineWrap(true);
            this.area.setBorder(BorderFactory.createLineBorder(UiTheme.BTN_TEAL, 2));
            MainFrame.addUppercaseDoc(this.area);
            MainFrame.bindEditorTabKeys(this.area, this, () -> this.editingTable, () -> this.editingRow, () -> this.editingColumn);
            this.area.addKeyListener(new KeyAdapter(){

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                        CodeCellEditor.this.stopCellEditing();
                        e.consume();
                    }
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            return MainFrame.normalizeCodeFormat(this.area.getText());
        }

        @Override
        public Component getTableCellEditorComponent(JTable tbl, Object value, boolean isSelected, int row, int column) {
            this.editingTable = tbl;
            this.editingRow = row;
            this.editingColumn = column;
            this.area.setText(MainFrame.normalizeCodeFormat(value == null ? "" : String.valueOf(value)));
            this.area.selectAll();
            return this.area;
        }
    }

    private final class CodeCellRenderer
    extends JPanel
    implements TableCellRenderer {
        private final JPanel block = new JPanel();
        private final JLabel typeLabel = new JLabel("", 0);
        private final JLabel brandLabel = new JLabel("", 0);

        CodeCellRenderer() {
            this.setLayout(new GridBagLayout());
            this.setOpaque(true);
            this.block.setLayout(new BoxLayout(this.block, 1));
            this.block.setOpaque(false);
            this.typeLabel.setFont(UiTheme.FONT_BOLD.deriveFont(18.0f));
            this.typeLabel.setAlignmentX(0.5f);
            this.brandLabel.setFont(UiTheme.FONT_BOLD.deriveFont(18.0f));
            this.brandLabel.setAlignmentX(0.5f);
            this.block.add(this.typeLabel);
            this.block.add(Box.createVerticalStrut(1));
            this.block.add(this.brandLabel);
            this.add(this.block);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String code = MainFrame.normalizeCodeFormat(value == null ? "" : String.valueOf(value));
            this.typeLabel.setText(MainFrame.typeOf(code));
            String brand = MainFrame.brandOf(code);
            this.brandLabel.setText(brand);
            this.brandLabel.setVisible(!brand.isBlank());
            Color brandColor = brand.isBlank() ? Color.DARK_GRAY : MainFrame.this.brandColors.colorFor(brand);
            this.setBackground(isSelected ? UiTheme.ROW_SELECTED : Color.WHITE);
            this.typeLabel.setForeground(Color.BLACK);
            this.brandLabel.setForeground(brandColor);
            return this;
        }
    }

    private static final class MultiLineRenderer
    extends JTextArea
    implements TableCellRenderer {
        MultiLineRenderer() {
            this.setLineWrap(true);
            this.setWrapStyleWord(true);
            this.setOpaque(true);
            this.setFocusable(false);
            this.setFont(UiTheme.FONT);
            this.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        }

        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String text = value == null ? "" : String.valueOf(value).replace("\r\n", "\n").replace('\r', '\n');
            this.setText(text);
            if (isSelected) {
                this.setBackground(UiTheme.ROW_SELECTED);
                this.setForeground(UiTheme.TEXT);
            } else {
                this.setBackground(Color.WHITE);
                this.setForeground(Color.BLACK);
            }
            return this;
        }
    }

    private static final class ProductTableModel
    extends AbstractTableModel {
        private final String[] columns = new String[]{"STT", "N\u1ed8I DUNG C\u00d4NG VI\u1ec6C", "M\u00c3 CODE", "\u0110\u01a0N V\u1eca T\u00cdNH", "S\u1ed0 L\u01af\u1ee2NG", "\u0110\u01a0N GI\u00c1", "TH\u00c0NH TI\u1ec0N"};
        private final Object[][] data = new Object[ROW_COUNT][7];

        ProductTableModel() {
            int i = 0;
            while (i < ROW_COUNT) {
                this.data[i][0] = i + 1;
                this.data[i][1] = "";
                this.data[i][2] = "";
                this.data[i][3] = "";
                this.data[i][4] = "0";
                this.data[i][5] = "0";
                this.data[i][6] = "";
                ++i;
            }
        }

        @Override
        public int getRowCount() {
            return ROW_COUNT;
        }

        @Override
        public int getColumnCount() {
            return this.columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return this.columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return this.data[rowIndex][columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != 0 && columnIndex != 6;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            String value;
            String string = value = aValue == null ? "" : String.valueOf(aValue);
            if (columnIndex == 4 || columnIndex == 5) {
                value = value.isBlank() ? "0" : NumberUtil.formatTyping(value);
            } else if (columnIndex == 2) {
                value = MainFrame.normalizeCodeFormat(value);
            } else if (columnIndex == 3) {
                value = value.toUpperCase(Locale.ROOT);
            } else if (columnIndex == 1) {
                value = value.replace("\r\n", "\n").replace('\r', '\n');
            }
            this.data[rowIndex][columnIndex] = value;
            this.fireTableCellUpdated(rowIndex, columnIndex);
            if (columnIndex == 2) {
                this.fireTableRowsUpdated(rowIndex, rowIndex);
            }
        }

        void setAmount(int row, String amount) {
            if (!String.valueOf(this.data[row][6]).equals(amount)) {
                this.data[row][6] = amount;
                this.fireTableCellUpdated(row, 6);
            }
        }

        void setAmountQuiet(int row, String amount) {
            this.data[row][6] = amount == null ? "" : amount;
        }

        void setContentDirect(int row, String content) {
            String v = content == null ? "" : content.replace("\r\n", "\n").replace('\r', '\n');
            if (!String.valueOf(this.data[row][1]).equals(v)) {
                this.data[row][1] = v;
                this.fireTableCellUpdated(row, 1);
            }
        }
    }
}

