package library;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);


        while (true) {

            System.out.println();
            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "       LIBRARY MANAGEMENT SYSTEM"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println("1. Add Book");
            System.out.println("2. Search Book");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Calculate Fine");
            System.out.println("6. Exit");

            System.out.println(
                    "========================================"
            );

            System.out.print(
                    "Enter your choice: "
            );

            int choice = sc.nextInt();


            switch (choice) {


                // =================================
                // ADD BOOK
                // =================================

                case 1:

                    System.out.println(
                            "\n----- ADD BOOK -----"
                    );

                    sc.nextLine();

                    System.out.print(
                            "Enter Book Title: "
                    );

                    String title =
                            sc.nextLine();


                    System.out.print(
                            "Enter Author: "
                    );

                    String author =
                            sc.nextLine();


                    System.out.print(
                            "Enter Category: "
                    );

                    String category =
                            sc.nextLine();


                    System.out.print(
                            "Enter Quantity: "
                    );

                    int quantity =
                            sc.nextInt();


                    BookDAO.addBook(
                            title,
                            author,
                            category,
                            quantity
                    );

                    break;


                // =================================
                // SEARCH BOOK
                // =================================

                case 2:

                    System.out.println(
                            "\n----- SEARCH BOOK -----"
                    );

                    sc.nextLine();

                    System.out.print(
                            "Enter Book Title: "
                    );

                    String searchTitle =
                            sc.nextLine();


                    BookDAO.searchBook(
                            searchTitle
                    );

                    break;


                // =================================
                // ISSUE BOOK
                // =================================

                case 3:

                    System.out.println(
                            "\n----- ISSUE BOOK -----"
                    );


                    System.out.print(
                            "Enter Book ID: "
                    );

                    int bookId =
                            sc.nextInt();


                    System.out.print(
                            "Enter Member ID: "
                    );

                    int memberId =
                            sc.nextInt();


                    sc.nextLine();


                    System.out.print(
                            "Enter Due Date (YYYY-MM-DD): "
                    );

                    String dueDate =
                            sc.nextLine();


                    try {

                        java.time.LocalDate.parse(dueDate);

                        BookIssueDAO.issueBook(
                                bookId,
                                memberId,
                                dueDate
                        );

                    } catch (java.time.format.DateTimeParseException e) {

                        System.out.println(
                                "Invalid date! Please enter date in YYYY-MM-DD format."
                        );
                    }

                    break;


                // =================================
                // RETURN BOOK
                // =================================

                case 4:

                    System.out.println("\n----- RETURN BOOK -----");

                    System.out.print("Enter Member ID: ");
                    int returnMemberId = sc.nextInt();

                    System.out.print("Enter Book ID: ");
                    int returnBookId = sc.nextInt();

                    BookIssueDAO.returnBook(
                            returnMemberId,
                            returnBookId
                    );

                    break;


                // =================================
                // CALCULATE FINE
                // =================================

                case 5:

                    System.out.println(
                            "\n----- CALCULATE FINE -----"
                    );


                    System.out.print(
                            "Enter Issue ID: "
                    );

                    int fineIssueId =
                            sc.nextInt();


                    BookIssueDAO.calculateFine(
                            fineIssueId
                    );

                    break;


                // =================================
                // EXIT
                // =================================

                case 6:

                    System.out.println(
                            "\nThank you for using " +
                            "Library Management System!"
                    );

                    sc.close();

                    System.exit(0);

                    break;


                // =================================
                // INVALID CHOICE
                // =================================

                default:

                    System.out.println(
                            "Invalid choice! " +
                            "Please enter 1 to 6."
                    );
            }
        }
    }
}
