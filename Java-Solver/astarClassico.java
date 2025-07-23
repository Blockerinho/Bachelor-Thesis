1import java.util.*;

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
    	if (this.priority() > y.priority()) {
            return 1;
        } else if (this.priority() < y.priority()) {
            return -1;
        } else {
            return 0;
        }
    }

    // Funzione di priorità: f(n) = g(n) + h(n) (segue il ragionamento di A*)
    public int priority(/*double peso*/) {
        return this.moves + /*config.linearConflicts()*/ + config.manhattan(); //*peso;
    }
}
	
class astarClassico {

		private State solution;

		private static HashMap<Integer, int[][]> finalConfigs = new HashMap<>();

		private static int[][] f4Conf = {
				{1, 2, 3, 4},
				{5, 6, 7, 8},
				{9, 10, 11, 12},
				{13, 14, 15, 16}
		};

		private static int[][] f3Conf = {
				{1,2,3},
				{4,5,6},
				{7,8,9}
		};

		private static int[][] f2Conf = {
				{1,2},
				{3,4}
		};

		static {

			finalConfigs.put(4,f4Conf);
			finalConfigs.put(3,f3Conf);
			finalConfigs.put(2,f2Conf);

		}

	public astarClassico(Board initial, int N) {
			Board finalConfig = new Board(finalConfigs.get(N), N);
			PriorityQueue<State> openSet = new PriorityQueue<>();
			Set<Board> visited = new HashSet<>();

        /*
        //debug
        System.out.println("Configurazione iniziale:");
        initial.printBoard();
		*/

			State start = new State(initial, 0, null);
			openSet.add(start);

			while (!openSet.isEmpty()) {
				State current = openSet.poll();

		    /*
		    //debug
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
		    } */

				for (Board neighbor : neighborsList) {
					//System.out.println("Neighbor config:\n" + neighbor);
					//System.out.println("visited.contains(neighbor): " + visited.contains(neighbor));
					if (!neighbor.equals(current.config) && !visited.contains(neighbor) && neighbor!=null) {
						//System.out.println("Adding to visited:\n" + neighbor);
						visited.add(neighbor);
						openSet.add(new State(neighbor, current.moves + 1, current));
					} else if (visited.contains(neighbor)) {
						//System.out.println("Neighbor già visitato:\n" + neighbor);
					}
				}
			}
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
			LinkedList<Board> path = new LinkedList<>();
			State current = solution;
			while (current != null) {
				path.addFirst(current.config);
				current = current.previous;
				//current.config.printBoard();
			}
			return path;
	}
	// Test client
	public static void main(String[] args) {

	    /*int[][] tiles = {
	        {16, 1, 7, 3},
	        {2, 6, 8, 4},
	        {5, 9, 11, 12},
	        {13, 10, 14, 15}
	    };*/

	    /*
	    int[][] tiles = {
			{6,9,4},
			{8,5,7},
			{3,2,1}
		};
		*/

			Scanner s = new Scanner(System.in);

			System.out.println("Inserisci la dimensione del piano di gioco: ");
			int N = s.nextInt();
			System.out.println("Dimensione scelta: " + N);

			System.out.println("Inserisci la configurazione del piano di gioco: ");
			int[][] inputTiles = new int[N][N];
			for(int i=0; i<N; i++){
				for(int j=0; j<N;j++){
					inputTiles[i][j] = s.nextInt();
				}
			}
			Board board = new Board(inputTiles, N);
			System.out.println("Configurazione scelta: ");
			board.printBoard();

			boolean solvable = board.isSolvable();

			if(solvable == false){
				System.out.println("E' risolvibile?: " + solvable);
				return;
			}

			System.out.println("E' risolvibile?: " + solvable);

			astarClassico soluzione = new astarClassico(board, N);
			System.out.println("Soluzione:");
			Iterable<Board> solution = soluzione.solution();
			for (Board step : solution) {
				step.printBoard();
				System.out.println();
			}

			System.out.println("Numero di mosse:" + soluzione.moves());
		}

}