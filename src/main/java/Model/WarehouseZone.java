package Model;

public class WarehouseZone {
    private Long ID;
    private String zoneName;
    private Cell[][] cells;
    private boolean isActive;
    private boolean isProductZone;
    private Double maxWeight;

    public WarehouseZone(long ID, String zoneName, int length, int width, boolean isProductZone, double maxWeight) {
        this.ID = ID;
        this.zoneName = zoneName;
        this.cells = new Cell[length][width];
        this.isActive = true;
        this.isProductZone = isProductZone;
        this.maxWeight = maxWeight;

        // Создание клеток и установка их имен
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                String cellName = ID + " - " + (i + 1) + " || " + (j + 1);
                cells[i][j] = new Cell(-1, cellName, this, maxWeight);
            }
        }
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public Long getID() {
        return ID;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        for (int i = 0; i < this.cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j].setActive(active);
            }
        }
    }

    public boolean isProductZone() {
        return isProductZone;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setProductZone(boolean productZone) {
        isProductZone = productZone;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public void setID(long ID) {
        this.ID = ID;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                String cellName = ID + " - " + (i + 1) + " || " + (j + 1);
                cells[i][j].setName(cellName);
            }
        }
    }

    @Override
    public String toString() {
        return zoneName;
    }
}
