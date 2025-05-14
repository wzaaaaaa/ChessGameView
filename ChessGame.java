public class ChessGame {
    public void init() {
        String[][] board = new String[9][10];

        board[0][0]="红车";board[1][0]="红马";board[2][0]="红相";
        board[3][0]="红士";board[4][0]="红帅";board[5][0]="红士";
        board[6][0]="红相";board[7][0]="红马";board[8][0]="红车";

        board[0][9]="黑车";board[1][9]="黑马";board[2][9]="黑象";
        board[3][9]="黑士";board[4][9]="黑将";board[5][9]="黑士";
        board[6][9]="黑象";board[7][9]="黑马";board[8][9]="黑车";

        board[0][3]=board[2][3]=board[4][3]=board[6][3]=board[8][3]="红兵";
        board[0][6]=board[2][6]=board[4][6]=board[6][6]=board[8][6]="黑卒";

        board[1][2]=board[1][7]="红炮";
        board[7][2]=board[7][7]="黑炮";

        char player = 'r';

        GameView view = new GameView();
        view.initView(board, player);
    }


    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        game.init();
    }
}