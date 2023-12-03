import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class MusicPlayer {

    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition turnCondition = lock.newCondition();
    private static boolean isFirstThreadTurn = true;
    private static final FilePlayer filePlayer = new FilePlayer();

    public static void main(String[] args) {
        Thread thread1 = new Thread(new PlayDoMiSolSiDo());
        Thread thread2 = new Thread(new PlayReFaLaDo());

        thread1.start();
        thread2.start();
    }

    private static void playSound(String soundFile, boolean isFirstThread) throws InterruptedException {
        lock.lock();
        try {
            while (isFirstThreadTurn != isFirstThread) {
                turnCondition.await();
            }
            filePlayer.play(soundFile);
            Thread.sleep(1000); // Wait for 1 second after playing a sound
            isFirstThreadTurn = !isFirstThreadTurn;
            turnCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    private static class PlayDoMiSolSiDo implements Runnable {
        public void run() {
            try {
                playSound("Sounds/do.wav", true);
                playSound("Sounds/mi.wav", true);
                playSound("Sounds/sol.wav", true);
                playSound("Sounds/si.wav", true);
                filePlayer.play("Sounds/do-octave.wav"); // Last part played without waiting
                Thread.sleep(1000); // Wait for 1 second after playing the last sound
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static class PlayReFaLaDo implements Runnable {
        public void run() {
            try {
                playSound("Sounds/re.wav", false);
                playSound("Sounds/fa.wav", false);
                playSound("Sounds/la.wav", false); // Last part played without waiting
                Thread.sleep(1000); // Wait for 1 second after playing the last sound
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}