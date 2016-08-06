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
	 * Gets the bytes in this buffer.
	 * 
	 * @return the bytes.
	 */
	public byte[] getBytes() {
		return bytes;
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
		return Arrays.copyOfRange(bytes, readIndex, bytes.length);
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
	public int getUnsignedByte() {
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
	public boolean equals(Object obj) {
		if (!(obj instanceof ReadOnlyBuffer)) {
			return false;
		}
		
		ReadOnlyBuffer other = (ReadOnlyBuffer) obj;
		return Arrays.equals(bytes, other.bytes)
				&& readIndex == other.readIndex;
	}
	
}