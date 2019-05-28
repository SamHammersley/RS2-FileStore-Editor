package com.runescape.cache.fs;

import com.runescape.io.ReadOnlyBuffer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
	 * Parses an index from the given path and buffer (which contains the entry data for the index).
	 *
	 * @param indexPath the {@link Path} to the index file.
	 * @param dataBuffer the buffer containing all entry data.
	 * @return an instance of {@link Index}.
	 */
	public static Index parse(Path indexPath, ReadOnlyBuffer dataBuffer) {
		final List<byte[]> entries = new ArrayList<>();
		
		try {
			ReadOnlyBuffer indexBuffer = ReadOnlyBuffer.wrap(Files.readAllBytes(indexPath));
			
			for (int fileId = 0; indexBuffer.hasRemainingBytes(INDEX_ENTRY_SIZE); fileId++) {
				final int fileSize = indexBuffer.getUnsigned24BitInt();
				final int dataBlockId = indexBuffer.getUnsigned24BitInt();
				
				if (fileSize == 0 || dataBlockId == 0) {
					continue;
				}
				
				byte[] entry = new byte[fileSize];
				
				for (int partId = 0, currentPartIndex = dataBlockId; partId < (fileSize / DataChunk.DATA_CHUNK_BODY_SIZE) + 1; partId++) {
					dataBuffer.seek(currentPartIndex * DataChunk.DATA_CHUNK_SIZE);
					
					DataChunk dataEntryPart = DataChunk.parse(dataBuffer, fileSize);
					final int nextPartIndex = dataEntryPart.getNextChunkId();
					
					if (!dataEntryPart.verify(fileId, partId) || nextPartIndex < 0
							|| nextPartIndex > dataBuffer.length() / DataChunk.DATA_CHUNK_BODY_SIZE) {
						break;
					}
					
					System.arraycopy(dataEntryPart.getData(), 0, entry, partId * DataChunk.DATA_CHUNK_BODY_SIZE, dataEntryPart.getData().length);
					
					currentPartIndex = nextPartIndex;
				}
				
				entries.add(entry);
			}
			
			return new Index(entries);
			
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse " + indexPath + " as an Index", e);
		}
	}
}