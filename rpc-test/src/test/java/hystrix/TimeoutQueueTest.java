package hystrix;

import java.util.UUID;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class TimeoutMessage implements Delayed {
    private long countDownTime;//到期时间
    private TimeUnit unit;
    private String requestId;

    public TimeoutMessage(String requestId, long countDownTime, TimeUnit unit) {
        //到期后的时间=开始设置的时间+传入的当前时间
        this.countDownTime = TimeUnit.MILLISECONDS.convert(countDownTime, unit) + System.currentTimeMillis();
        this.unit = unit;
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        //到期后的时间-设置的时间
        long delayTime = this.countDownTime - System.currentTimeMillis();
        return delayTime;
    }

    @Override
    public int compareTo(Delayed o) {
        TimeoutMessage otherMessage = (TimeoutMessage) o;
        long diff = this.countDownTime - otherMessage.countDownTime;
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Message{" + "countDownTime=" + countDownTime + ", unit=" + unit + ", requestId='" + requestId + '\'' + '}';
    }
}

class TimeoutConsumerQueue  {
    private static DelayQueue<TimeoutMessage> queue = new DelayQueue<>();

    public static void put(TimeoutMessage message) {
        queue.put(message);
    }
    public static void checkTimeout(String requestId) throws TimeoutException {
        TimeoutMessage[] timeoutMessages=queue.toArray(new TimeoutMessage[0]);
        boolean timeout=true;
        for (TimeoutMessage timeoutMessage:timeoutMessages){
            if(timeoutMessage.getRequestId().equals(requestId)){
                timeout=false;//存在了就表示没有超时
            }
        }
        if(timeout){
            throw new TimeoutException();
        }
    }

    public static void run() {
        while (true) {
            TimeoutMessage message = null;
            try {
                message = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(message);
            System.out.println("remain num:" + queue.size());
        }

    }

}
class TimeoutMessageConsumerQueue  implements Runnable{
    private static DelayQueue<TimeoutMessage> queue = new DelayQueue<>();

    public void put(TimeoutMessage message) {
        queue.put(message);
    }
    public void checkTimeout(String requestId) throws TimeoutException {
        TimeoutMessage[] timeoutMessages=queue.toArray(new TimeoutMessage[0]);
        boolean timeout=true;
        for (TimeoutMessage timeoutMessage:timeoutMessages){
            if(timeoutMessage.getRequestId().equals(requestId)){
                timeout=false;//存在了就表示没有超时
            }
        }
        if(timeout){
            throw new TimeoutException();
        }
    }
    @Override
    public void run() {
        while (true) {
            TimeoutMessage message = null;
            try {
                message = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(message);
            System.out.println("remain num:" + queue.size());
        }

    }

}


public class TimeoutQueueTest {
    public static void main(String[] args) {
        new Thread(()->TimeoutConsumerQueue.run()).start();
        String requestId = UUID.randomUUID().toString();
        String requestId1 = UUID.randomUUID().toString();

        TimeoutMessage timeoutMessage = new TimeoutMessage(requestId, 2, TimeUnit.SECONDS);
        TimeoutMessage timeoutMessage1 = new TimeoutMessage(requestId1, 3, TimeUnit.SECONDS);

        TimeoutConsumerQueue.put(timeoutMessage);
        TimeoutConsumerQueue.put(timeoutMessage1);
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            TimeoutConsumerQueue.checkTimeout(requestId);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
       /* synchronized (DelayQueueTest.class) {
            try {
                DelayQueueTest.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }
}
