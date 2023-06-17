package DAO;

import Model.Position;
import Model.User;

import java.util.ArrayList;

public interface IUserDAO {
    long addUser(User user);

    ArrayList<User> getUserList(boolean onlyActive, ArrayList<Position> positions);

    boolean updateUser(User user);

    boolean loginUserExists(String login);

    User getUserByLoginAndPassword(String login, String Password, ArrayList<Position> positions);

}
