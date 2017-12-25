import java.util.Random;

public class SharedMax {

  private volatile int maxIndex = -1;
  private volatile int maxValue = Integer.MIN_VALUE;

  public int getMaxIndex() {
    return maxIndex;
  }

  public int getMaxValue() {
    return maxValue;
  }

  public void updateMax(int index, int number) {
    if (maxValue < number) {
      maxIndex = index;
      maxValue = number;
    }
  }

  public static void main(String[] args) {
    //Initialize an array with numbers random ints between 0 and
    //maxNumber (inclusive)
    int numbers = 1_000_000;
    int maxNumber = 10_000_000;
    int[] numberArr = new int[numbers];

    //use a seed for reproducable results.
    Random rnd = new Random(42L);
    for (int i = 0; i < numbers; ++i) {
      numberArr[i] = rnd.nextInt(maxNumber + 1);
    }

    SharedMax sm = new SharedMax();

    //Use sm to find the index of largest number.
    int numThreads = 100;
    Thread[] threads = new Thread[numThreads];
    for (int i = 0; i < numThreads; ++i) {
      int threadId = i;
      Thread t = new Thread(() -> {
        for (int j = 0; j < numbers / numThreads; ++j) {
          int idx = threadId * (numbers / numThreads) + j;
          sm.updateMax(idx, numberArr[idx]);
        }
      });
      threads[i] = t;
    }

    for (Thread t : threads) {
      t.start();
    }

    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        throw new RuntimeException("Someone interrupted the main thread. This really should not have happened");
      }
    }
    System.out.printf("The max value is: %d, at index: %d%n", sm.getMaxValue(), sm.getMaxIndex());
  }
}
