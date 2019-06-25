package rs2.filestore.editor.cache.archive;

import com.google.common.base.Objects;
import rs2.filestore.editor.io.ReadOnlyBuffer;

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
	private final ReadOnlyBuffer buffer;
	
	/**
	 * Constructs a new {@link ArchiveEntry} with the given parameters.
	 * 
	 * @param identifier the unique identifier of the entry.
	 * @param buffer the contents of the entry.
	 */
	public ArchiveEntry(int identifier, byte[] buffer) {
		this.identifier = identifier;
		this.buffer = ReadOnlyBuffer.wrap(buffer);
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
	public ReadOnlyBuffer getBuffer() {
		return buffer;
	}

	public byte[] getBytes() {
		return buffer.getBytes();
	}

	@Override
	public String toString() {
		return String.valueOf(identifier);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(buffer, identifier);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ArchiveEntry)) {
			return false;
		}
		ArchiveEntry other = (ArchiveEntry) obj;

		boolean bufferEquality = buffer.equals(other.buffer);
		return bufferEquality && identifier == other.identifier;
	}

}