public abstract class Member {
    private final String name;
    private final String id;

    protected Member(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() { 
        return name; 
    }
    
    public String getId(){
        return id;
    }

    public abstract int maxItems();
    public abstract int loanPeriod();
}
