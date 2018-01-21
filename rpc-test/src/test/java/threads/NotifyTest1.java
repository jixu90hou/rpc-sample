package threads;

public class NotifyTest1 {
    public static void main(String[] args) throws InterruptedException {
        NotifyEntity notifyEntity = new NotifyEntity();
      /*  for (int i = 0; i < 1; i++) {
            final int value = i;
            new Thread(() -> {
                notifyEntity.say(value);
            }).start();
        }*/
        int countDownNum = 100;
        Object lock = new Object();
        Thread thread1 = new Thread(new CountDownRunner1(lock, countDownNum), "奇数");
        Thread thread2 = new Thread(new CountDownRunner2(lock, countDownNum), "偶数");
        thread1.start();
        thread2.start();

    }
}

class CountDownRunner1 implements Runnable {
    private int countDownNum;
    private Object lock;

    public CountDownRunner1(Object lock, int countDownNum) {
        this.lock = lock;
        this.countDownNum = countDownNum;
    }

    @Override
    public void run() {
        synchronized (lock) {
            while (countDownNum > 0) {
                countDownNum--;
                if (countDownNum % 2 == 1) {
                    System.out.println(Thread.currentThread().getName() + "\t 奇数 countDownNum:" + countDownNum);
                    lock.notify();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}

class CountDownRunner2 implements Runnable {
    private Object lock;
    private int countDownNum;

    public CountDownRunner2(Object lock, int countDownNum) {
        this.lock = lock;
        this.countDownNum = countDownNum;
    }

    @Override
    public void run() {
        synchronized (lock) {
            while (countDownNum > 0) {
                countDownNum--;
                if (countDownNum % 2 == 0) {
                    System.out.println(Thread.currentThread().getName() + "\t 偶数 countDownNum:" + countDownNum);
                    lock.notify();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

class NotifyEntity {
    public void down(int num) {
        while (num > 0) {
            System.out.println(Thread.currentThread().getName() + "\t print:" + num);
        }
    }

    private static Object lock = new Object();

    public synchronized void say(int value) {
        try {
            // System.out.println("exist");
            //this.notify();
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("value:" + value);
    }

}
