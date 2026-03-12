public class BorrowRecord {

    private String memberName;
    private int bookId;

    public BorrowRecord(String memberName,int bookId){
        this.memberName=memberName;
        this.bookId=bookId;
    }

    public String toString(){

        return memberName+" borrowed book id "+bookId;

    }

}