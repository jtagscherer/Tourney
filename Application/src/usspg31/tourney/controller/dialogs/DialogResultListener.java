package usspg31.tourney.controller.dialogs;

public interface DialogResultListener<R> {
	public void onDialogClosed(DialogResult result, R returnValue);
}
