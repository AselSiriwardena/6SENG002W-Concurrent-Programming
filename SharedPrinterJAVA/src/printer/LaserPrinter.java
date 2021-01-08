package printer;
import utils.Enums;

public class LaserPrinter implements ServicePrinter {

    private int currentPaperLevel = MAX_SHEET_STACK;
    private int currentTonerLevel = MAX_TONER_LEVEL;
    private int printedDocumentsCount = 0;
    private final String PRINTER_ID;
    private ThreadGroup students;

    public LaserPrinter(String PRINTER_ID, ThreadGroup students) {
        this.PRINTER_ID = PRINTER_ID;
        this.students = students;
    }

    @Override
    public synchronized void printDocument(Document document) {

        utils.LoggerProcess.logger("Before printing", Enums.SystemClass.PRINTING_SYSTEM, Enums.MessageStatus.DEFAULT);

        while (currentPaperLevel < document.getNumberOfPages() || currentTonerLevel < document.getNumberOfPages()) {
            try {
                utils.LoggerProcess.logger("Waiting to print" + document.getName()
                        + " (student: " + document.getStudentName() + ")", Enums.SystemClass.STUDENT, Enums.MessageStatus.DEFAULT);
                wait(); // Insufficient resources. Wait till it's refilled.
            } catch (InterruptedException ex) {
                System.out.println(ex.toString());
            }
        }

        // check if the printer has the sufficient resources to print the document
        if (currentPaperLevel > document.getNumberOfPages() && currentTonerLevel > document.getNumberOfPages()) {
            currentPaperLevel -= document.getNumberOfPages();
            currentTonerLevel -= document.getNumberOfPages();
            printedDocumentsCount += 1;
            utils.LoggerProcess.logger("Student" + document.getStudentName() + " printed the document: "
                    + document.getName() + " (pages: " + document.getNumberOfPages() + ")", Enums.SystemClass.STUDENT, Enums.MessageStatus.SUCCESS);
        }

        utils.LoggerProcess.logger("After printing", Enums.SystemClass.PRINTER, Enums.MessageStatus.DEFAULT);

        // notify to all other
        notifyAll();

    }

    @Override
    public synchronized void refillPaper(int attempt, String technicianName) {
        while (currentPaperLevel + SHEETS_PER_PACK > MAX_SHEET_STACK) {
            try {
                if (hasActiveStudents()) {
                    utils.LoggerProcess.logger("Refill paper, Waiting (attempt = " + attempt + ")",
                            Enums.SystemClass.PAPER_TECHNICIAN, Enums.MessageStatus.DEFAULT);
                    wait(5000);
                } else { // No student waiting. Can stop concurrent loop
                    utils.LoggerProcess.logger("Refill paper, No active students, operation aborted (attempt = " + attempt + ")",
                            Enums.SystemClass.PAPER_TECHNICIAN, Enums.MessageStatus.FAILED);
                    return;
                }
            } catch (InterruptedException exception) {
                System.out.println(exception);
            }
        }

        if (currentPaperLevel + SHEETS_PER_PACK <= MAX_SHEET_STACK) {
            int newPaperLevel = currentPaperLevel += SHEETS_PER_PACK;
            utils.LoggerProcess.logger("Refill paper, Complete, new level: " + newPaperLevel
                    + ", technician: " + technicianName + " (attempt = " + attempt + ")", Enums.SystemClass.PAPER_TECHNICIAN,
                    Enums.MessageStatus.SUCCESS);
        }

        notifyAll();
    }

    @Override
    public synchronized void replaceTonerCartridge(int attempt, String technicianName) {

        while (currentTonerLevel > MIN_TONER_LEVEL) {
            try {
                if (hasActiveStudents()) {
                    utils.LoggerProcess.logger("Refill toner, Waiting (attempt = " + attempt + ")",
                            Enums.SystemClass.TONER_TECHNICIAN, Enums.MessageStatus.DEFAULT);
                    wait(5000);
                } else { // No student waiting. Can stop concurrent loop
                    utils.LoggerProcess.logger("Refill toner, No active students, operation aborted (attempt = " + attempt + ")",
                            Enums.SystemClass.TONER_TECHNICIAN, Enums.MessageStatus.FAILED);
                    return;
                }

            } catch (InterruptedException exception) {
                System.out.println(exception);
            }
        }

        if (currentTonerLevel < MIN_TONER_LEVEL) {
            currentTonerLevel = MAX_TONER_LEVEL;
            utils.LoggerProcess.logger("Refill toner, Complete, new level: " + currentTonerLevel
                    + ", technician: " + technicianName + " (attempt = " + attempt + ")",
                    Enums.SystemClass.TONER_TECHNICIAN, Enums.MessageStatus.SUCCESS);
        }
        notifyAll();

    }

    private boolean hasActiveStudents() {
        return students.activeCount() > 0;
    }


    @Override
    public String toString() {
        return "[ "
                + "PrinterID: " + PRINTER_ID
                + ", Paper Level: " + currentPaperLevel
                + ", Toner Level: " + currentTonerLevel
                + ", Documents Printed: " + printedDocumentsCount
                + " ]";
    }

}
