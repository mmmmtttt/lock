/**
 * 模拟哲学家的吃饭、思考行为
 */
public class Philosopher implements Runnable {
    private final Object leftFork;
    private final Object rightFork;
    static volatile int concurrent = 4;//最多同一时间允许4个哲学家去尝试拿起叉子
    static PetersonLayeredLock permittedTrying = new PetersonLayeredLock();//限制同时只能有一个线程去修改concurrent

    Philosopher(Object left, Object right) {
        this.leftFork = left;
        this.rightFork = right;
    }

    private void doAction(String action) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " " +
                action);
        Thread.sleep(((int) (Math.random() * 100)));
    }

    @Override
    public void run() {
        try {
            while(true){
                doAction(System.nanoTime() + ": Thinking"); // thinking
                permittedTrying.lock();
                if (concurrent>0){
                    concurrent--;
                }else{//已经有四个哲学家正在竞争叉子
                    permittedTrying.unLock();
                    continue;
                }
                //允许尝试拿起叉子
                permittedTrying.unLock();
                ((BakeryLock) leftFork).lock();
                doAction(System.nanoTime()+": Pick up left fork");
                ((BakeryLock)rightFork).lock();
                doAction(System.nanoTime()+": Pick up right fork - eating");
                ((BakeryLock)rightFork).unLock();
                doAction(System.nanoTime()+": Put down right fork");
                ((BakeryLock)leftFork).unLock();
                doAction(System.nanoTime()+": Put down left fork");
                permittedTrying.lock();
                concurrent++;//退出资源的竞争
                permittedTrying.unLock();
            }
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
