public class Faculty extends Member {
    public Faculty(String id ,String name) { 
        super(id,name); 
    }
    @Override 
    public int maxItems() { 
        return 5; 
    }

    @Override 
    public int loanPeriod() { 
        return 30; 
    }
}
