public class Staff extends Member {
    public Staff( String name) { 
        super( name); 
    }

    @Override 
    public int maxItems() { 
        return 4; 
    }

    @Override 
    public int loanPeriod() { 
        return 21; 
    }
}
