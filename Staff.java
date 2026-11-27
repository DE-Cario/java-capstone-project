public class Staff extends Member {
    public Staff(String id,String name) { 
        super(id,name); 
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
