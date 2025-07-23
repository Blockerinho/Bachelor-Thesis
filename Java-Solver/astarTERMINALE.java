/*
 * Questo programma implementa una versione dell’algoritmo A* per risolvere puzzle sliding (come il 15-puzzle)
 * in una griglia N x M, con supporto per:
 * - Configurazioni di input personalizzabili,
 * - Verifica della risolvibilità del puzzle,
 * - Calcolo e tracciamento del percorso minimo verso la soluzione,
 * - Esportazione del percorso risolutivo in formato JSON su file.
 *
 * L'algoritmo A* utilizza una funzione di priorità nella forma:
 *     f(n) = g(n) + h(n)
 * dove:
 * - g(n): numero di mosse effettuate per raggiungere lo stato (moves),
 * - h(n): funzione euristica basata sulla somma della distanza di Manhattan e dei conflitti lineari.
 *
 * È predisposto anche il supporto (commentato) per una variante pesata dell’A*, in cui h(n) viene moltiplicata 
 * per un peso (configurabile nella classe `Peso`) per bilanciare precisione e velocità.
 *
 * Il programma include:
 * - Una classe `State` per rappresentare ciascuna configurazione e tracciarne il percorso,
 * - Una classe `astarTERMINALE` per eseguire la ricerca e produrre l’output,
 * - Un metodo `solutionToJson()` per esportare il percorso in formato JSON,
 * - Un metodo `writeSolutionToFile()` per scrivere il file JSON risultante,
 * - Un `main` di test che può essere facilmente adattato per input da tastiera o test hardcoded.
 *
 * Ulteriori funzionalità:
 * - Conta i nodi esplorati durante la ricerca (`nodesExplored()`),
 * - Verifica se la configurazione iniziale è risolvibile (`isSolvable()`),
 * - Gestisce casi non risolvibili evitando il calcolo.
 *
 * Il file JSON generato può essere utilizzato per visualizzazioni o analisi successive.
 */


import java.util.*;
import java.io.FileWriter; 
import java.io.IOException;

//per avere A* star pesato si tolgano i commenti e si aggiunta il peso

class Peso{
	//A* pesato aggiunge un peso alla funzione di costo, i valori di peso migliore si aggirano intorno a 1
	public static double peso = 1.2;
} 

class State implements Comparable<State>{
    
    Board config;
    int moves; 
    State previous;

    public State(Board config, int moves, State previous) {
        this.config = config;
        this.moves = moves;
        this.previous = previous;
    }
    
    //questo consente agli oggetti "state" di essere comparati sulla base della priorità 
    @Override
    public int compareTo(State y){
    	//return Integer.compare(this.priority(/*Peso.peso*/), y.priority(/*Peso.peso*/));
    	return Double.compare(this.priority(), y.priority()); 
    	/*if (this.priority() > y.priority()) {
            return 1;
        } else if (this.priority() < y.priority()) {
            return -1;
        } else {          
            return 0;
        }*/
    }

    // Funzione di priorità: f(n) = g(n) + h(n) (segue il ragionamento di A*)
    public int priority(/*double peso*/) {
        return config.linearConflicts() + config.manhattan();
    }
}

public class astarTERMINALE {
    
    private State solution;
    private boolean isSolvable; 
    private int nodesExplored; 

    HashMap<Integer, int[][]> finalConfigs = new HashMap<>();

    private Board generateFinalConfig(int N, int M) {
	    int[][] config = new int[N][M];
	    int count = 1;
	    for (int i = 0; i < N; i++) {
	        for (int j = 0; j < M; j++) {
	            config[i][j] = count++;
	        }
	    }
	    config[N-1][M-1] = N*M; 
	    return new Board(config, N, M , N*M);
	}

    public astarTERMINALE(Board initial, int N, int M, int empty) {
    	
    	Board finalConfig = generateFinalConfig(N,M);
        PriorityQueue<State> openSet = new PriorityQueue<>();
        Set<Board> visited = new HashSet<>();
        isSolvable = initial.isSolvable(); 

        /*
        //debug
        System.out.println("Configurazione iniziale:");
        initial.printBoard();
		*/
	
        State start = new State(initial, 0, null);
        openSet.add(start);
        nodesExplored = 0; 

        while (!openSet.isEmpty()) {
		    State current = openSet.poll();
		    nodesExplored++; 
		    
		    //debug 
		    /*
		    if(current.previous == null){
		    	System.out.println("null");
		    }else{
			    System.out.println("Configurazione attualmente estratta:"); 
			    current.config.printBoard();
			    System.out.println("Mossa attuale numero:" + current.moves); 
			    System.out.println("Mossa da cui sono arrivato (precedente):"); 
			    current.previous.config.printBoard();
			}*/

		    if (current.config.equals(finalConfig)) {
		        solution = current;
		        return;
		    }

		    visited.add(current.config);

		    Iterable<Board> neighborsList = current.config.neighbors();
		    
		    /*
		    //debug
		    int i=1; 
		    for(Board neighbor : neighborsList){
		    	System.out.println("Vicino " + i); 
		    	neighbor.printBoard();
		    	i++;
		    } 
		    */

			for (Board neighbor : neighborsList) {
			    if (/*!neighbor.equals(current.config) && */!visited.contains(neighbor) /*&& neighbor!=null*/) {
			        //System.out.println("Adding to visited:\n" + neighbor);
			        visited.add(neighbor);
			        openSet.add(new State(neighbor, current.moves + 1, current));
				}
			}
 		}

 	}

 	public boolean isSolvable(){
 		return isSolvable; 
 	}

 	public int nodesExplored(){
 		return nodesExplored; 
 	}
    
    // Ritorna il numero minimo di mosse
    public int moves() {
    	if (this.solution != null){
        	return solution.moves;
        }else{
        	return -1;
        }
    }

    // Ritorna la sequenza delle board dalla iniziale alla finale
    public Iterable<Board> solution() {
        
        if(!isSolvable || solution == null){
        	return new ArrayList<>();
        }

        LinkedList<Board> path = new LinkedList<>();
        State current = solution;
        while (current != null) {
            path.addFirst(current.config);
            current = current.previous;
           	//current.config.printBoard();
        }
        return path;
    }

    public String solutionToJson(){
    	if(!isSolvable || solution == null){
    		return "[]"; 
    	}

    	StringBuilder json = new StringBuilder(); 
    	json.append("["); 
    	Iterable<Board> path = solution(); 
    	boolean first = true; 
    	for(Board board : path){
    		if(!first) json.append(","); 
    		json.append(board.toJSON()); 
    		first = false; 
    	}
    	json.append("]"); 
    	return json.toString(); 
    }

	public void writeSolutionToFile(String filename, String jsonString) {

	    try (FileWriter file = new FileWriter(filename)) {
	        file.write(jsonString);
	        System.out.println("✅ File JSON scritto con successo: " + filename);
	    } catch (IOException e) {
	        System.err.println("❌ Errore nella scrittura del file JSON: " + e.getMessage());
	    }
	}
    
    // Test client
	public static void main(String[] args) {
	    
	    int[][] tiles = {
	        {16, 1, 7, 3},
	        {2, 6, 8, 4},
	        {5, 9, 11, 12},
	        {13, 10, 14, 15}
	    };

	    /*
	    int[][] tiles = {
			{6,9,4},
			{8,5,7},
			{3,2,1}	
		};
		*/

	    /*
		Scanner s = new Scanner(System.in);

		System.out.println("Inserisci la prima dimensione del piano di gioco: ");
		int N = s.nextInt();
		System.out.println("Dimensione scelta: " + N);

		System.out.println("Inserisci la seconda dimensione del piano di gioco: ");
		int M = s.nextInt();
		System.out.println("Dimensione scelta: " + M);

		System.out.println("Inserisci la pedina da considerare come cella vuota: ");
		int empty = s.nextInt();
		System.out.println("Dimensione scelta: " + empty);

		System.out.println("Inserisci la configurazione del piano di gioco: ");
		int[][] inputTiles = new int[N][M];
		for(int i=0; i<N; i++){
			for(int j=0; j<M;j++){
				inputTiles[i][j] = s.nextInt();
			}
		}*/
	    Board board = new Board(tiles, 4,4,16);
	    System.out.println("Configurazione scelta: ");
	    board.printBoard();

	    boolean solvable = board.isSolvable();

	    if(solvable == false){
	    	System.out.println("E' risolvibile?: " + solvable);
	    	return;
	    }
	    
	    System.out.println("E' risolvibile?: " + solvable);

	    astarTERMINALE soluzione = new astarTERMINALE(board, 4,4,16);
	    System.out.println("Soluzione:");
	    Iterable<Board> solution = soluzione.solution(); 
	    for (Board step : solution) {
	        step.printBoard();
	        System.out.println();
	    }

		System.out.println("Numero di mosse:" + soluzione.moves());

		String jsonString = soluzione.solutionToJson(); 
		soluzione.writeSolutionToFile("solution.json",jsonString);

}
    
}