class Example1Bad {
  private static volatile int count = 0;

  public static void main(String[] args) {
    Runnable runnable = () -> {
      count++;
      System.out.println(count);
    };
    new Thread(runnable).start();
    new Thread(runnable).start();
    // What gets printed:
    // Who knows!
  }
}

class Example1LessBad {
  private static volatile int count = 0;
  private static final Object LOCK = new Object();

  public static void main(String[] args) {
    Runnable runnable = () -> {
      synchronized (LOCK) {
        count++;
        System.out.println(count);
      }
    };
    new Thread(runnable).start();
    new Thread(runnable).start();
    // What gets printed:
    // 1
    // 2
  }
}
