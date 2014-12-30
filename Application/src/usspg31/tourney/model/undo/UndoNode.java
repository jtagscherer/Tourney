package usspg31.tourney.model.undo;

class UndoNode {
	private final UndoNode prev;
	private UndoNode next;
	private final UndoAction action;

	UndoNode(UndoNode prev, UndoAction action) {
		this.prev = prev;
		this.next = null;
		this.action = action;
	}

	boolean hasNext() {
		return this.next != null;
	}

	UndoNode getNext() {
		return this.next;
	}

	UndoNode getPrev() {
		return this.prev;
	}

	void setNext(UndoNode next) {
		this.next = next;
	}

	UndoAction getAction() {
		return this.action;
	}

	@Override
	public String toString() {
		return "UndoNode: " + (this.action != null ? this.action.toString() : "null");
	}
}