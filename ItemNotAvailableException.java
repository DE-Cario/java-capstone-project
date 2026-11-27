public class ItemNotAvailableException extends LibraryException {
    public ItemNotAvailableException(String message) { 
        super(message); 
    }

    public static ItemNotAvailableException onLoan(String id) {
        return new ItemNotAvailableException(id + " is on loan");
    }
    
    public static ItemNotAvailableException notBorrowable(String id) {
        return new ItemNotAvailableException(id + " cannot be borrowed");
    }
}
