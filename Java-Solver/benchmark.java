/*
 * Questo programma esegue un benchmark comparativo tra due versioni dell'algoritmo A* applicato al puzzle 15-puzzle,
 * utilizzando due diverse euristiche:
 *
 *  1. Distanza di Manhattan (usata nella classe `astarClassico`)
 *  2. Distanza di Manhattan + conflitti lineari (usata nella classe `astarTERMINALE`)
 *
 * Il benchmark viene eseguito su un insieme predefinito di 5 configurazioni iniziali (4x4),
 * specificate nell'array `configs`, ciascuna identificata da un nome (es. "Conf1").
 *
 * Per ogni configurazione:
 * - Viene eseguito A* 10 volte per ciascuna euristica.
 * - Il primo run è considerato di "warm-up" e non viene incluso nel tempo medio.
 * - Viene misurato il tempo medio (in nanosecondi) di esecuzione per ciascuna euristica.
 *
 * I risultati vengono salvati in un file CSV (`benchmark.csv`) nel seguente formato:
 * 
 *     Configurazione,Tempo_Manhattan_ns,Tempo_LinearConflict_ns
 * 
 * Requisiti:
 * - Le classi `Board`, `astarClassico` e `astarTERMINALE` devono essere definite separatamente.
 * - Le configurazioni devono essere risolvibili, altrimenti il benchmark si interrompe con un messaggio.
 *
 * Il benchmark è utile per valutare l’impatto delle diverse euristiche sul tempo di esecuzione dell’algoritmo A*.
 */


import java.io.FileWriter;          
import java.io.IOException;

public class benchmark {

    String[] configNames = {"Conf1", "Conf2", "Conf3", "Conf4", "Conf5"};
    int[][][] configs = {
        {{3,9,1,15},{14,11,16,6},{13,4,10,12},{2,7,8,5}},
        {{6,13,7,10},{8,9,11,16},{15,2,12,5},{14,3,1,4}},
        {{16,12,9,13},{15,11,10,14},{3,7,2,5},{4,8,6,1}},
        {{13,2,10,3},{1,12,8,4},{5,16,9,6},{15,14,11,7}},
        {{2,1,3,4},{5,6,7,8},{9,10,11,15},{13,14,16,12}},

    };
    long[] manhattanTimes;
    long[] lcTimes;
    String filename = "benchmark.csv";

    public benchmark() {
        manhattanTimes = new long[configNames.length];
        lcTimes = new long[configNames.length];
    }

    public void writeCSV() {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Configurazione,Tempo_Manhattan_ns,Tempo_LinearConflict_ns\n");
            for (int i = 0; i < configNames.length; i++) {
                writer.write(configNames[i] + "," + manhattanTimes[i] + "," + lcTimes[i] + "\n");
            }
            System.out.println("Benchmark salvato su " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void measure() {
        int runs = 10;
        for (int k = 0; k < 5; k++) {
            int[][] tiles = configs[k];
            long totalManhattan = 0;
            long totalLC = 0;

            // Misura tempo con Manhattan
            for (int i = 0; i < runs; i++) {
                Board board = new Board(tiles, 4);
                if (!board.isSolvable()) {
                    System.out.println("Configurazione " + configNames[k] + " non risolvibile.");
                    return;
                }
                long start = System.nanoTime();
                astarClassico soluzione = new astarClassico(board,4);
                // soluzione.solution(); // eventualmente usa la soluzione
                long end = System.nanoTime();
                if (i > 0) totalManhattan += (end - start); // skip primo run (warm-up)
            }
            manhattanTimes[k] = (totalManhattan / (long)(runs - 1));

            // Misura tempo con Linear Conflict
            for (int i = 0; i < runs; i++) {
                Board board = new Board(tiles, 4);
                if (!board.isSolvable()) {
                    System.out.println("Configurazione " + configNames[k] + " non risolvibile.");
                    return;
                }
                long start = System.nanoTime();
                astarTERMINALE soluzione = new astarTERMINALE(board, 4);
                // soluzione.solution(); // eventualmente usa la soluzione
                long end = System.nanoTime();
                if (i > 0) totalLC += (end - start);
            }
            lcTimes[k] = (totalLC / (long)(runs - 1));
        }
        writeCSV();
    }

    public static void main(String[] args) {
        benchmark bench = new benchmark();
        bench.measure();
    }
}
