import java.io.Serializable;
import java.util.Iterator;

public class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int idCounter = 1;

    private String id;
    private String name;
    private String address;
    private double balance;
    private Wishlist wishlist;

    public Client(String name, String address) {
        this.id = "C" + idCounter++;
        this.name = name;
        this.address = address;
        this.balance = 0.0;
        this.wishlist = new Wishlist();
    }

    public String getId() {
        return id;
    }

    public boolean addToWishlist(Product product, int quantity) {
        return wishlist.addProduct(product, quantity);
    }

    public Iterator<WishlistItem> getWishlist() {
        return wishlist.getIterator();
    }

    @Override
    public String toString() {
        return "Client ID: " + id
                + ", Name: " + name
                + ", Address: " + address
                + ", Balance: $" + String.format("%.2f", balance);
    }
}
