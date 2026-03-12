import java.util.*;

// ╔══════════════════════════════════════════════════════════════════════════════╗
// ║         LIBRARY RESOURCE MANAGEMENT SYSTEM — ALL-IN-ONE JAVA FILE           ║
// ║  Topics: Searching · Sorting · Linked Lists · Polynomial ADT                ║
// ║          Stacks & Queues · Hashing · Priority Queues (Heaps)                ║
// ╚══════════════════════════════════════════════════════════════════════════════╝

public class LibraryManagementSystem {

    // ══════════════════════════════════════════════════════════════════════════
    //  MODEL CLASSES
    // ══════════════════════════════════════════════════════════════════════════

    // ── Book ──────────────────────────────────────────────────────────────────
    static class Book implements Comparable<Book> {
        private int    id;
        private String title;
        private String author;
        private String genre;
        private int    year;
        private String status;
        private int    copies;
        private int    priority;

        Book(int id, String title, String author, String genre, int year) {
            this.id       = id;
            this.title    = title;
            this.author   = author;
            this.genre    = genre;
            this.year     = year;
            this.status   = "Available";
            this.copies   = 1;
            this.priority = id;
        }

        int    getId()       { return id; }
        String getTitle()    { return title; }
        String getAuthor()   { return author; }
        String getGenre()    { return genre; }
        int    getYear()     { return year; }
        String getStatus()   { return status; }
        int    getCopies()   { return copies; }
        int    getPriority() { return priority; }

        void setStatus(String s)   { this.status   = s; }
        void setCopies(int c)      { this.copies   = c; }
        void setPriority(int p)    { this.priority = p; }

        @Override
        public int compareTo(Book o) { return title.compareToIgnoreCase(o.title); }

        @Override
        public String toString() {
            return String.format("[%2d] %-35s | %-20s | %-12s | %d | %s",
                    id, title, author, genre, year, status);
        }

        Book copy() {
            Book b = new Book(id, title, author, genre, year);
            b.setStatus(status); b.setCopies(copies); b.setPriority(priority);
            return b;
        }
    }

    // ── BorrowRecord ─────────────────────────────────────────────────────────
    static class BorrowRecord {
        private static int counter = 1;
        private int    id;
        private String memberName, memberId, email;
        private int    bookId;
        private String bookTitle, borrowDate, returnDate, status;

        BorrowRecord(String memberName, String memberId, String email,
                     int bookId, String bookTitle, String borrowDate, String returnDate) {
            this.id = counter++;
            this.memberName = memberName; this.memberId  = memberId;
            this.email      = email;      this.bookId    = bookId;
            this.bookTitle  = bookTitle;  this.borrowDate = borrowDate;
            this.returnDate = returnDate; this.status    = "Active";
        }

        int    getId()         { return id; }
        String getMemberName() { return memberName; }
        String getMemberId()   { return memberId; }
        String getEmail()      { return email; }
        int    getBookId()     { return bookId; }
        String getBookTitle()  { return bookTitle; }
        String getBorrowDate() { return borrowDate; }
        String getReturnDate() { return returnDate; }
        String getStatus()     { return status; }
        void   setStatus(String s) { this.status = s; }

        @Override
        public String toString() {
            return String.format("Record#%d | %s (%s) | Book:'%s' | %s→%s | %s",
                    id, memberName, memberId, bookTitle, borrowDate, returnDate, status);
        }
    }

    // ── Library ───────────────────────────────────────────────────────────────
    static class Library {
        private ArrayList<Book>         books   = new ArrayList<>();
        private ArrayList<BorrowRecord> records = new ArrayList<>();
        private int nextId = 11;

        void addBook(Book b) { books.add(b); }

        void addBook(String title, String author, String genre, int year) {
            books.add(new Book(nextId++, title, author, genre, year));
        }

        boolean deleteBook(int id) { return books.removeIf(b -> b.getId() == id); }

        Book findById(int id) {
            for (Book b : books) if (b.getId() == id) return b;
            return null;
        }

        ArrayList<Book> getBooks()   { return books; }
        ArrayList<BorrowRecord> getRecords() { return records; }

        ArrayList<Book> booksCopy() {
            ArrayList<Book> copy = new ArrayList<>();
            for (Book b : books) copy.add(b.copy());
            return copy;
        }

        BorrowRecord borrowBook(int bookId, String memberName, String memberId,
                                String email, String borrowDate, String returnDate) {
            Book b = findById(bookId);
            if (b == null || !b.getStatus().equals("Available")) return null;
            b.setStatus("Borrowed");
            BorrowRecord rec = new BorrowRecord(memberName, memberId, email,
                    bookId, b.getTitle(), borrowDate, returnDate);
            records.add(rec);
            return rec;
        }

        boolean returnBook(int bookId) {
            Book b = findById(bookId);
            if (b == null) return false;
            b.setStatus("Available");
            for (BorrowRecord r : records)
                if (r.getBookId() == bookId && r.getStatus().equals("Active")) {
                    r.setStatus("Returned"); break;
                }
            return true;
        }

        void printStats() {
            long avail    = books.stream().filter(b -> b.getStatus().equals("Available")).count();
            long borrowed = books.stream().filter(b -> b.getStatus().equals("Borrowed")).count();
            long active   = records.stream().filter(r -> r.getStatus().equals("Active")).count();
            System.out.println("📊 Stats  →  Total: " + books.size()
                    + "  |  Available: " + avail
                    + "  |  Borrowed: " + borrowed
                    + "  |  Active Records: " + active);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MODULE 1 — SEARCHING
    // ══════════════════════════════════════════════════════════════════════════

    static Book linearSearchByTitle(ArrayList<Book> books, String query) {
        int comparisons = 0;
        String q = query.toLowerCase();
        for (Book b : books) {
            comparisons++;
            if (b.getTitle().toLowerCase().contains(q)) {
                System.out.println("   LinearSearch: found in " + comparisons + " comparison(s)");
                return b;
            }
        }
        System.out.println("   LinearSearch: not found after " + comparisons + " comparison(s)");
        return null;
    }

    static Book binarySearchByTitle(ArrayList<Book> sorted, String exactTitle) {
        int lo = 0, hi = sorted.size() - 1, comparisons = 0;
        String query = exactTitle.toLowerCase();
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            comparisons++;
            int cmp = sorted.get(mid).getTitle().toLowerCase().compareTo(query);
            if      (cmp == 0) {
                System.out.println("   BinarySearch: found at index " + mid + " in " + comparisons + " comparison(s)");
                return sorted.get(mid);
            }
            else if (cmp < 0) lo = mid + 1;
            else              hi = mid - 1;
        }
        System.out.println("   BinarySearch: not found after " + comparisons + " comparison(s)");
        return null;
    }

    static void demoSearching(Library library) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║             MODULE 1 — SEARCHING ALGORITHMS                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        ArrayList<Book> books = library.getBooks();

        System.out.println("\n▶ Linear Search (unsorted) for '1984':");
        Book found = linearSearchByTitle(books, "1984");
        if (found != null) System.out.println("   Result: " + found);

        System.out.println("\n▶ Linear Search for non-existent 'XYZ':");
        linearSearchByTitle(books, "XYZ");

        ArrayList<Book> sorted = library.booksCopy();
        Collections.sort(sorted);

        System.out.println("\n▶ Binary Search (sorted) for 'Dune':");
        found = binarySearchByTitle(sorted, "Dune");
        if (found != null) System.out.println("   Result: " + found);

        System.out.println("\n▶ Binary Search for 'Meditations':");
        found = binarySearchByTitle(sorted, "Meditations");
        if (found != null) System.out.println("   Result: " + found);

        int n = books.size();
        System.out.println("\n▶ Complexity at n=" + n + ":");
        System.out.println("   Linear O(n) = " + n + "  |  Binary O(log₂n) ≈ " + (int)(Math.log(n)/Math.log(2)));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MODULE 2 — SORTING
    // ══════════════════════════════════════════════════════════════════════════

    static ArrayList<Book> bubbleSort(ArrayList<Book> list) {
        ArrayList<Book> arr = cloneBooks(list);
        int n = arr.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr.get(j).getTitle().compareToIgnoreCase(arr.get(j+1).getTitle()) > 0) {
                    swapBooks(arr, j, j+1); swapped = true;
                }
            }
            if (!swapped) break;
        }
        return arr;
    }

    static ArrayList<Book> insertionSort(ArrayList<Book> list) {
        ArrayList<Book> arr = cloneBooks(list);
        for (int i = 1; i < arr.size(); i++) {
            Book key = arr.get(i);
            int j = i - 1;
            while (j >= 0 && arr.get(j).getTitle().compareToIgnoreCase(key.getTitle()) > 0) {
                arr.set(j + 1, arr.get(j)); j--;
            }
            arr.set(j + 1, key);
        }
        return arr;
    }

    static ArrayList<Book> selectionSort(ArrayList<Book> list) {
        ArrayList<Book> arr = cloneBooks(list);
        for (int i = 0; i < arr.size() - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < arr.size(); j++)
                if (arr.get(j).getTitle().compareToIgnoreCase(arr.get(minIdx).getTitle()) < 0)
                    minIdx = j;
            swapBooks(arr, i, minIdx);
        }
        return arr;
    }

    static ArrayList<Book> mergeSort(ArrayList<Book> list) {
        if (list.size() <= 1) return list;
        int mid = list.size() / 2;
        ArrayList<Book> left  = mergeSort(new ArrayList<>(list.subList(0, mid)));
        ArrayList<Book> right = mergeSort(new ArrayList<>(list.subList(mid, list.size())));
        return mergeLists(left, right);
    }

    private static ArrayList<Book> mergeLists(ArrayList<Book> L, ArrayList<Book> R) {
        ArrayList<Book> res = new ArrayList<>();
        int i = 0, j = 0;
        while (i < L.size() && j < R.size())
            res.add(L.get(i).getTitle().compareToIgnoreCase(R.get(j).getTitle()) <= 0
                    ? L.get(i++) : R.get(j++));
        while (i < L.size()) res.add(L.get(i++));
        while (j < R.size()) res.add(R.get(j++));
        return res;
    }

    static ArrayList<Book> quickSort(ArrayList<Book> list) {
        ArrayList<Book> arr = cloneBooks(list);
        quickSortHelper(arr, 0, arr.size() - 1);
        return arr;
    }

    private static void quickSortHelper(ArrayList<Book> arr, int lo, int hi) {
        if (lo < hi) {
            int p = partition(arr, lo, hi);
            quickSortHelper(arr, lo, p - 1);
            quickSortHelper(arr, p + 1, hi);
        }
    }

    private static int partition(ArrayList<Book> arr, int lo, int hi) {
        String pivot = arr.get(hi).getTitle().toLowerCase();
        int i = lo - 1;
        for (int j = lo; j < hi; j++)
            if (arr.get(j).getTitle().toLowerCase().compareTo(pivot) <= 0)
                swapBooks(arr, ++i, j);
        swapBooks(arr, i + 1, hi);
        return i + 1;
    }

    static ArrayList<Book> sortByYear(ArrayList<Book> list) {
        if (list.size() <= 1) return list;
        int mid = list.size() / 2;
        ArrayList<Book> L = sortByYear(new ArrayList<>(list.subList(0, mid)));
        ArrayList<Book> R = sortByYear(new ArrayList<>(list.subList(mid, list.size())));
        ArrayList<Book> res = new ArrayList<>();
        int i = 0, j = 0;
        while (i < L.size() && j < R.size())
            res.add(L.get(i).getYear() <= R.get(j).getYear() ? L.get(i++) : R.get(j++));
        while (i < L.size()) res.add(L.get(i++));
        while (j < R.size()) res.add(R.get(j++));
        return res;
    }

    private static void swapBooks(ArrayList<Book> arr, int a, int b) {
        Book tmp = arr.get(a); arr.set(a, arr.get(b)); arr.set(b, tmp);
    }

    private static ArrayList<Book> cloneBooks(ArrayList<Book> src) {
        ArrayList<Book> copy = new ArrayList<>();
        for (Book b : src) copy.add(b.copy());
        return copy;
    }

    static void demoSorting(Library library) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║             MODULE 2 — SORTING ALGORITHMS                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        ArrayList<Book> books = library.booksCopy();

        System.out.println("\n▶ Bubble Sort (by title A→Z)  [O(n²)]:");
        printTitles(bubbleSort(books));

        System.out.println("\n▶ Insertion Sort (by title A→Z)  [O(n²)]:");
        printTitles(insertionSort(books));

        System.out.println("\n▶ Selection Sort (by title A→Z)  [O(n²)]:");
        printTitles(selectionSort(books));

        System.out.println("\n▶ Merge Sort (by title A→Z)  [O(n log n)]:");
        printTitles(mergeSort(books));

        System.out.println("\n▶ Quick Sort (by title A→Z)  [O(n log n) avg]:");
        printTitles(quickSort(books));

        System.out.println("\n▶ Merge Sort by Year (oldest → newest):");
        printTitles(sortByYear(books));

        System.out.println("\n   Complexity Summary:");
        System.out.println("   ┌────────────────┬───────────┬───────────┬─────────┐");
        System.out.println("   │ Algorithm      │ Best      │ Worst     │ Space   │");
        System.out.println("   ├────────────────┼───────────┼───────────┼─────────┤");
        System.out.println("   │ Bubble Sort    │ O(n)      │ O(n²)     │ O(1)    │");
        System.out.println("   │ Insertion Sort │ O(n)      │ O(n²)     │ O(1)    │");
        System.out.println("   │ Selection Sort │ O(n²)     │ O(n²)     │ O(1)    │");
        System.out.println("   │ Merge Sort     │ O(n log n)│ O(n log n)│ O(n)    │");
        System.out.println("   │ Quick Sort     │ O(n log n)│ O(n²)     │ O(log n)│");
        System.out.println("   └────────────────┴───────────┴───────────┴─────────┘");
    }

    private static void printTitles(ArrayList<Book> books) {
        for (int i = 0; i < books.size(); i++)
            System.out.printf("   %2d. %s (%d)%n", i+1, books.get(i).getTitle(), books.get(i).getYear());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MODULE 3A — LINKED LISTS
    // ══════════════════════════════════════════════════════════════════════════

    static class SinglyLinkedList {
        private static class Node { Book data; Node next; Node(Book d){data=d;} }
        private Node head; private int size;

        void addLast(Book b) {
            Node n = new Node(b);
            if (head == null) { head = n; }
            else { Node c = head; while (c.next != null) c = c.next; c.next = n; }
            size++;
        }
        void addFirst(Book b) { Node n = new Node(b); n.next = head; head = n; size++; }

        boolean delete(int id) {
            if (head == null) return false;
            if (head.data.getId() == id) { head = head.next; size--; return true; }
            Node c = head;
            while (c.next != null) {
                if (c.next.data.getId() == id) { c.next = c.next.next; size--; return true; }
                c = c.next;
            }
            return false;
        }

        Book search(int id) {
            for (Node c = head; c != null; c = c.next) if (c.data.getId() == id) return c.data;
            return null;
        }

        int size() { return size; }

        void print() {
            System.out.print("   SLL: ");
            for (Node c = head; c != null; c = c.next)
                System.out.print("[" + c.data.getTitle() + "]" + (c.next != null ? " → " : ""));
            System.out.println(" → null");
        }
    }

    static class DoublyLinkedList {
        private static class Node { Book data; Node prev, next; Node(Book d){data=d;} }
        private Node head, tail; private int size;

        void addLast(Book b) {
            Node n = new Node(b);
            if (tail == null) { head = tail = n; }
            else { n.prev = tail; tail.next = n; tail = n; }
            size++;
        }
        void addFirst(Book b) {
            Node n = new Node(b);
            if (head == null) { head = tail = n; }
            else { n.next = head; head.prev = n; head = n; }
            size++;
        }
        boolean delete(int id) {
            for (Node c = head; c != null; c = c.next) {
                if (c.data.getId() == id) {
                    if (c.prev != null) c.prev.next = c.next; else head = c.next;
                    if (c.next != null) c.next.prev = c.prev; else tail = c.prev;
                    size--; return true;
                }
            }
            return false;
        }
        int size() { return size; }
        void printForward() {
            System.out.print("   DLL fwd: ");
            for (Node c = head; c != null; c = c.next)
                System.out.print("[" + c.data.getTitle() + "]" + (c.next != null ? " ⇄ " : ""));
            System.out.println();
        }
        void printBackward() {
            System.out.print("   DLL bwd: ");
            for (Node c = tail; c != null; c = c.prev)
                System.out.print("[" + c.data.getTitle() + "]" + (c.prev != null ? " ⇄ " : ""));
            System.out.println();
        }
    }

    static class CircularLinkedList {
        private static class Node { Book data; Node next; Node(Book d){data=d;} }
        private Node tail; private int size;

        void add(Book b) {
            Node n = new Node(b);
            if (tail == null) { tail = n; tail.next = tail; }
            else { n.next = tail.next; tail.next = n; tail = n; }
            size++;
        }
        void print() {
            if (tail == null) { System.out.println("   CLL: empty"); return; }
            Node c = tail.next;
            System.out.print("   CLL: ");
            for (int i = 0; i < size; i++) {
                System.out.print("[" + c.data.getTitle() + "]" + (i < size-1 ? " → " : ""));
                c = c.next;
            }
            System.out.println(" ↩ (wraps to head)");
        }
    }

    static void demoLinkedList() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           MODULE 3A — LINKED LISTS (List ADT)                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        Book b1 = new Book(101,"Clean Code",  "R. Martin", "Technology",2008);
        Book b2 = new Book(102,"Dune",         "F. Herbert","Fiction",   1965);
        Book b3 = new Book(103,"Sapiens",      "Y. Harari", "History",   2011);
        Book b4 = new Book(104,"1984",         "G. Orwell", "Fiction",   1949);

        System.out.println("\n▶ Singly Linked List:");
        SinglyLinkedList sll = new SinglyLinkedList();
        sll.addLast(b1); sll.addLast(b2); sll.addLast(b3); sll.addFirst(b4);
        sll.print();
        System.out.println("   Search id=102: " + sll.search(102));
        sll.delete(102);
        System.out.print("   After delete(102): "); sll.print();

        System.out.println("\n▶ Doubly Linked List:");
        DoublyLinkedList dll = new DoublyLinkedList();
        dll.addLast(b1); dll.addLast(b2); dll.addLast(b3); dll.addFirst(b4);
        dll.printForward();
        dll.printBackward();
        dll.delete(103);
        System.out.print("   After delete(103): "); dll.printForward();

        System.out.println("\n▶ Circularly Linked List (reading queue):");
        CircularLinkedList cll = new CircularLinkedList();
        cll.add(b1); cll.add(b2); cll.add(b3); cll.add(b4);
        cll.print();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MODULE 3B — POLYNOMIAL ADT
    // ══════════════════════════════════════════════════════════════════════════

    static class Polynomial {
        private static class TermNode { double coeff; int exp; TermNode next; TermNode(double c,int e){coeff=c;exp=e;} }
        private TermNode head;
        private String   name;

        Polynomial(String name) { this.name = name; }

        void addTerm(double coeff, int exp) {
            if (coeff == 0) return;
            TermNode n = new TermNode(coeff, exp);
            if (head == null || head.exp < exp) { n.next = head; head = n; return; }
            TermNode c = head;
            while (c.next != null && c.next.exp > exp) c = c.next;
            if (c.next != null && c.next.exp == exp) { c.next.coeff += coeff; return; }
            n.next = c.next; c.next = n;
        }

        double evaluate(double x) {
            double res = 0;
            for (TermNode t = head; t != null; t = t.next)
                res += t.coeff * Math.pow(x, t.exp);
            return res;
        }

        static Polynomial add(Polynomial p1, Polynomial p2) {
            Polynomial r = new Polynomial("sum");
            for (TermNode t = p1.head; t != null; t = t.next) r.addTerm(t.coeff, t.exp);
            for (TermNode t = p2.head; t != null; t = t.next) r.addTerm(t.coeff, t.exp);
            return r;
        }

        @Override
        public String toString() {
            if (head == null) return "0";
            StringBuilder sb = new StringBuilder();
            for (TermNode t = head; t != null; t = t.next) {
                if (sb.length() > 0 && t.coeff > 0) sb.append(" + ");
                else if (t.coeff < 0) sb.append(" - ");
                double abs = Math.abs(t.coeff);
                if      (t.exp == 0) sb.append(abs);
                else if (t.exp == 1) sb.append(abs == 1 ? "" : abs).append("x");
                else                  sb.append(abs == 1 ? "" : abs).append("x^").append(t.exp);
            }
            return sb.toString().trim();
        }
    }

    static void demoPolynomial() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║       MODULE 3B — POLYNOMIAL ADT (List Application)          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        Polynomial lateFee = new Polynomial("LateFee");
        lateFee.addTerm(0.5, 2); lateFee.addTerm(2.0, 1); lateFee.addTerm(5.0, 0);
        System.out.println("\n   LateFee f(x)  = " + lateFee);

        Polynomial discount = new Polynomial("Discount");
        discount.addTerm(0.1, 1); discount.addTerm(0.5, 0);
        System.out.println("   Discount g(x) = " + discount);

        System.out.println("\n   Days overdue=3  →  Late fee  = ₹" + lateFee.evaluate(3));
        System.out.println("   Days overdue=7  →  Late fee  = ₹" + lateFee.evaluate(7));
        System.out.println("   Member years=5  →  Discount  = ₹" + discount.evaluate(5));

        Polynomial net = Polynomial.add(lateFee, discount);
        System.out.println("\n   Sum polynomial = " + net);
        System.out.println("   Net at x=3     = " + net.evaluate(3));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MODULE 4 — STACKS & QUEUES
    // ══════════════════════════════════════════════════════════════════════════

    static class LinkedStack<T> {
        private static class Node<T> { T data; Node<T> next; Node(T d){data=d;} }
        private Node<T> top; private int size;
        void push(T item) { Node<T> n = new Node<>(item); n.next = top; top = n; size++; }
        T    pop()  { if (isEmpty()) throw new RuntimeException("Stack underflow"); T d=top.data; top=top.next; size--; return d; }
        T    peek() { if (isEmpty()) throw new RuntimeException("Stack empty"); return top.data; }
        boolean isEmpty() { return top == null; }
        int size() { return size; }
    }

    static class LinkedQueue<T> {
        private static class Node<T> { T data; Node<T> next; Node(T d){data=d;} }
        private Node<T> front, rear; private int size;
        void enqueue(T item) {
            Node<T> n = new Node<>(item);
            if (rear == null) { front = rear = n; } else { rear.next = n; rear = n; }
            size++;
        }
        T dequeue() {
            if (isEmpty()) throw new RuntimeException("Queue underflow");
            T d = front.data; front = front.next;
            if (front == null) rear = null;
            size--; return d;
        }
        T    peek()    { if (isEmpty()) throw new RuntimeException("Queue empty"); return front.data; }
        boolean isEmpty() { return front == null; }
        int size() { return size; }
    }

    static class CircularQueue<T> {
        private Object[] data;
        private int front, rear, size, capacity;
        @SuppressWarnings("unchecked")
        CircularQueue(int cap) { data = new Object[cap]; capacity = cap; }
        void enqueue(T item) {
            if (size == capacity) throw new RuntimeException("Circular queue full");
            data[rear] = item; rear = (rear+1) % capacity; size++;
        }
        @SuppressWarnings("unchecked")
        T dequeue() {
            if (size == 0) throw new RuntimeException("Circular queue empty");
            T item = (T) data[front]; front = (front+1) % capacity; size--; return item;
        }
        boolean isEmpty() { return size == 0; }
        int size() { return size; }
    }

    static class Dequeue<T> {
        private static class Node<T> { T data; Node<T> prev, next; Node(T d){data=d;} }
        private Node<T> front, rear; private int size;
        void addFront(T item) {
            Node<T> n = new Node<>(item);
            if (front == null) { front = rear = n; } else { n.next = front; front.prev = n; front = n; }
            size++;
        }
        void addRear(T item) {
            Node<T> n = new Node<>(item);
            if (rear == null) { front = rear = n; } else { rear.next = n; n.prev = rear; rear = n; }
            size++;
        }
        T removeFront() {
            if (front == null) throw new RuntimeException("Dequeue empty");
            T d = front.data; front = front.next;
            if (front != null) front.prev = null; else rear = null;
            size--; return d;
        }
        T removeRear() {
            if (rear == null) throw new RuntimeException("Dequeue empty");
            T d = rear.data; rear = rear.prev;
            if (rear != null) rear.next = null; else front = null;
            size--; return d;
        }
        boolean isEmpty() { return size == 0; }
        int size() { return size; }
    }

    static String infixToPostfix(String infix) {
        StringBuilder result = new StringBuilder();
        LinkedStack<Character> stack = new LinkedStack<>();
        for (char c : infix.replace(" ","").toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                result.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') result.append(stack.pop());
                if (!stack.isEmpty()) stack.pop();
            } else {
                while (!stack.isEmpty() && opPrecedence(stack.peek()) >= opPrecedence(c))
                    result.append(stack.pop());
                stack.push(c);
            }
        }
        while (!stack.isEmpty()) result.append(stack.pop());
        return result.toString();
    }

    private static int opPrecedence(char op) {
        if (op=='+' || op=='-') return 1;
        if (op=='*' || op=='/') return 2;
        if (op=='^') return 3;
        return 0;
    }

    static double evaluatePostfix(String postfix) {
        LinkedStack<Double> stack = new LinkedStack<>();
        for (String token : postfix.split("")) {
            if (token.matches("\\d+")) { stack.push(Double.parseDouble(token)); }
            else {
                double b = stack.pop(), a = stack.pop();
                switch (token) {
                    case "+": stack.push(a+b); break;
                    case "-": stack.push(a-b); break;
                    case "*": stack.push(a*b); break;
                    case "/": stack.push(a/b); break;
                    case "^": stack.push(Math.pow(a,b)); break;
                }
            }
        }
        return stack.pop();
    }

    static boolean isBalanced(String expr) {
        LinkedStack<Character> stack = new LinkedStack<>();
        for (char c : expr.toCharArray()) {
            if (c=='(' || c=='[' || c=='{') { stack.push(c); }
            else if (c==')' || c==']' || c=='}') {
                if (stack.isEmpty()) return false;
                char t = stack.pop();
                if ((c==')' && t!='(') || (c==']' && t!='[') || (c=='}' && t!='{')) return false;
            }
        }
        return stack.isEmpty();
    }

    static void demoStackQueue() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║             MODULE 4 — STACKS & QUEUES                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        System.out.println("\n▶ Linked Stack (librarian browsing history):");
        LinkedStack<String> stk = new LinkedStack<>();
        stk.push("Home"); stk.push("Catalog"); stk.push("Book#5"); stk.push("Borrow Form");
        System.out.println("   Pushed: Home → Catalog → Book#5 → Borrow Form");
        System.out.println("   Back (pop): " + stk.pop());
        System.out.println("   Back (pop): " + stk.pop());
        System.out.println("   Current   : " + stk.peek());

        System.out.println("\n▶ Linked Queue (borrow request queue):");
        LinkedQueue<String> q = new LinkedQueue<>();
        q.enqueue("Alice wants Dune"); q.enqueue("Bob wants 1984"); q.enqueue("Carol wants Sapiens");
        while (!q.isEmpty()) System.out.println("   → Processed: " + q.dequeue());

        System.out.println("\n▶ Circular Queue (reading room slots, cap=4):");
        CircularQueue<String> cq = new CircularQueue<>(4);
        cq.enqueue("Alice"); cq.enqueue("Bob"); cq.enqueue("Carol"); cq.enqueue("Dave");
        System.out.println("   Dequeue: " + cq.dequeue() + "  |  size=" + cq.size());
        cq.enqueue("Eve");
        System.out.println("   After re-enqueue Eve, size=" + cq.size());

        System.out.println("\n▶ Dequeue (VIP at front, normal at rear):");
        Dequeue<String> dq = new Dequeue<>();
        dq.addRear("Alice"); dq.addRear("Bob"); dq.addFront("VIP-Charlie");
        System.out.println("   Front served: " + dq.removeFront());
        System.out.println("   Front served: " + dq.removeFront());
        System.out.println("   Rear removed: " + dq.removeRear());

        System.out.println("\n▶ Infix → Postfix Conversion:");
        String[] exprs = {"a+b*c","(a+b)*c","a+b*c-d/e","a*(b+c)-d"};
        for (String ex : exprs)
            System.out.printf("   Infix: %-20s → Postfix: %s%n", ex, infixToPostfix(ex));

        System.out.println("\n▶ Postfix Evaluation (3*3*2+5 = late fee):");
        System.out.println("   33*2*5+  =  " + evaluatePostfix("33*2*5+"));

        System.out.println("\n▶ Balanced Symbol Checker:");
        String[] tests = {"{[()]}","([)]","((())","(a+b)*c","{book[id](genre)}"};
        for (String t : tests)
            System.out.printf("   %-25s → %s%n", t, isBalanced(t) ? "✅ Balanced" : "❌ Unbalanced");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MODULE 5 — HASHING
    // ══════════════════════════════════════════════════════════════════════════

    static int hashDivision(int key, int m)        { return Math.abs(key % m); }
    static int hashMultiplication(int key, int m)  { return (int)(m * ((key * 0.6180339887) % 1)); }
    static int hashFolding(String key, int m)      { int s=0; for(char c:key.toCharArray()) s+=c; return Math.abs(s%m); }
    static int hashPolynomial(String key, int m)   { int h=0; for(char c:key.toCharArray()) h=(h*31+c)%m; return Math.abs(h); }

    static class SeparateChainingTable {
        private static final int SIZE = 11;
        @SuppressWarnings("unchecked")
        private LinkedList<Book>[] table = new LinkedList[SIZE];
        private int size;

        SeparateChainingTable() {
            for (int i = 0; i < SIZE; i++) table[i] = new LinkedList<>();
        }

        private int hash(int key) { return hashDivision(key, SIZE); }

        void insert(Book b) {
            int idx = hash(b.getId());
            table[idx].removeIf(x -> x.getId() == b.getId());
            table[idx].add(b); size++;
        }
        Book search(int id) {
            for (Book b : table[hash(id)]) if (b.getId() == id) return b;
            return null;
        }
        boolean delete(int id) {
            boolean removed = table[hash(id)].removeIf(b -> b.getId() == id);
            if (removed) size--;
            return removed;
        }
        void print() {
            System.out.println("   Separate Chaining Table (tableSize=" + SIZE + "):");
            for (int i = 0; i < SIZE; i++)
                if (!table[i].isEmpty()) {
                    System.out.print("   [" + i + "] → ");
                    for (Book b : table[i]) System.out.print("("+b.getId()+":\""+b.getTitle()+"\") → ");
                    System.out.println("null");
                }
        }
        double loadFactor() { return (double) size / SIZE; }
    }

    static class LinearProbingTable {
        private static final int SIZE = 13;
        private Book[]   table   = new Book[SIZE];
        private boolean[] deleted = new boolean[SIZE];
        private int size;

        private int hash(int key) { return hashDivision(key, SIZE); }

        void insert(Book b) {
            if ((double)(size+1)/SIZE >= 0.7) rehash();
            int h = hash(b.getId());
            for (int i = 0; i < SIZE; i++) {
                int idx = (h+i) % SIZE;
                if (table[idx] == null || deleted[idx]) {
                    table[idx] = b; deleted[idx] = false; size++; return;
                }
            }
            throw new RuntimeException("Table full");
        }
        Book search(int id) {
            int h = hash(id);
            for (int i = 0; i < SIZE; i++) {
                int idx = (h+i) % SIZE;
                if (table[idx] == null && !deleted[idx]) return null;
                if (table[idx] != null && !deleted[idx] && table[idx].getId() == id) return table[idx];
            }
            return null;
        }
        boolean delete(int id) {
            int h = hash(id);
            for (int i = 0; i < SIZE; i++) {
                int idx = (h+i) % SIZE;
                if (table[idx] == null) return false;
                if (!deleted[idx] && table[idx].getId() == id) { deleted[idx] = true; size--; return true; }
            }
            return false;
        }
        private void rehash() {
            System.out.println("   ♻️  Rehashing! (load ≥ 0.7, new size=" + (SIZE*2+1) + ")");
            Book[] old = table; boolean[] oldDel = deleted;
            table = new Book[SIZE*2+1]; deleted = new boolean[table.length]; size = 0;
            for (int i = 0; i < old.length; i++) if (old[i] != null && !oldDel[i]) insert(old[i]);
        }
        void print() {
            System.out.println("   Linear Probing Table (tableSize=" + SIZE + "):");
            for (int i = 0; i < SIZE; i++)
                if (table[i] != null && !deleted[i])
                    System.out.println("   ["+i+"] id="+table[i].getId()+" \""+table[i].getTitle()+"\"");
        }
        double loadFactor() { return (double) size / SIZE; }
    }

    static void demoHashing(Library library) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║             MODULE 5 — HASHING                               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        ArrayList<Book> books = library.getBooks();

        System.out.println("\n▶ Hash Functions on book IDs (m=11):");
        System.out.printf("   %-8s %-12s %-16s %-14s  Title%n","BookID","Division","Multiplication","Folding(title)");
        for (Book b : books)
            System.out.printf("   %-8d %-12d %-16d %-14d  \"%s\"%n",
                b.getId(), hashDivision(b.getId(),11),
                hashMultiplication(b.getId(),11), hashFolding(b.getTitle(),11),
                b.getTitle());

        System.out.println("\n▶ Separate Chaining:");
        SeparateChainingTable sc = new SeparateChainingTable();
        for (Book b : books) sc.insert(b);
        sc.print();
        System.out.printf("   Load factor: %.2f%n", sc.loadFactor());
        System.out.println("   Search id=5: " + sc.search(5));
        sc.delete(5);
        System.out.println("   After delete(5): " + sc.search(5));

        System.out.println("\n▶ Open Addressing — Linear Probing:");
        LinearProbingTable lp = new LinearProbingTable();
        for (Book b : books) lp.insert(b);
        lp.print();
        System.out.printf("   Load factor: %.2f%n", lp.loadFactor());
        System.out.println("   Search id=7: " + lp.search(7));
        lp.delete(7);
        System.out.println("   After delete(7): " + lp.search(7));

        System.out.println("\n▶ Extendible Hashing (concept):");
        System.out.println("   • Directory of 2^d pointers; buckets have local depth");
        System.out.println("   • On overflow: split bucket; double directory only if localDepth==globalDepth");
        System.out.println("   • Advantage: only directory doubles, data stays in place");
        System.out.println("   Simulated (global depth=2, bucketSize=2):");
        System.out.println("   00 → Bucket A (ld=2): [Book#4, Book#8]");
        System.out.println("   01 → Bucket B (ld=2): [Book#1, Book#5]");
        System.out.println("   10 → Bucket C (ld=1): [Book#2, Book#6]");
        System.out.println("   11 → Bucket C (ld=1): (shared with 10)");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MODULE 6 — PRIORITY QUEUES (BINARY HEAPS)
    // ══════════════════════════════════════════════════════════════════════════

    static class MinHeap {
        private ArrayList<Book> heap = new ArrayList<>();

        void insert(Book b) { heap.add(b); percolateUp(heap.size()-1); }

        Book deleteMin() {
            if (heap.isEmpty()) throw new RuntimeException("Heap empty");
            Book min  = heap.get(0);
            Book last = heap.remove(heap.size()-1);
            if (!heap.isEmpty()) { heap.set(0, last); percolateDown(0); }
            return min;
        }

        Book findMin() { if (heap.isEmpty()) throw new RuntimeException("Heap empty"); return heap.get(0); }

        boolean isEmpty() { return heap.isEmpty(); }
        int size()        { return heap.size(); }

        private void percolateUp(int i) {
            while (i > 0) {
                int p = (i-1)/2;
                if (heap.get(i).getPriority() < heap.get(p).getPriority()) { swapH(i,p); i=p; } else break;
            }
        }
        private void percolateDown(int i) {
            int n = heap.size();
            while (true) {
                int l=2*i+1, r=2*i+2, s=i;
                if (l<n && heap.get(l).getPriority() < heap.get(s).getPriority()) s=l;
                if (r<n && heap.get(r).getPriority() < heap.get(s).getPriority()) s=r;
                if (s!=i) { swapH(i,s); i=s; } else break;
            }
        }
        private void swapH(int a,int b){ Book t=heap.get(a); heap.set(a,heap.get(b)); heap.set(b,t); }

        static MinHeap buildHeap(ArrayList<Book> books) {
            MinHeap h = new MinHeap();
            h.heap.addAll(books);
            for (int i=(h.heap.size()/2)-1; i>=0; i--) h.percolateDown(i);
            return h;
        }

        ArrayList<Book> heapSort() {
            ArrayList<Book> sorted = new ArrayList<>();
            while (!isEmpty()) sorted.add(deleteMin());
            return sorted;
        }

        void print() {
            System.out.print("   MinHeap: [");
            for (int i=0; i<heap.size(); i++)
                System.out.print("p"+heap.get(i).getPriority()+":"+heap.get(i).getTitle()+(i<heap.size()-1?", ":""));
            System.out.println("]");
        }
    }

    static class MaxHeap {
        private ArrayList<int[]> heap = new ArrayList<>();

        void insert(int bookId, int score) {
            heap.add(new int[]{bookId, score});
            int i = heap.size()-1;
            while (i>0) {
                int p=(i-1)/2;
                if (heap.get(i)[1] > heap.get(p)[1]) { swapH(i,p); i=p; } else break;
            }
        }
        int[] extractMax() {
            int[] max = heap.get(0);
            int[] last = heap.remove(heap.size()-1);
            if (!heap.isEmpty()) { heap.set(0,last); maxDown(0); }
            return max;
        }
        private void maxDown(int i) {
            int n=heap.size();
            while (true) {
                int l=2*i+1, r=2*i+2, lg=i;
                if (l<n && heap.get(l)[1]>heap.get(lg)[1]) lg=l;
                if (r<n && heap.get(r)[1]>heap.get(lg)[1]) lg=r;
                if (lg!=i) { swapH(i,lg); i=lg; } else break;
            }
        }
        private void swapH(int a,int b){ int[]t=heap.get(a); heap.set(a,heap.get(b)); heap.set(b,t); }
        boolean isEmpty() { return heap.isEmpty(); }
    }

    static void demoPriorityQueue(Library library) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║         MODULE 6 — PRIORITY QUEUES (BINARY HEAPS)            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        ArrayList<Book> books = library.booksCopy();
        for (Book b : books) b.setPriority(b.getId() % 5 + 1);

        System.out.println("\n▶ Build Min-Heap (priority = return urgency) O(n):");
        MinHeap mh = MinHeap.buildHeap(books);
        mh.print();

        System.out.println("   findMin() → " + mh.findMin().getTitle()
                + " (priority=" + mh.findMin().getPriority() + ")");

        Book urgent = new Book(99,"URGENT RESERVE","Admin","Technology",2024);
        urgent.setPriority(0);
        mh.insert(urgent);
        System.out.println("\n▶ After inserting URGENT (priority=0):");
        System.out.println("   findMin() → " + mh.findMin().getTitle());

        System.out.println("\n▶ Processing borrow queue (priority order, top 5):");
        MinHeap ph = MinHeap.buildHeap(books);
        for (int i=0; i<5 && !ph.isEmpty(); i++) {
            Book b = ph.deleteMin();
            System.out.println("   Served: [p="+b.getPriority()+"] "+b.getTitle());
        }

        System.out.println("\n▶ Heap Sort (by priority ascending):");
        MinHeap sh = MinHeap.buildHeap(books);
        for (Book b : sh.heapSort())
            System.out.println("   p="+b.getPriority()+" → "+b.getTitle());

        System.out.println("\n▶ Max-Heap: Top 3 Most Borrowed Books:");
        MaxHeap popular = new MaxHeap();
        popular.insert(1,42); popular.insert(3,78); popular.insert(5,61);
        popular.insert(7,95); popular.insert(2,33);
        for (int i=0; i<3; i++) {
            int[] top = popular.extractMax();
            System.out.println("   #"+(i+1)+" bookId="+top[0]+" borrowed "+top[1]+" times");
        }

        System.out.println("\n   Heap Complexity:");
        System.out.println("   insert/deleteMin: O(log n)  |  findMin: O(1)  |  buildHeap: O(n)");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INTERACTIVE MENU HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    static void printMainMenu() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║       LIBRARY RESOURCE MANAGEMENT SYSTEM — JAVA              ║");
        System.out.println("║  Searching · Sorting · Lists · Stacks/Queues · Hash · Heap   ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. Module 1  — Searching (Linear & Binary)                  ║");
        System.out.println("║  2. Module 2  — Sorting (Bubble/Insert/Select/Merge/Quick)   ║");
        System.out.println("║  3. Module 3A — Linked Lists (Singly/Doubly/Circular)        ║");
        System.out.println("║  4. Module 3B — Polynomial ADT                               ║");
        System.out.println("║  5. Module 4  — Stacks & Queues                              ║");
        System.out.println("║  6. Module 5  — Hashing                                      ║");
        System.out.println("║  7. Module 6  — Priority Queues (Min/Max Heap)               ║");
        System.out.println("║  8. Run ALL Modules                                          ║");
        System.out.println("║  9. Show Library Stats & Book List                           ║");
        System.out.println("║  10. Add a Book                                              ║");
        System.out.println("║  11. Borrow a Book                                           ║");
        System.out.println("║  12. Return a Book                                           ║");
        System.out.println("║  0. Exit                                                     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.print("  Enter choice: ");
    }

    static void printSearchMenu() {
        System.out.println("\n  ┌─ Searching ──────────────────────────────────────┐");
        System.out.println("  │  1. Linear Search by title (partial match)        │");
        System.out.println("  │  2. Binary Search by title (exact match)          │");
        System.out.println("  │  3. Run full demo (default examples)              │");
        System.out.println("  │  0. Back                                          │");
        System.out.println("  └───────────────────────────────────────────────────┘");
        System.out.print("  Choice: ");
    }

    static void printSortMenu() {
        System.out.println("\n  ┌─ Sorting ────────────────────────────────────────┐");
        System.out.println("  │  1. Bubble Sort      (by title)                   │");
        System.out.println("  │  2. Insertion Sort   (by title)                   │");
        System.out.println("  │  3. Selection Sort   (by title)                   │");
        System.out.println("  │  4. Merge Sort       (by title)                   │");
        System.out.println("  │  5. Quick Sort       (by title)                   │");
        System.out.println("  │  6. Merge Sort       (by year)                    │");
        System.out.println("  │  7. Run full demo (all sorts)                     │");
        System.out.println("  │  0. Back                                          │");
        System.out.println("  └───────────────────────────────────────────────────┘");
        System.out.print("  Choice: ");
    }

    static void printStackMenu() {
        System.out.println("\n  ┌─ Stacks & Queues ────────────────────────────────┐");
        System.out.println("  │  1. Infix → Postfix  (enter your expression)      │");
        System.out.println("  │  2. Postfix Evaluator (digits only)               │");
        System.out.println("  │  3. Balanced Symbol Checker                       │");
        System.out.println("  │  4. Run full demo                                 │");
        System.out.println("  │  0. Back                                          │");
        System.out.println("  └───────────────────────────────────────────────────┘");
        System.out.print("  Choice: ");
    }

    static void printPolyMenu() {
        System.out.println("\n  ┌─ Polynomial ADT ─────────────────────────────────┐");
        System.out.println("  │  f(x) = 0.5x² + 2x + 5   (Late Fee)              │");
        System.out.println("  │  g(x) = 0.1x + 0.5        (Discount)             │");
        System.out.println("  │                                                    │");
        System.out.println("  │  1. Evaluate f(x) for custom x                    │");
        System.out.println("  │  2. Evaluate g(x) for custom x                    │");
        System.out.println("  │  3. Run full demo                                 │");
        System.out.println("  │  0. Back                                          │");
        System.out.println("  └───────────────────────────────────────────────────┘");
        System.out.print("  Choice: ");
    }

    static void printHeapMenu() {
        System.out.println("\n  ┌─ Priority Queue ─────────────────────────────────┐");
        System.out.println("  │  1. Build Min-Heap from library                   │");
        System.out.println("  │  2. Insert a book with custom priority            │");
        System.out.println("  │  3. Process borrow queue (deleteMin × N)          │");
        System.out.println("  │  4. Heap Sort                                     │");
        System.out.println("  │  5. Run full demo                                 │");
        System.out.println("  │  0. Back                                          │");
        System.out.println("  └───────────────────────────────────────────────────┘");
        System.out.print("  Choice: ");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MAIN — INTERACTIVE
    // ══════════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ── Seed library ──
        Library library = new Library();
        library.addBook(new Book(1,  "Clean Code",              "Robert Martin",   "Technology",  2008));
        library.addBook(new Book(2,  "Dune",                    "Frank Herbert",   "Fiction",     1965));
        library.addBook(new Book(3,  "Sapiens",                 "Yuval Harari",    "History",     2011));
        library.addBook(new Book(4,  "The Pragmatic Programmer","Andrew Hunt",     "Technology",  1999));
        library.addBook(new Book(5,  "1984",                    "George Orwell",   "Fiction",     1949));
        library.addBook(new Book(6,  "A Brief History of Time", "Stephen Hawking", "Science",     1988));
        library.addBook(new Book(7,  "Thinking Fast and Slow",  "Daniel Kahneman", "Philosophy",  2011));
        library.addBook(new Book(8,  "Steve Jobs",              "Walter Isaacson", "Biography",   2011));
        library.addBook(new Book(9,  "The Selfish Gene",        "Richard Dawkins", "Science",     1976));
        library.addBook(new Book(10, "Meditations",             "Marcus Aurelius", "Philosophy",  180 ));

        library.borrowBook(2, "Alice", "MEM-001", "alice@lib.com", "2026-01-01", "2026-01-15");
        library.borrowBook(5, "Bob",   "MEM-002", "bob@lib.com",   "2026-01-05", "2026-01-19");

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║       LIBRARY RESOURCE MANAGEMENT SYSTEM — JAVA              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        library.printStats();

        boolean running = true;
        while (running) {
            printMainMenu();
            String input = sc.nextLine().trim();

            switch (input) {

                // ── MODULE 1: SEARCHING ──
                case "1": {
                    boolean back = false;
                    while (!back) {
                        printSearchMenu();
                        String ch = sc.nextLine().trim();
                        switch (ch) {
                            case "1":
                                System.out.print("  Enter title to search (partial ok): ");
                                String q1 = sc.nextLine().trim();
                                System.out.println("\n▶ Linear Search for '" + q1 + "':");
                                Book r1 = linearSearchByTitle(library.getBooks(), q1);
                                if (r1 != null) System.out.println("   Result: " + r1);
                                break;
                            case "2":
                                ArrayList<Book> sorted = library.booksCopy();
                                Collections.sort(sorted);
                                System.out.print("  Enter EXACT title: ");
                                String q2 = sc.nextLine().trim();
                                System.out.println("\n▶ Binary Search for '" + q2 + "':");
                                Book r2 = binarySearchByTitle(sorted, q2);
                                if (r2 != null) System.out.println("   Result: " + r2);
                                break;
                            case "3":
                                demoSearching(library);
                                break;
                            case "0":
                                back = true;
                                break;
                            default:
                                System.out.println("  Invalid choice.");
                        }
                    }
                    break;
                }

                // ── MODULE 2: SORTING ──
                case "2": {
                    boolean back = false;
                    while (!back) {
                        printSortMenu();
                        String ch = sc.nextLine().trim();
                        ArrayList<Book> books = library.booksCopy();
                        switch (ch) {
                            case "1":
                                System.out.println("\n▶ Bubble Sort (by title A→Z)  [O(n²)]:");
                                printTitles(bubbleSort(books)); break;
                            case "2":
                                System.out.println("\n▶ Insertion Sort (by title A→Z)  [O(n²)]:");
                                printTitles(insertionSort(books)); break;
                            case "3":
                                System.out.println("\n▶ Selection Sort (by title A→Z)  [O(n²)]:");
                                printTitles(selectionSort(books)); break;
                            case "4":
                                System.out.println("\n▶ Merge Sort (by title A→Z)  [O(n log n)]:");
                                printTitles(mergeSort(books)); break;
                            case "5":
                                System.out.println("\n▶ Quick Sort (by title A→Z)  [O(n log n) avg]:");
                                printTitles(quickSort(books)); break;
                            case "6":
                                System.out.println("\n▶ Merge Sort by Year (oldest → newest):");
                                printTitles(sortByYear(books)); break;
                            case "7":
                                demoSorting(library); break;
                            case "0": back = true; break;
                            default: System.out.println("  Invalid choice.");
                        }
                    }
                    break;
                }

                // ── MODULE 3A: LINKED LISTS ──
                case "3":
                    demoLinkedList();
                    break;

                // ── MODULE 3B: POLYNOMIAL ──
                case "4": {
                    boolean back = false;
                    Polynomial lateFee = new Polynomial("LateFee");
                    lateFee.addTerm(0.5, 2); lateFee.addTerm(2.0, 1); lateFee.addTerm(5.0, 0);
                    Polynomial discount = new Polynomial("Discount");
                    discount.addTerm(0.1, 1); discount.addTerm(0.5, 0);

                    while (!back) {
                        printPolyMenu();
                        String ch = sc.nextLine().trim();
                        switch (ch) {
                            case "1":
                                System.out.print("  Enter x (days overdue): ");
                                try {
                                    double x = Double.parseDouble(sc.nextLine().trim());
                                    System.out.println("   f(" + x + ") = ₹" + lateFee.evaluate(x));
                                } catch (NumberFormatException e) { System.out.println("  Invalid number."); }
                                break;
                            case "2":
                                System.out.print("  Enter x (member years): ");
                                try {
                                    double x = Double.parseDouble(sc.nextLine().trim());
                                    System.out.println("   g(" + x + ") = ₹" + discount.evaluate(x));
                                } catch (NumberFormatException e) { System.out.println("  Invalid number."); }
                                break;
                            case "3":
                                demoPolynomial(); break;
                            case "0": back = true; break;
                            default: System.out.println("  Invalid choice.");
                        }
                    }
                    break;
                }

                // ── MODULE 4: STACKS & QUEUES ──
                case "5": {
                    boolean back = false;
                    while (!back) {
                        printStackMenu();
                        String ch = sc.nextLine().trim();
                        switch (ch) {
                            case "1":
                                System.out.print("  Enter infix expression (e.g. a+b*c): ");
                                String inf = sc.nextLine().trim();
                                System.out.println("   Postfix: " + infixToPostfix(inf));
                                break;
                            case "2":
                                System.out.print("  Enter postfix expression (digits only, e.g. 33*2*5+): ");
                                String pf = sc.nextLine().trim();
                                try {
                                    System.out.println("   Result: " + evaluatePostfix(pf));
                                } catch (Exception e) { System.out.println("  Error: " + e.getMessage()); }
                                break;
                            case "3":
                                System.out.print("  Enter expression to check (e.g. {[()]}): ");
                                String expr = sc.nextLine().trim();
                                System.out.println("   " + expr + "  →  " + (isBalanced(expr) ? "✅ Balanced" : "❌ Unbalanced"));
                                break;
                            case "4":
                                demoStackQueue(); break;
                            case "0": back = true; break;
                            default: System.out.println("  Invalid choice.");
                        }
                    }
                    break;
                }

                // ── MODULE 5: HASHING ──
                case "6":
                    demoHashing(library);
                    break;

                // ── MODULE 6: HEAP ──
                case "7": {
                    boolean back = false;
                    while (!back) {
                        printHeapMenu();
                        String ch = sc.nextLine().trim();
                        ArrayList<Book> books = library.booksCopy();
                        for (Book b : books) b.setPriority(b.getId() % 5 + 1);

                        switch (ch) {
                            case "1":
                                System.out.println("\n▶ Build Min-Heap O(n):");
                                MinHeap h1 = MinHeap.buildHeap(books);
                                h1.print();
                                System.out.println("   findMin() → " + h1.findMin().getTitle()
                                        + " (priority=" + h1.findMin().getPriority() + ")");
                                break;
                            case "2":
                                System.out.print("  Enter book title: ");
                                String bt = sc.nextLine().trim();
                                System.out.print("  Enter priority (0 = most urgent): ");
                                try {
                                    int prio = Integer.parseInt(sc.nextLine().trim());
                                    MinHeap h2 = MinHeap.buildHeap(books);
                                    Book nb = new Book(99, bt, "User", "Custom", 2024);
                                    nb.setPriority(prio);
                                    h2.insert(nb);
                                    System.out.println("\n▶ After inserting '" + bt + "' (priority=" + prio + "):");
                                    h2.print();
                                    System.out.println("   findMin() → " + h2.findMin().getTitle());
                                } catch (NumberFormatException e) { System.out.println("  Invalid priority."); }
                                break;
                            case "3":
                                System.out.print("  How many to serve (N): ");
                                try {
                                    int n = Integer.parseInt(sc.nextLine().trim());
                                    MinHeap h3 = MinHeap.buildHeap(books);
                                    System.out.println("\n▶ Processing top " + n + " from queue:");
                                    for (int i = 0; i < n && !h3.isEmpty(); i++) {
                                        Book b = h3.deleteMin();
                                        System.out.println("   Served: [p=" + b.getPriority() + "] " + b.getTitle());
                                    }
                                } catch (NumberFormatException e) { System.out.println("  Invalid number."); }
                                break;
                            case "4":
                                System.out.println("\n▶ Heap Sort (by priority ascending):");
                                MinHeap h4 = MinHeap.buildHeap(books);
                                for (Book b : h4.heapSort())
                                    System.out.println("   p=" + b.getPriority() + " → " + b.getTitle());
                                break;
                            case "5":
                                demoPriorityQueue(library); break;
                            case "0": back = true; break;
                            default: System.out.println("  Invalid choice.");
                        }
                    }
                    break;
                }

                // ── RUN ALL ──
                case "8":
                    demoSearching(library);
                    demoSorting(library);
                    demoLinkedList();
                    demoPolynomial();
                    demoStackQueue();
                    demoHashing(library);
                    demoPriorityQueue(library);
                    System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
                    System.out.println("║               ALL MODULES COMPLETED SUCCESSFULLY             ║");
                    System.out.println("╚══════════════════════════════════════════════════════════════╝");
                    break;

                // ── STATS & BOOK LIST ──
                case "9":
                    library.printStats();
                    System.out.println("\n  Books in library:");
                    for (Book b : library.getBooks()) System.out.println("  " + b);
                    System.out.println("\n  Borrow Records:");
                    if (library.getRecords().isEmpty()) System.out.println("  (none)");
                    for (BorrowRecord r : library.getRecords()) System.out.println("  " + r);
                    break;

                // ── ADD BOOK ──
                case "10": {
                    System.out.print("  Title  : "); String title  = sc.nextLine().trim();
                    System.out.print("  Author : "); String author = sc.nextLine().trim();
                    System.out.print("  Genre  : "); String genre  = sc.nextLine().trim();
                    System.out.print("  Year   : ");
                    try {
                        int year = Integer.parseInt(sc.nextLine().trim());
                        library.addBook(title, author, genre, year);
                        System.out.println("  ✅ Book '" + title + "' added successfully.");
                    } catch (NumberFormatException e) { System.out.println("  Invalid year."); }
                    break;
                }

                // ── BORROW BOOK ──
                case "11": {
                    System.out.println("  Available books:");
                    for (Book b : library.getBooks())
                        if (b.getStatus().equals("Available"))
                            System.out.println("    [" + b.getId() + "] " + b.getTitle());
                    System.out.print("  Book ID to borrow: ");
                    try {
                        int bid = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("  Member name  : "); String mn  = sc.nextLine().trim();
                        System.out.print("  Member ID    : "); String mid = sc.nextLine().trim();
                        System.out.print("  Email        : "); String em  = sc.nextLine().trim();
                        System.out.print("  Borrow date  (YYYY-MM-DD): "); String bd = sc.nextLine().trim();
                        System.out.print("  Return date  (YYYY-MM-DD): "); String rd = sc.nextLine().trim();
                        BorrowRecord rec = library.borrowBook(bid, mn, mid, em, bd, rd);
                        if (rec != null) System.out.println("  ✅ Borrowed! " + rec);
                        else System.out.println("  ❌ Book not available or not found.");
                    } catch (NumberFormatException e) { System.out.println("  Invalid ID."); }
                    break;
                }

                // ── RETURN BOOK ──
                case "12": {
                    System.out.println("  Borrowed books:");
                    for (Book b : library.getBooks())
                        if (b.getStatus().equals("Borrowed"))
                            System.out.println("    [" + b.getId() + "] " + b.getTitle());
                    System.out.print("  Book ID to return: ");
                    try {
                        int bid = Integer.parseInt(sc.nextLine().trim());
                        if (library.returnBook(bid)) System.out.println("  ✅ Book returned successfully.");
                        else System.out.println("  ❌ Book not found.");
                    } catch (NumberFormatException e) { System.out.println("  Invalid ID."); }
                    break;
                }

                case "0":
                    running = false;
                    System.out.println("\n  Goodbye! 👋");
                    break;

                default:
                    System.out.println("  ⚠ Invalid choice. Try again.");
            }
        }
        sc.close();
    }
}