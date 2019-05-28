package com.runescape.io;

import java.util.Arrays;

/**
 * A simple read-only byte buffer.
 * 
 * @author Sam.
 */
public final class ReadOnlyBuffer {
	
	/**
	 * The bytes of data in this buffer.
	 */
	private byte[] bytes;

	/**
	 * The current index to read from.
	 */
	private int readIndex;
	
	/**
	 * Constructs a new {@link ReadOnlyBuffer} with the given byte array.
	 * 
	 * @param buffer
	 */
	private ReadOnlyBuffer(byte[] buffer) {
		this.bytes = buffer;
		readIndex = 0;
	}
	
	/**
	 * Checks whether this buffer has the specified number of bytes to be read (available).
	 *
	 * @param bytes the number of bytes available
	 * @return {@code true} if there are >= bytes in this buffer
	 */
	public boolean hasRemainingBytes(int bytes) {
		return readIndex <= (this.bytes.length - bytes);
	}
	
	/**
	 * Sets {@link #readIndex} to the given position.
	 *
	 * @param position the position to set {@link #readIndex} to.
	 */
	public void seek(int position) {
		if (position >= bytes.length) {
			throw new IndexOutOfBoundsException("position >= bytes.length, attempted to seek too far");
		}
		this.readIndex = position;
	}
	
	/**
	 * Gets the bytes in this buffer.
	 * 
	 * @return the bytes.
	 */
	public byte[] getBytes() {
		return Arrays.copyOf(bytes, bytes.length);
	}
	
	public ReadOnlyBuffer split(int offset) {
		return new ReadOnlyBuffer(Arrays.copyOfRange(bytes, offset, bytes.length));
	}
	
	/**
	 * Gets the number of bytes specified and progresses the read pointer.
	 *
	 * @param length the amount of bytes to get.
	 * @return a byte array of the next {@code length} bytes.
	 */
	public byte[] getBytes(int length) {
		return Arrays.copyOfRange(bytes, readIndex, readIndex += length);
	}
	
	public int[] getUnsignedShorts(int length) {
		int[] shorts = new int[length];
		for (int index = 0; index < length; index++) {
			shorts[index] = (short) ((getUnsigned() << 8) | getUnsigned());
		}
		return shorts;
	}
	
	/**
	 * Get the length of the under-lying byte buffer.
	 * @return
	 */
	public int length() {
		return bytes.length;
	}
	
	/**
	 * Gets the remaining bytes, from the {@link #readIndex} to the end of {@link #bytes}.
	 * @return the remaining bytes to be read.
	 */
	public byte[] getRemaining() {
		return Arrays.copyOfRange(bytes, readIndex = bytes.length, bytes.length);
	}
	
	/**
	 * Gets the index from where to read.
	 * 
	 * @return the index in the buffer to read from.
	 */
	public int getReadIndex() {
		return readIndex;
	}

	/**
	 * Gets an unsigned byte from {@link #bytes}.
	 * 
	 * @return an unsigned byte.
	 */
	public int getUnsigned() {
		return bytes[readIndex++] & 0xff;
	}

	/**
	 * Gets an unsigned short (16-bits) from {@link #bytes}.
	 * 
	 * @return an unsigned short.
	 */
	public int getUnsignedShort() {
		readIndex += 2;
		return ((bytes[readIndex - 2] & 0xff) << 8) + (bytes[readIndex - 1] & 0xff);
	}
	
	/**
	 * Gets an unsigned 24-bit integer from {@link #bytes}.
	 * 
	 * @return a 24-bit integer.
	 */
	public int getUnsigned24BitInt() {
		readIndex += 3;
		return ((bytes[readIndex - 3] & 0xff) << 16)
			 + ((bytes[readIndex - 2] & 0xff) << 8)
			  + (bytes[readIndex - 1] & 0xff);
	}
	
	/**
	 * Gets an unsigned 32-bit integer from {@link #bytes}.
	 * 
	 * @return a 32-bit integer.
	 */
	public int getUnsignedInt() {
		readIndex += 4;
		return ((bytes[readIndex - 4] & 0xff) << 24)
			 + ((bytes[readIndex - 3] & 0xff) << 16)
			 + ((bytes[readIndex - 2] & 0xff) << 8)
			  + (bytes[readIndex - 1] & 0xff);
	}
	
	/**
	 * Advances the {@link #readIndex} by the amount specified.
	 * @param amount the amount to move the read index by.
	 */
	public void advance(int amount) {
		this.readIndex += amount;
	}
	
	/**
	 * Wraps the specified byte array.
	 * 
	 * @param buffer the buffer to wrap.
	 * @return a {@link ReadOnlyBuffer} instance wrapping the given array of bytes.
	 */
	public static ReadOnlyBuffer wrap(byte[] buffer) {
		return new ReadOnlyBuffer(buffer);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bytes);
		result = prime * result + readIndex;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ReadOnlyBuffer)) {
			return false;
		}
		
		ReadOnlyBuffer other = (ReadOnlyBuffer) obj;
		return Arrays.equals(bytes, other.bytes)
				&& readIndex == other.readIndex;
	}
	
}