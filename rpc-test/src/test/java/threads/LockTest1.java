package threads;

public class LockTest1 {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            final int a = i;
            LockEntity lockEntity = new LockEntity();
            new Thread(() -> {
                lockEntity.print(a);
            }).start();
        }

    }
}

