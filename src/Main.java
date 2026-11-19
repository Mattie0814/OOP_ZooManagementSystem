import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Database.loadUsers();
        Database.loadEnclosures();
        Database.loadInventory();

        SwingUtilities.invokeLater(GUI_Login::new);
    }
}