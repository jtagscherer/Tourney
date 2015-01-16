package usspg31.tourney.controller.dialogs.modal;

import javafx.scene.control.Label;

public class SimpleDialog<P, R> extends Label implements IModalDialogProvider<P, R> {

	public SimpleDialog(String content) {
		super(content);
	}

	@Override
	public void initModalDialog(ModalDialog<P, R> modalDialog) {

	}

}
