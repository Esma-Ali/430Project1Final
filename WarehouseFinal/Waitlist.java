import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

public class Waitlist implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LinkedList<WaitlistItem> items =
            new LinkedList<>();

    public void add(Client client, int quantity) {
        items.addLast(new WaitlistItem(client, quantity));
    }

    public Iterator<WaitlistItem> getIterator() {
        return items.iterator();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}