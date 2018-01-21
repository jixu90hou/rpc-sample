package threads;

class LockEntity {
    Object lock = new Object();
    public synchronized void print(int a) {
        synchronized (LockEntity.class){
            try {
                System.out.println("---------------------");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("print " + a);
        }
    }
}