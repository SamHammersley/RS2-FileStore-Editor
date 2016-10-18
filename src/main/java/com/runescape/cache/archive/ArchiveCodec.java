package com.runescape.cache.archive;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.runescape.io.ReadOnlyBuffer;
import com.runescape.io.util.Bzip2Util;

public class ArchiveCodec {

	/**
	 * The size of an archive entry's header in bytes.
	 */
	private static final int ENTRY_HEADER_SIZE = 10;
	
	/**
	 * Decodes the specified array of bytes from JaGex's proprietary format into an {@link Archive} object.
	 * 
	 * @param archiveBuffer raw data of the JAG archive in byte array.
	 * @return an {@link Archive}.
	 * @throws IOException 
	 */
	public static Archive decode(byte[] archiveBuffer) throws IOException {
		ReadOnlyBuffer buffer = ReadOnlyBuffer.wrap(archiveBuffer);
		
		int archiveSize = buffer.getUnsigned24BitInt();
		int compressedArchiveSize = buffer.getUnsigned24BitInt();
		
		boolean archiveCompressed = compressedArchiveSize != archiveSize;
		
		if (archiveCompressed) {
			byte[] decompressedArchive = Bzip2Util.unbzip2(buffer.getRemaining());
			buffer = ReadOnlyBuffer.wrap(decompressedArchive);
		}

		int entryCount = buffer.getUnsignedShort();
		int dataIndex = buffer.getReadIndex() + (entryCount * ENTRY_HEADER_SIZE);
		Set<ArchiveEntry> entries = new HashSet<>(entryCount);

		for (int entryIndex = 0; entryIndex < entryCount; entryIndex++) {
			int identifier = buffer.getUnsignedInt();
			int size = buffer.getUnsigned24BitInt();
			int compressedSize = buffer.getUnsigned24BitInt();
			int entrySize = archiveCompressed ? size : compressedSize;

			byte[] entryDataBuffer = buffer.getBytes(dataIndex, dataIndex += entrySize);
			entries.add(new ArchiveEntry(identifier, archiveCompressed ? entryDataBuffer : Bzip2Util.unbzip2(entryDataBuffer)));
		}
		
		return new Archive(entries, archiveCompressed, archiveSize, compressedArchiveSize);
	}
	
	/**
	 * Writes the data back to JaGex's proprietary format, known as JAG. This
	 * method only needs to be invoked after adding new entries.
	 * 
	 * @throws IOException 
	 */
	public static byte[] encode(Archive archive) throws IOException {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(byteOut)) {
			Set<ArchiveEntry> entries = archive.getEntries();
			
			out.writeShort(archive.getSize() >> 8);
			out.writeByte(archive.getSize() & 0xFF);
			
			out.writeShort(archive.getCompressedSize() >> 8);
			out.writeByte(archive.getCompressedSize() & 0xFF);
			
			out.writeShort(entries.size());
			
			boolean isCompressed = archive.isCompressed();
			
			//TODO: better solution for compressing entries twice.
			Map<Integer, byte[]> compressedEntries = new HashMap<>();
			for (ArchiveEntry entry : archive.getEntries()) {
				out.writeInt(entry.getIdentifier());
				
				byte[] entryBuffer = entry.getBuffer().getBytes();
				out.writeShort(entryBuffer.length);
				out.writeByte(entryBuffer.length & 0xFF);
				
				byte[] compressed = Bzip2Util.bzip2(entryBuffer);
				compressedEntries.put(entry.getIdentifier(), compressed);
				int compressedSize = isCompressed ? entryBuffer.length : compressed.length;

				out.writeShort(compressedSize >> 8);
				out.writeByte(compressedSize & 0xFF);
			}
			
			for (ArchiveEntry entry : entries) {
				out.write(isCompressed ? entry.getBuffer().getBytes() : compressedEntries.get(entry.getIdentifier()));
			}
			return byteOut.toByteArray();
		}
	}
	
}