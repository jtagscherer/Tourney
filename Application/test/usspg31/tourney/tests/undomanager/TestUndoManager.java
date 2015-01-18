package usspg31.tourney.tests.undomanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.Before;
import org.junit.Test;

import usspg31.tourney.model.undo.UndoManager;

public class TestUndoManager {
    private UndoManager undoManager;

    @Before
    public void init() {
	this.undoManager = new UndoManager();
    }

    @Test
    public void testSimpleProperty() {
	StringProperty testProperty = new SimpleStringProperty();
	testProperty.set("FirstState");

	this.undoManager.registerUndoProperty(testProperty);
	this.undoManager.undo();
	this.undoManager.redo();

	assertFalse(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());

	testProperty.set("SecondState");

	assertTrue(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());

	this.undoManager.undo();

	assertEquals("FirstState", testProperty.get());

	assertFalse(this.undoManager.undoAvailable());
	assertTrue(this.undoManager.redoAvailable());

	this.undoManager.redo();

	assertEquals("SecondState", testProperty.get());

	assertTrue(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());

	assertTrue(this.undoManager.toString().length() > 0);

	testProperty.set("ThirdState");
	testProperty.set("FourthState");
	this.undoManager.undo();
	assertTrue(this.undoManager.toString().length() > 0);

	assertTrue(this.undoManager.getNodeInformation().length() > 0);

	this.undoManager.unregisterUndoProperty(testProperty);

	assertTrue(this.undoManager.undoAvailableProperty() != null);
	assertTrue(this.undoManager.redoAvailableProperty() != null);
    }

    @Test
    public void testListProperty() {
	ObservableList<String> testProperty = FXCollections
		.observableArrayList();
	testProperty.add("FirstItem");

	this.undoManager.registerUndoProperty(testProperty);

	assertFalse(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());

	testProperty.add("SecondItem");

	assertTrue(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());

	this.undoManager.undo();

	assertEquals(1, testProperty.size());

	assertFalse(this.undoManager.undoAvailable());
	assertTrue(this.undoManager.redoAvailable());

	this.undoManager.redo();

	assertEquals(2, testProperty.size());
	assertTrue(testProperty.contains("FirstItem"));
	assertTrue(testProperty.contains("SecondItem"));

	assertTrue(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());

	assertTrue(this.undoManager.getNodeInformation().length() > 0);

	this.undoManager.unregisterUndoProperty(testProperty);
    }

    @Test
    public void testSimplePropertyBatch() {
	StringProperty testProperty = new SimpleStringProperty();
	testProperty.set("FirstState");

	this.undoManager.registerUndoProperty(testProperty);
	this.undoManager.beginUndoBatch();

	assertFalse(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());

	testProperty.set("Second");
	testProperty.set("SecondState");

	this.undoManager.endUndoBatch();

	assertTrue(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());

	this.undoManager.undo();

	assertEquals("FirstState", testProperty.get());

	assertFalse(this.undoManager.undoAvailable());
	assertTrue(this.undoManager.redoAvailable());

	this.undoManager.redo();

	assertEquals("SecondState", testProperty.get());

	assertTrue(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());
    }

    @Test
    public void testListPropertyBatch() {

	ObservableList<String> testProperty = FXCollections
		.observableArrayList();
	testProperty.add("FirstItem");

	this.undoManager.registerUndoProperty(testProperty);
	this.undoManager.beginUndoBatch();

	assertFalse(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());

	testProperty.add("SecondItem");
	testProperty.add("ThirdItem");

	assertTrue(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());

	this.undoManager.undo();

	assertEquals(1, testProperty.size());

	assertFalse(this.undoManager.undoAvailable());
	assertTrue(this.undoManager.redoAvailable());

	this.undoManager.redo();

	assertEquals(3, testProperty.size());
	assertTrue(testProperty.contains("FirstItem"));
	assertTrue(testProperty.contains("SecondItem"));
	assertTrue(testProperty.contains("ThirdItem"));

	assertTrue(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());

	this.undoManager.unregisterUndoProperty(testProperty);
    }

    @Test
    public void testClearHistory() {
	StringProperty testProperty = new SimpleStringProperty();
	testProperty.set("FirstState");

	this.undoManager.registerUndoProperty(testProperty);

	assertFalse(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());

	testProperty.set("SecondState");
	this.undoManager.clearHistory();

	assertFalse(this.undoManager.undoAvailable());
	assertFalse(this.undoManager.redoAvailable());
    }
}
