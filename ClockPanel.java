import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 双倒计时面板：
 * 总局时间  mm:ss
 * 当前回合时间  mm:ss
 * 使用 Swing Timer 保证线程安全；默认每秒刷新
 */
public class ClockPanel extends JPanel implements ActionListener {

    /* 初始时间（秒） */
    private final int gameTotalSecondsInit;
    private final int turnTotalSecondsInit;

    /* 剩余时间（秒）*/
    private int gameSecondsLeft;
    private int turnSecondsLeft;

    /* 组件 */
    private final JLabel gameLabel = new JLabel();
    private final JLabel turnLabel = new JLabel();
    private final Timer  timer     = new Timer(1000, this);  // 每 1 秒 tick


    public ClockPanel(int gameTotalSeconds, int turnTotalSeconds) {
        this.gameTotalSecondsInit = gameTotalSeconds;
        this.turnTotalSecondsInit = turnTotalSeconds;
        this.gameSecondsLeft = gameTotalSeconds;
        this.turnSecondsLeft = turnTotalSeconds;

        setOpaque(false); // 透明背景
        setLayout(new GridLayout(2, 1)); // 垂直两行

        gameLabel.setForeground(new Color(255, 255, 255, 128));  // 半透明白
        turnLabel.setForeground(new Color(255, 255, 255, 128));
        gameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameLabel.setFont(new Font("Serif", Font.BOLD, 16));
        turnLabel.setFont(new Font("Serif", Font.BOLD, 16));

        add(gameLabel);
        add(turnLabel);

        refreshLabels();
        timer.start();
    }


    /* API */

    /** 换手后调用：重置回合时间 */
    public void resetTurn() {
        turnSecondsLeft = turnTotalSecondsInit;
        refreshLabels();
    }

    /** 暂停计时（如对局暂定、网络等待） */
    public void pause() {
        if (timer.isRunning()) timer.stop();
    }

    /** 继续计时 */
    public void resume() {
        if (!timer.isRunning()) timer.start();
    }

    /** 彻底停止计时（对局结束） */
    public void stop() {
        timer.stop();
    }

    /* Timer tick */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameSecondsLeft > 0) gameSecondsLeft--;
        if (turnSecondsLeft > 0) turnSecondsLeft--;
        refreshLabels();
    }




    /* 工具 */
    private void refreshLabels() {
        gameLabel.setText("局  " + format(gameSecondsLeft));
        turnLabel.setText("回  " + format(turnSecondsLeft));
    }

    private static String format(int sec) {
        int m = sec / 60, s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }
}