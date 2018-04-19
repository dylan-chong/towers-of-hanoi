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

class X {
  static volatile int want1 =0;
  static volatile int want2 =0;
//first worker
...while(true){
//normal operations, it can loop forever
    if(want2==-1)//pre-protocol
    {want1=-1;}//pre-protocol
    else{want1=1;}//pre-protocol
    while(want2==want1){}//pre-protocol
    try{
//critical section operations,
// termination assured
    }
    finally{want1=0;}//post-protocol
  }
//second worker
...while(true){
//normal operations, it can loop forever
    if(want1==-1)//pre-protocol
    {want2=1;}//pre-protocol
    else{want2=-1;}//pre-protocol
    while(want1==-want2){}//pre-protocol
    try{
//critical section operations,
// termination assured
    }
    finally{want2=0;}//post-protocol
  }
}
