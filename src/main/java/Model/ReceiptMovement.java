package Model;

import java.time.LocalDate;
import java.util.ArrayList;

public class ReceiptMovement extends Receipt{

    ArrayList<ListOfReceiptMovement> list;
    public ReceiptMovement(long ID, LocalDate date, User performer, String invoiceNumberField,boolean isProduct) {
        super(ID, date, performer, invoiceNumberField,isProduct);
        list=new ArrayList<>();
    }

    public ArrayList<ListOfReceiptMovement> getListMovement() {
        return list;
    }

    public void setListMovement(ArrayList<ListOfReceiptMovement> list) {
        this.list = list;
    }

    public static class ListOfReceiptMovement extends ListOfReceipt {
        private Cell where;
        public ListOfReceiptMovement(long ID, Receipt receipt, StorageItem item, Integer amount, Cell from, Cell where) {
            super(ID, receipt, item, amount, from);
            this.where=where;
        }

        public Cell getFrom(){
            return this.cell;
        }

        public Cell getWhere(){
            return where;
        }

        public void setWhere(Cell where) {
            this.where = where;
        }

        public void setFrom(Cell from) {
            this.setCell(from);
        }
    }
}
