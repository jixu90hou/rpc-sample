package hystrix;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

interface ICommand {
    boolean execute() throws InterruptedException;
}
interface DevCommand{
    abstract boolean dev() throws TimeoutException;
}

abstract class AbstractCommand implements ICommand {
    private Object lock = new Object();
    protected volatile boolean testFinished = false;

    abstract boolean dev() throws TimeoutException;

    abstract boolean test(boolean testFinished);

    abstract boolean fixBug();
    private void retryDev(int times,DevCommand devCommand){
        Object retryLock = new Object();
        for (int i = 0; i < times; i++) {
            boolean success = false;
            try {
                success = devCommand.dev();
            } catch (TimeoutException e) {
                //重试一下
                System.out.println("========重试一下========"+(times-i));
                continue;
            }
            if(success){
               break; //开发完成，直接退出
            }
            System.out.println("success:"+success);
        }
    }
 /*   private void retry(int times, Function<Object, Boolean> function) {
        Object retryLock = new Object();
        for (int i = 0; i < times; i++) {
            boolean result = function.apply(null);
            if (!result) {//如果没有开发完成，重试几次
                synchronized (retryLock) {
                    try {
                        System.out.println("count down times:" + (times - i));
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
*/
    @Override
    public boolean execute() throws InterruptedException {
        retryDev(3,()->dev());
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
    boolean dev() throws TimeoutException{
        System.out.println(".........remote request........");
        boolean success = false;
        Future<Boolean> future = executorService.submit(() -> response());
        try {
           return future.get();
            //e.printStackTrace();
        } catch (InterruptedException e) {
          //  e.printStackTrace();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof TimeoutException){
                throw new TimeoutException();
            }
        }
        return success;
    }

    public boolean response() throws TimeoutException, InterruptedException {
        String requestId = UUID.randomUUID().toString();
        timeoutConsumerQueue.put(new TimeoutMessage(requestId, 3, TimeUnit.SECONDS));
        //模拟远程调用消耗时间
        Random random=new Random();
        int num=random.nextInt(2)+4;
        System.out.println("num:"+num);
        TimeUnit.SECONDS.sleep(num);
        timeoutConsumerQueue.checkTimeout(requestId);
        return true;
    }

    @Override
    boolean test(boolean testFinished) {
        System.out.println("test success");
        return false;
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
        System.out.println("success:" + success);
    }
}


class Test {
    private DelayQueue delayQueue = new DelayQueue();


    public String print(Integer num) {
        return "print message " + num;
    }

    public static void main(String[] args) {
        Test test = new Test();

        boolean success = test.response();
        if (success) {

        }

        if (test.response()) {

        }
        // test.printB((num)->"print "+num);
        //  test.printA();
    }

    public boolean response() {
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

}

