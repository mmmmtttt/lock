import java.util.Arrays;

/**
 * 基于bakery algorithm实现多线程的互斥锁
 */
public class BakeryLock {
    volatile boolean[] choosing;
    volatile int[] order;
    volatile int size;//当前数组大小

    public BakeryLock() {
        choosing = new boolean[size];
        order = new int[size];
    }

    public void lock() {
        int id = (int) Thread.currentThread().getId();
        choosing[id] = true;
        order[id] = Arrays.stream(order).max().getAsInt() + 1;
        choosing[id] = false;
        for (int i = 0; i < size; i++) {
            while (choosing[i]) ;//等i线程选完
            while (order[i] != 0 && (order[i] < order[id] || (order[i] == order[id] && i < id))) ;
        }
    }

    public void unLock() {
        int id = (int) Thread.currentThread().getId();
        order[id] = 0;
    }
}
