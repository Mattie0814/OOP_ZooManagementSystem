import javax.swing.*;

public class Main{
    public static void main(String[] args) {
        Database.loadEnclosures();
        Database.loadInventory();

        GUI_Login login = new GUI_Login();
    }
}