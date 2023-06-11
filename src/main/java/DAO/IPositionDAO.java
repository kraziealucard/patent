package DAO;

import Model.Position;

import java.util.ArrayList;

public interface IPositionDAO {
    ArrayList<Position> getPositionList(boolean onlyActive);

    boolean updatePosition(Position position);

    long addPosition(Position position);
}
