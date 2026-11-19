import java.util.ArrayList;
import java.time.LocalDate;

public class UserData {
    public enum ROLE {ADMIN, STAFF, ZOOKEEPER}

    private String username;
    private String password;
    private ROLE type;

    private String employeeId;
    private String name;
    private String address;
    private String contactNumber;
    private String emergencyContact;
    private LocalDate birthdate;
    private String sexGender;
    private String civilStatus;
    private String workHours;
    private String salary;
    private String picturePath;

    private boolean isActive = false;

    private ArrayList<TaskData> schedule;

    public UserData(String username, String password, ROLE type) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.schedule = new ArrayList<>();
    }

    public UserData() {
        this.schedule = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ROLE getType() {
        return type;
    }

    public ArrayList<TaskData> getSchedule() {
        return schedule;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public LocalDate getBirthdate() {return birthdate;}

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getSexGender() {
        return sexGender;
    }

    public void setSexGender(String sexGender) {
        this.sexGender = sexGender;
    }

    public String getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(String civilStatus) {
        this.civilStatus = civilStatus;
    }

    public String getWorkHours() {
        return workHours;
    }

    public void setWorkHours(String workHours) {
        this.workHours = workHours;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean bool) {
        this.isActive = bool;
    }

    public void addTask(TaskData task) {
        schedule.add(task);
    }

    public void removeTask(TaskData task) {
        schedule.remove(task);
    }
}