package usspg31.tourney.controller.dialogs.modal;

public enum DialogButtons {
	NONE(false, false, false, false),
	OK(false, false, true, false),
	OK_CANCEL(false, false, true, true),
	YES_NO(true, true, false, false),
	YES_NO_CANCEL(true, true, false, true);

	private boolean yes;
	private boolean no;
	private boolean ok;
	private boolean cancel;

	private DialogButtons(boolean yes, boolean no, boolean ok, boolean cancel) {
		this.yes = yes;
		this.no = no;
		this.ok = ok;
		this.cancel = cancel;
	}

	public boolean containsYes() {
		return this.yes;
	}
	public boolean containsNo() {
		return this.no;
	}
	public boolean containsOk() {
		return this.ok;
	}
	public boolean containsCancel() {
		return this.cancel;
	}
}
