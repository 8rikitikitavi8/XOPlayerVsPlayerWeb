package server.gameModel;

public class Field {
    public static final int SIZE = 3;
    private volatile boolean isRightShoot = false;

    public enum Type {
        X, O, NONE
    }

    private Type[][] cells = new Type[SIZE][SIZE];

    public void init() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = Type.NONE;
            }
        }
    }

    public void showField() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                switch (cells[i][j]) {
                    case NONE -> System.out.print(" . ");
                    case X -> System.out.print(" X ");
                    case O -> System.out.print(" O ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public String fieldToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                switch (cells[i][j]) {
                    case NONE -> sb.append(".");
                    case X -> sb.append("X");
                    case O -> sb.append("O");
                }
            }
        }
        return sb.toString();
    }

    public void shoot(String string, Type who) {
        System.out.println("string shoot" + string);
        int x = string.charAt(1) - 48;
        System.out.println("x" + x);
        int y = string.charAt(0) - 48;
        System.out.println("y" + y);
        if (cells[x][y] != Type.O && cells[x][y] != Type.X) {
            cells[x][y] = who;
            isRightShoot = true;
        }
    }

    public Type whoIsWinner() {
        if (checkWin(Type.X)) {
            return Type.X;
        }
        if (checkWin(Type.O)) {
            return Type.O;
        }
        return Type.NONE;
    }

    private boolean checkWin(Type t) {
        for (int i = 0; i < SIZE; i++) {
            if (cells[i][0] == t && cells[i][1] == t && cells[i][2] == t) {
                return true;
            }
        }
        for (int i = 0; i < SIZE; i++) {
            if (cells[0][i] == t && cells[1][i] == t && cells[2][i] == t) {
                return true;
            }
        }
        return (cells[0][0] == t && cells[1][1] == t && cells[2][2] == t) ||
                (cells[2][0] == t && cells[1][1] == t && cells[0][2] == t);
    }

    public boolean draw() {
        int counter = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (cells[i][j] == Type.NONE) {
                    counter++;
                }
            }
        }
        return counter == 0;
    }
}
