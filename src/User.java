public class User
{
    protected String username;

    public User(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public String getRole()
    {
        return "User";
    }
}


// Admin inherits from User
class Admin extends User
{
    public Admin(String username)
    {
        super(username);
    }

    @Override
    public String getRole()
    {
        return "Admin";
    }
}


// ZooKeeper inherits from User
class ZooKeeper extends User
{
    public ZooKeeper(String username)
    {
        super(username);
    }

    @Override
    public String getRole()
    {
        return "Zoo Keeper";
    }
}


// Staff inherits from User
class Staff extends User
{
    public Staff(String username)
    {
        super(username);
    }

    @Override
    public String getRole()
    {
        return "Staff";
    }
}