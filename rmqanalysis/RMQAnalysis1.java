/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.util.Random;
import java.util.Arrays;
/**
 *
 * @author Ebrar Yıldız
 */
// UNSORTED  ARRAYDE 500, 5000, 10000 SİZE KARŞILAŞTIRMA
public class RMQAnalysis1 {


    public static void main(String[] args) {
        // Array sizes for testing
        int[] sizes = {500, 5000, 10000};

        for (int size : sizes) {
            System.out.println("Array Size: " + size);

            // Generate and test for unsorted array
            int[] unsortedArray = generateRandomArray(size, 1000);
            System.out.println("Testing Unsorted Array:");
            testAllAlgorithms(unsortedArray);


            System.out.println("=============================================");
        }
    }

    // Function to test all algorithms on the given array
    public static void testAllAlgorithms(int[] array) {
        // Query range for testing
        int left = 100;
        int right = 200;

        // Test Naive RMQ
        long startTime = System.nanoTime();
        int naiveMin = naiveRMQ(array, left, right);
        long naiveQueryTime = System.nanoTime() - startTime;
        System.out.println("Naive RMQ Query Time: " + naiveQueryTime / 1_000_000.0 + " ms");

        // Test Precompute All
        startTime = System.nanoTime();
        int[] precomputeAllMin = precomputeAll(array);
        long precomputeAllBuildTime = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        int precomputeAllMinQuery = precomputeAllQuery(precomputeAllMin, left, right);
        long precomputeAllQueryTime = System.nanoTime() - startTime;
        System.out.println("Precompute All Build Time: " + precomputeAllBuildTime / 1_000_000.0 + " ms");
        System.out.println("Precompute All Query Time: " + precomputeAllQueryTime / 1_000_000.0 + " ms");

        // Test Sparse Table
        startTime = System.nanoTime();
        int[][] sparseTable = buildSparseTable(array);
        long sparseTableBuildTime = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        int sparseTableMinQuery = sparseTableQuery(sparseTable, left, right);
        long sparseTableQueryTime = System.nanoTime() - startTime;
        System.out.println("Sparse Table Build Time: " + sparseTableBuildTime / 1_000_000.0 + " ms");
        System.out.println("Sparse Table Query Time: " + sparseTableQueryTime / 1_000_000.0 + " ms");

        // Test Blocking RMQ
        startTime = System.nanoTime();
        int[] blockMin = buildBlocking(array);
        long blockingBuildTime = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        int blockingMinQuery = blockingQuery(array, blockMin, left, right);
        long blockingQueryTime = System.nanoTime() - startTime;
        System.out.println("Blocking Build Time: " + blockingBuildTime / 1_000_000.0 + " ms");
        System.out.println("Blocking Query Time: " + blockingQueryTime / 1_000_000.0 + " ms");

        System.out.println("---------------------------------------------");
    }

    // Generate random array
    public static int[] generateRandomArray(int size, int maxVal) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(maxVal);
        }
        return array;
    }

    // Generate sorted array
    public static int[] generateSortedArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }
        return array;
    }

    // Generate reverse sorted array
    public static int[] generateReverseSortedArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = size - i;
        }
        return array;
    }

    // Naive RMQ
    public static int naiveRMQ(int[] array, int left, int right) {
        int min = array[left];
        for (int i = left; i <= right; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    // Precompute All
    public static int[] precomputeAll(int[] array) {
        int n = array.length;
        int[] min = new int[n];
        min[0] = array[0];
        for (int i = 1; i < n; i++) {
            min[i] = Math.min(min[i - 1], array[i]);
        }
        return min;
    }

    public static int precomputeAllQuery(int[] precomputed, int left, int right) {
        return precomputed[right];
    }

    // Sparse Table
    public static int[][] buildSparseTable(int[] array) {
        int n = array.length;
        int log = (int) (Math.log(n) / Math.log(2)) + 1;
        int[][] st = new int[n][log];
        for (int i = 0; i < n; i++)
            st[i][0] = array[i];
        for (int j = 1; j < log; j++)
            for (int i = 0; i + (1 << j) - 1 < n; i++)
                st[i][j] = Math.min(st[i][j - 1], st[i + (1 << (j - 1))][j - 1]);
        return st;
    }

    public static int sparseTableQuery(int[][] st, int left, int right) {
        int j = (int) (Math.log(right - left + 1) / Math.log(2));
        return Math.min(st[left][j], st[right - (1 << j) + 1][j]);
    }

    // Blocking RMQ
    public static int[] buildBlocking(int[] array) {
        int blockSize = (int) Math.sqrt(array.length);
        int[] blockMin = new int[(array.length + blockSize - 1) / blockSize];
        Arrays.fill(blockMin, Integer.MAX_VALUE);

        for (int i = 0; i < array.length; i++) {
            int blockIndex = i / blockSize;
            blockMin[blockIndex] = Math.min(blockMin[blockIndex], array[i]);
        }
        return blockMin;
    }

    public static int blockingQuery(int[] array, int[] blockMin, int left, int right) {
        int blockSize = (int) Math.sqrt(array.length);
        int min = Integer.MAX_VALUE;
        int startBlock = left / blockSize;
        int endBlock = right / blockSize;

        if (startBlock == endBlock) {
            for (int i = left; i <= right; i++) {
                min = Math.min(min, array[i]);
            }
        } else {
            for (int i = left; i < (startBlock + 1) * blockSize; i++) {
                min = Math.min(min, array[i]);
            }
            for (int i = startBlock + 1; i < endBlock; i++) {
                min = Math.min(min, blockMin[i]);
            }
            for (int i = endBlock * blockSize; i <= right; i++) {
                min = Math.min(min, array[i]);
            }
        }

        return min;
    }
}

