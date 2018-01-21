package threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownThread {
    static Object countDownLock = new Object();
    static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        int num = 10;
        ChildThread childThread = new ChildThread(num);
        for (int i = 0; i < num; i++) {
            new Thread(() -> childThread.countDown()).start();
        }
       // new Thread().interrupt();
        //executorService.execute(()->childThread.countDown());
        childThread.await();
    }
}

class ChildThread {
    private Object lock = new Object();
    private int countDown;

    public ChildThread(int countDown) {
        this.countDown = countDown;
    }

    public void await() {
        synchronized (lock) {
            try {
                System.out.println("countDown:" + countDown);
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.notify();
            }
        }

    }

    public void countDown() {
        synchronized (lock) {
            countDown--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("countDown invoke\t" + countDown);
            if (countDown == 0) {
                lock.notify();
            }

        }

    }
}
