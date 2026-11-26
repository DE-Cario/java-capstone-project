public class Book extends LibraryItem implements Borrowable {
    private final String author;

    public Book(String id, String title, String author) {
        super(id, title);
        this.author = author;
    }

    public String getAuthor() { return author; }
    @Override public int fineRate() { return 2; }
    @Override public String typeName() { return "BOOK"; }
}
