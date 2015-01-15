package usspg31.tourney.controller.dialogs.modal;

public interface DialogResultListener<R> {
	public void onDialogClosed(DialogResult result, R returnValue);
}
