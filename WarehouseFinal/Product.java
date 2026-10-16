import java.io.*;

public class Product implements Serializable {
	private static final long serialVersionUID = 1L;
	private static int idCounter = 1;
	
	private String id;
	private String name;
	private int quantity;
	private double price;
	
	public Product(String name, int quantity, double price) {
		this.id = "P" + idCounter++;
		this.name = name;
		this.quantity = quantity;
		this.price = price;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "ID: " + id + ", Name: " + name + ", Quantity: " + quantity + ", Price: $" + price;
	}
}
