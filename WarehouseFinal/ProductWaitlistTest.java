import java.util.Iterator;

public class ProductWaitlistTest {
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        System.out.println("PASS: " + message);
    }

    public static void main(String[] args) {
        Product product = new Product("Test Product", 10, 2.0);
        Client first = new Client("First Client", "Address 1");
        Client second = new Client("Second Client", "Address 2");

        check(product.supply(7) == 7
                && product.getQuantity() == 3,
                "Supplying 7 leaves stock of 3");

        check(product.supply(6) == 3
                && product.getQuantity() == 0,
                "Short stock supplies only the available 3");

        check(product.supply(1) == 0,
                "Empty stock supplies nothing");

        product.addToWaitlist(first, 3);
        product.addToWaitlist(second, 5);

        WaitlistItem item = product.getFirstWaitlistItem();

        check(item.getClient() == first && item.getQuantity() == 3,
                "First request stays first");

        item.deductQuantity(2);
        check(item.getQuantity() == 1,
                "Partial fulfillment updates waiting quantity");

        boolean readOnly = false;
        try {
            Iterator<WaitlistItem> iterator = product.getWaitlist();
            iterator.next();
            iterator.remove();
        } catch (UnsupportedOperationException e) {
            readOnly = true;
        }
        check(readOnly, "Waitlist iterator is read-only");

        product.removeFirstWaitlistItem();
        item = product.getFirstWaitlistItem();

        check(item.getClient() == second && item.getQuantity() == 5,
                "Removing first request preserves second request");

        product.removeFirstWaitlistItem();
        check(product.getFirstWaitlistItem() == null
                && !product.getWaitlist().hasNext(),
                "Completed requests leave an empty waitlist");

        product.addStock(4);
        check(product.getQuantity() == 4,
                "Adding stock updates quantity");

        testReceiveShipment();
    }

    private static void testReceiveShipment() {
        Warehouse warehouse = Warehouse.instance();
        Product product = warehouse.addProduct("Shipped Product", 0, 2.0);
        Client a = warehouse.addClient("Client A", "Address A");
        Client b = warehouse.addClient("Client B", "Address B");
        Client c = warehouse.addClient("Client C", "Address C");
        String pid = product.getId();

        product.addToWaitlist(a, 4);
        product.addToWaitlist(b, 5);
        product.addToWaitlist(c, 2);

        check(warehouse.receiveShipment("NOPE", 5) == null
        && warehouse.receiveShipment(pid, 0) == null,
                "Invalid product or quantity is rejected");

        Iterator<Invoice> filled = warehouse.receiveShipment(pid, 6);
        int count = 0;
        while (filled.hasNext()) {
            filled.next();
            count++;
        }
        check(count == 2, "Shipment of 6 fills two waitlist entries");
        check(a.getBalance() == 8.0 && b.getBalance() == 4.0
                && c.getBalance() == 0.0,
                "Filled clients are charged; unfilled client unchanged");
        check(product.getQuantity() == 0,
                "Nothing goes to stock while the waitlist uses it all");

        WaitlistItem first = product.getFirstWaitlistItem();
        check(first.getClient() == b && first.getQuantity() == 3,
                "Partially filled client stays first with reduced quantity");

        warehouse.receiveShipment(pid, 10);
        check(product.getFirstWaitlistItem() == null
                && product.getQuantity() == 5,
                "Leftover shipment is added to stock after waitlist");
        check(b.getBalance() == 10.0 && c.getBalance() == 4.0,
                "Remaining waitlist clients are charged when filled");

        Iterator<Invoice> invoices = warehouse.getClientInvoices(b.getId());
        int bInvoices = 0;
        while (invoices.hasNext()) {
            invoices.next();
            bInvoices++;
        }
        check(bInvoices == 2, "Each fill creates an invoice for the client");

        filled = warehouse.receiveShipment(pid, 3);
        check(!filled.hasNext() && product.getQuantity() == 8,
                "Empty waitlist sends whole shipment to stock");
    }
}
