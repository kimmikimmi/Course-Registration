
public class MemberData {
    private String ID = "";
    private String password = "";
    private String name = "";

    public MemberData() {
    }
    public MemberData(String iD, String password ,String name) {
        super();
        ID = iD;
        this.password = password;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
