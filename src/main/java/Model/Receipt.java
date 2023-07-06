package Model;

import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Receipt {
    private long ID;
    private final LocalDate date;
    private final User performer;
    private final String invoiceNumberField;
    private ArrayList<ListOfReceipt> lists;
    private boolean isProduct;

    public Receipt(long ID, LocalDate date, User performer, String invoiceNumberField, boolean isProduct) {
        this.lists = new ArrayList<>();
        this.ID = ID;
        this.date = date;
        this.performer = performer;
        this.invoiceNumberField = invoiceNumberField;
        this.isProduct = isProduct;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public LocalDate getDate() {
        return date;
    }

    public User getPerformer() {
        return performer;
    }

    public String getInvoiceNumberField() {
        return invoiceNumberField;
    }

    public ArrayList<ListOfReceipt> getLists() {
        return lists;
    }

    public void setLists(ArrayList<ListOfReceipt> lists) {
        this.lists = lists;
    }

    public boolean isProduct() {
        return isProduct;
    }

    public void setProduct(boolean product) {
        isProduct = product;
    }

    public static class ListOfReceipt {
        long ID;
        StorageItem item;
        Integer amount;
        Cell cell;
        Receipt receipt;

        public ListOfReceipt(long ID, Receipt receipt, StorageItem item, Integer amount, Cell cell) {
            this.ID = ID;
            this.item = item;
            this.amount = amount;
            this.cell = cell;
            this.receipt = receipt;
        }

        public long getID() {
            return ID;
        }

        public void setID(long ID) {
            this.ID = ID;
        }

        public StorageItem getItem() {
            return item;
        }

        public void setItem(StorageItem item) {
            this.item = item;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public Cell getCell() {
            return cell;
        }

        public void setCell(Cell cell) {
            this.cell = cell;
        }

        public Receipt getReceipt() {
            return receipt;
        }

        public void setReceipt(Receipt receipt) {
            this.receipt = receipt;
        }
    }
}
