package com.runescape.cache.fs;

import com.runescape.io.ReadOnlyBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Represents a JaGeX local file store.
 */
public final class FileStore {
	
	/**
	 * The indices in this file store.
	 */
	private final Index[] indices;
	
	private FileStore(Index[] indices) {
		this.indices = indices;
	}
	
	/**
	 * Gets the index at the specified index from FileStore#indices.
	 *
	 * @param index
	 * @return
	 */
	public Index getIndex(int index) {
		return indices[index];
	}
	
	public Index[] getIndices() {
		return indices;
	}
	
	/**
	 * Validates the given fileStoreDirectory and then gets all file store data from the files in the directory, if valid.
	 *
	 * @param fileStoreDirectory the directory in which the local file store files reside.
	 * @return a {@link FileStore} instance containing data for the local file store.
	 * @throws IOException where the given fileStoreDirectory is not a directory.
	 */
	static FileStore load(Path fileStoreDirectory) throws IOException {
		Path dataPath = validCachePath(fileStoreDirectory);

		ReadOnlyBuffer dataBuffer = ReadOnlyBuffer.fromPath(dataPath);
		
		Stream<Index> indices = Files
				.list(fileStoreDirectory)
				.filter(p -> p.toString().contains("idx"))
				.map(p -> Index.decode(ReadOnlyBuffer.fromPath(p), dataBuffer));
		
		return new FileStore(indices.toArray(Index[]::new));
	}
	
	/**
	 * Validates the given {@link Path} fileStoreDirectory.
	 *
	 * @param fileStoreDirectory the path to the directory containing the file store.
	 * @return {@link Path} to the data file of the file store.
	 * @throws IOException
	 */
	private static Path validCachePath(Path fileStoreDirectory) throws IOException {
		if (!Files.isDirectory(fileStoreDirectory)) {
			throw new IOException(fileStoreDirectory.toString() + ": Invalid path specified, must be a directory");
		}
		
		Path dataPath = fileStoreDirectory.resolve("main_file_cache.dat");
		
		if (Files.notExists(dataPath)) {
			throw new FileNotFoundException(fileStoreDirectory.toString() + ": Invalid path specified, must contain data and index files.");
		}
		
		return dataPath;
	}
	
}