public class Dinning {
    public static void main(String[] args) throws Exception {
        Philosopher[] philosophers = new Philosopher[5];
        Object[] forks = new Object[philosophers.length];
        for (int i = 0; i < forks.length; i++) {
            // initialize fork object
            forks[i] = new BakeryLock();//给每个fork加锁
        }
        for (int i = 0; i < philosophers.length; i++) {
            // initialize Philosopher object
            int right = i + 1 == 5 ? 0 : i + 1;
            philosophers[i] = new Philosopher(forks[i], forks[right]);
            new Thread(philosophers[i]).start();
        }
    }
}
