package factorysim.model;

import factorysim.config.PortConfig;

public class OutputPort implements OutputSource {

    private final String itemType;
    private final int capacity;
    private int quantity;
    private final String beltName;

    public OutputPort(PortConfig portConfig) {
        this.itemType = portConfig.getItemName();
        this.capacity = portConfig.getAmount();
        this.beltName = portConfig.getBeltName();
        this.quantity = 0;
    }

    @Override
    public boolean canPull() {
        return quantity > 0;
    }

    @Override
    public void pullItem() {
        if (canPull()) {
            quantity--;
        }
    }

    @Override
    public String itemType() {
        return itemType;
    }

    public void addItems(int amount) {
        quantity += amount;
    }

    public boolean isEmpty() {
        return quantity == 0;
    }

    public boolean isFull() {
        return quantity == capacity;
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
}