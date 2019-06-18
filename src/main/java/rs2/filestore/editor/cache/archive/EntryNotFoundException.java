package rs2.filestore.editor.cache.archive;

import java.util.NoSuchElementException;

@SuppressWarnings("serial")
public class EntryNotFoundException extends NoSuchElementException {

	public EntryNotFoundException() {
		super();
	}
	
	public EntryNotFoundException(String message) {
		super(message);
	}
	
}