package Model;

public class StorageItem {
    long ID;
    private TypeOfStorageItem type;
    private Cell locationOnStorage;
    private Supplier supplier;
    private Customer customer;

    public StorageItem(long ID, TypeOfStorageItem type, Cell locationOnStorage) {
        this.ID = ID;
        this.type = type;
        this.locationOnStorage = locationOnStorage;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public TypeOfStorageItem getType() {
        return type;
    }

    public void setType(TypeOfStorageItem type) {
        this.type = type;
    }

    public Cell getLocationOnStorage() {
        return locationOnStorage;
    }

    public void setLocationOnStorage(Cell locationOnStorage) {
        this.locationOnStorage = locationOnStorage;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
