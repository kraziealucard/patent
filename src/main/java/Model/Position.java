package Model;

import java.util.ArrayList;

public class Position implements Cloneable {
    long ID;
    private String name;
    private ArrayList<Permission> permissions;
    private boolean isActive;

    public Position(long ID, String name, ArrayList<Permission> permissions) {
        this.ID = ID;
        this.name = name;
        this.permissions = permissions;
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public boolean hasPermissions(Permission permission) {
        for (int i = 0; i < this.permissions.size(); i++) {
            if (this.permissions.get(i) == permission) return true;
        }
        return false;
    }

    public void removePermissions(Permission permission) {
        permissions.remove(permission);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<Permission> permissions) {
        this.permissions = permissions;
    }

    public long getID() {
        return ID;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Position clone() {
        try {
            Position cloned = (Position) super.clone();
            cloned.permissions = new ArrayList<>(this.permissions);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
