import java.io.Serializable;

public class WaitlistItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Client client;
    private int quantity;

    public WaitlistItem(Client client, int quantity) {
        if (client == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "A client and positive quantity are required.");
        }

        this.client = client;
        this.quantity = quantity;
    }

    public Client getClient() {
        return client;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot be negative.");
        }

        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Client ID: " + client.getId()
                + ", Waiting quantity: " + quantity;
    }
}