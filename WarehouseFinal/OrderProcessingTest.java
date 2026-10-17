public class OrderProcessingTest {
    public static void main(String[] args) {
        Warehouse warehouse = Warehouse.instance();
        Client client = warehouse.addClient("Test Client", "Test Address");
        Product product = warehouse.addProduct("Free Product", 2, 0.0);

        warehouse.addToWishlist(client.getId(), product.getId(), 1);

        if (!warehouse.processOrder(client.getId())) {
            throw new AssertionError("Order should succeed");
        }

        java.util.Iterator<Invoice> invoices =
                warehouse.getClientInvoices(client.getId());

        if (!invoices.hasNext()) {
            throw new AssertionError("Free product should create an invoice");
        }

        if (invoices.next().getTotal() != 0.0 || invoices.hasNext()) {
            throw new AssertionError("Expected exactly one $0 invoice");
        }

        if (product.getQuantity() != 1 || client.getBalance() != 0.0) {
            throw new AssertionError("Incorrect stock or balance");
        }

        if (warehouse.getClientWishlist(client.getId()).hasNext()) {
            throw new AssertionError("Processed wishlist should be empty");
        }

        if (warehouse.processOrder(client.getId())) {
            throw new AssertionError("Empty wishlist should not process again");
        }

        System.out.println("PASS: Free product creates one invoice, "
                + "updates stock, and clears the wishlist");
    }
}