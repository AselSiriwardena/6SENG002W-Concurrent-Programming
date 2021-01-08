package printer;

public class Technician extends Thread {

    protected LaserPrinter printer;
    protected final String NAME;

    public Technician(String name, ThreadGroup group, LaserPrinter printer) {
        super(group, name);
        this.printer = printer;
        NAME = name;
    }
}
