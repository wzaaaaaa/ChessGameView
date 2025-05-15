import java.awt.event.MouseEvent;

import static java.lang.Thread.sleep;

public class ChessGame {
    private GameView view;

    public void initView() {
        String[][] board = new String[9][10];

        board[0][0]="红车";board[1][0]="红马";board[2][0]="红相";
        board[3][0]="红士";board[4][0]="红帅";board[5][0]="红士";
        board[6][0]="红相";board[7][0]="红马";board[8][0]="红车";

        board[0][9]="黑车";board[1][9]="黑马";board[2][9]="黑象";
        board[3][9]="黑士";board[4][9]="黑将";board[5][9]="黑士";
        board[6][9]="黑象";board[7][9]="黑马";board[8][9]="黑车";

        board[0][3]=board[2][3]=board[4][3]=board[6][3]=board[8][3]="红兵";
        board[0][6]=board[2][6]=board[4][6]=board[6][6]=board[8][6]="黑卒";

        board[1][2]=board[7][2]="红炮";
        board[1][7]=board[7][7]="黑炮";

        boolean player = true;

        view = new GameView();
        view.initView(board, player);
    }

    public void updateBoard(String[][]board, boolean player) {
        view.updateView(board, player);
    }



    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        game.initView();

        try {
            Thread.sleep(5000); // 必须处理 InterruptedException
        } catch (InterruptedException e) {
            e.printStackTrace(); // 或者直接忽略异常
        }

        String [][] board = new String[9][10];
        board[0][0]="红车";board[1][0]="红马";board[2][0]="红相";
        board[3][0]="红士";board[4][0]="红帅";board[5][0]="红士";
        board[6][0]="红相";board[7][0]="红马";board[8][0]="红车";

        board[0][9]="黑车";board[1][9]="黑马";board[2][9]="黑象";
        board[3][9]="黑士";board[4][9]="黑将";board[5][9]="黑士";
        board[6][9]="黑象";board[7][9]="黑马";board[8][9]="黑车";

        board[0][3]=board[2][3]=board[4][3]=board[6][3]=board[8][3]="红兵";
        board[0][6]=board[2][6]=board[4][6]=board[6][6]=board[8][6]="黑卒";

        board[1][2]=board[7][1]="红炮";
        board[1][7]=board[7][7]="黑炮";

        game.updateBoard(board, false);

    }
}
