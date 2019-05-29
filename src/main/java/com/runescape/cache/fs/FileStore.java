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
		if (!Files.isDirectory(cacheDirectory)) {
			throw new RuntimeException(cacheDirectory.toString() + ": Invalid path specified, must be a directory containing data and index files.");
		}
		
		Path dataPath = cacheDirectory.resolve("main_file_cache.dat");
		
		ReadOnlyBuffer dataBuffer = ReadOnlyBuffer.wrap(Files.readAllBytes(dataPath));
		
		Stream<ReadOnlyBuffer> indexFiles = Files
				.list(cacheDirectory)
				.filter(p -> p.toString().contains("idx"))
				.map(ReadOnlyBuffer::fromPath);
		
		return new FileStore(indexFiles.map(indexBuffer -> Index.decode(indexBuffer, dataBuffer)).toArray(Index[]::new));
	}
	
}