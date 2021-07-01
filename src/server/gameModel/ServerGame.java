package server.gameModel;

import server.ServerHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ServerGame extends Thread {
    private final DataInputStream inUser1;
    private final DataOutputStream outUser1;
    private final DataInputStream inUser2;
    private final DataOutputStream outUser2;
    private final List<ServerHandler> handlers;
    private Field field;
    private volatile int winner = 0;
    private String user1name;
    private String user2name;

    public ServerGame(List<ServerHandler> handlers) {
        this.handlers = handlers;
        inUser1 = handlers.get(0).getIn();
        outUser1 = handlers.get(0).getOut();
        inUser2 = handlers.get(1).getIn();
        outUser2 = handlers.get(1).getOut();
        System.out.println("ServerGame construct");

    }

    @Override
    public void run() {
        try {
            user1name = inUser1.readUTF();
            user2name = inUser2.readUTF();
            startGame(whoFirst());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean whoFirst() {
        return new Random().nextBoolean();
    }

    public void startGame(Boolean b) throws IOException {
        field = new Field();
        field.init();
        field.showField();

        if (b) {
            whoFirstToCUI("1", "0", field.fieldToString(), user1name);
            while (true) {
                System.out.println("1 first");
                if (turnClient(inUser1, inUser2, outUser1, Field.Type.X, outUser2, user2name, user1name)) {
                    break;
                }
                if (turnClient(inUser2, inUser1, outUser2, Field.Type.O, outUser1, user1name, user2name)) {
                    break;
                }
            }
        } else {
            whoFirstToCUI("0", "1", field.fieldToString(), user2name);
            while (true) {
                System.out.println("2 first");
                if (turnClient(inUser2, inUser1, outUser2, Field.Type.O, outUser1, user1name, user2name)) {
                    break;
                }
                if (turnClient(inUser1, inUser2, outUser1, Field.Type.X, outUser2, user2name, user1name)) {
                    break;
                }
            }
        }
    }

    private void whoFirstToCUI(String s1, String s2, String field, String userNameFirst) throws IOException {
        outUser1.writeUTF(s1 + field + userNameFirst);
        outUser1.flush();
        outUser2.writeUTF(s2 + field + userNameFirst);
        outUser2.flush();
    }

    private boolean turnClient(DataInputStream in1, DataInputStream in2, DataOutputStream out1, Field.Type type,
                               DataOutputStream out2, String nameWhoNextTurn, String nameWhoWin) throws IOException {
        String shootUser = in1.readUTF();
        System.out.println("shoot from client " + shootUser);
        field.shoot(shootUser, type);
        field.showField();
        if (endGame()) {
            out1.writeUTF(Integer.toString(winner) + nameWhoWin);
            out1.flush();
            out2.writeUTF(Integer.toString(winner) + nameWhoWin);
            out2.flush();
            responseToClientAfterEndGame(in1, in2, out1, out2);
            return true;
        }
        System.out.println("field to client " + field.fieldToString());
        out1.writeUTF(field.fieldToString() + 0 + nameWhoNextTurn);
        out1.flush();
        out2.writeUTF(field.fieldToString() + 1 + nameWhoNextTurn);
        out2.flush();
        return false;
    }

    private void responseToClientAfterEndGame(DataInputStream in1, DataInputStream in2, DataOutputStream out1, DataOutputStream out2) throws IOException {
        String from1 = in1.readUTF();
        String from2 = in2.readUTF();
        if (from1.equalsIgnoreCase("End")) {
            out2.writeUTF("disconnect");
            out2.flush();
        } else if (from2.equalsIgnoreCase("End")) {
            out1.writeUTF("disconnect");
            out1.flush();
        } else if (from1.equalsIgnoreCase("Continue") && from2.equalsIgnoreCase("Continue")) {
            System.out.println("both continue");
            out1.writeUTF("game");
            out1.flush();
            out2.writeUTF("game");
            out2.flush();
            startGame(whoFirst());
        }
    }
        private boolean endGame () {
            if (field.whoIsWinner() == Field.Type.X) {
                System.out.println("Победил " + Field.Type.X);
                winner = 1;
                return true;
            } else if (field.whoIsWinner() == Field.Type.O) {
                System.out.println("Победил " + Field.Type.O);
                winner = 2;
                return true;
            } else if (field.draw()) {
                System.out.println("Ничья");
                winner = 3;
                return true;
            }
            return false;
        }
    }
