import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class CommAcceptThread extends Thread{
    boolean flag = true;
    ServerSocket mServerSocket;

    private ArrayList<CommThread> threadList;

    public CourseQScheduler courseQScheduler;

    public CommAcceptThread(int port){

        threadList = new ArrayList<>();
        courseQScheduler = new CourseQScheduler(this);
        courseQScheduler.setDaemon(true);


        try{
            mServerSocket = new ServerSocket(port);
            System.out.println("CommAcceptThread(" + port + ") Start..");
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public ArrayList<CommThread> getThreadList() {
        return threadList;
    }

    public void run(){
        courseQScheduler.start();
        try{
            while(flag){
                Socket socket = mServerSocket.accept();
                CommThread thread = new CommThread(socket, this);
                threadList.add(thread);
                thread.setDaemon(true);
                thread.start();
            }

        }
        catch(Exception e){
            System.out.println("server closed");
            //e.printStackTrace();
        }
    }
    public void exit(){
        for (CommThread thread : threadList) {
            thread.exit();
        }
        try {
            this.flag = false;
            mServerSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
