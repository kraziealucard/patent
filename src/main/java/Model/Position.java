package Model;

import java.util.ArrayList;

public class Position {
    private long ID;
    private String name;
    private ArrayList<Permission> permissions;
    private boolean isActive;

    public Position(long ID, String name, ArrayList<Permission> permissions) {
        this.ID = ID;
        this.name = name;
        this.permissions = permissions;
        this.isActive = true;
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

    public void setID(long ID) {
        this.ID = ID;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
