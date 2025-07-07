import java.sql.*;
import java.util.*;

public class OnlineBookshop {
    static final String DB_URL = "jdbc:postgresql://localhost:5432/bookshop"; // PostgreSQL DB
    static final String USER = "postgres"; // Your PostgreSQL username
    static final String PASS = "your_password"; // Your PostgreSQL password

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            while (true) {
                System.out.println("\n--- Online Bookshop Menu ---");
                System.out.println("1. Add Book");
                System.out.println("2. View All Books");
                System.out.println("3. Place Order");
                System.out.println("4. View Orders");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1 -> addBook(conn);
                    case 2 -> viewBooks(conn);
                    case 3 -> placeOrder(conn);
                    case 4 -> viewOrders(conn);
                    case 5 -> {
                        System.out.println("Thank you for using Online Bookshop!");
                        return;
                    }
                    default -> System.out.println("Invalid choice!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 1. Add Book
    public static void addBook(Connection conn) throws SQLException {
        sc.nextLine(); // Clear buffer
        System.out.print("Enter title: ");
        String title = sc.nextLine();
        System.out.print("Enter author ID: ");
        int authorId = sc.nextInt();
        System.out.print("Enter price: ");
        double price = sc.nextDouble();
        System.out.print("Enter category ID: ");
        int categoryId = sc.nextInt();

        String sql = "INSERT INTO Books (title, author_id, price, category_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, title);
            pst.setInt(2, authorId);
            pst.setDouble(3, price);
            pst.setInt(4, categoryId);
            int rows = pst.executeUpdate();
            System.out.println(rows + " book(s) added.");
        }
    }

    // 2. View All Books
    public static void viewBooks(Connection conn) throws SQLException {
        String sql = "SELECT b.book_id, b.title, a.name as author, b.price, c.name as category " +
                     "FROM Books b JOIN Authors a ON b.author_id = a.author_id " +
                     "JOIN Categories c ON b.category_id = c.category_id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Book List ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Title: %s | Author: %s | Price: %.2f | Category: %s%n",
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getDouble("price"),
                        rs.getString("category"));
            }
        }
    }

    // 3. Place Order
    public static void placeOrder(Connection conn) throws SQLException {
        System.out.print("Enter user ID: ");
        int userId = sc.nextInt();
        String insertOrder = "INSERT INTO Orders (user_id, order_date) VALUES (?, CURRENT_TIMESTAMP)";
        String insertItem = "INSERT INTO Order_Items (order_id, book_id, quantity) VALUES (?, ?, ?)";

        conn.setAutoCommit(false);
        try (PreparedStatement orderStmt = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement itemStmt = conn.prepareStatement(insertItem)) {

            orderStmt.setInt(1, userId);
            orderStmt.executeUpdate();
            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            int orderId = -1;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            }

            while (true) {
                System.out.print("Enter book ID to add to order (or 0 to finish): ");
                int bookId = sc.nextInt();
                if (bookId == 0) break;
                System.out.print("Enter quantity: ");
                int qty = sc.nextInt();
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, bookId);
                itemStmt.setInt(3, qty);
                itemStmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Order placed successfully with Order ID: " + orderId);
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Order failed. Rolled back.");
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // 4. View Orders
    public static void viewOrders(Connection conn) throws SQLException {
        String sql = "SELECT o.order_id, u.name, o.order_date, b.title, oi.quantity " +
                     "FROM Orders o " +
                     "JOIN Users u ON o.user_id = u.user_id " +
                     "JOIN Order_Items oi ON o.order_id = oi.order_id " +
                     "JOIN Books b ON oi.book_id = b.book_id " +
                     "ORDER BY o.order_id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Orders ---");
            while (rs.next()) {
                System.out.printf("Order ID: %d | User: %s | Date: %s | Book: %s | Quantity: %d%n",
                        rs.getInt("order_id"),
                        rs.getString("name"),
                        rs.getString("order_date"),
                        rs.getString("title"),
                        rs.getInt("quantity"));
            }
        }
    }
}
