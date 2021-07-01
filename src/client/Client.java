package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client extends JFrame implements Runnable {

    private String clientName;
    private JTextArea outTextArea;
    public static final int SIZE = 3;
    JButton[][] buttons;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private final String SERVER_IP = "127.0.0.1";
    private final int SERVER_PORT = 8083;
    private JPanel jPanel;

    @Override
    public void run() {
        connectServer();
        try {
            clientName = JOptionPane.showInputDialog(Client.this,
                    "Enter your name and wait for the second player to connect");
            try {
                out.writeUTF(clientName);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            showGUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void connectServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void showGUI() throws IOException {
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(clientName);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JTextField textField = new JTextField();
        add(BorderLayout.SOUTH, textField);
        textField.setText("wait for the second player to connect");

        jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(SIZE, SIZE));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                try {
                    out.close();
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        buttons = new JButton[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton jButton = new JButton(".");
                final int finalJ = j;
                final int finalI = i;
                jButton.addActionListener(e -> {
                    String toServer = Integer.toString(finalJ) + Integer.toString(finalI);
                    try {
                        out.writeUTF(toServer);
                        out.flush();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });
                buttons[i][j] = jButton;
                jPanel.add(jButton);
            }
        }
        add(jPanel);
        setVisible(true);

        String response = in.readUTF();
        System.out.println("response who first " + response);
        refreshAndShowField(response.substring(1, 10), false);
        if (response.charAt(0) == '0') {
            System.out.println("disableButtons");
            disableButtons();
        }
        String name = response.substring(10);
        textField.setText(name + " your turn");

        while (true) {
            String resp = in.readUTF();
            System.out.println("resp from server " + resp);
            char endGame = resp.charAt(0);

            switch (endGame) {
                case '1', '2' -> whoWin(resp.substring(1), false);
                case '3' -> whoWin(resp.substring(1), true);
            }
            String field = resp.substring(0, 9);
            System.out.println("field " + field);

            boolean disableAll;
            if (resp.charAt(9) == '1') {
                disableAll = false;
            } else disableAll = true;
            refreshAndShowField(field, disableAll);
            textField.setText(resp.substring(10) + " your turn");
        }
    }

    private void whoWin(String name, boolean isDraw) throws IOException {
        JFrame thisFrame = this;
        remove(jPanel);
        Object[] options = {"Yes, please", "No, thanks"};
        int n = 0;
        if (!isDraw) {
            n = JOptionPane.showOptionDialog(thisFrame, name + " Wins! Would you like to restart", "Restart",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        } else {
            n = JOptionPane.showOptionDialog(thisFrame, "Draw! Would you like to restart", "Restart",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        }
        if (n == 1) {
            out.writeUTF("End");
            out.flush();
            System.exit(0);
        } else {
            out.writeUTF("Continue");
            out.flush();
            String continueOrEndGame = in.readUTF();
            System.out.println("continueOrEndGame = " + continueOrEndGame);
            disconnectSecondPlayer(continueOrEndGame);
            showGUI();
        }
    }

    private void disconnectSecondPlayer(String s) {
        if (s.equalsIgnoreCase("disconnect")) {
            JFrame thisFrame = this;
            JOptionPane.showMessageDialog(thisFrame, "Your opponent has disconnected from the server." +
                    " To start the game with a new opponent, enter the game again");
            System.out.println("disconnect");
            System.exit(0);
        }
    }

    private void disableButtons() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton button = buttons[i][j];
                button.setEnabled(false);
            }
        }
    }

    private void refreshAndShowField(String s, boolean disableAll) {
        List<JButton> listButtons = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                listButtons.add(buttons[i][j]);
            }
        }
        for (int i = 0; i < listButtons.size(); i++) {
            char state = s.charAt(i);
            JButton button = listButtons.get(i);
            if (disableAll) {
                button.setText(String.valueOf(state));
                button.setEnabled(false);
            } else {
                if (state == '.') {
                    button.setText(String.valueOf(state));
                    button.setEnabled(true);
                } else {
                    button.setText(String.valueOf(state));
                    button.setEnabled(false);
                }
            }
        }
    }

}
