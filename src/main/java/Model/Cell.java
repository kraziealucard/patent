package Model;

import java.util.ArrayList;

public class Cell {
    private Long ID;
    private double currentWeight;
    private String name;
    private final ArrayList<StorageItem> stored;
    private boolean isActive;
    private final double maxWeight;
    final private WarehouseZone zone;

    public Cell(long ID, String name, WarehouseZone zone, double maxWeight) {
        this.ID = ID;
        this.name = name;
        currentWeight = 0;
        this.maxWeight = maxWeight;
        this.isActive = true;
        stored = new ArrayList<StorageItem>();
        this.zone = zone;
    }

    //Добавляет продукты в клетку, если вес продуктов не привышает максимального веса клетки
    public boolean addProductsAll(ArrayList<StorageItem> addedTypeOfStorageItems) {
        double w = currentWeight;
        for (StorageItem addedTypeOfStorageItem : addedTypeOfStorageItems) {
            w += addedTypeOfStorageItem.getType().getWeight();
        }
        if (w > maxWeight) return false;
        stored.addAll(addedTypeOfStorageItems);
        currentWeight = w;
        return true;
    }

    public boolean addProduct(StorageItem addedItem) {
        double w = currentWeight;
        w += addedItem.getType().getWeight();
        if (w > maxWeight) return false;
        stored.add(addedItem);
        currentWeight = w;
        return true;
    }

    public void removeItem(StorageItem item) {
        stored.remove(item);
        currentWeight -= item.getType().getWeight();
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getID() {
        return ID;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public WarehouseZone getZone() {
        return zone;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public ArrayList<StorageItem> getStored() {
        return stored;
    }

    @Override
    public String toString() {
        return name;
    }
}
