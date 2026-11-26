public class Student extends Member {
    public Student(String name) { 
        super( name); 
    
    }
    @Override 
    public int maxItems() { 
        return 3; 
    }

    @Override 
    public int loanPeriod() { 
        return 14; 
    }
}
