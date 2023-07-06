package Model;

import java.util.ArrayList;
import java.util.Locale;

public class User {
    private Long ID;
    private String name;
    private Position position;
    private String login;
    private String password;
    private boolean isActive;

    public User(Long ID, String name, Position position, String login, String password) {
        this.ID = ID;
        this.name = name;
        this.login = login;
        this.password = password;
        this.position = position;
        isActive = true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getID() {
        return ID;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return name;
    }
}
