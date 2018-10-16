package Controller.UIAction.WindowAction;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Creates the option menu, and then uses choices, so that it can be used from elsewhere.
 */
public class Option extends HBox {
    private Label optionText;
    private ComboBox<String> choiceBox;
    private String choice;

    Option(String text, String[] choices, ChangeListener<String> cl){
        this.optionText = new Label(text);
        this.choiceBox = new ComboBox<>();

        for (String choice : choices) {
            choiceBox.getItems().add(choice);
        }
        getChildren().addAll(optionText, choiceBox);
        setAlignment(Pos.CENTER_RIGHT);
        setSpacing(5);
        choiceBox.getSelectionModel().selectedItemProperty().addListener(cl);
        choiceBox.getSelectionModel().selectedItemProperty().addListener(e->
                choice = choiceBox.getSelectionModel().getSelectedItem()
        );
        if(choiceBox.getItems().size() > 0) choiceBox.getSelectionModel().select(0);
    }

    /**
     * Uses StringBuilder to make make the choiceBox and get the items.
     * @return The StringBuilder is returned.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Option: ").append(optionText.getText()).append(" | Choices: ");
        for (int i = 0; i < choiceBox.getItems().size(); i++) {
            if(i != 0) sb.append(", ").append(choiceBox.getItems().get(i));
            else sb.append(choiceBox.getItems().get(i));
        }
        return sb.toString();
    }
}
