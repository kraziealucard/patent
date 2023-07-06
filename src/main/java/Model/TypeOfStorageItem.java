package Model;

public class TypeOfStorageItem {
    private long ID;
    private String name;
    private GroupItems groupItems;
    private double weight;
    private boolean isProduct;
    private boolean isActive;

    public TypeOfStorageItem(long ID, String name, double weight, boolean isProduct) {
        this.ID = ID;
        this.name = name;
        this.weight = weight;
        this.isProduct = isProduct;
        isActive = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getID() {
        return ID;
    }

    public GroupItems getGroup() {
        return groupItems;
    }

    public void setGroup(GroupItems groupItems) {
        this.groupItems = groupItems;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }


    public boolean isProduct() {
        return isProduct;
    }

    public void setProduct(boolean product) {
        isProduct = product;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return name;
    }
}
