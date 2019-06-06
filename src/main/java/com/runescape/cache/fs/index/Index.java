package com.runescape.cache.fs.index;

import com.runescape.io.ReadOnlyBuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an index within a file store. An index contains a set of items each of which has a pointer to a position
 * in the data file and the size of the file stored. Instances of this class contain the data, of a file, corresponding
 * to an entry in the index that an instance represents.
 */
public class Index implements Iterable<IndexEntry> {
	
	/**
	 * The size of an index entry in bytes.
	 */
	private static final int INDEX_ENTRY_SIZE = 6;
	
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
	 * if the specified {@link IndexEntry} is different from the previous.
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
	 * Adds the specified {@link IndexEntry} to {@link #entries}.
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
	
	/**
	 * Decodes an index from the given path and buffer (which contains the raw data for files in this index).
	 *
	 * @param indexBuffer the buffer containing index data.
	 * @param dataBuffer  the buffer containing all entry data.
	 * @return an instance of {@link Index}.
	 */
	public static Index decode(ReadOnlyBuffer indexBuffer, ReadOnlyBuffer dataBuffer) {
		final List<IndexEntry> entries = new ArrayList<>();
		
		int totalSize = 0;
		
		for (int fileId = 0; indexBuffer.hasRemainingBytes(INDEX_ENTRY_SIZE); fileId++) {
			final int fileSize = indexBuffer.getUnsigned24BitInt();
			final int initialChunkId = indexBuffer.getUnsigned24BitInt();
			
			if (initialChunkId <= 0 || initialChunkId > dataBuffer.length() / DataChunk.DATA_CHUNK_BODY_SIZE) {
				entries.add(IndexEntry.EMPTY_ENTRY);
				continue;
			}
			
			List<DataChunk> entryData = new ArrayList<>((fileSize / DataChunk.DATA_CHUNK_BODY_SIZE) + 1);
			
			for (int chunkId = 0, currentChunkIndex = initialChunkId; chunkId < (fileSize / DataChunk.DATA_CHUNK_BODY_SIZE) + 1; chunkId++) {
				dataBuffer.seek(currentChunkIndex * DataChunk.DATA_CHUNK_SIZE);
				
				DataChunk dataChunk = DataChunk.decode(dataBuffer, fileSize, fileId, chunkId);
				
				entryData.add(dataChunk);
				
				final int nextChunkId = dataChunk.getNextChunkId();
				if (nextChunkId == 0) {
					break;
				}
				
				currentChunkIndex = nextChunkId;
			}
			totalSize += fileSize;
			
			entries.add(new IndexEntry(fileSize, initialChunkId, entryData));
		}
		
		return new Index(entries, totalSize);
	}

}