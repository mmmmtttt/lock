/**
 * 综合peterson和filter算法，分多层的锁，只有在每一层都得到锁才可以最终获得锁
 */
public class PetersonLayeredLock {
    PetersonLock[] petersonLocks;
    int depth;

    PetersonLayeredLock() {
        int threads = 20;
        depth = (int) Math.ceil(Math.log(threads) / Math.log(2));//二叉树的深度
        int size = (1 << depth);//用数组储存二叉树，根节点在index=1处
        petersonLocks = new PetersonLock[size];
        for (int i = 1; i < size; i++) {
            petersonLocks[i] = new PetersonLock();
        }
    }

    PetersonLayeredLock(int threads) {
        depth = (int) Math.ceil(Math.log(threads) / Math.log(2));//二叉树的深度
        int size = (1 << depth);//用数组储存二叉树，根节点在index=1处
        petersonLocks = new PetersonLock[size];
        for (int i = 1; i < size; i++) {
            petersonLocks[i] = new PetersonLock();
        }
    }

    public void lock() {
        int id = getId();//当前所在位置
        int lock = id + (1 << depth);//要竞争的锁
        int layer = depth;
        while (layer > 0) {//没有赢得根节点的锁时
            lock = lock / 2;//计算父节点的index
            petersonLocks[lock].lock(id % 2);//尝试得到父节点的锁（和兄弟节点竞争）
            id = lock;
            layer--;
        }
    }

    public void unLock() {
        int id = getId();
        int[] lockOrder = new int[depth + 1];
        lockOrder[depth] = id + (1 << depth);
        for (int i = depth - 1; i >= 0; i--) {
            lockOrder[i] = lockOrder[i + 1] / 2;
        }
        for (int i = 0; i < depth; i++) {
            petersonLocks[lockOrder[i]].unLock(lockOrder[i + 1] % 2);
        }
    }

    private int getId() {
        return Integer.parseInt(Thread.currentThread().getName().replace("Thread-", ""));
    }
}

class PetersonLock {
    volatile boolean[] interested;
    volatile int turn;

    PetersonLock() {
        interested = new boolean[2];
    }

    public void lock(int i) {//i可能是0或1
        interested[i] = true;
        turn = i ^ 1;//礼让对方先获得锁
        while (interested[i ^ 1] && turn == (i ^ 1)) ;//如果另一个线程在上一步后修改了turn，就可以先获得锁
        //自己获得锁
    }

    public void unLock(int i) {
        interested[i] = false;
    }
}
