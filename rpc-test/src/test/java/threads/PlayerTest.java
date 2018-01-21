package threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class PlayerTest {
    public static void main(String[] args) {
        //运动员跑步，开始准备所有运动员一起，所有运动员完成表示运动结束
        ExecutorService executorService= Executors.newFixedThreadPool(10);
        Semaphore semaphore=new Semaphore(1,false);
        for (int i=0;i<200;i++){
            final int a=i;
            executorService.execute(()->{
                try {
                    semaphore.acquire();
                    System.out.println(a+"-------------------"+Thread.currentThread().getName());
                    Thread.sleep(100);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();

    }
}
class Player{
    public void run(int a){
        System.out.println(a+"-------------------"+Thread.currentThread().getName());
    }
}