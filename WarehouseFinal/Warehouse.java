import java.io.Serializable;
import java.util.Iterator;

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
}
