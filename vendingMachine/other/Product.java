package other;

public class Product {
    private static int productCounter = 0;
    private int id;
    private String name;
    private String category;
    private double price;
    
    public Product(String name, String category, double price) {
        this.id = ++productCounter;
        this.name = name;
        this.category = category;
        this.price = price;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public double getPrice() {
        return price;
    }
    
    public boolean setPrice(double newPrice) {
        if (newPrice >= 0.0) {
            this.price = newPrice;
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', category='" + category + "', price=" + price + "}";
    }
}
