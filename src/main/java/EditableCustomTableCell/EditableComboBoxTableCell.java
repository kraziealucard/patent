package EditableCustomTableCell;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;

public class EditableComboBoxTableCell<S> extends TableCell<S, String> {

    private ComboBox<String> comboBox;

    private final ItemOptionsProvider<S> optionsProvider;

    public interface ItemOptionsProvider<T> {
        ObservableList<String> getOptions(T item);
    }

    public EditableComboBoxTableCell(ItemOptionsProvider<S> optionsProvider) {
        this.optionsProvider = optionsProvider;
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (comboBox == null) {
            comboBox = new ComboBox<>();
            comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

            comboBox.setOnAction(e -> commitEdit(comboBox.getValue()));

            comboBox.setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });

            comboBox.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused && comboBox != null) {
                    commitEdit(comboBox.getValue());
                }
            });
        }

        S currentItem = getTableView().getItems().get(getIndex());
        comboBox.setItems(optionsProvider.getOptions(currentItem));
        comboBox.setValue(getItem());

        setText(null);
        setGraphic(comboBox);
        comboBox.show();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (comboBox != null) {
                    comboBox.setValue(item);
                }
                setText(null);
                setGraphic(comboBox);
            } else {
                setText(item);
                setGraphic(null);
            }
        }
    }

    @Override
    public void commitEdit(String newValue) {
        super.commitEdit(newValue);
    }
}