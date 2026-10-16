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

        Iterator<WaitlistItem> iterator = product.getWaitlist();
        WaitlistItem item = iterator.next();

        check(item.getClient() == first && item.getQuantity() == 3,
                "First request stays first");

        item.setQuantity(1);
        check(item.getQuantity() == 1,
                "Partial fulfillment updates waiting quantity");

        iterator.remove();
        item = iterator.next();

        check(item.getClient() == second && item.getQuantity() == 5,
                "Removing first request preserves second request");

        iterator.remove();
        check(!product.getWaitlist().hasNext(),
                "Completed requests leave an empty waitlist");

        product.addStock(4);
        check(product.getQuantity() == 4,
                "Adding stock updates quantity");
    }
}