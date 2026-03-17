package other;

public class Slot {
    private String slotID;
    private Product product;
    private int quantity;
    
    public Slot(String slotID, Product product, int quantity) {
        setSlotID(slotID);
        setProduct(product);
        setQuantity(quantity);
    }
    
    public String getSlotID() {
        return slotID;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void addQuantity(int amount) {
        int next = getQuantity() + amount;
        if (next >= 0) {
            setQuantity(next);
        }
    }
    
    public void setSlotID(String slotID) {
        this.slotID = slotID;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public void setQuantity(int quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        }
    }
    
    @Override
    public String toString() {
        return "Slot{id='" + slotID + "', product=" + product + ", qty=" + quantity + "}";
    }
}
