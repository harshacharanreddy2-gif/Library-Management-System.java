    public class Book {

    private int id;
    private String title;
    private String author;
    private String genre;
    private int year;
    private String status="Available";

    public Book(int id,String title,String author,String genre,int year){
        this.id=id;
        this.title=title;
        this.author=author;
        this.genre=genre;
        this.year=year;
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String s){
        status=s;
    }

    public String toString(){

        return id+" | "+title+" | "+author+" | "+genre+" | "+year+" | "+status;

    }

}