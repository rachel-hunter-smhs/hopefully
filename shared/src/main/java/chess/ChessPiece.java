package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor =pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
       return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        switch (pieceType){
            case PAWN -> {
                int dir = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
                int startRow = (teamColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
                int nextRow = myPosition.getRow() + dir;
                int pawnCol = myPosition.getColumn();
                int pawnRow = myPosition.getRow();
                ChessPosition oldPos = new ChessPosition(pawnRow, pawnCol);
                ChessPiece oldPiece = board.getPiece(oldPos);
                if(inBounds(nextRow,pawnCol) && board.getPiece(new ChessPosition(nextRow,pawnCol)) == null){
                    ChessPosition newPos = new ChessPosition(nextRow,pawnCol);
                    addPawnAdvance(oldPos, newPos ,board, moves);
                }
                if(pawnRow == startRow){
                    int jumpRow = pawnRow + 2 * dir;
                    ChessPosition newPosStar = new ChessPosition(jumpRow, column);
                    addPawnAdvance(oldPos,newPosStar , board, moves);

                }
                int captureCol = 1 + pawnCol;
                if(inBounds(pawnRow, captureCol) && board.getPiece(new ChessPosition(pawnRow, captureCol)) != null){
                    ChessPosition capturePos = new ChessPosition(nextRow, captureCol);
                    ChessPiece capturePiece = board.getPiece(capturePos);
                    if(capturePiece.getTeamColor() != oldPiece.getTeamColor() ){
                        ChessPosition newPos = new ChessPosition(nextRow,captureCol);
                        addPawnAdvance(oldPos, newPos ,board, moves);
                    }

                }
                captureCol = pawnCol -1;
                if(inBounds(pawnRow, captureCol) && board.getPiece(new ChessPosition(pawnRow, captureCol)) != null){
                    ChessPosition capturePos = new ChessPosition(nextRow, captureCol);
                    ChessPiece capturePiece = board.getPiece(capturePos);
                    if(capturePiece.getTeamColor() != oldPiece.getTeamColor() ){
                        ChessPosition newPos = new ChessPosition(nextRow,captureCol);
                        addPawnAdvance(oldPos, newPos ,board, moves);
                    }

                }








            }
            case KNIGHT -> {
                row = myPosition.getRow();
                column = myPosition.getColumn();
                int [][] d = {{2,1},{-2,1},{2,-1},{-2,-1}, {1,2}, {-1,2}, {1,-2}, {-1, -2}};
                for(int [] s:d){
                    int r = row +s[0];
                    int col = column + s[1];
                    if(inBounds(r,col)){
                        ChessPiece targetSpace = board.getPiece(new ChessPosition(r, col));
                        ChessPiece oldPiece = board.getPiece(new ChessPosition(row, column));
                        if(targetSpace == null){
                            moves.add(new ChessMove(new ChessPosition(row, column), new ChessPosition(r,col), null));
                        }
                        else if(targetSpace.getTeamColor() != oldPiece.getTeamColor()){
                            moves.add(new ChessMove(new ChessPosition(row, column), new ChessPosition(r,col), null));
                        }
                    }
                }
            }
            case KING -> {
                for(int dr = -1; dr <= 1; dr++ ){
                    for(int dc = -1; dc <= 1; dc++){
                        if(dr != 0 || dc != 0){
                           int r = row + dr;
                            int col = column + dc;
                            if(inBounds(r,col)){
                                ChessPiece targetSpace = board.getPiece(new ChessPosition(r, col));
                                ChessPiece oldPiece = board.getPiece(new ChessPosition(row, column));

                                if(targetSpace == null){
                                    moves.add(new ChessMove(new ChessPosition(row, column), new ChessPosition(r,col), null));
                                }
                                else if(targetSpace.getTeamColor() != oldPiece.getTeamColor()){
                                    moves.add(new ChessMove(new ChessPosition(row, column), new ChessPosition(r,col), null));
                                }


                            }

                        }
                    }
                }
            }
            case BISHOP -> {
                int [][] dir = {{1,1}, {1,-1}, {-1, -1}, {-1, 1}};
                slide(board, myPosition, moves, dir);
            }
            case ROOK -> {
                int [][] dir = {{0,1}, {0,-1}, {-1, 0}, {1, 0}};
                slide(board, myPosition, moves, dir);
            }
            case QUEEN -> {
                int [][] dir = {{1,1}, {1,-1}, {-1, -1}, {-1, 1}, {0,1}, {0,-1}, {-1, 0}, {1, 0}};
                slide(board, myPosition, moves, dir);
            }
        }
        return moves;
    }

    public boolean inBounds(int r, int c) {
        return r >= 1 && r <= 8 && c >= 1 && c <=8;

    }
    private void addPawnAdvance(ChessPosition myPosition, ChessPosition to, ChessBoard board, Collection<ChessMove> moves){
        int promoRow = (teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
        if(to.getRow() == promoRow){
            moves.add(new ChessMove(myPosition,to, PieceType.QUEEN));
            moves.add(new ChessMove(myPosition,to, PieceType.KNIGHT));
            moves.add(new ChessMove(myPosition,to, PieceType.BISHOP));
            moves.add(new ChessMove(myPosition,to, PieceType.ROOK));

        }
        else {
            moves.add(new ChessMove(myPosition,to, null));
        }


    }
    private void slide(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int [][] d){
        ChessPiece oldPiece = board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()));
        for(int [] s:d){
            int slideR = myPosition.getRow() + s[0];
            int slideC = myPosition.getColumn() + s[1];
            while(inBounds(slideR, slideC)){
                ChessPiece newPiece = board.getPiece(new ChessPosition(slideR,slideC));
                if (newPiece == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(slideR, slideC), null));
                }
                else if (newPiece.getTeamColor()!= oldPiece.getTeamColor()){
                    moves.add(new ChessMove(myPosition, new ChessPosition(slideR, slideC), null));
                    break;

                }
                slideR += s[0];
                slideC += s[1];

            }


        }

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }
}