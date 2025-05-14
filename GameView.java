import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class GameView {
    /**
     * 常量参数
     */
    private static final int VIEW_WIDTH = 700, VIEW_HEIGHT = 712; // 整个窗口大小（用于 frame.setSize）
    private static final int PIECE_WIDTH = 67, PIECE_HEIGHT = 67; // 棋子图标大小
    private static final int SX_GAP = 68, SY_GAP = 68; // 把棋盘上的格子坐标（第几行第几列）转换成图形界面中像素位置。棋盘上每列/行的宽度68px
    private static final int SX_OFFSET = 50, SY_OFFSET = 15; // 显示位置的起始偏移量

    private String[][] board;
    private char player;

    private JFrame frame; // 窗口
    private JLayeredPane pane; // 图层容器，用于显示出与不同图层的棋盘棋子（下层1，上层0）
    private JLabel playerPic; // 玩家头像

    // private Map<String, JLabel> pieceObjects = new HashMap<String, JLabel>(); // 用于移动棋子

    /**
     * 初始化GUI棋盘
     * 注意！设置窗口大小部分，由于不同系统标题栏宽度不同，+28适用于macOS，+40适用于Windows下
     * @param board 给出初始化完成的整个棋盘信息
     */
    public void initView(String[][] board, char player) {
        this.board = board;
        this.player = player;

        // 创建窗口，设置属性
        frame = new JFrame("Chinese Chess");
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT + 28); // 设置窗口大小，不被标题栏遮挡
        frame.setLocationRelativeTo(null); // 居中显示
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // 关闭窗口时退出程序

        // 添加图层容器，方便分层叠加图像
        pane = new JLayeredPane();  // 创建图层容器（支持背景和前景图像分层）
        frame.add(pane); // 把图层加到窗口里

        initBoardLayer();
        initPieces(board);
        initPlayerLayer();

        // 显示窗口
        frame.setVisible(true);
    }


    /**
     * 加载背景棋盘图像
     */
    private void initBoardLayer() {
        JLabel bgBoard = new JLabel(new ImageIcon("img/board.png")); // 背景图像
        bgBoard.setLocation(0, 0); // 放在窗口左上角
        bgBoard.setSize(VIEW_WIDTH, VIEW_HEIGHT); // 设置图像大小
        // bgBoard.addMouseListener(new BoardClickListener()); // 添加点击监听器
        pane.add(bgBoard, 1); // 添加到图层：第1层是背景
    }

    private void initPieces(String[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                if(board[i][j] == null) continue;

                int[] pos = {i, j};
                int[] sPos = modelToViewConverter(pos);

                String camp = board[i][j].substring(0, 1) == "红" ? "r" : "b";
                String unit = "";
                switch (board[i][j].substring(1, 2)) {
                    case "车": unit = "j"; break;
                    case "马": unit = "m"; break;
                    case "相": case "象": unit = "x"; break;
                    case "士": case "仕": unit = "s"; break;
                    case "将": case "帅": unit = "b"; break;
                    case "炮": unit = "p"; break;
                    case "卒": case "兵": unit = "z"; break;
                    default: unit = "?"; // 异常标记
                }

                JLabel piecePic = new JLabel(new ImageIcon("img/" + camp + unit + ".png"));
                piecePic.setLocation(sPos[0], sPos[1]);
                piecePic.setSize(PIECE_WIDTH, PIECE_HEIGHT);
                pane.add(piecePic, 0);
            }
        }
    }

    /**
     * 显示玩家头像
     */
    private void initPlayerLayer() {
        playerPic = new JLabel(new ImageIcon("img/rPic.png")); // 默认是红方回合
        playerPic.setLocation(3, 320);
        playerPic.setSize(PIECE_WIDTH, PIECE_HEIGHT);
        pane.add(playerPic, 0); // 加到最上层
    }






    /**
     * 将模型坐标转换为像素坐标
     * @param pos
     * @return 像素坐标（sx，sy）
     */
    private int[] modelToViewConverter(int[] pos) {
        int sx = pos[1] * SX_GAP + SX_OFFSET, sy = pos[0] * SY_GAP + SY_OFFSET;
        return new int[]{sx, sy};
    }


}
