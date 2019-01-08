import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.*;

class AutoCompleteTextField extends TextField {

    private ContextMenu entriesPopup;

    AutoCompleteTextField(SortedMap<String, String> candidateList) {
        super();
        this.entriesPopup = new ContextMenu();

        textProperty().addListener((observable, oldValue, newValue) -> {
            String enteredText = getText();

            if (enteredText == null || enteredText.isEmpty()) {
                entriesPopup.hide();
            } else {
                String prefix = newValue.toUpperCase();
                SortedMap<String, String> filtered = filterPrefix(candidateList, prefix);

                if (!filtered.isEmpty()) {
                    populatePopUp(filtered);

                    if (!entriesPopup.isShowing()) {
                        entriesPopup.show(this, Side.BOTTOM, 0, 0);
                    }

                } else {
                    entriesPopup.hide();
                }

            }
        });

        focusedProperty().addListener((observableValue, oldValue, newValue) -> entriesPopup.hide());
    }

    private void populatePopUp(SortedMap<String, String> results) {
        int maxEntries = Math.min(results.size(), 10);
        List<CustomMenuItem> menuItems = new LinkedList<>();

        for (Map.Entry<String, String> entry : results.entrySet()) {
            final String result = entry.getValue();
            CustomMenuItem item = new CustomMenuItem(new Label(result), true);
            item.setOnAction(actionEvent -> {
                setText(result);
                entriesPopup.hide();
            });
            menuItems.add(item);

            if (menuItems.size() >= maxEntries) {
                break;
            }
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }

    private static SortedMap<String, String> filterPrefix(SortedMap<String, String> baseMap, String prefix) {
        char nextLetter = (char) (prefix.charAt(prefix.length() - 1) + 1);
        String end = prefix.substring(0, prefix.length() - 1) + nextLetter;
        return baseMap.subMap(prefix, end);
    }

}
