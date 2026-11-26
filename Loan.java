public class Loan {
    private final LibraryItem item;   
    private final Member member;
    private final int borrowedDay;
    private final int due;

    public Loan(LibraryItem item, Member member, int borrowedDay) {
        this.item = item;
        this.member = member;
        this.borrowedDay = borrowedDay;
        this.due = borrowedDay + member.loanPeriod();
    }

    public LibraryItem getItem() { 
        return item; 
    }

    public Member getMember() { 
        return member; 
    }

    public int getBorrowedDay() { 
        return borrowedDay; 
    }

    public int getDueDay() { 
        return due; 
    }

    public int daysLate(int today) {
        if(today>due){
            return (today-due);
        }
        else{
            return 0;
        }
    }
    
    public int fine(int today) { 
        return (daysLate(today) * ((Borrowable) item).fine()); 
    }

    public boolean isOverdue(int today) { 
        if(today >due){
            return true;
        }
        else {
            return false;
        }
    }
}
