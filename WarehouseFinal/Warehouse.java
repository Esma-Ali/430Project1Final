import java.io.Serializable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class Warehouse implements Serializable {
    private static final long serialVersionUID = 1L;

    private static Warehouse instance;
    private ClientList clientList;
    private ProductList productList;

    private Warehouse() {
        clientList = new ClientList();
        productList = new ProductList();
    }

    public static Warehouse instance() {
        if (instance == null) {
            instance = new Warehouse();
        }

        return instance;
    }

    public Client addClient(String name, String address) {
        Client client = new Client(name, address);

        if (clientList.insertClient(client)) {
            return client;
        }

        return null;
    }

    public Iterator<Client> getClients() {
        return clientList.getClients();
    }

    public Product addProduct(String name, int quantity, double price) {
        Product product = new Product(name, quantity, price);

        if (productList.insertProduct(product)) {
            return product;
        }

        return null;
    }

    public Iterator<Product> getProducts() {
        return productList.getProducts();
    }

    public Client getClient(String clientID) {
        return clientList.findClient(clientID);
    }

    public Product getProduct(String productID) {
        return productList.findProduct(productID);
    }

    public boolean addToWishlist(
            String clientID, String productID, int quantity) {

        Client client = getClient(clientID);
        Product product = getProduct(productID);

        if (client == null || product == null || quantity <= 0) {
            return false;
        }

        return client.addToWishlist(product, quantity);
    }

    public Iterator<WishlistItem> getClientWishlist(String clientID) {
        Client client = getClient(clientID);

        if (client == null) {
            return null;
        }

        return client.getWishlist();
    }
        public List<WishlistItem> getClientWishlistSnapshot(String clientID) {
        Iterator<WishlistItem> iterator = getClientWishlist(clientID);

        if (iterator == null) {
            return null;
        }

        List<WishlistItem> snapshot = new ArrayList<>();

        while (iterator.hasNext()) {
            WishlistItem item = iterator.next();
            snapshot.add(new WishlistItem(
                    item.getProduct(),
                    item.getQuantity()));
        }

        return snapshot;
    }
        public boolean processOrder(String clientID) {
        Client client = getClient(clientID);
        List<WishlistItem> items =
                getClientWishlistSnapshot(clientID);

        if (client == null || items == null || items.isEmpty()) {
            return false;
        }

        Invoice invoice = new Invoice(clientID);

        for (WishlistItem item : items) {
            Product product = item.getProduct();
            int requested = item.getQuantity();

            int supplied = product.supply(requested);
            int shortage = requested - supplied;

            if (supplied > 0) {
                invoice.addItem(
                        product, supplied, product.getPrice());
            }

            if (shortage > 0) {
                product.addToWaitlist(client, shortage);
            }

            client.removeFromWishlist(product.getId());
        }

        if (invoice.getTotal() > 0) {
            client.debit(invoice.getTotal());
            client.addInvoice(invoice);
        }

        return true;
    }
            public boolean receiveShipment(String productID, int received) {
        Product product = getProduct(productID);

        if (product == null || received <= 0) {
            return false;
        }

        int remaining = received;
        Iterator<WaitlistItem> iterator = product.getWaitlist();

        while (iterator.hasNext() && remaining > 0) {
            WaitlistItem item = iterator.next();
            Client client = item.getClient();

            int supplied = Math.min(
                    remaining, item.getQuantity());

            Invoice invoice = new Invoice(client.getId());
            invoice.addItem(
                    product, supplied, product.getPrice());

            client.debit(invoice.getTotal());
            client.addInvoice(invoice);

            remaining -= supplied;
            int stillWaiting = item.getQuantity() - supplied;

            if (stillWaiting == 0) {
                iterator.remove();
            } else {
                item.setQuantity(stillWaiting);
            }
        }

        if (remaining > 0) {
            product.addStock(remaining);
        }

        return true;
    }

    public Iterator<WaitlistItem> getProductWaitlist(
            String productID) {

        Product product = getProduct(productID);

        if (product == null) {
            return null;
        }

        return product.getWaitlist();
    }
}
