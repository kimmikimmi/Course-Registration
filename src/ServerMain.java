import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Member;
import java.util.ArrayList;

import javax.swing.*;
public class ServerMain extends JFrame{
    static long serialVersionUID = 0;
    static ArrayList<MemberData> MemberList =null;
    static JLabel countText =null;
    CommAcceptThread mCommAcceptThread;

    ServerMain(){
        MemberList = new ArrayList<MemberData>();
        countText = new JLabel("The number of users : 0");
        JLabel label = new JLabel("server is not opened");
        JButton button = new JButton("Start");

        ButtonActionListener listener = new ButtonActionListener(label, button);
        button.addActionListener(listener);

        Frame frame = new Frame("server");
        frame.add(countText, BorderLayout.NORTH);
        frame.add(label, BorderLayout.WEST);
        frame.add(button, BorderLayout.EAST);

        frame.setLocation(500, 500);
        frame.setSize(200, 100);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                System.exit(0);
            }
        });
    }
    class ButtonActionListener implements ActionListener{
        JLabel label;
        JButton button;
        ButtonActionListener(JLabel label, JButton button){
            this.label = label;
            this.button = button;
        }
        public void actionPerformed(ActionEvent e){
            if(button.getText().equals("Start")){
                DBHandler DB = new DBHandler();
                if(DB.isDB == false) return ;//check connection to db

                label.setText("server is opened");
                button.setText("Stop");
                mCommAcceptThread = new CommAcceptThread(10210);
                mCommAcceptThread.start();

            }
            else{
                mCommAcceptThread.exit();
                mCommAcceptThread=null;
                countText.setText("The number of users : 0");
                label.setText("server is not opened");
                button.setText("Start");
            }
        }
    }
    public static void main(String[] args){
        ServerMain main = new ServerMain();
    }
}