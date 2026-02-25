public class Slot {
    private String slotID;
    private Product product;
    private int quantity;
    
    public Slot(String slotID, Product product, int quantity) {
        this.slotID = slotID;
        this.product = product;
        this.quantity = quantity;
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
        int next = quantity + amount;
        if (next >= 0) {
            quantity = next;
        }
    }
    
    @Override
    public String toString() {
        return "Slot{id='" + slotID + "', product=" + product + ", qty=" + quantity + "}";
    }
}
