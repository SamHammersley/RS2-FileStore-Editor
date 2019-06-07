package com.runescape.cache.fs.index;

import com.runescape.cache.fs.index.entry.IndexEntry;

import java.util.Iterator;
import java.util.List;

/**
 * Represents an index within a file store. An index contains a set of items each of which has a pointer to a position
 * in the data file and the size of the file stored. Instances of this class contain the data, of a file, corresponding
 * to an entry in the index that an instance represents.
 */
public class Index implements Iterable<IndexEntry> {
	
	/**
	 * List of entries that reside in this index.
	 */
	private final List<IndexEntry> entries;
	
	/**
	 * The length of this index in bytes.
	 */
	private final int length;
	
	/**
	 * Denotes whether this index has been changed.
	 */
	private boolean indexChanged = false;

	public Index(List<IndexEntry> entries, int length) {
		this.entries = entries;
		this.length = length;
	}
	
	/**
	 * Gets a particular entry in this index.
	 *
	 * @param index the index of the entry.
	 * @return
	 */
	public IndexEntry getEntry(int index) {
		return entries.get(index);
	}
	
	/**
	 * Sets the entry at the given index to the specified entry. Sets {@link #indexChanged} to true,
	 * if the specified entry is different from the previous.
	 *
	 * @param index the index to place the given entry.
	 * @param entry the entry to replace the entry at the given index.
	 */
	public void setEntry(int index, IndexEntry entry) {
		IndexEntry previous = entries.set(index, entry);
		
		if (!entry.equals(previous)) {
			indexChanged = true;
		}
	}
	
	/**
	 * Adds the specified entry to {@link #entries}.
	 *
	 * @param entry the entry to add.
	 * @return {@link List#add}
	 */
	public boolean addEntry(IndexEntry entry) {
		return entries.add(entry);
	}
	
	/**
	 * @return the number of entries in this index.
	 */
	public int size() {
		return entries.size();
	}
	
	public boolean hasChanged() {
		return indexChanged;
	}
	
	public int getLength() {
		return length;
	}
	
	@Override
	public Iterator<IndexEntry> iterator() {
		return entries.iterator();
	}

}