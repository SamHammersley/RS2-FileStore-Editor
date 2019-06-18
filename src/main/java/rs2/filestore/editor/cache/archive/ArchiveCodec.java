package rs2.filestore.editor.cache.archive;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import rs2.filestore.editor.io.ReadOnlyBuffer;
import rs2.filestore.editor.io.util.Bzip2Util;

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
		ReadOnlyBuffer indexBuffer = ReadOnlyBuffer.wrap(archiveBuffer);
		
		int archiveSize = indexBuffer.getUnsigned24BitInt();
		int compressedArchiveSize = indexBuffer.getUnsigned24BitInt();

		boolean archiveCompressed = compressedArchiveSize != archiveSize;

		if (archiveCompressed) {
			indexBuffer = ReadOnlyBuffer.wrap(Bzip2Util.unbzip2(indexBuffer.getRemaining()));
		}

		int entryCount = indexBuffer.getUnsignedShort();
		
		ReadOnlyBuffer dataBuffer = indexBuffer.split(indexBuffer.getReadIndex() + (entryCount * ENTRY_HEADER_SIZE));
		LinkedHashSet<ArchiveEntry> entries = new LinkedHashSet<>(entryCount);

		for (int entryIndex = 0; entryIndex < entryCount; entryIndex++) {
			int identifier = indexBuffer.getUnsignedInt();
			int size = indexBuffer.getUnsigned24BitInt();
			int compressedSize = indexBuffer.getUnsigned24BitInt();
			int entrySize = archiveCompressed ? size : compressedSize;
			
			byte[] entryDataBuffer = dataBuffer.getBytes(entrySize);
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
			
			Map<Integer, byte[]> compressedEntries = new HashMap<>();
			
			for (ArchiveEntry entry : entries) {
				out.writeInt(entry.getIdentifier());
				
				byte[] entryBuffer = entry.getBuffer().getBytes();
				out.writeShort(entryBuffer.length >> 8);
				out.writeByte(entryBuffer.length & 0xFF);
				
				int compressedSize = entryBuffer.length;
				if (!isCompressed) {
					byte[] compressed = Bzip2Util.bzip2(entryBuffer);
					compressedEntries.put(entry.getIdentifier(), compressed);
					
					compressedSize = compressed.length;
				}

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