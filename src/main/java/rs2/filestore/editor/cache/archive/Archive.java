package rs2.filestore.editor.cache.archive;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This class represents a JAG Archive. The JAG Archive is a container for
 * different types of files. Either the archive as a whole is compressed or each
 * individual entry/file is compressed (using a header-less implementation of
 * BZip2 compression).
 * 
 * @author Sam
 */
public final class Archive {
	
	/**
	 * A {@link LinkedHashSet} of {@link ArchiveEntry}s that make up this archive.
	 */
	private final LinkedHashSet<ArchiveEntry> entries;

	/**
	 * Denotes whether the archive is compress (as a whole).
	 */
	private final boolean archiveCompressed;

	/**
	 * The size of the archive decompressed.
	 */
	private final int size;

	/**
	 * The size of the archive when compressed with JaGex's header-less BZip2
	 * implementation.
	 */
	private final int compressedSize;
	
	/**
	 * Whether or not the archive has changed since being decoded.
	 */
	private boolean changed;
	
	public Archive(LinkedHashSet<ArchiveEntry> entries, boolean archiveCompressed, int size, int compressedSize) {
		this.entries = entries;
		this.archiveCompressed = archiveCompressed;
		this.size = size;
		this.compressedSize = compressedSize;
	}
	
	/**
	 * Gets the contained instances of {@link ArchiveEntry}.
	 * @return {@link Set} of archive entries.
	 */
	public Set<ArchiveEntry> getEntries() {
		return entries;
	}
	
	/**
	 * Gets the compression state of this archive.
	 * @return <code>true</code> if the archive is compressed whole.
	 */
	public boolean isCompressed() {
		return archiveCompressed;
	}
	
	/**
	 * Gets the size of this archive.
	 * @return the size of this archive (not compressed).
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Gets the size of this archive when compressed.
	 * @return the size of this archive (compressed).
	 */
	public int getCompressedSize() {
		return compressedSize;
	}
	
	/**
	 * Whether or not this {@link Archive} has been changed since decoding and
	 * therefore would require serialisation and persistence to save changes.
	 * 
	 * @return <code>true</code> if the archive has changed since initial decode.
	 */
	public boolean hasChanged() {
		return changed;
	}

	/**
	 * This function gets the unique identifier for a given archive name.
	 * 
	 * @param name the archive name to get the identifier for.
	 * @return a unique identifier for the given archive name.
	 */
	private static int getIdentifier(String name) {
		int id = 0;
		for (char c : name.toCharArray()) {
			id *= 61;
			id += Character.toUpperCase(c) - 32;
		}
		return id;
	}

	/**
	 * Adds an entry to the archive. If the archive file, as a whole, is not
	 * compressed we must compress each individual entry (using JaGex's
	 * header-less BZip2 implementation).
	 * 
	 * @param name the archive's name.
	 * @param contents the contents of the archive.
	 */
	public void addEntry(String name, byte[] contents) {
		int identifier = getIdentifier(name);

		changed = entries.add(new ArchiveEntry(identifier, contents));
	}
	
	/**
	 * Gets an {@link ArchiveEntry} for the given identifier.
	 * 
	 * @param identifier the value by which the entry is identified.
	 * @return the {@link ArchiveEntry} identified by the given identifier.
	 * @throws EntryNotFoundException if there is no entry for the given identifier.
	 */
	public ArchiveEntry getEntry(int identifier) throws EntryNotFoundException {
		Optional<ArchiveEntry> e = entries.stream().filter(entry -> entry.getIdentifier() == identifier).findAny();
		
		return e.orElseThrow(() -> new EntryNotFoundException("No Archive found for " + identifier));
	}
	
	/**
	 * Gets an {@link ArchiveEntry} for the given name.
	 * 
	 * @param name the name associated with the desired {@link ArchiveEntry}.
	 * @return an entry from the archive wrapped in a {@link ArchiveEntry}.
	 * @throws EntryNotFoundException if the entry is not found.
	 */
	public ArchiveEntry getEntry(String name) throws EntryNotFoundException {
		int identifier = getIdentifier(name);

		return getEntry(identifier);
	}
	
	/**
	 * Removes an entry from the archive and returns the previously associated
	 * {@link ArchiveEntry} if one existed.
	 * 
	 * @param name the name of the archive.
	 * @throws EntryNotFoundException if no {@link ArchiveEntry} exists for given name.
	 */
	public void removeEntry(String name) throws EntryNotFoundException {
		ArchiveEntry entry = getEntry(name);
		
		changed = entries.remove(entry);
	}

}