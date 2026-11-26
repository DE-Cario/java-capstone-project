public abstract class Member {
    private final String name;

    protected Member( String name) {
        this.name = name;
    }

    public String getName() { 
        return name; 
    }

    public abstract int maxItems();
    public abstract int loanPeriod();
}
