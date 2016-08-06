package com.runescape.cache.archive;

import java.util.Arrays;

/**
 * Represents a file/entry stored in an {@link Archive} container.
 * 
 * @author Sam
 */
public final class ArchiveEntry {
	
	/**
	 * The unique identifier of this entry.
	 */
	private final int identifier;
	
	/**
	 * The entry contents may be either compressed or decompressed.
	 */
	private final byte[] buffer;
	
	/**
	 * Constructs a new {@link ArchiveEntry} with the given parameters.
	 * 
	 * @param identifier the unique identifier of the entry.
	 * @param compressedSize the compressed size of the entry.
	 * @param size the decompressed size of the entry.
	 * @param buffer the contents of the entry.
	 */
	public ArchiveEntry(int identifier, byte[] buffer) {
		this.identifier = identifier;
		this.buffer = buffer;
	}
	
	/**
	 * Gets the unique identifier.
	 * 
	 * @return the unique identifier for this entry.
	 */
	public int getIdentifier() {
		return identifier;
	}
	
	/**
	 * Gets the contents of this entry.
	 * 
	 * @return the contents.
	 */
	public byte[] getBuffer() {
		return buffer;
	}
	
	@Override
	public String toString() {
		return String.valueOf(identifier);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ArchiveEntry)) {
			return false;
		}
		ArchiveEntry other = (ArchiveEntry) obj;
		
		boolean bufferEquality = Arrays.equals(buffer, other.buffer);
		return bufferEquality && identifier == other.identifier;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + identifier;
		result = 31 * result + Arrays.hashCode(buffer);
		return result;
	}
	
}