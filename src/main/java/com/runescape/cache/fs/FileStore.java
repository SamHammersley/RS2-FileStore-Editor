package com.runescape.cache.fs;

import com.runescape.io.ReadOnlyBuffer;

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
	
	/**
	 * Gets all file store data from the files in a given directory. If the necessary files do not exist
	 * {@link NoSuchFileException} will be thrown.
	 *
	 * @param cacheDirectory the directory in which the local file store files reside.
	 * @return a {@link FileStore} instance containing data for the local file store.
	 * @throws IOException
	 */
	public static FileStore load(Path cacheDirectory) throws IOException {
		Path dataPath = cacheDirectory.resolve("main_file_cache.dat");
		ReadOnlyBuffer dataBuffer = ReadOnlyBuffer.wrap(Files.readAllBytes(dataPath));
		
		Stream<Path> indexFiles = Files.list(cacheDirectory).filter(p -> p.toString().contains("idx"));
		return new FileStore(indexFiles.map(p -> Index.parse(p, dataBuffer)).toArray(Index[]::new));
	}
	
}