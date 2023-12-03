import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class StarPlayer {

    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    private static boolean isThreadOneTurn = true; // Thread 1 starts first
    private static final FilePlayer FilePlayer = new FilePlayer();

    public static void main(String[] args) {
        Thread thread1 = new Thread(new PlayDoMiSolSi(), "Thread 1");
        Thread thread2 = new Thread(new PlayReFaLaDo(), "Thread 2");

        thread1.start();
        thread2.start();
    }

    private static void playSound(String soundFile) throws InterruptedException {
        lock.lock();
        try {
            // Check which thread is running and wait if it's not its turn
            while ((Thread.currentThread().getName().equals("Thread 1") && !isThreadOneTurn) ||
                    (Thread.currentThread().getName().equals("Thread 2") && isThreadOneTurn)) {
                condition.await();
            }
            FilePlayer.play(soundFile);
            Thread.sleep(1000); // Wait for 1 second after playing a sound
            // Toggle the turn
            isThreadOneTurn = !isThreadOneTurn;
            // Signal the other thread
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    private static class PlayDoMiSolSi implements Runnable {
        public void run() {
            try {
                playSound("Sounds/do.wav");
                playSound("Sounds/mi.wav");
                playSound("Sounds/sol.wav");
                playSound("Sounds/si.wav");
                // Signal the other thread to play the last "do-octave"
                lock.lock();
                try {
                    isThreadOneTurn = !isThreadOneTurn;
                    condition.signal();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static class PlayReFaLaDo implements Runnable {
        public void run() {
            try {
                // Wait for the first thread to finish its first note
                lock.lock();
                try {
                    while (isThreadOneTurn) {
                        condition.await();
                    }
                } finally {
                    lock.unlock();
                }
                playSound("Sounds/re.wav");
                playSound("Sounds/fa.wav");
                playSound("Sounds/la.wav");
                playSound("Sounds/do-octave.wav");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
