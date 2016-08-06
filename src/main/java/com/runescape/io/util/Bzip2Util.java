package com.runescape.io.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;

public final class Bzip2Util {
	
	private static final byte[] BZIP2_HEADER = { 'B', 'Z', 'h', '1' };
	
	/** 
	 * Bzip2 header size in bytes.
	 */
	private static final int BZIP2_HEADER_SIZE = 4;
	
	/**
	 * Decompresses the given input byte array. This function assumes that the bzip2 header is missing. 
	 * 
	 * @param input compressed input byte array intended to be decompressed.
	 * @return the decompressed input.
	 * @throws IOException 
	 */
	public static byte[] unbzip2(final byte[] input) throws IOException {
		byte[] concat = Bytes.concat(BZIP2_HEADER, input);

		return ByteStreams.toByteArray(new BZip2CompressorInputStream(new ByteArrayInputStream(concat)));
	}
	
	public static byte[] bzip2(final byte[] input) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				BZip2CompressorOutputStream bz2Out = new BZip2CompressorOutputStream(out)) {
			bz2Out.write(input);
			bz2Out.finish();
			byte[] output = out.toByteArray();
			return Arrays.copyOfRange(output, BZIP2_HEADER_SIZE, output.length);
		}
	}

	private Bzip2Util() {
		
	}
	
}