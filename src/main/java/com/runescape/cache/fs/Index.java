package com.runescape.cache.fs;

import com.runescape.io.ReadOnlyBuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an index within a file store. An index contains a set of items each of which has a pointer to a position
 * in the data file and the size of the file stored. Instances of this class contain the data, of a file, corresponding
 * to an entry in the index that an instance represents.
 */
public class Index implements Iterable<byte[]> {
	
	/**
	 * The size of an index entry in bytes.
	 */
	private static final int INDEX_ENTRY_SIZE = 6;
	
	/**
	 * List of entries that reside in this index.
	 */
	private final List<byte[]> entries;
	
	public Index(List<byte[]> entries) {
		this.entries = entries;
	}
	
	/**
	 * Gets a particular entry in this index.
	 *
	 * @param index the index of the entry.
	 * @return
	 */
	public byte[] getEntry(int index) {
		return entries.get(index);
	}
	
	/**
	 * @return the number of entries in this index.
	 */
	public int size() {
		return entries.size();
	}
	
	@Override
	public Iterator<byte[]> iterator() {
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
		final List<byte[]> entries = new ArrayList<>();
		
		for (int fileId = 0; indexBuffer.hasRemainingBytes(INDEX_ENTRY_SIZE); fileId++) {
			final int fileSize = indexBuffer.getUnsigned24BitInt();
			final int initialChunkId = indexBuffer.getUnsigned24BitInt();
			
			if (initialChunkId <= 0 || initialChunkId > dataBuffer.length() / DataChunk.DATA_CHUNK_BODY_SIZE) {
				continue;
			}
			
			byte[] entry = new byte[fileSize];
			
			for (int chunkId = 0, currentChunkIndex = initialChunkId; chunkId < (fileSize / DataChunk.DATA_CHUNK_BODY_SIZE) + 1; chunkId++) {
				dataBuffer.seek(currentChunkIndex * DataChunk.DATA_CHUNK_SIZE);
				
				DataChunk dataChunk = DataChunk.decode(dataBuffer, fileSize, fileId, chunkId);
				
				System.arraycopy(dataChunk.getData(), 0, entry, chunkId * DataChunk.DATA_CHUNK_BODY_SIZE, dataChunk.getData().length);
				
				final int nextChunkId = dataChunk.getNextChunkId();
				if (nextChunkId == 0) {
					break;
				}
				
				currentChunkIndex = nextChunkId;
			}
			
			entries.add(entry);
		}
		
		return new Index(entries);
	}
}