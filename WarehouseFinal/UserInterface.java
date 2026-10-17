import java.util.*;
import java.io.*;

public class UserInterface {
  private static UserInterface userInterface;
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private static final int EXIT = 0;
  private static final int ADD_CLIENT = 1;
  private static final int ADD_PRODUCTS = 2;
  private static final int SHOW_CLIENTS = 3;
  private static final int SHOW_PRODUCTS = 4;
  private static final int ADD_TO_WISHLIST = 5;
  private static final int SHOW_WISHLIST = 6;
  private static final int RECEIVE_SHIPMENT = 7;
  private static final int SHOW_WAITLIST = 8;
  private static final int SHOW_INVOICES = 9;
  private static final int HELP = 10;
  private static final int RECORD_PAYMENT = 11;
  private static final int MAX_COMMAND = RECORD_PAYMENT;

  private UserInterface() {
    warehouse = Warehouse.instance();
  }

  public static UserInterface instance() {
    if (userInterface == null) {
      return userInterface = new UserInterface();
    } else {
      return userInterface;
    }
  }

  public String getToken(String prompt) {
    do {
      try {
        System.out.println(prompt);
        String line = reader.readLine();
        if (line == null) {
          System.exit(0);
        }
        StringTokenizer tokenizer = new StringTokenizer(line, "\n\r\f");
        if (tokenizer.hasMoreTokens()) {
          return tokenizer.nextToken();
        }
      } catch (IOException ioe) {
        System.exit(0);
      }
    } while (true);
  }

  private boolean yesOrNo(String prompt) {
    String more = getToken(prompt + " (Y|y)[es] or anything else for no");
    if (more.charAt(0) != 'y' && more.charAt(0) != 'Y') {
      return false;
    }
    return true;
  }

  public int getNumber(String prompt) {
    do {
      try {
        String item = getToken(prompt);
        Integer num = Integer.valueOf(item.trim());
        return num.intValue();
      } catch (NumberFormatException nfe) {
        System.out.println("Please input a number ");
      }
    } while (true);
  }

  public double getDouble(String prompt) {
    do {
      try {
        String item = getToken(prompt);
        return Double.parseDouble(item.trim());
      } catch (NumberFormatException nfe) {
        System.out.println("Please input a valid amount (e.g. 2.50)");
      }
    } while (true);
  }

  public int getCommand() {
    do {
      try {
        int value = Integer.parseInt(getToken("Enter command: " + HELP + " for help").trim());
        if (value >= EXIT && value <= MAX_COMMAND) {
          return value;
        }
        System.out.println("Enter a number between " + EXIT + " and " + MAX_COMMAND);
      } catch (NumberFormatException nfe) {
        System.out.println("Enter a number");
      }
    } while (true);
  }

  public void help() {
    System.out.println("Enter a number between " + EXIT + " and " + MAX_COMMAND + " as explained below:");
    System.out.println(EXIT + " to Exit\n");
    System.out.println(ADD_CLIENT + " to add a client");
    System.out.println(ADD_PRODUCTS + " to add products");
    System.out.println(SHOW_CLIENTS + " to display all clients");
    System.out.println(SHOW_PRODUCTS + " to display all products");
    System.out.println(ADD_TO_WISHLIST + " to add a product to a client's wishlist");
    System.out.println(SHOW_WISHLIST + " to display a client's wishlist");
    System.out.println(RECEIVE_SHIPMENT + " to receive a shipment of a product");
    System.out.println(SHOW_WAITLIST + " to display a product's waitlist");
    System.out.println(SHOW_INVOICES + " to display a client's invoices");
    System.out.println(HELP + " for help");
    System.out.println(RECORD_PAYMENT + " to record a payment from a client");
  }

  public void addClient() {
    String name = getToken("Enter client name");
    String address = getToken("Enter address");
    Client result = warehouse.addClient(name, address);
    if (result == null) {
      System.out.println("Could not add client");
    } else {
      System.out.println("Added: " + result);
    }
  }

  public void addProducts() {
    do {
      String name = getToken("Enter product name");
      int quantity = getNumber("Enter quantity");
      double price = getDouble("Enter unit price");
      Product result = warehouse.addProduct(name, quantity, price);
      if (result != null) {
        System.out.println("Added: " + result);
      } else {
        System.out.println("Product could not be added");
      }
      if (!yesOrNo("Add more products?")) {
        break;
      }
    } while (true);
  }

  public void showClients() {
    System.out.println("---- All Clients ----");
    Iterator<Client> allClients = warehouse.getClients();
    while (allClients.hasNext()) {
      System.out.println(allClients.next());
    }
    System.out.println("---------------------");
  }

  public void showProducts() {
    System.out.println("---- All Products ----");
    Iterator<Product> allProducts = warehouse.getProducts();
    while (allProducts.hasNext()) {
      System.out.println(allProducts.next());
    }
    System.out.println("----------------------");
  }

  public void addToWishlist() {
    String clientId = getToken("Enter client ID (e.g. C1)");
    do {
      String productId = getToken("Enter product ID (e.g. P1)");
      int quantity = getNumber("Enter quantity");
      if (warehouse.addToWishlist(clientId, productId, quantity)) {
        System.out.println("Added " + quantity + " of " + productId
            + " to " + clientId + "'s wishlist");
      } else {
        System.out.println("Could not add to wishlist (check client ID, product ID and quantity)");
      }
      if (!yesOrNo("Add more products to this wishlist?")) {
        break;
      }
    } while (true);
  }

  public void showWishlist() {
    String clientId = getToken("Enter client ID (e.g. C1)");
    Iterator<WishlistItem> items = warehouse.getClientWishlist(clientId);
    if (items == null) {
      System.out.println("No client found with ID " + clientId);
      return;
    }
    System.out.println("---- Wishlist for " + clientId + " ----");
    if (!items.hasNext()) {
      System.out.println("(empty)");
    }
    while (items.hasNext()) {
      System.out.println(items.next());
    }
    System.out.println("-------------------------");
  }

  public void receiveShipment() {
    String productId;
    while (warehouse.getProduct(productId = getToken("Enter product ID (e.g. P1)").trim()) == null) {
      System.out.println("No product found with ID " + productId);
    }

    int quantity;
    while ((quantity = getNumber("Enter quantity received")) <= 0) {
      System.out.println("Quantity must be a positive whole number");
    }

    Iterator<Invoice> filled = warehouse.receiveShipment(productId, quantity);
    if (filled == null) {
      System.out.println("Could not receive shipment");
      return;
    }

    if (!filled.hasNext()) {
      System.out.println("No waitlisted orders were filled; entire shipment added to stock.");
    } else {
      System.out.println("---- Waitlisted orders filled ----");
      while (filled.hasNext()) {
        System.out.println(filled.next());
      }
    }

    System.out.println("Updated product: " + warehouse.getProduct(productId));
  }

  public void showWaitlist() {
    String productId = getToken("Enter product ID (e.g. P1)").trim();
    Iterator<WaitlistItem> items = warehouse.getProductWaitlist(productId);
    if (items == null) {
      System.out.println("No product found with ID " + productId);
      return;
    }

    System.out.println("---- Waitlist for " + productId + " ----");
    if (!items.hasNext()) {
      System.out.println("No clients are on the waitlist");
    }

    while (items.hasNext()) {
      System.out.println(items.next());
    }

    System.out.println("-------------------------");
  }

  public void showInvoices() {
    String clientId = getToken("Enter client ID (e.g. C1)").trim();
    Iterator<Invoice> invoices = warehouse.getClientInvoices(clientId);
    if (invoices == null) {
      System.out.println("No client found with ID " + clientId);
      return;
    }

    System.out.println("---- Invoices for " + clientId + " ----");
    if (!invoices.hasNext()) {
      System.out.println("No invoices were found");
    }

    while (invoices.hasNext()) {
      System.out.println(invoices.next());
    }
    
    System.out.println("-------------------------");
  }

    public void recordPayment() {
    String clientId = getToken("Enter client ID (e.g. C1)").trim();
    if (warehouse.getClient(clientId) == null) {
      System.out.println("No client found with ID " + clientId);
      return;
    }
    double amount;
    while ((amount = getDouble("Enter payment amount")) <= 0 || !Double.isFinite(amount)) {
      System.out.println("Payment must be a positive amount");
    }
    Double balance = warehouse.recordPayment(clientId, amount);
    if (balance == null) {
      System.out.println("Could not record payment");
    } else {
      System.out.println("Recorded payment of $" + String.format("%.2f", amount)
          + " for " + clientId + ". New balance: $" + String.format("%.2f", balance));
    }
  }
  public void process() {
    int command;
    help();
    while ((command = getCommand()) != EXIT) {
      switch (command) {
        case ADD_CLIENT:      addClient();
                              break;
        case ADD_PRODUCTS:    addProducts();
                              break;
        case SHOW_CLIENTS:    showClients();
                              break;
        case SHOW_PRODUCTS:   showProducts();
                              break;
        case ADD_TO_WISHLIST: addToWishlist();
                              break;
        case SHOW_WISHLIST:   showWishlist();
                              break;
        case RECEIVE_SHIPMENT: receiveShipment();
                              break;
        case SHOW_WAITLIST:   showWaitlist();
                              break;
        case SHOW_INVOICES:   showInvoices();
                              break;
        case HELP:            help();
                              break;
        case RECORD_PAYMENT:  recordPayment();
                              break;
      }
    }
  }

  public static void main(String[] args) {
    UserInterface.instance().process();
  }
}