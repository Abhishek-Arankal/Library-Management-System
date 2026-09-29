package library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookIssueDAO {


    // ISSUE BOOK
	public static void issueBook(
	        int bookId,
	        int memberId,
	        String dueDate) {

	    Connection connection = null;

	    try {

	        connection = DBConnection.getConnection();

	        // Start transaction
	        connection.setAutoCommit(false);


	        // ==============================
	        // 1. CHECK MEMBER
	        // ==============================

	        String memberSQL =
	                "SELECT member_id FROM members WHERE member_id = ?";

	        PreparedStatement memberStatement =
	                connection.prepareStatement(memberSQL);

	        memberStatement.setInt(1, memberId);

	        ResultSet memberResult =
	                memberStatement.executeQuery();

	        if (!memberResult.next()) {

	            System.out.println(
	                    "Member not found! Please enter a valid Member ID."
	            );

	            memberResult.close();
	            memberStatement.close();

	            connection.rollback();
	            connection.close();

	            return;
	        }

	        memberResult.close();
	        memberStatement.close();


	        // ==============================
	        // 2. CHECK BOOK
	        // ==============================

	        String bookSQL =
	                "SELECT quantity FROM books WHERE book_id = ?";

	        PreparedStatement bookStatement =
	                connection.prepareStatement(bookSQL);

	        bookStatement.setInt(1, bookId);

	        ResultSet bookResult =
	                bookStatement.executeQuery();

	        if (!bookResult.next()) {

	            System.out.println(
	                    "Book not found! Please enter a valid Book ID."
	            );

	            bookResult.close();
	            bookStatement.close();

	            connection.rollback();
	            connection.close();

	            return;
	        }

	        int quantity =
	                bookResult.getInt("quantity");

	        bookResult.close();
	        bookStatement.close();


	        // ==============================
	        // 3. CHECK BOOK AVAILABILITY
	        // ==============================

	        if (quantity <= 0) {

	            System.out.println(
	                    "Book is not available!"
	            );

	            connection.rollback();
	            connection.close();

	            return;
	        }


	        // ==============================
	        // 4. INSERT ISSUE RECORD
	        // ==============================

	     // INSERT ISSUE RECORD

	        String issueSQL =
	                "INSERT INTO book_issues " +
	                "(book_id, member_id, issue_date, due_date) " +
	                "VALUES (?, ?, CURDATE(), ?)";

	        PreparedStatement issueStatement =
	                connection.prepareStatement(
	                        issueSQL,
	                        java.sql.Statement.RETURN_GENERATED_KEYS
	                );

	        issueStatement.setInt(1, bookId);
	        issueStatement.setInt(2, memberId);
	        issueStatement.setDate(
	                3,
	                java.sql.Date.valueOf(dueDate)
	        );

	        issueStatement.executeUpdate();


	        // GET GENERATED ISSUE ID

	        ResultSet generatedKeys =
	                issueStatement.getGeneratedKeys();

	        int issueId = 0;

	        if (generatedKeys.next()) {
	            issueId = generatedKeys.getInt(1);
	        }

	        generatedKeys.close();
	        issueStatement.close();


	        // ==============================
	        // 5. REDUCE BOOK QUANTITY
	        // ==============================

	        String updateSQL =
	                "UPDATE books " +
	                "SET quantity = quantity - 1 " +
	                "WHERE book_id = ?";

	        PreparedStatement updateStatement =
	                connection.prepareStatement(updateSQL);

	        updateStatement.setInt(1, bookId);

	        updateStatement.executeUpdate();

	        updateStatement.close();


	        // ==============================
	        // 6. COMMIT
	        // ==============================

	        connection.commit();

	        System.out.println(
	                "Book issued successfully!"
	        );
	        System.out.println(
	                "Your Issue ID is: " + issueId
	        );

	        connection.close();


	    } catch (Exception e) {

	        try {

	            if (connection != null) {
	                connection.rollback();
	            }

	        } catch (Exception rollbackError) {

	            rollbackError.printStackTrace();
	        }

	        e.printStackTrace();
	    }
	}
	
	public static void returnBook(int memberId, int bookId) {

	    Connection connection = null;

	    try {

	        connection = DBConnection.getConnection();

	        connection.setAutoCommit(false);

	        // Find active issue
	        String sql =
	                "SELECT issue_id, due_date " +
	                "FROM book_issues " +
	                "WHERE member_id = ? " +
	                "AND book_id = ? " +
	                "AND return_date IS NULL";

	        PreparedStatement statement =
	                connection.prepareStatement(sql);

	        statement.setInt(1, memberId);
	        statement.setInt(2, bookId);

	        ResultSet resultSet =
	                statement.executeQuery();

	        if (!resultSet.next()) {

	            System.out.println(
	                    "This book is not currently issued to this member!"
	            );

	            connection.rollback();
	            connection.close();

	            return;
	        }

	        int issueId =
	                resultSet.getInt("issue_id");

	        java.sql.Date dueDate =
	                resultSet.getDate("due_date");

	        resultSet.close();
	        statement.close();


	        // Calculate fine
	        LocalDate today = LocalDate.now();

	        LocalDate due =
	                dueDate.toLocalDate();

	        long lateDays = 0;

	        if (today.isAfter(due)) {

	            lateDays =
	                    ChronoUnit.DAYS.between(
	                            due,
	                            today
	                    );
	        }

	        double fine = lateDays * 5;


	        // Update issue
	        String updateIssue =
	                "UPDATE book_issues " +
	                "SET return_date = ?, fine = ? " +
	                "WHERE issue_id = ?";

	        PreparedStatement updateStatement =
	                connection.prepareStatement(updateIssue);

	        updateStatement.setDate(
	                1,
	                java.sql.Date.valueOf(today)
	        );

	        updateStatement.setDouble(2, fine);

	        updateStatement.setInt(3, issueId);

	        updateStatement.executeUpdate();

	        updateStatement.close();


	        // Increase book quantity
	        String updateBook =
	                "UPDATE books " +
	                "SET quantity = quantity + 1 " +
	                "WHERE book_id = ?";

	        PreparedStatement bookStatement =
	                connection.prepareStatement(updateBook);

	        bookStatement.setInt(1, bookId);

	        bookStatement.executeUpdate();

	        bookStatement.close();


	        connection.commit();

	        System.out.println(
	                "Book returned successfully!"
	        );

	        System.out.println(
	                "Fine: ₹" + fine
	        );

	        connection.close();

	    } catch (Exception e) {

	        try {

	            if (connection != null) {
	                connection.rollback();
	            }

	        } catch (Exception rollbackError) {

	            rollbackError.printStackTrace();
	        }

	        e.printStackTrace();
	    }
	}


    // CALCULATE FINE
    public static void calculateFine(int issueId) {

        String sql =
                "SELECT due_date, return_date, fine " +
                "FROM book_issues " +
                "WHERE issue_id = ?";

        try {

            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(1, issueId);

            ResultSet resultSet =
                    statement.executeQuery();


            if (!resultSet.next()) {

                System.out.println(
                        "Issue record not found!"
                );

                return;
            }


            Date dueDate =
                    resultSet.getDate("due_date");

            Date returnDate =
                    resultSet.getDate("return_date");

            double fine =
                    resultSet.getDouble("fine");


            System.out.println(
                    "\n===== FINE DETAILS ====="
            );

            System.out.println(
                    "Due Date: " + dueDate
            );

            System.out.println(
                    "Return Date: " + returnDate
            );


            if (returnDate == null) {

                LocalDate today =
                        LocalDate.now();

                LocalDate due =
                        dueDate.toLocalDate();

                long lateDays = 0;

                if (today.isAfter(due)) {

                    lateDays =
                            ChronoUnit.DAYS.between(
                                    due,
                                    today
                            );
                }

                fine = lateDays * 5;

                System.out.println(
                        "Current Late Days: " +
                        lateDays
                );

            }


            System.out.println(
                    "Fine: ₹" + fine
            );


            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
