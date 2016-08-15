import java.sql.*;

public class DBHandler {
    private String driver = "org.sqlite.JDBC";

    private Connection connection = null;
    private Statement statement = null;
    //private PreparedStatement preStatement = null;
    private ResultSet result = null;
    private String query = "";

    boolean isDB =false;

    public DBHandler()
    {
        try{
            Class.forName(driver);
            connection = DriverManager.getConnection("JDBC:sqlite:database.db");
            statement = connection.createStatement();
            //preStatement = connection.prepareStatement("insert into book valuus (?, ?, ?, ?, ?, ?, ? ,?);");
            isDB=true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            isDB=false;
        }
    }

    //check id pw
    public MemberData checkLogin(String mid, String mpassword, String mname)
    {
        MemberData member = null;
        query = "select * from member where name='" + mname + "' and id='" + mid + "' and password='" + mpassword + "'";

        try{
            result = statement.executeQuery(query);
            if(result.next())
            {
                member = new MemberData(result.getString("id"),result.getString("password"), result.getString("name"));
            }
            result.close();
        }
        catch(SQLException se)
        {
            se.printStackTrace();
        }

        return member;
    }

    public boolean isExistID(String id)
    {
        boolean isExist = false;
        query = "select id from member where id='" + id + "'";

        try{
            result = statement.executeQuery(query);
            if(result.next())
            {
                isExist = true;
            }
            result.close();
        }
        catch(SQLException se)
        {
            se.printStackTrace();
        }

        return isExist;
    }

    public int registerMember(MemberData member)
    {
        int rowResult = 0;

        query = "insert into member values('" + member.getName() + "', '" + member.getID() + "', '" + member.getPassword() + "')";

        try{
            rowResult = statement.executeUpdate(query);

        }
        catch(SQLException se)
        {
            se.printStackTrace();
        }

        return rowResult;
    }

    public String getCourseList(){
        String str = "LIST@";
        query = "select * from Course";

        try{
            result = statement.executeQuery(query);
            while(result.next())
            {
                str +=result.getString("id")+"@"+result.getString("registration")+"@"+result.getString("capacity")+"@"+result.getString("department")+"@"+result.getString("professor")+"@"+result.getString("term")+"@"+result.getString("time");
                str += "@@";
            }
            result.close();
        }
        catch(SQLException se)
        {
            se.printStackTrace();
        }
        System.out.println(str);
        return str;
    }

    public int addCourse(String courseID, String studentID){
        int rowResult = 0;
        try{
            query = "select * from registrationlist where course_id='"+courseID+"' and member_id='"+studentID+"'";
            result = statement.executeQuery(query);
            if(result.next()){
                result.close();
                return 0;//alredy exist
            }else{
                result.close();
                query = "select registration, capacity from course where id='"+courseID+"'";
                result = statement.executeQuery(query);
                if(result.next()){
                    int r = Integer.parseInt(result.getString("registration"));
                    int c = Integer.parseInt(result.getString("capacity"));
                    result.close();
                    if(r>=c){
                        return 0;//full
                    }

                }
                query = "insert into registrationlist values('" + courseID + "', '" + studentID + "')";
                rowResult = statement.executeUpdate(query);
                result.close();
                query = "update course set registration = registration + 1 where id ='"+courseID+"'";
                rowResult = statement.executeUpdate(query);
                result.close();
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return rowResult;
    }

    public int dropCourse(String courseID, String studentID){
        int rowResult = 0;
        try{
            query = "select * from registrationlist where course_id='"+courseID+"' and member_id='"+studentID+"'";
            result = statement.executeQuery(query);
            if(result.next()){
                result.close();
                query = "Delete from registrationlist where course_id='" + courseID + "' and member_id='" + studentID + "'";
                rowResult = statement.executeUpdate(query);
                query = "update course set registration = registration - 1 where id ='"+courseID+"'";
                rowResult = statement.executeUpdate(query);
                result.close();
                return 1;//alredy exist
            }
        }
        catch(SQLException se)
        {
            se.printStackTrace();
        }
        return rowResult;
    }


    public String getHolds(String studentID){
        String str = "";
        query = "select course_id from registrationlist where member_id = '"+studentID+"'";

        try{
            result = statement.executeQuery(query);
            while(result.next())
            {
                str +=result.getString("course_id");
                str += "@";
            }
            result.close();
        }
        catch(SQLException se)
        {
            se.printStackTrace();
        }
        System.out.println(str);
        return str;
    }

}