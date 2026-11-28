import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandProcessor {
    private final Library library = new Library();

    public void run(Scanner input) {
        while (input.hasNextLine()) {
            String line = input.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            try {
                System.out.println(handle(line));
            } catch (LibraryException exception) {
                System.out.println("ERROR " + exception.getMessage());
            }
        }
    }

    public String handle(String line) throws LibraryException {
        String[] fields = line.split("\\|", -1);
        for (int index = 0; index < fields.length; index++) {
            fields[index] = fields[index].trim();
        }

        switch (fields[0]) {
            case "ADD_BOOK":
                library.addItem(new Book(fields[1], fields[2], fields[3]));
                return "OK";
            case "ADD_MAGAZINE":
                library.addItem(new Magazine(fields[1], fields[2], fields[3]));
                return "OK";
            case "ADD_REFERENCE":
                library.addItem(new ReferenceBook(fields[1], fields[2], fields[3]));
                return "OK";
            case "ADD_MEMBER":
                library.addMember(makeMember(fields[1], fields[2], fields[3]));
                return "OK";
            case "BORROW":
                return "OK due on day " + library.borrow(fields[1], fields[2]);
            case "RETURN": {
                Library.ReturnResult result = library.returnItem(fields[1]);
                if (result.issued == null) {
                    return "OK fine " + result.fine;
                }
                return "OK fine " + result.fine + ", issued to " + result.issued.getMember().getId()
                        + " due on day " + result.issued.getDueDay();
            }
            case "ADVANCE":
                library.advance(Integer.parseInt(fields[1]));
                return "OK today is day " + library.getToday();
            case "RESERVE":
                library.reserve(fields[1], fields[2]);
                return "OK reserved";
            case "SEARCH": {
                List<String> rows = new ArrayList<>();
                for (LibraryItem item : library.search(fields[1])) {
                    rows.add(item.getId() + "|" + item.typeName() + "|" + item.getTitle());
                }
                return join(rows);
            }
            case "LOANS": {
                List<String> rows = new ArrayList<>();
                for (Loan loan : library.listLoans(fields[1])) {
                    rows.add(loan.getItem().getId() + "|" + loan.getItem().getTitle() + "|" + loan.getDueDay());
                }
                return join(rows);
            }
            case "OVERDUE": {
                int today = library.getToday();
                List<String> rows = new ArrayList<>();
                for (Loan loan : library.listOverdue()) {
                    rows.add(loan.getItem().getId() + "|" + loan.getMember().getId() + "|"
                            + loan.daysLate(today) + "|" + loan.fine(today));
                }
                return join(rows);
            }
            case "INVENTORY": {
                List<String> rows = new ArrayList<>();
                for (LibraryItem item : library.inventory()) {
                    rows.add(item.getId() + "|" + item.typeName() + "|" + library.statusOf(item));
                }
                return join(rows);
            }
            default:
                return "ERROR unknown command " + fields[0];
        }
    }

    private Member makeMember(String id, String name, String type) {
        switch (type) {
            case "STUDENT":
                return new Student(id, name);
            case "STAFF":
                return new Staff(id, name);
            default:
                return new Faculty(id, name);
        }
    }

    private String join(List<String> rows) {
        return rows.isEmpty() ? "NONE" : String.join("\n", rows);
    }
}
