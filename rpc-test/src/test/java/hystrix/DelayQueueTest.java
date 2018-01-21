package hystrix;

import java.util.UUID;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

class Message implements Delayed {
    private long countDownTime;//到期时间
    private TimeUnit unit;
    private String requestId;

    public Message(String requestId, long countDownTime, TimeUnit unit) {
        //到期后的时间=开始设置的时间+传入的当前时间
        this.countDownTime = TimeUnit.MILLISECONDS.convert(countDownTime, unit) + System.currentTimeMillis();
        this.unit = unit;
        this.requestId = requestId;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        //到期后的时间-设置的时间
        long delayTime = this.countDownTime - System.currentTimeMillis();
        return delayTime;
    }

    @Override
    public int compareTo(Delayed o) {
        Message otherMessage = (Message) o;
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

class ConsumerQueue implements Runnable {
    private DelayQueue<Message> queue = new DelayQueue<>();

    public void put(Message message) {
        queue.put(message);
    }
    @Override
    public void run() {
        while (true){
            Message message = null;
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

class DelayQueueConfig {

}

public class DelayQueueTest {
    public static void main(String[] args) {
        ConsumerQueue consumerQueue = new ConsumerQueue();
        new Thread(consumerQueue).start();
        String requestId = UUID.randomUUID().toString();
        String requestId1 = UUID.randomUUID().toString();
        String requestId2 = UUID.randomUUID().toString();

        Message message = new Message("1", 1, TimeUnit.SECONDS);
        consumerQueue.put(message);
        Message message1 = new Message("2", 4, TimeUnit.SECONDS);
        consumerQueue.put(message1);
        Message message2 = new Message("3", 2, TimeUnit.SECONDS);

        consumerQueue.put(message2);
        /*synchronized (DelayQueueTest.class) {
            try {
                DelayQueueTest.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }
}
