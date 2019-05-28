package com.runescape.cache.fs;

import com.runescape.io.ReadOnlyBuffer;

/**
 * Represents a chunk of data from a file stored in a local file store.
 */
public final class DataChunk {
	
	/**
	 * Size of an chunk's header, in bytes.
	 */
	public static final int DATA_CHUNK_HEADER_SIZE = 8;
	
	/**
	 * Size of an chunk's body, in bytes.
	 */
	public static final int DATA_CHUNK_BODY_SIZE = 512;
	
	/**
	 * Size of an chunk, in bytes.
	 */
	public static final int DATA_CHUNK_SIZE = DATA_CHUNK_HEADER_SIZE + DATA_CHUNK_BODY_SIZE;
	
	/**
	 * The id of the file this chunk belongs to.
	 */
	private final int fileId;
	
	/**
	 * The id of this chunk.
	 */
	private final int chunkId;
	
	/**
	 * The id of the next chunk.
	 */
	private final int nextChunkId;
	
	/**
	 * The data type of this file.
	 */
	private final int dataType;
	
	/**
	 * The data of this chunk in bytes.
	 */
	private final byte[] data;
	
	private DataChunk(int fileId, int chunkId, int nextChunkId, int dataType, byte[] data) {
		this.fileId = fileId;
		this.chunkId = chunkId;
		this.nextChunkId = nextChunkId;
		this.dataType = dataType;
		this.data = data;
	}
	
	/**
	 * Checks if the fileId and chunkId match the given ids.
	 *
	 * @param fileId the file id to check with the file id of this chunk.
	 * @param chunkId the chunk id to check with the id of this chunk.
	 * @return {@code true} if ids match.
	 */
	public boolean verify(int fileId, int chunkId) {
		return this.fileId == fileId || this.chunkId == chunkId;
	}
	
	public int getNextChunkId() {
		return nextChunkId;
	}
	
	public byte[] getData() {
		return data;
	}
	
	/**
	 * Parses a {@link DataChunk} from the given {@link ReadOnlyBuffer}.
	 *
	 * @param dataBuffer the data buffer to parse from.
	 * @param fileSize the size of the file the chunk belongs to, in bytes.
	 * @return a {@link DataChunk} instance.
	 */
	static DataChunk parse(ReadOnlyBuffer dataBuffer, int fileSize) {
		int fileId = dataBuffer.getUnsignedShort();
		int partId = dataBuffer.getUnsignedShort();
		int nextPartId = dataBuffer.getUnsigned24BitInt();
		int dataType = dataBuffer.getUnsigned();
		
		int remainder = fileSize % DATA_CHUNK_BODY_SIZE;
		int bytesToRead = (partId + 1) * DATA_CHUNK_BODY_SIZE > fileSize ? remainder : DataChunk.DATA_CHUNK_BODY_SIZE;
		
		return new DataChunk(fileId, partId, nextPartId, dataType, dataBuffer.getBytes(bytesToRead));
	}
	
}