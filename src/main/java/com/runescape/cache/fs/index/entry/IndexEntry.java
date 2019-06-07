package com.runescape.cache.fs.index.entry;

import com.runescape.cache.fs.FileStore;
import com.runescape.cache.fs.index.DataChunk;
import com.runescape.cache.fs.index.Index;

import java.util.Collections;
import java.util.List;

/**
 * Represents an entry, file, stored in an {@link Index} of some {@link FileStore}.
 */
public final class IndexEntry {
	
	/**
	 * Represents an empty entry in an index.
	 */
	public static final IndexEntry EMPTY_ENTRY = new IndexEntry(-1,-1,0, 0, Collections.emptyList());

    /**
     * The id of the index this entry belongs to.
     */
	private final int indexId;

	/**
	 * The identifier for this entry.
	 */
	private final int id;

	/**
	 * The file size of this entry in bytes.
	 */
	private final int fileSize;
	
	/**
	 *  The position, in the data file, of the first chunk for this entry.
	 */
	private final int initialChunkPosition;
	
	/**
	 * All the {@link DataChunk}s that make up the file this represents.
	 */
	private final List<DataChunk> data;
	
	public IndexEntry(int indexId, int id, int fileSize, int initialChunkPosition, List<DataChunk> data) {
	    this.indexId = indexId;
		this.id = id;
		this.fileSize = fileSize;
		this.initialChunkPosition = initialChunkPosition;
		this.data = data;
	}

	public int getIndexId() {
	    return indexId;
    }

	public int getId() {
		return id;
	}
	
	public int getFileSize() {
		return fileSize;
	}
	
	public int getInitialChunkPosition() {
		return initialChunkPosition;
	}

	public boolean isEmpty() {
		return id == -1;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof IndexEntry)) {
			return false;
		}
		
		IndexEntry other = (IndexEntry) object;
		
		return fileSize == other.fileSize && initialChunkPosition == other.initialChunkPosition && data.equals(other.data);
	}

	@Override
	public String toString() {
		return indexId + " ["+ id + ": (" + fileSize + "), first chunk pos: " + initialChunkPosition + "]";
	}

}