import java.util.*;

public class LibraryManagementSystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Library library = new Library();

        library.addBook(new Book(1,"Clean Code","Robert Martin","Technology",2008));
        library.addBook(new Book(2,"Dune","Frank Herbert","Fiction",1965));
        library.addBook(new Book(3,"Sapiens","Yuval Harari","History",2011));
        library.addBook(new Book(4,"1984","George Orwell","Fiction",1949));

        while(true){

            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Show Books");
            System.out.println("2. Add Book");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch(choice){

                case 1:
                    library.showBooks();
                    break;

                case 2:

                    System.out.print("Title: ");
                    String title=sc.nextLine();

                    System.out.print("Author: ");
                    String author=sc.nextLine();

                    System.out.print("Genre: ");
                    String genre=sc.nextLine();

                    System.out.print("Year: ");
                    int year=sc.nextInt();

                    library.addBook(title,author,genre,year);
                    break;

                case 3:

                    System.out.print("Book ID: ");
                    int id=sc.nextInt();
                    sc.nextLine();

                    System.out.print("Member Name: ");
                    String name=sc.nextLine();

                    library.borrowBook(id,name);
                    break;

                case 4:

                    System.out.print("Book ID: ");
                    int rid=sc.nextInt();

                    library.returnBook(rid);
                    break;

                case 5:
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice");
            }

        }

    }

}