package ui;

import dao.UserDao;
import model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Displays the menu for the already-authenticated user's assigned role.
 * It receives the same Scanner and UserDao instances created at startup.
 */
public class MenuUI {
    private final Scanner scanner;
    private final UserDao userDao;

    public MenuUI(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void openRoleMenu(User user) {
        switch (user.roles().toLowerCase()) {
            case "admin/owner" -> adminMenu(user);
            case "staff" -> staffMenu(user);
            case "customer" -> customerMenu(user);
            default -> System.out.println("Unknown role. Access denied.");
        }
        System.out.println("Logged out: " + user.username());
    }

    private void adminMenu(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\\n--- OWNER/ADMIN MENU ---");
            System.out.println("Signed in as: " + user.username());
            System.out.println("1. List Accounts");
            System.out.println("2. Activate/Change Account Status");
            System.out.println("3. Logout");
            System.out.print("Choose: ");
            switch (readInt()) {
                case 1 -> listAccounts();
                case 2 -> changeAccountStatus();
                case 3 -> loggedIn = false;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void staffMenu(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\\n--- STAFF MENU ---");
            System.out.println("Welcome, " + user.username() + "!");
            System.out.println("1. View Staff Account");
            System.out.println("2. Logout");
            System.out.print("Choose: ");
            switch (readInt()) {
                case 1 -> System.out.printf("Username: %s%nRole: %s%nStatus: %s%n",
                        user.username(), user.roles(), user.status());
                case 2 -> loggedIn = false;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void customerMenu(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\\n--- CUSTOMER MENU ---");
            System.out.println("Welcome, " + user.username() + "!");
            System.out.println("1. View Customer Account");
            System.out.println("2. Logout");
            System.out.print("Choose: ");
            switch (readInt()) {
                case 1 -> System.out.printf("Username: %s%nRole: %s%nStatus: %s%n",
                        user.username(), user.roles(), user.status());
                case 2 -> loggedIn = false;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void listAccounts() {
        try {
            List<User> users = userDao.findAll();
            System.out.println("\\nID | USERNAME | ROLE | STATUS");
            for (User user : users) {
                System.out.printf("%d | %s | %s | %s%n",
                        user.id(), user.username(), user.roles(), user.status());
            }
        } catch (SQLException e) {
            System.out.println("Could not load accounts: " + e.getMessage());
        }
    }

    private void changeAccountStatus() {
        listAccounts();
        System.out.print("Enter account ID: ");
        int id = readInt();
        System.out.println("1. ACTIVE");
        System.out.println("2. PENDING");
        System.out.println("3. DISABLED");
        System.out.print("New status: ");
        String status = switch (readInt()) {
            case 1 -> "ACTIVE";
            case 2 -> "PENDING";
            case 3 -> "DISABLED";
            default -> null;
        };
        if (status == null) {
            System.out.println("Invalid status.");
            return;
        }
        try {
            if (userDao.updateStatus(id, status)) {
                System.out.println("Account status updated to " + status + ".");
            } else {
                System.out.println("Account ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Could not update account: " + e.getMessage());
        }
    }

    private int readInt() {
        String value = scanner.nextLine().trim();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
