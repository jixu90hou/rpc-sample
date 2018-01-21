package hystrix;

import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Function;

interface ICommand {
    boolean execute() throws InterruptedException;
}

interface DevCommand {
    boolean dev() throws TimeoutException;
}

interface TestCommand {
    boolean test() throws TimeoutException;
}

abstract class AbstractCommand implements ICommand {
    private Object lock = new Object();

    abstract boolean dev() throws TimeoutException;

    abstract boolean test() throws TimeoutException;

    abstract boolean fixBug();

    private boolean retryDev(int times, DevCommand devCommand) {
        Object retryLock = new Object();
        boolean success = false;
        for (int i = 0; i < times; i++) {
            try {
                success = devCommand.dev();
            } catch (TimeoutException e) {
                //重试一下
                System.out.println("========重试一下 dev========" + (times - i));
                continue;
            }
            if (success) {
                break; //开发完成，直接退出
            }
        }
        System.out.println("dev success:" + success);
        return success;
    }

    private boolean retryTest(int times, TestCommand testCommand) {
        Object retryLock = new Object();
        boolean success = false;
        for (int i = 0; i < times; i++) {
            try {
                success = testCommand.test();
            } catch (TimeoutException e) {
                //重试一下
                System.out.println("========重试一下 test========" + (times - i));
                continue;
            }
            if (success) {
                break; //开发完成，直接退出
            }
        }
        System.out.println("test success:" + success);
        return success;
    }

    private void retry(int times, Function<Object, Boolean> function) {
        Object retryLock = new Object();
        for (int i = 0; i < times; i++) {
            boolean result = function.apply(null);
            if (!result) {//如果没有开发完成，重试几次
                synchronized (retryLock) {
                    try {
                        System.out.println("========重试一下 test========" + (times - i));
                        retryLock.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {//开发完成，直接退出
                break;
            }
        }
    }

    @Override
    public boolean execute() throws InterruptedException {
        if (retryDev(5, () -> dev())) {
            if (retryTest(5, () -> test())){
                System.out.println("-----test success-----");
            }
        }

        return true;
    }
}

class DevCommandA extends AbstractCommand {
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    static TimeoutMessageConsumerQueue timeoutConsumerQueue = new TimeoutMessageConsumerQueue();

    static {
        new Thread(timeoutConsumerQueue).start();
    }

    @Override
    boolean dev() throws TimeoutException {
        System.out.println(".........remote dev request........");
        boolean success = false;
        Future<Boolean> future = executorService.submit(() -> res());
        try {
            return future.get(3,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }

 /*   public boolean response() throws TimeoutException, InterruptedException {
        String requestId = UUID.randomUUID().toString();
        timeoutConsumerQueue.put(new TimeoutMessage(requestId, 3, TimeUnit.SECONDS));
        //模拟远程调用消耗时间
        Random random = new Random();
        int num = random.nextInt(4) + 1;
        System.out.println("num:" + num);
        TimeUnit.SECONDS.sleep(num);
        timeoutConsumerQueue.checkTimeout(requestId);
        return true;
    }*/

    @Override
    boolean test() throws TimeoutException {
        Future<Boolean> future=executorService.submit(()->res());
        try {
            return future.get(3,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean res(){
        try {
            Random random = new Random();
            int num = random.nextInt(4) + 1;
            System.out.println("num:" + num);
            TimeUnit.SECONDS.sleep(num);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    boolean fixBug() {
        return false;
    }

    @Override
    public boolean execute() throws InterruptedException {
        //  new Thread(()->test()).start();
        return super.execute();
    }
}

public class CommandTest {
    public static void main(String[] args) throws InterruptedException {
        DevCommandA commandA = new DevCommandA();
        boolean success = commandA.execute();
        System.out.println("execute success:" + success);
    }
}

