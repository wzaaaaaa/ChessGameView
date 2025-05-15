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
    private static final int PIECE_WIDTH = 67, PIECE_HEIGHT = 67; // 棋子图标大小
    private static final int PAD = 50;
    private static final int VIEW_WIDTH = 700 + 2 * PAD, VIEW_HEIGHT = 712 + 2 * PAD; // 整个窗口大小（用于 frame.setSize）
    private static final int SCOL_GAP = 68, SROW_GAP = 68; // 把棋盘上的格子坐标（第几行第几列）转换成图形界面中像素位置。棋盘上每列/行的宽度68px
    private static final int SCOL_OFFSET = 50 + PAD, SROW_OFFSET = 15 + PAD; // 显示位置的起始偏移量

    private String[][] board;
    private boolean player;
    private boolean turn;

    private JFrame frame; // 窗口
    private JLayeredPane pane; // 图层容器，用于显示出与不同图层的棋盘棋子（下层1，上层0）
    private JLabel playerPic; // 玩家头像

    private int[] selectedBoardPos;
    private int[] selectedPiecePos;

    // private Map<String, JLabel> pieceObjects = new HashMap<String, JLabel>(); // 用于移动棋子

    /**
     * 初始化GUI棋盘
     * 注意！设置窗口大小部分，由于不同系统标题栏宽度不同，+28适用于macOS，+40适用于Windows下
     * @param board 给出初始化完成的整个棋盘信息
     */
    public void initView(String[][] board, boolean player) {
        this.player = player;
        this.board = player ? board : flipBoard(board); // 对于黑方，把他的棋盘信息翻转

        // 创建窗口，设置属性
        frame = new JFrame("Chinese Chess");
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT + 28); // 设置窗口大小，不被标题栏遮挡
        frame.setLocationRelativeTo(null); // 居中显示
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // 关闭窗口时退出程序

        // 添加图层容器，方便分层叠加图像
        pane = new JLayeredPane();  // 创建图层容器（支持背景和前景图像分层）
        frame.add(pane); // 把图层加到窗口里

        initAvatarLayer();

        initBoardLayer();
        initPieces();
        initPlayerLayer();

        // 显示窗口
        frame.setVisible(true);
    }

    /**
     * 收到新棋盘后刷新界面
     *
     * @param newBoard   最新棋盘（红方视角 board[col][row]）
     * @param nextPlayer 轮到谁走：true=红方，false=黑方
     */
    public void updateView(String[][] newBoard, boolean nextPlayer) {
        this.board = player ? newBoard : flipBoard(newBoard);
        this.player = nextPlayer;

        for (java.awt.Component c : pane.getComponents()) {
            if (pane.getLayer(c) == 0) {
                pane.remove(c);
            }
        }

        initPieces();          // 棋子
        initPlayerLayer();     // 回合标志

        pane.revalidate(); // 刷新图层
        pane.repaint();

        selectedPiecePos = null; // 清除旧的选中状态
        selectedBoardPos = null;
    }


    /**
     * 初始化两个玩家头像，此后该图层不再改动
     */
    private void initAvatarLayer() {
        // 整个游戏背景
        JLabel bg = new JLabel(new ImageIcon("img/bg.png"));
        bg.setBounds(0, 0, VIEW_WIDTH, VIEW_HEIGHT);   // ← 关键：给它占满整个窗口
        pane.add(bg, Integer.valueOf(-3));

        // 左上角头像
        JLabel avatarTL = player ? new JLabel(new ImageIcon("img/avatar_black.png")) : new JLabel(new ImageIcon("img/avatar_red.png"));
        avatarTL.setSize(30, 30);
        avatarTL.setLocation(8, 8);
        pane.add(avatarTL, Integer.valueOf(-2));

        // 右下角头像
        JLabel avatarBR = player ? new JLabel(new ImageIcon("img/avatar_red.png")) : new JLabel(new ImageIcon("img/avatar_black.png"));
        avatarBR.setSize(30, 30);
        int x = VIEW_WIDTH  - avatarBR.getWidth()  - 8; // 右侧贴边
        int y = VIEW_HEIGHT - avatarBR.getHeight() - 8; // 底部贴边
        avatarBR.setLocation(x, y);
        pane.add(avatarBR, Integer.valueOf(-2));         // 同一图层
    }


    /**
     * 加载背景棋盘图像
     */
    private void initBoardLayer() {
        JLabel bgBoard = new JLabel(new ImageIcon("img/board.png")); // 背景图像
        bgBoard.setLocation(0, 0); // 放在窗口左上角
        bgBoard.setSize(VIEW_WIDTH, VIEW_HEIGHT); // 设置图像大小
        bgBoard.addMouseListener(new BoardClickListener());
        pane.add(bgBoard, Integer.valueOf(-1)); // 添加到图层：第1层是背景
    }

    /**
     * 初始化棋子图层（红在下，黑在上）
     */
    private void initPieces() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                if(board[i][j] == null) continue;

                int[] pos = {i, j};
                int[] sPos = modelToViewConverter(pos);

                String camp = board[i][j].substring(0, 1).equals("红") ? "r" : "b";
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
                piecePic.addMouseListener(new PieceClickListener(i, j));
                pane.add(piecePic, Integer.valueOf(0));
            }
        }
    }


    /**
     * 初始化当前阵营图标
     */
    private void initPlayerLayer() {
        playerPic = player ? new JLabel(new ImageIcon("img/rPic.png")) : new JLabel(new ImageIcon("img/bPic.png"));
        playerPic.setLocation(10 + PAD, 320 + PAD);
        playerPic.setSize(PIECE_WIDTH, PIECE_HEIGHT);
        pane.add(playerPic, Integer.valueOf(0)); // 加到最上层
    }




    /**
     * 将模型坐标转换为像素坐标
     * @param pos
     * @return 像素坐标（sx，sy）
     */
    private int[] modelToViewConverter(int[] pos) {
        int col = pos[0];
        int row = 9 - pos[1];  // 将逻辑棋盘行翻转以匹配屏幕视觉方向，像素是左上向右下绘制的
        int scol = col * SCOL_GAP + SCOL_OFFSET;
        int srow = row * SROW_GAP + SROW_OFFSET;
        return new int[]{scol, srow};
    }

    /**
     * 将像素坐标转换为模型坐标
     * @param sPos
     * @return 模型坐标
     */
    private int[] viewToModelConverter(int sPos[]) {
       int scol = sPos[0];
       int srow = sPos[1];
       int col = (scol - SCOL_OFFSET) / SCOL_GAP;
       int row = 9 - (srow - SROW_OFFSET) / SROW_GAP;
       return new int[]{col, row};
    }

    /**
     * 翻转整个棋盘，用于给对方玩家显示视角（己方在下）
     * @param board 原始棋盘（board[col][row]）
     * @return 翻转后的棋盘副本
     */
    private String[][] flipBoard(String[][] board) {
        String[][] flipped = new String[9][10];  // 创建新棋盘

        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 10; row++) {
                int newCol = 8 - col;
                int newRow = 9 - row;
                flipped[newCol][newRow] = board[col][row];
            }
        }

        return flipped;
    }





    class PieceClickListener extends MouseAdapter {
        int col,row;
        PieceClickListener(int col, int row) {
            this.col = col;
            this.row = row;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int[] pos = new int[]{col, row};

            String camp = board[col][row].substring(0, 1); // 得到被点击的棋子后，需要检查它是己方的还是敌方的。如果是己方的，那么把它作为被选中棋子；如果是敌方的那么把它作为目标位置
            boolean isMyCamp = (player && camp.equals("红")) || (!player && camp.equals("黑"));
            if(isMyCamp) {
                selectedPiecePos = pos;
                System.out.printf("选中己方棋子 ➜ (%d, %d)%n", col, row); // 这里的坐标不是转换后的，是黑方玩家自己看到的
            }
            if(selectedPiecePos != null && (!isMyCamp)) {
                selectedBoardPos = pos;
                System.out.printf("选中目标位置(对方棋子) ➜ (%d, %d)%n", pos[0], pos[1]);
                handleMove(selectedPiecePos, selectedBoardPos);
            }

        }

    }



    class BoardClickListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int[] sPos = new int[]{e.getXOnScreen() - frame.getX(), e.getYOnScreen() - frame.getY()};
            int[] pos = viewToModelConverter(sPos);
            if(selectedPiecePos != null) {
                selectedBoardPos = pos;
                System.out.printf("选中目标位置(空位) ➜ (%d, %d)%n", pos[0], pos[1]); // 这里的坐标不是转换过的，是黑方玩家自己看到的
                handleMove(selectedPiecePos, selectedBoardPos);
            }
        }
    }




    /**
     * 玩家选好了移动策略，传出它们并重置位置参数
     * @param from
     * @param to
     */
    private void handleMove(int[] from, int[] to) {
        if(player) { // 如果当前选择移动策略的玩家是红方，那么直接传出坐标即可
            System.out.printf("红方玩家选中棋子 (%d, %d) -> 目标 (%d, %d)%n", from[0], from[1], to[0], to[1]);
        }
        if(!player) { // 对于黑方玩家，他看到的棋盘（他选中的位置）是上下左右颠倒的，传给服务器的坐标应该颠倒过来
            System.out.printf("黑方玩家选中棋子 (%d, %d) -> 目标 (%d, %d)%n", 8 - from[0], 9 - from[1], 8 - to[0], 9 - to[1]);
        }
        selectedPiecePos = null;
        selectedBoardPos = null;
    }


}
