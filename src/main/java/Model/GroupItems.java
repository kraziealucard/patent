package Model;

public class GroupItems {
    long ID;
    String name;
    GroupItems parent;
    boolean isProduct;
    boolean isActive;

    public GroupItems(long ID, String name, boolean isProduct) {
        this.ID = ID;
        this.name = name;
        this.isProduct = isProduct;
        this.isActive = true;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupItems getParent() {
        return parent;
    }

    public void setParent(GroupItems parent) {
        this.parent = parent;
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

    @Override
    public String toString() {
        return name;
    }
}
