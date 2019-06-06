package com.runescape.cache.fs;

import java.util.Collections;
import java.util.List;

public final class IndexEntry {
	
	public static final IndexEntry EMPTY_ENTRY = new IndexEntry(0, 0, Collections.emptyList());
	
	private final int length;
	
	private final int initialChunkPosition;
	
	private final List<DataChunk> data;
	
	public IndexEntry(int length, int initialChunkPosition, List<DataChunk> data) {
		this.length = length;
		this.initialChunkPosition = initialChunkPosition;
		this.data = data;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getInitialChunkPosition() {
		return initialChunkPosition;
	}
	
	public List<DataChunk> getData() {
		return data;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof IndexEntry)) {
			return false;
		}
		
		IndexEntry other = (IndexEntry) object;
		
		return length == other.length && initialChunkPosition == other.initialChunkPosition && data.equals(other.data);
	}
	
}