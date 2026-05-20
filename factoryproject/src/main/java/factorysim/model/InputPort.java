package factorysim.model;

import factorysim.config.PortConfig;

public class InputPort{

    private final String itemType;
    private final int capacity;
    private int quantity;
    private final String beltName;

    public InputPort(PortConfig portConfig) {
        this.itemType = portConfig.getItemName();
        this.capacity = portConfig.getAmount();
        this.beltName = portConfig.getBeltName();
        this.quantity = 0;
    }

    public boolean canPush() {
        return quantity > 0;
    }

    public void push() {
        quantity--;
    }

    public boolean canReceive() {
        return quantity < capacity; 
    }

    public void receiveItem() {
        if (canReceive()) {
            quantity++;
        }
    }

    public String getItemName() {
        return itemType;
    }

    public boolean hasEnoughItems() {
        return quantity >= capacity;
    }


    public int getQuantity() {
        return quantity;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getBeltName() {
        return beltName;
    }

    public boolean isFull() {
        return quantity == capacity;
    }
}