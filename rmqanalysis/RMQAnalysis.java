/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package rmqanalysis;

/**
 *
 * @author Ebrar Yıldız
 */
import java.util.*;

// SORTED UNSORTED REVERSE ÇALIŞMA SÜRESİ KARŞILAŞTIRMA normal
public class RMQAnalysis {

    // Helper to generate arrays
    public static int[] generateSortedArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }
        return array;
    }

    public static int[] generateReverseSortedArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = size - i - 1;
        }
        return array;
    }

    public static int[] generateRandomArray(int size, int maxVal) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(maxVal);
        }
        return array;
    }

    // Naive (Precompute None) Implementation
    public static int naiveRMQ(int[] array, int L, int R) {
        int min = Integer.MAX_VALUE;
        for (int i = L; i <= R; i++) {
            min = Math.min(min, array[i]);
        }
        return min;
    }

    // Precompute All Implementation
    public static int[][] precomputeAll(int[] array) {
        int n = array.length;
        int[][] minTable = new int[n][n];
        for (int i = 0; i < n; i++) {
            minTable[i][i] = array[i];
            for (int j = i + 1; j < n; j++) {
                minTable[i][j] = Math.min(minTable[i][j - 1], array[j]);
            }
        }
        return minTable;
    }

    public static int queryPrecomputeAll(int[][] minTable, int L, int R) {
        return minTable[L][R];
    }

    // Sparse Table Implementation
    public static int[][] buildSparseTable(int[] array) {
        int n = array.length;
        int log = (int) Math.floor(Math.log(n) / Math.log(2)) + 1;
        int[][] sparseTable = new int[n][log];

        for (int i = 0; i < n; i++) {
            sparseTable[i][0] = array[i];
        }

        for (int j = 1; (1 << j) <= n; j++) {
            for (int i = 0; i + (1 << j) - 1 < n; i++) {
                sparseTable[i][j] = Math.min(sparseTable[i][j - 1], sparseTable[i + (1 << (j - 1))][j - 1]);
            }
        }
        return sparseTable;
    }

    public static int querySparseTable(int[][] sparseTable, int[] logTable, int L, int R) {
        int log = logTable[R - L + 1];
        return Math.min(sparseTable[L][log], sparseTable[R - (1 << log) + 1][log]);
    }

    // Blocking (Square Root Decomposition) Implementation
    public static int[] buildBlockRMQ(int[] array) {
        int n = array.length;
        int blockSize = (int) Math.sqrt(n);
        int blockCount = (n + blockSize - 1) / blockSize;
        int[] blockMin = new int[blockCount];
        Arrays.fill(blockMin, Integer.MAX_VALUE);

        for (int i = 0; i < n; i++) {
            int blockIndex = i / blockSize;
            blockMin[blockIndex] = Math.min(blockMin[blockIndex], array[i]);
        }
        return blockMin;
    }

    public static int queryBlockRMQ(int[] array, int[] blockMin, int L, int R) {
        int n = array.length;
        int blockSize = (int) Math.sqrt(n);
        int leftBlock = L / blockSize;
        int rightBlock = R / blockSize;
        int min = Integer.MAX_VALUE;

        if (leftBlock == rightBlock) {
            for (int i = L; i <= R; i++) {
                min = Math.min(min, array[i]);
            }
        } else {
            for (int i = L; i < (leftBlock + 1) * blockSize; i++) {
                min = Math.min(min, array[i]);
            }
            for (int i = leftBlock + 1; i < rightBlock; i++) {
                min = Math.min(min, blockMin[i]);
            }
            for (int i = rightBlock * blockSize; i <= R; i++) {
                min = Math.min(min, array[i]);
            }
        }
        return min;
    }

    public static void main(String[] args) {
        int size = 50; // Size of the array
        int maxVal = 100; // Maximum value in random array

        int[] sortedArray = generateSortedArray(size);
        int[] reverseSortedArray = generateReverseSortedArray(size);
        int[] randomArray = generateRandomArray(size, maxVal);

        // Arrays to test
        int[][] arrays = { sortedArray, reverseSortedArray, randomArray };
        String[] arrayTypes = { "Sorted", "Reverse Sorted", "Random" };

        int L = 20; // Fixed start index for queries
        int R = 30; // Fixed end index for queries

        for (int a = 0; a < arrays.length; a++) {
            int[] array = arrays[a];
            System.out.println("\nTesting on " + arrayTypes[a] + " Array:");

            // Naive Approach
            long start = System.nanoTime();
            naiveRMQ(array, L, R);
            long end = System.nanoTime();
            System.out.println("Naive RMQ Time: " + (end - start) / 1e6 + " ms");

            // Precompute All
            start = System.nanoTime();
            int[][] minTable = precomputeAll(array);
            end = System.nanoTime();
            System.out.println("Precompute All Build Time: " + (end - start) / 1e6 + " ms");

            start = System.nanoTime();
            queryPrecomputeAll(minTable, L, R);
            end = System.nanoTime();
            System.out.println("Precompute All Query Time: " + (end - start) / 1e6 + " ms");

            // Sparse Table
            start = System.nanoTime();
            int[][] sparseTable = buildSparseTable(array);
            end = System.nanoTime();
            System.out.println("Sparse Table Build Time: " + (end - start) / 1e6 + " ms");

            int[] logTable = new int[size + 1];
            for (int i = 2; i <= size; i++) {
                logTable[i] = logTable[i / 2] + 1;
            }

            start = System.nanoTime();
            querySparseTable(sparseTable, logTable, L, R);
            end = System.nanoTime();
            System.out.println("Sparse Table Query Time: " + (end - start) / 1e6 + " ms");

            // Blocking
            start = System.nanoTime();
            int[] blockMin = buildBlockRMQ(array);
            end = System.nanoTime();
            System.out.println("Blocking Build Time: " + (end - start) / 1e6 + " ms");

            start = System.nanoTime();
            queryBlockRMQ(array, blockMin, L, R);
            end = System.nanoTime();
            System.out.println("Blocking Query Time: " + (end - start) / 1e6 + " ms");
        }
    }
}
