import java.io.*;
import java.net.*;

import sun.rmi.log.ReliableLog.LogFile;
public class CommThread extends Thread{
    private Socket mSocket;
    private BufferedReader in;
    private PrintWriter out;
    private MemberData memberData = null;

    private CommAcceptThread acceptThread;

    public CommThread(Socket socket, CommAcceptThread acceptThread) throws Exception{
        this.acceptThread = acceptThread;
        mSocket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
    }

    public void exit(){
        try {
            mSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void send(String msg){
        out.println(msg);
        out.flush();
        if(memberData!=null){
            System.out.println("send msg to ["+memberData.getName()+"]:"+msg);
        }
    }

    public MemberData getMemberData() {
        if(memberData == null) {
            return null;
        }
        return memberData;
    }

    public void run(){
        String client_id ="";
        String command;
        try{
            while(true){
                command = in.readLine();
                if(memberData!=null){
                    System.out.println("["+memberData.getName()+"]s rq msg:"+command);
                }
                if(command == null)
                    break;
                else if(command.startsWith("SIGN")){
                    String str[] = command.split("@");
                    String id = str[1], pass = str[2], name = str[3];
                    DBHandler dbHandler = new DBHandler();
                    if(dbHandler.isExistID(id)){
                        send("ALREADYEXIST");
                        break;
                    }else{
                        dbHandler.registerMember(new MemberData(id,pass,name));
                        send("REGISTRATION");
                        break;
                    }
                }
                else if(command.startsWith("LOGIN")){
                    String str[] = command.split("@");
                    String id = str[1], pass = str[2], name = str[3];
                    DBHandler dbHandler = new DBHandler();
                    memberData = dbHandler.checkLogin(id, pass, name);

                    boolean login=false;
                    for (MemberData member : ServerMain.MemberList) {
                        if(member.getID().equals(id)){
                            login=true;
                        }
                    }

                    if (memberData == null) {
                        send("NOTEXIST");
                        break;
                    }else if(login){
                        send("ALREADYLOGIN");
                    }else {
                        send("LOGIN");
                        ServerMain.MemberList.add(memberData);
                        ServerMain.countText.setText("The number of users : "+ServerMain.MemberList.size());
                        client_id = id;
                        System.out.println("[Connection Start] ID: "+ client_id);
                    }
                } else if(command.startsWith("LIST")) {
                    if (memberData == null) {
                        break;
                    }
                    acceptThread.courseQScheduler.addQ(3+"@"+memberData.getID()+"@"+"null");
                } else if(command.startsWith("ADD")) {
                    if (memberData == null) {
                        break;
                    }
                    String[] str = command.split("@");
                    acceptThread.courseQScheduler.addQ(2+"@"+memberData.getID()+"@"+str[1]);
                } else if(command.startsWith("DROP")) {
                    if (memberData == null) {
                        break;
                    }
                    String[] str = command.split("@");
                    acceptThread.courseQScheduler.addQ(1+"@"+memberData.getID()+"@"+ str[1]);
                }

            }
            mSocket.close();
        }
        catch(Exception e){
            System.out.println("[Connection End] ID: "+ client_id);
            ServerMain.MemberList.remove(memberData);
            ServerMain.countText.setText("The number of user : "+ServerMain.MemberList.size());
        }
    }
}
