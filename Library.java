import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Library {
    private final Map<String, LibraryItem> items = new TreeMap<>();
    private final Map<String, Member> members = new TreeMap<>();
    private final List<Loan> loans = new ArrayList<>();
    private final Map<String, Deque<Member>> reservations = new HashMap<>();
    private int today = 0;

    public static class ReturnResult {
        public final int fine;
        public final Loan issued;

        ReturnResult(int fine, Loan issued) {
            this.fine = fine;
            this.issued = issued;
        }
    }

    public int getToday() {
        return today;
    }

    public void addItem(LibraryItem item) {
        items.put(item.getId(), item);
    }

    public void addMember(Member member) {
        members.put(member.getId(), member);
    }

    public void advance(int days) {
        today += days;
    }

    private LibraryItem findItem(String id) throws ItemNotFoundException {
        LibraryItem item = items.get(id);
        if (item == null) {
            throw new ItemNotFoundException(id);
        }
        return item;
    }

    private Member findMember(String id) throws MemberNotFoundException {
        Member member = members.get(id);
        if (member == null) {
            throw new MemberNotFoundException(id);
        }
        return member;
    }

    private Loan loanOf(String itemId) {
        for (Loan loan : loans) {
            if (loan.getItem().getId().equals(itemId)) {
                return loan;
            }
        }
        return null;
    }

    private int countLoans(Member member) {
        int count = 0;
        for (Loan loan : loans) {
            if (loan.getMember() == member) {
                count++;
            }
        }
        return count;
    }

    /** Returns the due day. */
    public int borrow(String memberId, String itemId) throws LibraryException {
        LibraryItem item = findItem(itemId);
        Member member = findMember(memberId);
        if (!(item instanceof Borrowable)) {
            throw ItemNotAvailableException.notBorrowable(itemId);
        }
        if (loanOf(itemId) != null) {
            throw ItemNotAvailableException.onLoan(itemId);
        }
        if (countLoans(member) >= member.maxItems()) {
            throw new BorrowLimitExceededException(memberId, member.maxItems());
        }
        Loan loan = new Loan(item, member, today);
        loans.add(loan);
        return loan.getDueDay();
    }

    public ReturnResult returnItem(String itemId) throws LibraryException {
        LibraryItem item = findItem(itemId);
        Loan loan = loanOf(itemId);
        if (loan == null) {
            throw new ItemNotOnLoanException(itemId);
        }
        int fine = loan.fine(today);
        loans.remove(loan);

        Deque<Member> queue = reservations.get(itemId);
        if (queue != null) {
            Iterator<Member> iterator = queue.iterator();
            while (iterator.hasNext()) {
                Member member = iterator.next();
                if (countLoans(member) < member.maxItems()) {
                    iterator.remove();
                    Loan next = new Loan(item, member, today);
                    loans.add(next);
                    return new ReturnResult(fine, next);
                }
            }
        }
        return new ReturnResult(fine, null);
    }

    public void reserve(String memberId, String itemId) throws LibraryException {
        LibraryItem item = findItem(itemId);
        Member member = findMember(memberId);
        if (!(item instanceof Borrowable)) {
            throw ItemNotAvailableException.notBorrowable(itemId);
        }
        Loan loan = loanOf(itemId);
        if (loan == null) {
            throw new ReservationNotAllowedException(itemId + " is available");
        }
        if (loan.getMember() == member) {
            throw new ReservationNotAllowedException(memberId + " already has " + itemId);
        }
        Deque<Member> queue = reservations.computeIfAbsent(itemId, key -> new ArrayDeque<>());
        if (queue.contains(member)) {
            throw new ReservationNotAllowedException(memberId + " already reserved " + itemId);
        }
        queue.addLast(member);
    }

    public List<Loan> listLoans(String memberId) throws MemberNotFoundException {
        Member member = findMember(memberId);
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.getMember() == member) {
                result.add(loan);
            }
        }
        result.sort(Comparator.comparingInt(Loan::getDueDay)
                .thenComparing(loan -> loan.getItem().getId()));
        return result;
    }

    public List<Loan> listOverdue() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.isOverdue(today)) {
                result.add(loan);
            }
        }
        result.sort(Comparator.comparingInt((Loan loan) -> loan.daysLate(today)).reversed()
                .thenComparing(loan -> loan.getItem().getId()));
        return result;
    }

    public List<LibraryItem> inventory() {
        // TreeMap keeps inventory entries sorted by item ID.
        return new ArrayList<>(items.values());
    }

    public List<LibraryItem> search(String text) {
        String needle = text.toLowerCase();
        List<LibraryItem> result = new ArrayList<>();
        for (LibraryItem item : items.values()) {
            if (item.getTitle().toLowerCase().contains(needle)) {
                result.add(item);
            }
        }
        result.sort(Comparator.comparing(LibraryItem::getTitle));
        return result;
    }

    public String statusOf(LibraryItem item) {
        if (!(item instanceof Borrowable)) {
            return "NOT_BORROWABLE";
        }
        Loan loan = loanOf(item.getId());
        return loan == null ? "AVAILABLE" : "ON_LOAN|" + loan.getMember().getId();
    }
}
