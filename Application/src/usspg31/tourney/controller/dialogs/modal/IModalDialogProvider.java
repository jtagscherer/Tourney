package usspg31.tourney.controller.dialogs.modal;

public interface IModalDialogProvider<P, R> extends DialogContent<P, R> {

	void initModalDialog(ModalDialog<P, R> modalDialog);

	public default ModalDialog<P, R> modalDialog() {
		ModalDialog<P, R> modalDialog = new ModalDialog<>(this);
		this.initModalDialog(modalDialog);
		return modalDialog;
	}

}
