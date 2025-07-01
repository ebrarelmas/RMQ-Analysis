/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rmqanalysis;

/**
 *
 * @author Ebrar Yıldız
 */
//subquery boyutu değiştirme hepsi

import java.util.Random;
import java.util.Arrays;

public class RMQQueryTest {

    public static void main(String[] args) {
        int size = 50; // Array size
        int maxVal = 100; // Max value for random numbers

        // Generate arrays
        int[] unsortedArray = generateRandomArray(size, maxVal);
        int[] sortedArray = Arrays.copyOf(unsortedArray, size);
        Arrays.sort(sortedArray);
        int[] reverseSortedArray = reverseArray(sortedArray);

        // Array types
        int[][] arrays = {unsortedArray, sortedArray, reverseSortedArray};
        String[] arrayTypes = {"Unsorted", "Sorted", "Reverse Sorted"};

        // Query ranges to test different subarray sizes
        int[][] queryRanges = {
            {10, 15},  // Narrow range
            {5, 20},   // Medium range
            {0, 49}    // Full range
        };

        // Test each array type
        for (int i = 0; i < arrays.length; i++) {
            int[] array = arrays[i];
            System.out.println("Testing " + arrayTypes[i] + " Array\n");

            for (int[] range : queryRanges) {
                int left = range[0];
                int right = range[1];

                System.out.println("Testing Range: (" + left + ", " + right + ")\n");

                // Naive RMQ
                long startTime = System.nanoTime();
                int naiveMin = naiveRMQ(array, left, right);
                long endTime = System.nanoTime();
                System.out.println("Naive RMQ Min: " + naiveMin + " | Time: " + (endTime - startTime) + " ns");

                // Precompute All
                startTime = System.nanoTime();
                int[][] precompute = precomputeAll(array);
                endTime = System.nanoTime();
                System.out.println("Precompute All Build Time: " + (endTime - startTime) + " ns");

                startTime = System.nanoTime();
                int precomputeMin = queryPrecompute(precompute, left, right);
                endTime = System.nanoTime();
                System.out.println("Precompute All Query Min: " + precomputeMin + " | Time: " + (endTime - startTime) + " ns");

                // Sparse Table
                startTime = System.nanoTime();
                int[][] sparseTable = buildSparseTable(array);
                endTime = System.nanoTime();
                System.out.println("Sparse Table Build Time: " + (endTime - startTime) + " ns");

                startTime = System.nanoTime();
                int sparseMin = querySparseTable(sparseTable, array, left, right);
                endTime = System.nanoTime();
                System.out.println("Sparse Table Query Min: " + sparseMin + " | Time: " + (endTime - startTime) + " ns");

                // Blocking RMQ
                startTime = System.nanoTime();
                BlockingRMQ blockingRMQ = new BlockingRMQ(array);
                endTime = System.nanoTime();
                System.out.println("Blocking RMQ Build Time: " + (endTime - startTime) + " ns");

                startTime = System.nanoTime();
                int blockingMin = blockingRMQ.query(left, right);
                endTime = System.nanoTime();
                System.out.println("Blocking RMQ Query Min: " + blockingMin + " | Time: " + (endTime - startTime) + " ns");

                System.out.println();
            }
        }
    }

    public static int[] generateRandomArray(int size, int maxVal) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(maxVal);
        }
        return array;
    }

    public static int[] reverseArray(int[] array) {
        int[] reversed = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    public static int naiveRMQ(int[] array, int left, int right) {
        int min = Integer.MAX_VALUE;
        for (int i = left; i <= right; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static int[][] precomputeAll(int[] array) {
        int n = array.length;
        int[][] table = new int[n][n];
        for (int i = 0; i < n; i++) {
            table[i][i] = array[i];
            for (int j = i + 1; j < n; j++) {
                table[i][j] = Math.min(table[i][j - 1], array[j]);
            }
        }
        return table;
    }

    public static int queryPrecompute(int[][] table, int left, int right) {
        return table[left][right];
    }

    public static int[][] buildSparseTable(int[] array) {
        int n = array.length;
        int log = (int) Math.ceil(Math.log(n) / Math.log(2));
        int[][] table = new int[n][log + 1];

        for (int i = 0; i < n; i++) {
            table[i][0] = array[i];
        }

        for (int j = 1; (1 << j) <= n; j++) {
            for (int i = 0; i + (1 << j) - 1 < n; i++) {
                table[i][j] = Math.min(table[i][j - 1], table[i + (1 << (j - 1))][j - 1]);
            }
        }
        return table;
    }

    public static int querySparseTable(int[][] sparseTable, int[] array, int left, int right) {
        int length = right - left + 1;
        int k = (int) (Math.log(length) / Math.log(2));
        return Math.min(sparseTable[left][k], sparseTable[right - (1 << k) + 1][k]);
    }

    static class BlockingRMQ {
        private int[] array;
        private int blockSize;
        private int[] blockMins;

        public BlockingRMQ(int[] array) {
            this.array = array;
            this.blockSize = (int) Math.sqrt(array.length);
            int numBlocks = (int) Math.ceil((double) array.length / blockSize);
            blockMins = new int[numBlocks];

            for (int i = 0; i < numBlocks; i++) {
                int blockMin = Integer.MAX_VALUE;
                for (int j = i * blockSize; j < Math.min((i + 1) * blockSize, array.length); j++) {
                    blockMin = Math.min(blockMin, array[j]);
                }
                blockMins[i] = blockMin;
            }
        }

        public int query(int left, int right) {
            int leftBlock = left / blockSize;
            int rightBlock = right / blockSize;
            int min = Integer.MAX_VALUE;

            if (leftBlock == rightBlock) {
                for (int i = left; i <= right; i++) {
                    min = Math.min(min, array[i]);
                }
            } else {
                for (int i = left; i < (leftBlock + 1) * blockSize; i++) {
                    min = Math.min(min, array[i]);
                }
                for (int i = leftBlock + 1; i < rightBlock; i++) {
                    min = Math.min(min, blockMins[i]);
                }
                for (int i = rightBlock * blockSize; i <= right; i++) {
                    min = Math.min(min, array[i]);
                }
            }

            return min;
        }
    }
}
