/*
 * Classe: puzzleGUI
 * -----------------
 * Questa classe implementa un'interfaccia grafica (GUI) per il gioco del 15-puzzle (o N-puzzle) usando Java Swing.
 * Consente all'utente di:
 * 
 * 1. Inserire manualmente una configurazione iniziale del puzzle tramite una griglia di JTextField.
 * 2. Scegliere l’algoritmo di risoluzione tra:
 *    - A* classico (basato sulla distanza di Manhattan)
 *    - A* con Linear Conflicts (euristica migliorata)
 * 3. Visualizzare passo per passo la risoluzione animata del puzzle nella GUI.
 * 
 * Funzionalità principali:
 * -------------------------
 * - Input dell’utente: griglia N×N (default: 4×4) per inserire i numeri iniziali.
 * - Selezione algoritmo: JComboBox con opzioni “A* classico” e “A* linear conflicts”.
 * - Validazione configurazione: verifica della risolvibilità tramite il metodo `Board.isSolvable()`.
 * - Visualizzazione grafica del puzzle con animazione del percorso di soluzione (300ms tra ogni mossa).
 * - Visualizzazione di messaggi popup per avvisare l’utente se il puzzle è risolvibile e quante mosse sono richieste.
 * 
 * Componenti Swing:
 * ------------------
 * - `inputPanel`: permette l'inserimento della configurazione iniziale.
 * - `boardPanel`: visualizza graficamente la griglia del puzzle durante la risoluzione.
 * - `controlPanel`: contiene la JComboBox per la selezione dell'algoritmo e il pulsante "Avvia Puzzle".
 * 
 * Requisiti:
 * ----------
 * - Le classi `Board`, `astarClassico`, e `astarTERMINALE` devono essere correttamente implementate e disponibili nel progetto.
 * - Il valore 16 viene interpretato come la casella vuota.
 * 
 * Autore: [Inserisci il tuo nome]
 * Data: [Inserisci la data]
 */


import javax.swing.*;
import java.awt.*;

public class puzzleGUI extends JFrame {
    private int N;
    private JButton[][] buttons;
    private JTextField[][] inputFields;
    private int[][] board;

    private JPanel inputPanel;
    private JPanel boardPanel;
    private JPanel controlPanel;
    private JComboBox<String> algorithmComboBox;  // ComboBox per la selezione dell'algoritmo

    public puzzleGUI(int N) {
        this.N = N;
        this.board = new int[N][N];
        this.buttons = new JButton[N][N];
        this.inputFields = new JTextField[N][N];

        setTitle("15 Puzzle");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        initBegin();
        setVisible(true);
    }

    // Mostra i JTextField iniziali per inserire i numeri
    private void initBegin() {
        inputPanel = new JPanel(new GridLayout(N, N));
        controlPanel = new JPanel();

        // Aggiungi i JTextField per inserire i numeri
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                JTextField inputField = new JTextField();
                inputField.setFont(new Font("Arial", Font.BOLD, 36));
                inputField.setHorizontalAlignment(JTextField.CENTER);
                inputFields[i][j] = inputField;
                inputPanel.add(inputField);
            }
        }

        // Aggiungi la ComboBox per scegliere l'algoritmo
        String[] algorithms = {"A* classico", "A* linear conflicts"};
        algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.setFont(new Font("Arial", Font.BOLD, 20));
        controlPanel.add(new JLabel("Seleziona Algoritmo:"));
        controlPanel.add(algorithmComboBox);

        // Bottone per avviare il puzzle
        JButton startButton = new JButton("Avvia Puzzle");
        controlPanel.add(startButton);
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.addActionListener(e -> startGame());

        controlPanel.add(startButton);

        add(inputPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void startGame(){
        boardPanel = new JPanel(new GridLayout(N, N));

        // Leggi i numeri inseriti
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                String text = inputFields[i][j].getText();
                board[i][j] = text.isEmpty() ? 0 : Integer.parseInt(text);

                JButton button = new JButton(board[i][j] == 0 ? "" : String.valueOf(board[i][j]));
                button.setFont(new Font("Arial", Font.BOLD, 36));
                if (board[i][j] == 16) button.setBackground(Color.RED);
                buttons[i][j] = button;
                boardPanel.add(button);
            }
        }

        Board config = new Board(board,4);

        if (!config.isSolvable()) {
            String message = "Questa configurazione non ha soluzione!";
            JOptionPane.showMessageDialog(null, message, "Dialog", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Rimuove i pannelli iniziali e mostra il puzzle
        getContentPane().removeAll();
        add(boardPanel, BorderLayout.CENTER);
        revalidate();
        repaint();

        // Ottieni l'algoritmo selezionato dalla ComboBox
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();

        // Esegui l'algoritmo in base alla selezione
        if ("A* classico".equals(selectedAlgorithm)) {
            astarClassico sol = new astarClassico(config,4);
            new Thread(() -> {
                try {
                    for (Board step : sol.solution()) {
                        SwingUtilities.invokeLater(() -> updateBoard(step.board));
                        Thread.sleep(300); // ritardo tra le mosse
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
            String message = "Questa configurazione ha richiesto: " + sol.moves() + " mosse";
            JOptionPane.showMessageDialog(null, message);

        } else if ("A* linear conflicts".equals(selectedAlgorithm)) {
            astarTERMINALE sol = new astarTERMINALE(config, 4);
            new Thread(() -> {
                try {
                    for (Board step : sol.solution()) {
                        SwingUtilities.invokeLater(() -> updateBoard(step.board));
                        Thread.sleep(300); // ritardo tra le mosse
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
            String message = "Questa configurazione ha richiesto: " + sol.moves() + " mosse";
            JOptionPane.showMessageDialog(null, message);
        }
    }

    // Metodo per aggiornare la GUI durante il solving
    public void updateBoard(int[][] newBoard) {
        this.board = newBoard;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int value = board[i][j];
                buttons[i][j].setText(value == 16 ? "" : String.valueOf(value));
                buttons[i][j].setBackground(value==16 ? Color.RED : Color.BLACK);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new puzzleGUI(4));
    }
}




