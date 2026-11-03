import java.util.ArrayList;

public class UserData {
    public enum ROLE {ADMIN, STAFF, ZOOKEEPER}

    private String username;
    private String password;
    private ROLE type;

    private ArrayList<TaskData> schedule;

    public UserData(String username, String password, ROLE type) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.schedule = new ArrayList<>();
    }

    public UserData() {this.schedule = new ArrayList<>();}

    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public ROLE getType() {return type;}
    public ArrayList<TaskData> getSchedule() {return schedule;}

    public void addTask(TaskData task) {schedule.add(task);}
    public void removeTask(TaskData task) {schedule.remove(task);}
}