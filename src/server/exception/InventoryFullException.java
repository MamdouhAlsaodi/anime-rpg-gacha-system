package server.exception;

public class InventoryFullException extends GachaBaseException {
    private int capacity;

    public InventoryFullException(int capacity) {
        super("Inventory is full! Capacity: " + capacity, 1002);
        this.capacity = capacity;
    }

    @Override
    public String getErrorCategory() { return "INVENTORY"; }

    public int getCapacity() { return capacity; }
}
