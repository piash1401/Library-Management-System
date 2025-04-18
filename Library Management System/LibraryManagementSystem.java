import java.io.*;
import java.util.*;

class Book implements Serializable {
    private String title;
    private String author;
    private int id;
    private int quantity;

    public Book(String title, String author, int id, int quantity) {
        this.title = title;
        this.author = author;
        this.id = id;
        this.quantity = quantity;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getId() { return id; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void displayInfo() {
        System.out.println("ID: " + id + " | " + title + " by " + author + " | Quantity: " + quantity);
    }
}

abstract class LibraryUser {
    protected String name;

    public LibraryUser(String name) {
        this.name = name;
    }

    public abstract void borrowBook(ArrayList<Book> books, int bookId);
}

class Member extends LibraryUser {
    public Member(String name) {
        super(name);
    }

    @Override
    public void borrowBook(ArrayList<Book> books, int bookId) {
        for (Book book : books) {
            if (book.getId() == bookId) {
                if (book.getQuantity() > 0) {
                    System.out.println(name + " borrowed " + book.getTitle());
                    book.setQuantity(book.getQuantity() - 1);
                    return;
                } else {
                    System.out.println("Book not available.");
                    return;
                }
            }
        }
        System.out.println("Book not found.");
    }

    public void displayInfo() {
        System.out.println("Member: " + name);
    }
}

class Admin extends LibraryUser {
    public Admin(String name) {
        super(name);
    }

    @Override
    public void borrowBook(ArrayList<Book> books, int bookId) {
        System.out.println("Admin doesn't borrow books.");
    }

    public void addBook(ArrayList<Book> books, Book newBook) {
        books.add(newBook);
        System.out.println("Book added: " + newBook.getTitle());
    }

    public void displayInfo() {
        System.out.println("Admin: " + name);
    }
}

public class LibraryManagementSystem {
    static final String FILE_NAME = "books.dat";

    public static void saveBooks(ArrayList<Book> books) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(books);
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

    public static ArrayList<Book> loadBooks() {
        ArrayList<Book> books = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            books = (ArrayList<Book>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing book data found. Starting fresh.");
        }
        return books;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Book> books = loadBooks();
        Admin admin = new Admin("Alice");
        Member member = new Member("Bob");

        int choice;
        do {
            System.out.println("\n--- Library Menu ---");
            System.out.println("1. Display Books");
            System.out.println("2. Borrow Book (Member)");
            System.out.println("3. Add Book (Admin)");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    for (Book b : books) {
                        b.displayInfo();
                    }
                    break;
                case 2:
                    System.out.print("Enter Book ID to borrow: ");
                    int borrowId = scanner.nextInt();
                    member.borrowBook(books, borrowId);
                    break;
                case 3:
                    scanner.nextLine(); // consume newline
                    System.out.print("Enter Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter Author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter ID: ");
                    int id = scanner.nextInt();
                    System.out.print("Enter Quantity: ");
                    int qty = scanner.nextInt();
                    Book newBook = new Book(title, author, id, qty);
                    admin.addBook(books, newBook);
                    break;
                case 4:
                    saveBooks(books);
                    System.out.println("Exiting and saving data...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 4);

        scanner.close();
    }
}