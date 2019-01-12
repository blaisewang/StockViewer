import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.*;


class AutoCompleteTextField extends TextField {

    private ContextMenu contextMenu;

    AutoCompleteTextField(SortedMap<String, String> generalMap) {

        super();
        this.contextMenu = new ContextMenu();

        textProperty().addListener((observable, oldValue, newValue) -> {

            String prefix = getText();

            if (prefix == null || prefix.isEmpty()) {
                contextMenu.hide();
            } else {

                prefix = prefix.toUpperCase();
                SortedMap<String, String> filteredMap = filterPrefix(generalMap, prefix);

                if (!filteredMap.isEmpty()) {

                    populatePopUp(filteredMap);

                    if (!contextMenu.isShowing()) {

                        contextMenu.show(this, Side.BOTTOM, 0, 0);

                    }

                } else {

                    contextMenu.hide();

                }

            }
        });

        focusedProperty().addListener((observableValue, oldValue, newValue) -> contextMenu.hide());
    }

    private void populatePopUp(SortedMap<String, String> resultMap) {

        int maxEntries = Math.min(resultMap.size(), 10);
        List<CustomMenuItem> menuItems = new LinkedList<>();

        for (Map.Entry<String, String> entry : resultMap.entrySet()) {

            String result = entry.getValue();
            CustomMenuItem item = new CustomMenuItem(new Label(result), true);

            item.setOnAction(actionEvent -> {

                setText(result);
                contextMenu.hide();

            });
            menuItems.add(item);

            if (menuItems.size() >= maxEntries) {

                break;

            }

        }

        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(menuItems);

    }

    private static SortedMap<String, String> filterPrefix(SortedMap<String, String> generalMap, String prefix) {

        char nextLetter = (char) (prefix.charAt(prefix.length() - 1) + 1);
        String endPoint = prefix.substring(0, prefix.length() - 1) + nextLetter;
        return generalMap.subMap(prefix, endPoint);

    }

}
