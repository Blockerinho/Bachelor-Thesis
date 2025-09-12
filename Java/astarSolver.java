
/*
 * Questo programma implementa una versione generale dell'algoritmo A* per risolvere puzzle di tipo sliding puzzle 
 * (puzzle a scorrimento) di dimensione arbitraria N x M, in cui il valore che rappresenta lo spazio vuoto è specificato 
 * dall'utente (parametro `empty`).
 *
 * Il programma consente all'utente di:
 * - Inserire le dimensioni del puzzle (N righe e M colonne),
 * - Definire il valore che rappresenta la casella vuota,
 * - Fornire una configurazione iniziale della board tramite input da tastiera.
 *
 * Il cuore della ricerca è nella classe `astarSolver`, che costruisce lo stato finale ordinato (in ordine crescente) 
 * e utilizza una `PriorityQueue` per esplorare gli stati (`State`) in ordine crescente di costo stimato:
 * 
 *     f(n) = g(n) + peso * h(n)
 *
 * Dove:
 * - `g(n)` è il numero di mosse effettuate (distanza dal nodo iniziale),
 * - `h(n)` è la somma della distanza di Manhattan e dei conflitti lineari (euristiche),
 * - `peso` è un fattore moltiplicativo configurabile per bilanciare velocità e ottimalità (default: 1.2).
 *
 * Ogni stato viene memorizzato come oggetto `State`, che tiene traccia della board corrente, del numero di mosse
 * e del predecessore, per ricostruire il percorso di soluzione.
 *
 * L’algoritmo restituisce:
 * - Il numero minimo di mosse per risolvere il puzzle (`moves()`),
 * - La sequenza di configurazioni dalla iniziale alla finale (`solution()`), che viene stampata nel metodo `main`.
 *
 * Il programma verifica anche se il puzzle iniziale è risolvibile tramite il metodo `isSolvable()` della classe `Board`.
 */


import java.util.*;

public class astarSolver {
    static class Peso { public static double peso = 1.2; }

    static class State implements Comparable<State> {
        Board config; int moves; State prev;
        State(Board c,int m,State p){config=c;moves=m;prev=p;}
        public double priority() {
            return moves + Peso.peso * (config.linearConflicts() + config.manhattan());
        }
        @Override public int compareTo(State o) {
            return Double.compare(this.priority(), o.priority());
        }
    }

    private State solution;

    private int[][] generateFinal(int N,int M,int empty) {
        int[][] f = new int[N][M]; int c=1;
        for(int i=0;i<N;i++) for(int j=0;j<M;j++) f[i][j] = c++;
        return f;
    }

    public astarSolver(Board start,int N,int M,int empty) {
        Board goal = new Board(generateFinal(N,M,empty),N,M,empty);
        PriorityQueue<State> open = new PriorityQueue<>();
        Set<Board> closed = new HashSet<>();
        open.add(new State(start,0,null));
        while(!open.isEmpty()) {
            State cur = open.poll();
            if(cur.config.equals(goal)) { solution=cur; break; }
            if(!closed.add(cur.config)) continue;
            for(Board nb: cur.config.neighbors()) {
                if(!closed.contains(nb)) open.add(new State(nb,cur.moves+1,cur));
            }
        }
    }

    public Iterable<Board> solution() {
        LinkedList<Board> path = new LinkedList<>();
        for(State s=solution; s!=null; s=s.prev) path.addFirst(s.config);
        return path;
    }
    public int moves(){return solution!=null?solution.moves:-1;}

    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        System.out.print("Righe N: "); int N=sc.nextInt();
        System.out.print("Colonne M: "); int M=sc.nextInt();
        System.out.print("Valore empty: "); int empty=sc.nextInt();
        int[][] tiles=new int[N][M]; System.out.println("Inserisci matrice:");
        for(int i=0;i<N;i++) for(int j=0;j<M;j++) tiles[i][j]=sc.nextInt();
        Board b=new Board(tiles,N,M,empty);
        if(!b.isSolvable()){ System.out.println("Non risolvibile"); return;} 
        astarSolver solver=new astarSolver(b,N,M,empty);
        for(Board step:solver.solution()){ step.printBoard(); System.out.println(); }
        System.out.println("Mosse: "+solver.moves());
    }
}