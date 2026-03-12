import java.util.*;

public class Library {

    private ArrayList<Book> books=new ArrayList<>();
    private ArrayList<BorrowRecord> records=new ArrayList<>();

    private int nextId=5;

    public void addBook(Book b){

        books.add(b);

    }

    public void addBook(String title,String author,String genre,int year){

        books.add(new Book(nextId++,title,author,genre,year));

        System.out.println("Book added!");

    }

    public void showBooks(){

        for(Book b:books){

            System.out.println(b);

        }

    }

    public Book findBook(int id){

        for(Book b:books){

            if(b.getId()==id)
                return b;

        }

        return null;

    }

    public void borrowBook(int id,String name){

        Book b=findBook(id);

        if(b!=null && b.getStatus().equals("Available")){

            b.setStatus("Borrowed");

            records.add(new BorrowRecord(name,id));

            System.out.println("Book borrowed!");

        }

        else{

            System.out.println("Book not available");

        }

    }

    public void returnBook(int id){

        Book b=findBook(id);

        if(b!=null){

            b.setStatus("Available");

            System.out.println("Book returned");

        }

    }

}