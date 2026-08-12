package com.lexsynanalyzer.gui;

import com.lexsynanalyzer.analyzer.AnalysisResult;
import com.lexsynanalyzer.analyzer.LexSynAnalyzer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class LexSynAnalyzerGui extends JFrame {

    private static final String PLACEHOLDER_TEXT = "// Seleccione o abra un archivo .cps para comenzar el análisis...";

    private static final Color COLOR_BG = new Color(0x1E, 0x1E, 0x1E);
    private static final Color COLOR_PANEL = new Color(0x25, 0x25, 0x26);
    private static final Color COLOR_TOOLBAR = new Color(0x2D, 0x2D, 0x2D);
    private static final Color COLOR_BORDER = new Color(0x3C, 0x3C, 0x3C);
    private static final Color COLOR_TEXT = new Color(0xD4, 0xD4, 0xD4);
    private static final Color COLOR_ACCENT = new Color(0x1B, 0x6E, 0xC2);
    private static final Color COLOR_ACCENT_HOVER = new Color(0x15, 0x58, 0x9C);
    private static final Color COLOR_ACCENT_BORDER = new Color(0x0F, 0x46, 0x80);
    private static final Color COLOR_LINE_NUMBERS = new Color(0x85, 0x85, 0x85);

    private final JTextArea editorArea;
    private final JTextArea lineNumbersArea;
    private final ResultsTablePanel resultsPanel;
    private final JButton btnAbrir;
    private final JButton btnAnalizar;
    private final JButton btnLimpiar;
    private final JLabel lblArchivoPath;
    private final JLabel lblStatusBar;

    private File archivoActual;

    public LexSynAnalyzerGui() {
        super("Compiscript — Analizador Léxico y Sintáctico (ANTLR4)");

        lblArchivoPath = new JLabel("[ Ningún archivo cargado ]");
        lblArchivoPath.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblArchivoPath.setForeground(COLOR_LINE_NUMBERS);
        lblArchivoPath.setBorder(new EmptyBorder(0, 12, 0, 12));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 750);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        // Estructura principal
        JPanel rootPanel = new JPanel(new BorderLayout(0, 0));
        rootPanel.setBackground(COLOR_BG);

        // 1. Barra superior (Header + Toolbar)
        JPanel topPanel = crearTopPanel();
        rootPanel.add(topPanel, BorderLayout.NORTH);

        // 2. Editor de Código (Con Numeración de Líneas y Placeholder)
        editorArea = new JTextArea(PLACEHOLDER_TEXT);
        editorArea.setBackground(COLOR_BG);
        editorArea.setForeground(COLOR_TEXT);
        editorArea.setCaretColor(Color.WHITE);
        editorArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        editorArea.setTabSize(4);
        editorArea.setMargin(new Insets(6, 8, 6, 8));

        lineNumbersArea = new JTextArea("1 ");
        lineNumbersArea.setBackground(COLOR_PANEL);
        lineNumbersArea.setForeground(COLOR_LINE_NUMBERS);
        lineNumbersArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        lineNumbersArea.setEditable(false);
        lineNumbersArea.setFocusable(false);
        lineNumbersArea.setMargin(new Insets(6, 8, 6, 8));

        JScrollPane editorScrollPane = new JScrollPane(editorArea);
        editorScrollPane.setRowHeaderView(lineNumbersArea);
        editorScrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        editorScrollPane.getViewport().setBackground(COLOR_BG);

        JPanel editorContainer = new JPanel(new BorderLayout(0, 0));
        editorContainer.setBackground(COLOR_BG);

        JLabel editorHeader = new JLabel("Visor de Código Fuente (.cps)");
        editorHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        editorHeader.setForeground(COLOR_TEXT);
        editorHeader.setBackground(COLOR_PANEL);
        editorHeader.setOpaque(true);
        editorHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 0, 1, COLOR_BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));

        editorContainer.add(editorHeader, BorderLayout.NORTH);
        editorContainer.add(editorScrollPane, BorderLayout.CENTER);

        // Actualizar números de línea al escribir o cargar texto
        editorArea.addCaretListener(e -> actualizarNumerosDeLinea());

        // 3. Panel de Resultados
        resultsPanel = new ResultsTablePanel();
        resultsPanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                resaltarLineaSeleccionada();
            }
        });

        // 4. SplitPane Redimensionable (Vertical: Editor arriba, Resultados abajo)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorContainer, resultsPanel);
        splitPane.setDividerLocation(260); // Proporción cómoda para ver bien los resultados
        splitPane.setResizeWeight(0.4);
        splitPane.setBackground(COLOR_BG);
        splitPane.setBorder(new EmptyBorder(8, 8, 8, 8));
        splitPane.setDividerSize(8);

        rootPanel.add(splitPane, BorderLayout.CENTER);

        // 5. Barra de Estado Inferior
        lblStatusBar = new JLabel(" Listo — Esperando selección de archivo .cps");
        lblStatusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatusBar.setForeground(COLOR_TEXT);
        lblStatusBar.setBackground(COLOR_PANEL);
        lblStatusBar.setOpaque(true);
        lblStatusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
        rootPanel.add(lblStatusBar, BorderLayout.SOUTH);

        setContentPane(rootPanel);

        // Asignar Eventos a Botones
        btnAbrir = encontrarBoton("Abrir");
        btnAnalizar = encontrarBoton("Analizar");
        btnLimpiar = encontrarBoton("Limpiar");

        actualizarEstadoBotonAnalizar(false);

        btnAbrir.addActionListener(e -> abrirArchivo());
        btnAnalizar.addActionListener(e -> ejecutarAnalisis());
        btnLimpiar.addActionListener(e -> limpiarVista());

        actualizarNumerosDeLinea();
    }

    private JPanel crearTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_TOOLBAR);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        // Título de la aplicación
        JLabel lblTitle = new JLabel("  COMPISCRIPT IDE  |  Analizador ANTLR4");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_TEXT);

        // Toolbar con 3 botones con estilo consistente y tooltips
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        toolbar.setBackground(COLOR_TOOLBAR);

        JButton btnOpen = crearBoton("Abrir Archivo (.cps)", COLOR_ACCENT, COLOR_ACCENT_HOVER,
                "Abrir un archivo de código fuente Compiscript (.cps)");
        JButton btnRun = crearBoton("Analizar", COLOR_ACCENT, COLOR_ACCENT_HOVER,
                "Ejecutar el análisis léxico y sintáctico sobre el archivo cargado");
        JButton btnClear = crearBoton("Limpiar", COLOR_ACCENT, COLOR_ACCENT_HOVER,
                "Limpiar el editor de código y los resultados");

        btnOpen.setName("Abrir");
        btnRun.setName("Analizar");
        btnClear.setName("Limpiar");

        toolbar.add(lblArchivoPath);
        toolbar.add(btnOpen);
        toolbar.add(btnRun);
        toolbar.add(btnClear);

        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(toolbar, BorderLayout.EAST);

        return panel;
    }

    private JButton crearBoton(String texto, Color bg, Color bgHover, String tooltip) {
        JButton btn = new JButton(texto);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setToolTipText(tooltip);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT_BORDER, 1),
                new EmptyBorder(6, 16, 6, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgHover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private void actualizarEstadoBotonAnalizar(boolean habilitado) {
        btnAnalizar.setEnabled(true);
        btnAnalizar.setBackground(COLOR_ACCENT);
        btnAnalizar.setForeground(Color.WHITE);
    }

    private JButton encontrarBoton(String name) {
        for (Component c : getAllComponents(this)) {
            if (c instanceof JButton b && name.equals(b.getName())) {
                return b;
            }
        }
        throw new IllegalStateException("Botón no encontrado: " + name);
    }

    private java.util.List<Component> getAllComponents(Container c) {
        java.util.List<Component> compList = new java.util.ArrayList<>();
        for (Component comp : c.getComponents()) {
            compList.add(comp);
            if (comp instanceof Container cont) {
                compList.addAll(getAllComponents(cont));
            }
        }
        return compList;
    }

    private void abrirArchivo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar archivo Compiscript (.cps)");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos Compiscript (*.cps)", "cps"));

        Path defaultDir = Paths.get("src", "test", "resources", "casos");
        if (Files.exists(defaultDir)) {
            chooser.setCurrentDirectory(defaultDir.toFile());
        }

        int opcion = chooser.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            archivoActual = chooser.getSelectedFile();
            try {
                String contenido = Files.readString(archivoActual.toPath());
                editorArea.setText(contenido);
                editorArea.setCaretPosition(0);
                actualizarNumerosDeLinea();
                actualizarEstadoBotonAnalizar(true);
                lblArchivoPath.setText(archivoActual.getName());
                lblStatusBar.setText(" Archivo cargado: " + archivoActual.getAbsolutePath());
                resultsPanel.limpiar();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo:\n" + ex.getMessage(),
                        "Error de Lectura", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ejecutarAnalisis() {
        if (archivoActual == null || !archivoActual.exists()) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un archivo válido antes de analizar.",
                    "Archivo no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        actualizarEstadoBotonAnalizar(false);
        btnAbrir.setEnabled(false);
        lblStatusBar.setText(" [BUSY] Ejecutando análisis léxico y sintáctico...");

        SwingWorker<AnalysisResult, Void> worker = new SwingWorker<>() {
            @Override
            protected AnalysisResult doInBackground() throws Exception {
                return LexSynAnalyzer.analyze(archivoActual);
            }

            @Override
            protected void done() {
                try {
                    AnalysisResult result = get();
                    if (result.exitoso()) {
                        resultsPanel.mostrarExito();
                        lblStatusBar.setText(" [OK] Análisis completado: 0 errores encontrados.");
                    } else {
                        resultsPanel.mostrarErrores(result.errores());
                        lblStatusBar.setText(String.format(" [ERROR] Análisis completado: %d errores detectados.", result.errores().size()));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(LexSynAnalyzerGui.this,
                            "Error crítico durante el análisis:\n" + ex.getCause().getMessage(),
                            "Error de Análisis", JOptionPane.ERROR_MESSAGE);
                    lblStatusBar.setText(" [ERROR] Fallo crítico durante el análisis.");
                } finally {
                    actualizarEstadoBotonAnalizar(true);
                    btnAbrir.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void limpiarVista() {
        archivoActual = null;
        editorArea.setText(PLACEHOLDER_TEXT);
        actualizarNumerosDeLinea();
        resultsPanel.limpiar();
        actualizarEstadoBotonAnalizar(false);
        lblArchivoPath.setText("[ Ningún archivo cargado ]");
        lblStatusBar.setText(" Vista limpiada. Seleccione un archivo .cps.");
    }

    private void actualizarNumerosDeLinea() {
        int lineas = editorArea.getLineCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lineas; i++) {
            sb.append(i).append("\n");
        }
        lineNumbersArea.setText(sb.toString());
    }

    private void resaltarLineaSeleccionada() {
        int row = resultsPanel.getTable().getSelectedRow();
        if (row < 0) return;

        Object valLinea = resultsPanel.getTable().getValueAt(row, 1);
        if (valLinea instanceof Integer numLinea) {
            try {
                int lineIndex = numLinea - 1; // 1-indexed to 0-indexed
                int startOffset = editorArea.getLineStartOffset(lineIndex);
                int endOffset = editorArea.getLineEndOffset(lineIndex);

                editorArea.requestFocusInWindow();
                editorArea.setCaretPosition(startOffset);
                editorArea.moveCaretPosition(endOffset);
            } catch (BadLocationException ignored) {
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            LexSynAnalyzerGui gui = new LexSynAnalyzerGui();
            gui.setVisible(true);
        });
    }
}
