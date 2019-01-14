import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.*;

/**
 * Extension of JavaFX TextField
 */
class AutoCompleteTextField extends TextField {

    private ContextMenu contextMenu;

    /**
     * Class constructor
     *
     * @param generalMap prefix tree
     */
    AutoCompleteTextField(SortedMap<String, String> generalMap) {

        super();
        this.contextMenu = new ContextMenu();

        textProperty().addListener((observable, oldValue, newValue) -> {

            String prefix = getText();

            if (prefix == null || prefix.isEmpty()) {
                // empty text
                contextMenu.hide();
            } else {

                // format text to uppercase
                prefix = prefix.toUpperCase();

                SortedMap<String, String> filteredMap = filterPrefix(generalMap, prefix);

                if (!filteredMap.isEmpty()) {
                    // matched result is not empty
                    populatePopUp(filteredMap);

                    if (!contextMenu.isShowing()) {
                        // display context menu
                        contextMenu.show(this, Side.BOTTOM, 0, 0);

                    }

                } else {
                    // noting matched
                    contextMenu.hide();

                }

            }
        });

        // hide the context menu when losing focus
        focusedProperty().addListener((observableValue, oldValue, newValue) -> contextMenu.hide());
    }

    /**
     * Bind context menu with matched result
     *
     * @param resultMap result
     */
    private void populatePopUp(SortedMap<String, String> resultMap) {

        // max item number
        int maxEntries = Math.min(resultMap.size(), 10);

        List<CustomMenuItem> menuItems = new LinkedList<>();

        for (Map.Entry<String, String> entry : resultMap.entrySet()) {

            String result = entry.getValue();
            CustomMenuItem item = new CustomMenuItem(new Label(result), true);

            item.setOnAction(actionEvent -> {
                // when the item is selected
                // set text of text filed to the text of item
                // hide the context menu
                setText(result);
                contextMenu.hide();

            });
            menuItems.add(item);

            if (menuItems.size() >= maxEntries) {

                break;

            }

        }

        // clear last binding
        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(menuItems);

    }

    /**
     * filter prefix tree by text
     *
     * @param prefixTree prefix tree
     * @param prefix     text
     * @return matched result
     */
    private static SortedMap<String, String> filterPrefix(SortedMap<String, String> prefixTree, String prefix) {

        // get last letter of text's next letter
        char nextLetter = (char) (prefix.charAt(prefix.length() - 1) + 1);
        String endPoint = prefix.substring(0, prefix.length() - 1) + nextLetter;

        // e.g. fromKey: TEXT, toKey: TEXU
        return prefixTree.subMap(prefix, endPoint);

    }

}
