public class ReferenceBook extends LibraryItem {
    private final String author;

    public ReferenceBook(String id, String title, String author){
        super(id, title);
        this.author = author;
    }

    public String getAuthor(){ 
        return author; 
    }

    @Override 
    public String typeName(){ 
        return "REFERENCE"; 
    }
}
