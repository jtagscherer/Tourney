package usspg31.tourney.controller.dialogs.modal;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class SimpleDialog<P, R> implements IModalDialogProvider<P, R> {

    private final Node content;

    public SimpleDialog(String content) {
        this.content = new Label(content);
    }

    public SimpleDialog(Node content) {
        this.content = content;
    }

    @Override
    public Node getRoot() {
        return this.content;
    }

    @Override
    public void initModalDialog(ModalDialog<P, R> modalDialog) {

    }

}
