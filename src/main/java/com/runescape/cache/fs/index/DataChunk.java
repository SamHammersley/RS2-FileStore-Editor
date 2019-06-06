package com.runescape.cache.fs.index;

import com.runescape.io.ReadOnlyBuffer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

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
	
	public int getNextChunkId() {
		return nextChunkId;
	}
	
	public byte[] getData() {
		return data;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof DataChunk)) {
			return false;
		}
		
		DataChunk other = (DataChunk) object;
		
		return fileId == other.fileId
				&& chunkId == other.chunkId
				&& nextChunkId == other.nextChunkId
				&& dataType == other.dataType
				&& Arrays.equals(data, other.data);
	}

	/**
	 * Encodes this {@link DataChunk} in a byte array.
	 *
	 * @return a byte array representation of this data chunk
	 * @throws IOException
	 */
	public byte[] encode() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(DATA_CHUNK_HEADER_SIZE + data.length);
		DataOutputStream dos = new DataOutputStream(baos);

		dos.writeShort(fileId);
		dos.writeShort(chunkId);

		dos.writeShort(nextChunkId >> 8);
		dos.writeByte(nextChunkId & 0xFF);

		dos.writeByte(dataType);

		dos.write(data, 0, data.length);
		
		return baos.toByteArray();
	}

	/**
	 * Decodes a {@link DataChunk} from the given {@link ReadOnlyBuffer}.
	 *
	 * @param dataBuffer the data buffer to decode from.
	 * @param fileSize the size of the file the chunk belongs to, in bytes.
	 * @param expectedFileId if this doesn't match with decoded fileId exception is thrown.
	 * @param expectedChunkId if this doesn't match with decoded chunkId exception is thrown.
	 * @return a {@link DataChunk} instance.
	 */
	static DataChunk decode(ReadOnlyBuffer dataBuffer, int fileSize, int expectedFileId, int expectedChunkId) {
		int actualFileId = dataBuffer.getUnsignedShort();
		int actualChunkId = dataBuffer.getUnsignedShort();
		int nextChunkId = dataBuffer.getUnsigned24BitInt();
		int dataType = dataBuffer.getUnsigned();

		if (nextChunkId < 0 || nextChunkId > dataBuffer.length() / DataChunk.DATA_CHUNK_BODY_SIZE) {
			throw new RuntimeException("Invalid Index format! Invalid nextChunkId");
		}

		if (expectedFileId != actualFileId) {
			throw new RuntimeException("Invalid Index format! Incorrect expectedFileId");
		}

		if (expectedChunkId != actualChunkId) {
			throw new RuntimeException("Invalid Index format! Incorrect expectedChunkId");
		}

		int remainder = fileSize % DATA_CHUNK_BODY_SIZE;
		int bytesToRead = (actualChunkId + 1) * DATA_CHUNK_BODY_SIZE > fileSize ? remainder : DataChunk.DATA_CHUNK_BODY_SIZE;

		return new DataChunk(actualFileId, actualChunkId, nextChunkId, dataType, dataBuffer.getBytes(bytesToRead));
	}

}