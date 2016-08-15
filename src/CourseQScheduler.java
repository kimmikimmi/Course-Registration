import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Created by Jaden on 24/10/2015.
 */
public class CourseQScheduler extends Thread{
    private MyConcurrentQ myCourseQ; // defined by me
    //private ArrayList<String> courseQ; // jdk component.
    public Semaphore courseSem;
    DBHandler courseDBHandler;
    CommAcceptThread commAcceptThread;

    private String priority = "";
    private String stuId = "";
    private String courseTitle = "";

    CourseQScheduler(CommAcceptThread commAcceptThread) {
        myCourseQ = new MyConcurrentQ(1000);// initial size is 10000;
        //courseQ = new ArrayList<String>();
        courseSem = new Semaphore(1);
        courseSem.release();
        this.commAcceptThread = commAcceptThread;
        courseDBHandler = new DBHandler();
    }

    private CommThread getThreadBy(String stuId) {
        for(CommThread thread : commAcceptThread.getThreadList()) {
            if(thread.getMemberData() != null) {
                if(thread.getMemberData().getID().equals(stuId)) {
                    return thread;
                }
            }
        }
        return null;
    }

    private void parse(String str) {
        String[] parseStr = str.split("@");

        priority = parseStr[0];
        stuId = parseStr[1];
        courseTitle = parseStr[2];

    }

    private int pop() {
        int value = 0;

        parse(myCourseQ.dequeue().toString());
        //parse(courseQ.get(0));
        System.out.println("priority = " + priority);
        switch (priority) {
            case "1":
                value = courseDBHandler.dropCourse(courseTitle, stuId);
                getThreadBy(stuId).send("DROP" + "@" + value + ""); // if value == 1 -> success , 0 -> fail
                break;
            case "2":
                value = courseDBHandler.addCourse(courseTitle, stuId);
                getThreadBy(stuId).send("ADD" + "@" + value + ""); // if value == 1 -> success , 0 -> fail
                break;
            case "3":
                getThreadBy(stuId).send(courseDBHandler.getCourseList() + "-" + courseDBHandler.getHolds(stuId));
                value = 2;
                break;
            default:
                value = 0;
        }
        //courseQ.remove(0);
        //myCourseQ.dequeue();

        return value;
    }

    /*private void reload() {

        Collections.sort(courseQ);
    }*/



    public boolean addQ(String str) {

        boolean value = false;

        value = myCourseQ.enqueue(str);
        //value = courseQ.add(str);
        System.out.println(myCourseQ.getElement());

        System.out.println("addQ = " + str);

        System.out.println("size Q = " + myCourseQ.getSize());

        return value;
    }

    public void broadcast(){
        ArrayList<CommThread> list=null;
        list = commAcceptThread.getThreadList();
        if(list == null){
            return;
        }else{
            for (CommThread thread : list) {
                MemberData member = thread.getMemberData();
                if (member != null) {
                    thread.send(courseDBHandler.getCourseList() + "-" + courseDBHandler.getHolds(member.getID()));
                }
            }
        }
    }

    @Override
    public void run() {
        while(true) {

            try {
                courseSem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //if(courseQ.size() != 0) {
            if(myCourseQ.getSize() != 0) {

                System.out.println("pop now");
                if(pop()==1){
                    broadcast();
                }

            }
            courseSem.release();

        }
    }

}
