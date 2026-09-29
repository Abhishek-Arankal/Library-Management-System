package library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookDAO {

    // ADD BOOK
    public static void addBook(
            String title,
            String author,
            String category,
            int quantity) {

        String sql =
                "INSERT INTO books(title, author, category, quantity) " +
                "VALUES (?, ?, ?, ?)";

        try {

            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, category);
            statement.setInt(4, quantity);

            int result = statement.executeUpdate();

            if (result > 0) {
                System.out.println(
                        "Book added successfully!"
                );
            }

            statement.close();
            connection.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // SEARCH BOOK
    public static void searchBook(String title) {

        String sql =
                "SELECT * FROM books WHERE title LIKE ?";

        try {

            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, "%" + title + "%");

            ResultSet resultSet =
                    statement.executeQuery();

            boolean found = false;

            System.out.println("\n===== SEARCH RESULTS =====");

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        "Book ID: " +
                        resultSet.getInt("book_id")
                );

                System.out.println(
                        "Title: " +
                        resultSet.getString("title")
                );

                System.out.println(
                        "Author: " +
                        resultSet.getString("author")
                );

                System.out.println(
                        "Category: " +
                        resultSet.getString("category")
                );

                System.out.println(
                        "Quantity: " +
                        resultSet.getInt("quantity")
                );

                System.out.println("-------------------------");
            }

            if (!found) {

                System.out.println(
                        "Book not found!"
                );
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
