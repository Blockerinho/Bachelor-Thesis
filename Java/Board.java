
/*
 * La classe `Board` rappresenta una configurazione di un puzzle sliding (es. 8-puzzle, 15-puzzle, NxM puzzle).
 * 
 * Funzionalità principali:
 * ------------------------
 * - Rappresentazione flessibile della board NxM, con supporto per specificare il valore che rappresenta la cella vuota.
 * - Calcolo di euristiche utili all’algoritmo A*:
 *   • `hamming()`: numero di blocchi fuori posto.
 *   • `manhattan()`: somma delle distanze di Manhattan per ogni blocco.
 *   • `linearConflicts()`: aggiunge penalità per pedine in conflitto nella stessa riga/colonna (euristica migliorata).
 *   • `manhattanPesato(int fase)`: versione pesata della distanza di Manhattan, utile per algoritmi sequenziali.
 * 
 * - Verifica della risolvibilità del puzzle (`isSolvable()`), considerando la posizione della cella vuota e il numero
 *   di inversioni, a seconda della parità delle dimensioni.
 * 
 * - Generazione delle configurazioni adiacenti valide (`neighbors()`), utilizzando mosse su/giù/sinistra/destra.
 * 
 * - Supporto a:
 *   • Stampa e conversione in stringa (`printBoard()`, `toString()`),
 *   • Conversione in formato JSON (`toJSON()`),
 *   • Uguaglianza tra board e hashing (`equals()`, `hashCode()`),
 *   • Copia della board (`copyBoard()`).
 * 
 * - Mapping interno delle posizioni obiettivo (`pos`) e delle posizioni correnti (`pebbles`) per facilitare il calcolo
 *   delle distanze.
 * 
 * Architettura:
 * -------------
 * Ogni casella viene identificata da un valore intero. La posizione desiderata di ogni pedina è mappata tramite `pos`.
 * La posizione corrente di ogni pedina è tracciata in `pebbles`.
 * 
 * Uso:
 * ----
 * La classe è progettata per essere usata con algoritmi di risoluzione come A*, con supporto per analisi e debugging
 * approfondito del comportamento del puzzle.
 * 
 * Note:
 * -----
 * - Sono presenti metodi commentati per ulteriori verifiche o fasi sperimentali (es. `isOddSolvable`, `main` di test).
 * - L’euristica Linear Conflict migliora notevolmente la velocità di risoluzione rispetto alla sola Manhattan.
 * - Il codice può essere facilmente esteso per supportare visualizzazioni (es. tramite Swing o JSON viewer).
 */

import java.util.*;
import javax.swing.*;

class Pair {
    public final int first;
    public final int second;

    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }
}

public class Board {
    public int[][] board;
    public int firstDimension;
    public int secondDimension;
    //private static int N=3;  // La dimensione della board (NxN)
    private final Dictionary<Integer, Pair> pos = new Hashtable<>();
    private final Dictionary<Integer, Pair> pebbles = new Hashtable<>();
    private int empty;

    //Serve a mappare le pedine nelle corrispondenti posizioni nella configurazione
    /*
    static {
        int counter = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                pos.put(counter++, new Pair(i, j));
            }
        }
    }
    */

    // Costruttore: Crea una board da un array N x N di tiles
    public Board(int[][] tiles, int N, int M, int empty) {
        //this.N = tiles.length;
        this.board = new int[N][M];
        this.firstDimension = N;
        this.secondDimension = M;
        this.empty = empty;

        // Ricrea posizioni aggiornate in pebbles
        for (int i = 0; i < this.firstDimension; i++) {
            for (int j = 0; j < this.secondDimension; j++) {
                board[i][j] = tiles[i][j];
                pebbles.put(tiles[i][j], new Pair(i, j));
            }
        }

        //Serve a mappare le pedine nelle corrispondenti posizioni nella configurazione
        //Complessità O(n^2) considerando n = una dimensione (this.dimension)
        int counter = 1;
        for (int i = 0; i < this.firstDimension; i++) {
            for (int j = 0; j < this.secondDimension; j++) {
                pos.put(counter, new Pair(i, j));
                counter++;
            }
        }
    }

/*
    public boolean isOddSolvable() {
        int inv = 0;

        int[] flat = new int[this.dimension * this.dimension];
        int index = 0;
        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {    
                flat[index++] = board[i][j];
                //index++;
            }
        }

        for (int i = 0; i < flat.length; i++) {
            if (flat[i] == empty) continue;
            for (int j = i + 1; j < flat.length; j++) {
                if (flat[j] != empty && flat[i] > flat[j]) {
                    inv++;
                }
            }
        }

        return (inv % 2) ==0; // La parità deve essere la stessa
    }
*/


    //complessità temporale: O(n^2) -> possibile migliorare a O(nlogn) ? 
    public boolean isSolvable() {
        int size = this.firstDimension * this.secondDimension;
        int[] flat = new int[size];
        int emptyIndex = -1;
        //int count = 0; 

        int k = 0;
        for (int i = 0; i < this.firstDimension; i++){
            for (int j = 0; j < this.secondDimension; j++){
                flat[k++] = this.board[i][j];
                if(board[i][j] == empty){
                    emptyIndex = this.firstDimension - i; 
                }
            }
        }

        int inv = 0;
        for (int i = 0; i < this.firstDimension * this.secondDimension - 1; i++) {
            if(flat[i]==empty) continue;
            for (int j = i + 1; j < this.firstDimension * this.secondDimension; j++) {
                // count pairs(arr[i], arr[j]) such that
                // i < j but arr[i] > arr[j]
                if (flat[j] != this.empty && flat[i] > flat[j])
                    inv++;
            }
        }

        //debug
        System.out.println("Numero di inversioni: " + inv);
        /*
        for (int i = this.firstDimension - 1; i >= 0; i--){
            for (int j = this.secondDimension - 1; j >= 0; j--){
                if (this.board[i][j] == this.empty){
                    emptyIndex = this.firstDimension - i;
                }
            }
        }*/

        //debug
        System.out.println("Indice della riga del vuoto: " + emptyIndex);

        if (this.secondDimension % 2 == 1){
            return inv % 2 == 0;
        }else{
            return (inv + emptyIndex) % 2 == 1;
        }

        /*
        //riempie il flat
        for(int i=0; i<this.dimension; i++){
            for(int j=0; j<this.dimension; j++){
                flat[count] = this.board[i][j];
                count++;
            }
        }

        //debug
        for(int i=0; i<size; i++){
            System.out.println(flat[i]);
        }

        //trova la posizione della cella vuota
        for (int i = 0; i < this.dimension; i++){
            for (int j = 0; j < this.dimension; j++){
                if (this.board[i][j] == this.empty){
                    emptyIndex = i+1;
                }
            }
        }

        //debug
        System.out.println("Indice della riga del vuoto: " + emptyIndex);

        //conta le inversioni
        int inv = 0; 
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                // count pairs(arr[i], arr[j]) such that
                // i < j but arr[i] > arr[j]
                // l'inizializzazione di j garantisce che i<j
                if (flat[j] != 0 && flat[i] != 0 && flat[i] > flat[j]){
                    inv++;
                }
            }
        }

        //debug 
        System.out.println("Numero di inversioni: " + inv);


        if(this.dimension % 2 == 1){
            return inv%2==0;
        }else{
            return (inv+emptyIndex)%2==0;
        }
        */
    }

    public void printBoard(){
        for (int i = 0; i < this.firstDimension; i++) {
            for (int j = 0; j < this.secondDimension; j++) {
                System.out.print(this.board[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Metodo Hamming: ritorna il numero di blocchi fuori posto
    public int hamming() {
        int count = 0;
        int goal = 1;
        for (int i = 0; i < this.firstDimension; i++) {
            for (int j = 0; j < this.secondDimension; j++) {
                if (board[i][j] != goal && board[i][j] != empty) {
                    count++;
                }
                goal++;
            }
        }
        return count;
    }

    // Metodo Manhattan: ritorna la somma delle distanze di Manhattan
    public int manhattan() {
        int dist = 0;
        for (int i = 0; i < this.firstDimension; i++) {
            for (int j = 0; j < this.secondDimension; j++) {
                int value = board[i][j];
                if (value != empty) {
                    Pair targetPosition = pos.get(value); 
                    dist += Math.abs(i - targetPosition.first) + Math.abs(j - targetPosition.second);

                }
            }
        }
        return dist;
    }

    private int getPhase(int val) {
        if (val >= 1 && val <= 4) return 1;
        if (val >= 5 && val <= 8) return 2;
        if (val == 9 || val == 13) return 3;
        if (val == 10 || val == 14) return 4;
        return 5;
    }


    //modifica i pesi in base alla fase in cui ci si trova
    public int pesoPerFase(int value, int fase){

        /*int peso = 0;

        if (getPhase(value) < fase) {
                peso = 10; // deve essere già a posto!
            } else if (getPhase(value) == fase) {
                peso = 5; // deve essere messo adesso
            } else {
                peso = 1; // da ignorare per ora
            }

        return peso; */


        switch(fase){
            case 1: 
                if (value == 1 || value == 2 || value == 3 || value == 4) return 20;
                else return 0;
            case 2:
                if(value == 5 || value == 6 || value == 7 || value == 8) return 20; 
                else return 0; 
            case 3: 
                if (value == 9 || value == 13) return 20; 
                else return 0; 
            case 4: 
                if(value == 10 || value == 14) return 20;  
                else return 0; 
            case 5:
                // Priorità per 9, 10, 13, 14
                if (value == 11 || value == 12 || value == 15) return 20;
                else return 0;
            case 6:
                // Fase finale: tutti hanno lo stesso peso
                return 1;
            default:
                return 1;
        }
    }

    //calcola il valore di manhattan pesato in base alla fase
    public int manhattanPesato(int fase){
        int totalCost = 0; 
        for(int i=0; i<this.firstDimension; i++){
            for(int j=0; j<this.secondDimension; j++){
                int value = board[i][j]; 
                if(value == this.empty) continue; 

                Pair targetPosition = pos.get(value);

                int manhDist = Math.abs(i - targetPosition.first) + Math.abs(j - targetPosition.second); 
                int peso = pesoPerFase(value, fase);

                totalCost =  manhDist * peso; 
            }
        }
        return totalCost;
    }


    //le seguenti funzioni hanno lo scopo di migliorare l'euristica del costo calcolandolo come manhattan + conflitti lineari
    //con conflitto lineare si intendono due concetti: 
    //conflitto lineare di riga : due pedine che sono entrambe nella stessa riga, la quale è anche la riga in cui devono stare
                                //alla fine di tutto, sono in conflitto lineare se non sono nella colonna giusta, cioè l'uno impedisce all'
                                //altro di andare nella posizione corretta, quindi almeno uno deve essere spostato dalla sua posizione, ogni
                                //conflitto lineare implica 2 mosse in più da fare;
    //conflitto lineare di colonna: due pedine che sono entrambe nella stessa colonna, la quale è anche la colonna in cui devono stare
                                //alla fine di tutto, sono in conflitto lineare se non sono nella riga giusta, cioè l'uno impedisce all'
                                //altro di andare nella posizione corretta
    //la somma dei due valori moltiplicata per 2(numero di mosse in più che aggiunge ogni conflitto) restituisce il valore del linear conflict. 

    //l'uso del linear conflict risolve in tempo estremamente più veloce situazioni prima quasi irrisolvibili. 


    //calcola il numero di conflitti lineari sulle righe
    public int countLCrow(int row){
        int conflicts = 0; 
        for(int i=0; i<this.secondDimension; i++){
            int tile = this.board[row][i]; 
            if(tile == this.empty) continue; //cerco una pedina diversa dalla vuota
            int goalRow = (tile - 1)/this.secondDimension;
            if(goalRow != row) continue; //che è nella riga corretta

            for(int j=i+1; j<this.secondDimension; j++){
                int tile2 = this.board[row][j]; 
                if(tile2 == tile) continue; //non deve essere uguale a quella sopra
                if(tile2 == this.empty) continue; //cerco una seconda pedina diversa dalla vuota
                int goalRow2 = (tile2 - 1)/this.secondDimension;
                if(goalRow2 != row) continue; //che è nella riga corretta

                if (tile > tile2) conflicts++; //se sono invertiti allora c'è un conflitto
            }

        }

        return conflicts; 
    }

    //calcola il numero di conflitti lineari sulle colonne
    public int countLCcol(int col){
        int conflicts = 0; 
        for(int i=0; i<this.firstDimension; i++){
            int tile = this.board[i][col]; 
            if(tile == this.empty) continue; //cerco una pedina diversa dalla vuota
            int goalCol = (tile - 1)%this.secondDimension;
            if(goalCol != col) continue; //che è nella riga corretta

            for(int j=i+1; j<this.firstDimension; j++){
                int tile2 = this.board[j][col]; 
                //if(tile2 == tile) continue; //non deve essere uguale a quella sopra
                if(tile2 == this.empty) continue; //cerco una seconda pedina diversa dalla vuota
                int goalCol2 = (tile2 - 1)%this.secondDimension;
                if(goalCol2 != col) continue; //che è nella riga corretta

                if (tile > tile2) conflicts++; //se sono invertiti allora c'è un conflitto
            }
        }

        return conflicts; 
    }

    //calcola il numero di conflitti lineari
    public int linearConflicts(){
        int conflicts = 0; 
        for(int i=0; i<this.firstDimension; i++){
            conflicts += countLCrow(i);
        }

        for(int j=0; j<this.secondDimension; j++){
            conflicts += countLCcol(j);
        }

        return 2 * conflicts;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Board other = (Board) obj;
        if (this.firstDimension != other.firstDimension || this.secondDimension != other.secondDimension) return false; 
        return Arrays.deepEquals(this.board, other.board); 
        /*
        for (int i = 0; i < this.firstDimension; i++) {
            for (int j = 0; j < this.secondDimension; j++) {
                if (this.board[i][j] != other.board[i][j]) return false;
            }
        }
        return true;*/
    }

    @Override
    public int hashCode() {
        /*int result = 17;
        for (int i = 0; i < this.firstDimension; i++) {
            for (int j = 0; j < this.secondDimension; j++) {
                result = 31 * result + board[i][j];
            }
        }
        return result;*/
        return Objects.hash(this.firstDimension, this.secondDimension, Arrays.deepHashCode(this.board));
    }

    public int[][] copyBoard(){
        int[][] copy = new int[this.firstDimension][this.secondDimension]; 
        for(int i=0; i<this.firstDimension; i++){
            /*for(int j=0; j<this.secondDimension;j++){
                copy[i][j] = this.board[i][j]; 
            }*/
            System.arraycopy(this.board[i], 0, copy[i], 0, this.secondDimension);
        }
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.firstDimension; i++) {
            for (int j = 0; j < this.secondDimension; j++) {
                sb.append(board[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public Iterable<Board> neighbors() {
        List<Board> neighbors = new ArrayList<>();
        Pair emptyCell = pebbles.get(empty);  // Posizione della cella vuota
        int row = emptyCell.first;
        int col = emptyCell.second;

        int[] dRow = {-1, 1, 0, 0};  // Su, giù, sinistra, destra
        int[] dCol = {0, 0, -1, 1};

        //itero sulle 4 direzioni possibili!!!!!!
        for (int i = 0; i < 4; i++) {  // Ciclo corretto: 4 direzioni
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];

            if (newRow >= 0 && newRow < this.firstDimension && newCol >= 0 && newCol < this.secondDimension) {
                int[][] newTiles = copyBoard();
                newTiles[row][col] = newTiles[newRow][newCol];
                newTiles[newRow][newCol] = empty;

                neighbors.add(new Board(newTiles, this.firstDimension, secondDimension, empty));
            }
        }

        return neighbors;
    }

    public String toJSON(){
        StringBuilder json = new StringBuilder(); 
        json.append("["); 
        for(int i=0; i<this.firstDimension; i++){
            json.append("[");
            for(int j=0; j<this.secondDimension; j++){
                json.append(board[i][j]); 
                if(j<this.secondDimension-1) json.append(","); 
            }
            json.append("]"); 
            if(i<this.firstDimension-1) json.append(","); 
        }
        json.append("]"); 
        return json.toString(); 
    }





    // Test client
    /*
    public static void main(String[] args) {
        int[][] tiles = {
            {1, 3, 7, 4},
            {6, 2, empty, 8},
            {5, 9, 12, 11},
            {13, 10, 14, 15}
        };

        // Crea una board da una configurazione di tiles
        Board board = new Board(tiles);
        System.out.println("Board:");
        board.printBoard();

        try {
            if (!board.isSolvable()) {
                String message = "Questa configurazione non ha soluzione!";
                JOptionPane.showMessageDialog(null, message, "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace(); // stampi l'errore su console
            JOptionPane.showMessageDialog(null, "Errore durante il controllo di risolvibilità:\n" + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }


        // Calcola e stampa la distanza di Hamming
        System.out.println("Hamming: " + board.hamming());

        // Calcola e stampa la distanza di Manhattan
        System.out.println("Manhattan: " + board.manhattan());

        //controlla se le due configurazioni sono uguali
        //System.out.println("Equals?: " + board.equals(board2)); 

        Iterable<Board> neighborsList = board.neighbors(); 

        for( Board element : neighborsList ){ 
            element.printBoard();  
            System.out.println(); 
        } 
        
    }*/
    
}