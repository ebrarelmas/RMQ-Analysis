/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rmqanalysis;

/**
 *
 * @author Ebrar Yıldız
 */
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Ebrar Yıldız
 */
// space compl tüm array types
public class RMQAnalysis2 {


    public static void main(String[] args) {
        int size = 1000; // Array size
        int maxVal = 100; // Max value for random numbers

        // Generate arrays
        int[] unsortedArray = generateRandomArray(size, maxVal);
        int[] sortedArray = Arrays.copyOf(unsortedArray, size);
        Arrays.sort(sortedArray);
        int[] reverseSortedArray = reverseArray(sortedArray);

        // Array types
        int[][] arrays = {unsortedArray, sortedArray, reverseSortedArray};
        String[] arrayTypes = {"Unsorted", "Sorted", "Reverse Sorted"};

        // Analyze space complexity for each array type
        for (int i = 0; i < arrays.length; i++) {
            int[] array = arrays[i];
            System.out.println("Array Type: " + arrayTypes[i]);

            // Naive RMQ
            long naiveSpace = calculateNaiveRMQSpace();
            System.out.println("Naive RMQ Space Complexity: " + naiveSpace + " bytes");

            // Precompute All
            long precomputeSpace = calculatePrecomputeAllSpace(array.length);
            System.out.println("Precompute All Space Complexity: " + precomputeSpace + " bytes");

            // Sparse Table
            long sparseTableSpace = calculateSparseTableSpace(array.length);
            System.out.println("Sparse Table Space Complexity: " + sparseTableSpace + " bytes");

            // Blocking RMQ
            long blockingSpace = calculateBlockingRMQSpace(array.length);
            System.out.println("Blocking RMQ Space Complexity: " + blockingSpace + " bytes");

            System.out.println();
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

    public static long calculateNaiveRMQSpace() {
        // Naive RMQ uses O(1) additional space
        return 0;
    }

    public static long calculatePrecomputeAllSpace(int n) {
        // Precompute All uses a 2D table of size n x n
        return (long) n * n * Integer.BYTES;
    }

    public static long calculateSparseTableSpace(int n) {
        // Sparse Table uses a 2D table of size n x log(n)
        int log = (int) Math.ceil(Math.log(n) / Math.log(2));
        return (long) n * (log + 1) * Integer.BYTES;
    }

    public static long calculateBlockingRMQSpace(int n) {
        // Blocking RMQ uses block minimums array of size sqrt(n)
        int blockSize = (int) Math.sqrt(n);
        return (long) blockSize * Integer.BYTES;
    }
}
