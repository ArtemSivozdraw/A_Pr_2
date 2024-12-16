import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        long startTime = System.currentTimeMillis();    // лічильник часу виконання програми

        int minRange = 0;           // мінімальне та максимальне значення елементів масиву
        int maxRange = 100;

        Random random = new Random();
        int size = random.nextInt(21) + 40;     // генерування випадкової довжини масиву
        int[] array = new int[size];                   // створення масиву

        for (int i = 0; i < size; i++) {                                                // наповнення масиву випадковими даними
            array[i] = random.nextInt(maxRange - minRange + 1) + minRange;
        }

        System.out.println("Згенерований масив: " + Arrays.toString(array));

        int numThreads = 4;                                                                 // Кількість потоків
        int interval_length = (int) Math.ceil(((double)size / numThreads));                 // Довжина відрізку який буде обробляти кожен потк
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);         // Створення фіксованого пулу потоків

        List<Future<List<Integer>>> futures = new ArrayList<>();                // Створення списку об'єктів Future, що будуть містити в собі результат обробри потока

        for (int i = 0; i < numThreads; i++) {
            int start = i * interval_length;                                                            // початок відрізку
            int end = Math.min(start + interval_length, size);                                          // кінець відрізку
            Callable<List<Integer>> task = new SuperCallable(Arrays.copyOfRange(array, start, end));    // Передача потоку завдачі
            futures.add(executorService.submit(task));                                                  // запуск потока
        }

        List<Integer> results = new CopyOnWriteArrayList<>();       // Список для зберігання кінцевого результату
        for (Future<List<Integer>> future : futures) {
            // тут мала б бути перевірка isDone(), але метод .get() і так повертає значення тільки тоді коли потік завершить роботу
            // тому я не придумав куди цю перевірку вставити
            results.addAll(future.get());                           // Передача до фінального результату, результат роботи кожного потоку
        }

        System.out.println("Результати обробки масиву: " + results);

        long endTime = System.currentTimeMillis();                                          // Лічильник часу виконання програми
        System.out.println("Час виконання програми: " + (endTime - startTime) + " мс");

        executorService.shutdown();                                                         // Закриття пулу потоків
    }

    static class SuperCallable implements Callable<List<Integer>> {
        private final int[] part;

        public SuperCallable(int[] part) {
            this.part = part;
        }

        @Override
        public List<Integer> call() {                                       // Звичайна, стандартна, скучна функція обрахування (а1*а2),(а3*а4)...
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < part.length - 1; i += 2) {
                int product = part[i] * part[i + 1];
                result.add(product);
            }
            return result;
        }
    }
}
