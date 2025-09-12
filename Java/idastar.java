/*

Algoritmo risolutivo: IDA* 

L'algoritmo nasce come variante dell'algoritmo A* ma viene implementato in maniera differente per varie ragioni.
Inanzitutto IDA è un algoritmo di DFS quindi utilizza meno memoria di A* (il quale è un BFS).
A differenza di A*, non tiene conto dei nodi precedentemente visitati ma non prende mai una strada percorsa due volte
in quanto a ogni iterazione aggiorna una soglia di costo e intraprende una strada solo il suo costo è minore della soglia.

L'algoritmo tiene in considerazione una funzione costo calcolata come in A*:
    f(x) = g(x) + h(x)

ma tiene anche conto di un threshold, inizialmente impostato al costo della configurazione iniziale e poi modificato 
andando a visitare in profondità tutte le configurazioni "sorelle" di quella iniziale e restituendo quella con il costo minore.
In tal caso il percorso fatto rappresenta la soluzione con il costo minore. 

In questo caso viene utilizzata una lista visited il cui scopo però è diverso da quello di A*.
La lista dei nodi visitati serve solo durante un ciclo ricorsivo in modo che la "camminata" di IDA* non torni indietro e non si metta
a ciclare all'infinito, ma percorsi diversi (iterazioni separate) non hanno problemi a considerare gli stessi nodi più volte.


Disposizione di prova: 

            {1, 3, 7, 4},
            {6, 2, 16, 8},
            {5, 9, 11, 12},
            {13, 10, 14, 15}

Numero di mosse richieste : 9
    
*/ 


import java.util.*;

public class idastar {
    private State solution;
    private double threshold;

    private int[][] generateFinal(int N,int M,int empty) {
        int[][] f = new int[N][M]; int c=1;
        for(int i=0;i<N;i++) for(int j=0;j<M;j++) f[i][j] = c++;
        return f;
    }

    public idastar(Board initial,int N,int M,int empty) {
        State start = new State(initial,0, null);
        threshold = start.priority(); // Inizializza la soglia con f(n) = g(n) + h(n)
        start.config.printBoard();
        Board finalConfig = new Board(generateFinal(N,M,empty),N,M,empty);

        while (true) {
            Set<Board> visited = new HashSet<>();
            Result result = search(start, threshold, visited, finalConfig);

            if (result.found) {
                solution = result.state;
                return;
            }

            // Se non esiste una soluzione
            if (result.nextThreshold == Double.MAX_VALUE) {
                break;
            }

            // Incrementa la soglia per la prossima iterazione
            threshold = result.nextThreshold;
        }
    }

    // crea la tupla per rappresentare il risultato della ricerca (found, nextThresh, state) -> (false, f, Set<Board>)
    private class Result {
        boolean found;
        double nextThreshold;
        State state;

        Result(boolean found, double nextThreshold, State state) {
            this.found = found;
            this.nextThreshold = nextThreshold;
            this.state = state;
        }
    }

    // Funzione di ricerca IDA* (ricerca ricorsiva)
    private Result search(State current, double threshold, Set<Board> visited, Board finalConfig) {
        double f = current.priority();  // Calcola f(n) = g(n) + h(n)
        if (f > threshold) {
            return new Result(false, f, null); //se f supera la soglia non ritorna nulla ma si ferma
        }

        // Se la configurazione corrente è quella finale, ritorna la soluzione
        if (current.config.equals(finalConfig)) {
            return new Result(true, f, current);
        }

        visited.add(current.config);
        double min = Double.MAX_VALUE;

        // Esplora i vicini della configurazione corrente
        for (Board neighbor : current.config.neighbors()) {
            if (!visited.contains(neighbor)) {
                State next = new State(neighbor, current.moves + 1, current);
                Result result = search(next, threshold, visited, finalConfig); //ricorsione -> permette visita in profondità 

                if (result.found) {
                    return result;  // Se la soluzione è trovata, ritorna
                }

                // Tieni traccia del minimo threshold trovato
                min = Math.min(min, result.nextThreshold);
            }
        }
        visited.remove(current.config);  // Backtracking
        return new Result(false, min, null);
    }

    // Restituisce il numero di mosse per risolvere il puzzle
    public int moves() {
        return (solution == null) ? -1 : solution.moves;
    }

    // Restituisce la sequenza di board dalla iniziale alla finale
    public Iterable<Board> solution() {
        if (solution == null) return null;
        LinkedList<Board> path = new LinkedList<>();
        State current = solution;
        while (current != null) {
            path.addFirst(current.config);  // Aggiungi al percorso
            current = current.previous;
        }
        return path;
    }

    // Test client
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.print("Righe N: "); int N=sc.nextInt();
        System.out.print("Colonne M: "); int M=sc.nextInt();
        System.out.print("Valore empty: "); int empty=sc.nextInt();
        int[][] tiles=new int[N][M]; System.out.println("Inserisci matrice:");
        for(int i=0;i<N;i++) for(int j=0;j<M;j++) tiles[i][j]=sc.nextInt();
        Board b=new Board(tiles,N,M,empty);
        if(!b.isSolvable()){ System.out.println("Non risolvibile"); return;} 
        idastar solver=new idastar(b,N,M,empty);
        for(Board step:solver.solution()){ step.printBoard(); System.out.println(); }
        System.out.println("Mosse: "+solver.moves());
}
}
