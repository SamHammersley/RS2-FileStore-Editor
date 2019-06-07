package com.runescape.cache.fs;

import com.runescape.cache.fs.index.Index;
import com.runescape.cache.fs.index.IndexDecoder;
import com.runescape.io.ReadOnlyBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Represents a JaGeX local file store.
 */
public final class FileStore {

	private static final String INDEX_FILE_NAME_REGEX = "main_file_cache.idx\\d$";

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
	 * Validates the given fileStoreDirectory and then gets all file store data from the files in the directory, if valid.
	 *
	 * @param fileStoreDirectory the directory in which the local file store files reside.
	 * @return a {@link FileStore} instance containing data for the local file store.
	 * @throws IOException where the given fileStoreDirectory is not a directory.
	 */
	public static FileStore load(Path fileStoreDirectory) throws IOException {
		Path dataPath = validCachePath(fileStoreDirectory);

		ReadOnlyBuffer dataBuffer = ReadOnlyBuffer.fromPath(dataPath);
		IndexDecoder indexDecoder = new IndexDecoder(dataBuffer);

		Stream<Index> indices = Files
				.list(fileStoreDirectory)
				.sorted()
				.filter(p -> p.getFileName().toString().matches(INDEX_FILE_NAME_REGEX))
				.map(p -> decodeFromPath(p, indexDecoder));
		
		return new FileStore(indices.toArray(Index[]::new));
	}

	/**
	 * Decodes {@link Index} from the given path.
	 *
	 * @param path the path to decode index from.
	 * @param indexDecoder for decoding the bytes from the given path.
	 * @return an instance of {@link Index} representing the index from the file.
	 */
	private static Index decodeFromPath(Path path, IndexDecoder indexDecoder) {
		String extension = path.getFileName().toString();
		int id = Integer.parseInt(extension.substring(extension.length() - 1));

		return indexDecoder.decode(id, ReadOnlyBuffer.fromPath(path));
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