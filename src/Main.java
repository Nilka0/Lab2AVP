import java.util.Arrays;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) {
        int[] arr = generateRandomArray(30000000);


        int[] arr1 = arr;
        long startTime = System.currentTimeMillis();
        Arrays.sort(arr);
        long endTime = System.currentTimeMillis();
        System.out.println("Время выполнения обычной сортировки: " + (endTime - startTime) + " миллисекунд");


        System.out.println("Первые 50 чисел из обычно отсортированного массива:");
        for (int i = 0; i < 50; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();

        ForkJoinPool pool = new ForkJoinPool();
        startTime = System.currentTimeMillis();
        int[] sortedArray = pool.invoke(new ParallelSortTask(arr1));
        endTime = System.currentTimeMillis();
        System.out.println("Время выполнения параллельной сортировки: " + (endTime - startTime) + " миллисекунд");


        System.out.println("Первые 50 чисел из параллельно отсортированного массива:");
        for (int i = 0; i < 50; i++) {
            System.out.print(sortedArray[i] + " ");
        }
        System.out.println();
    }

    public static int[] generateRandomArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = (int) (Math.random() * size * 10);
        }
        return arr;
    }

    static class ParallelSortTask extends RecursiveTask<int[]> {
        private int[] array;

        ParallelSortTask(int[] array) {
            this.array = array;
        }

        @Override
        protected int[] compute() {
            if (array.length <= 1000) {
                Arrays.sort(array);
                return array;
            } else {
                int mid = array.length / 2;
                int[] left = Arrays.copyOfRange(array, 0, mid);
                int[] right = Arrays.copyOfRange(array, mid, array.length);

                ParallelSortTask leftTask = new ParallelSortTask(left);
                ParallelSortTask rightTask = new ParallelSortTask(right);

                leftTask.fork();
                rightTask.fork();

                int[] leftResult = leftTask.join();
                int[] rightResult = rightTask.join();

                return merge(leftResult, rightResult);
            }
        }

        private int[] merge(int[] left, int[] right) {
            int[] result = new int[left.length + right.length];
            int i = 0, j = 0, k = 0;
            while (i < left.length && j < right.length) {
                if (left[i] < right[j]) {
                    result[k++] = left[i++];
                } else {
                    result[k++] = right[j++];
                }
            }
            while (i < left.length) {
                result[k++] = left[i++];
            }
            while (j < right.length) {
                result[k++] = right[j++];
            }
            return result;
        }
    }
}