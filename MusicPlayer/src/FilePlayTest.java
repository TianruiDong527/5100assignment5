public class FilePlayTest {

    private static FilePlayer filePlayer = new FilePlayer();

    public static void main(String[] args) {
        playDo(); // 直接调用playDo方法来播放"do"音符
    }

    // Method implementation for playing the "do" note
    private static void playDo() {
        filePlayer.play("Sounds/si.wav"); // 替换为您音符文件的实际路径
    }
}
