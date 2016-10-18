package com.runescape.cache.archive.version;

import com.runescape.cache.archive.Archive;
import com.runescape.cache.archive.ArchiveEntry;
import com.runescape.io.ReadOnlyBuffer;

public final class VersionListArchiveUnpacker {
	
	private static final String[] FILE_PREFIXES = {
		"model", "anim", "midi", "map"
	};

	private static final String[] FILE_TYPES = {
		"version", "crc", "index"
	};

	public VersionListArchive unpack(Archive archive) {
		for (String prefix : FILE_PREFIXES) {
			ArchiveEntry versionList = archive.getEntry(prefix + FILE_TYPES[0]);
			ReadOnlyBuffer buffer = versionList.getBuffer();
			int versionListSize = buffer.length() / 2;
			
			int versions[] = buffer.getUnsignedShorts(versionListSize);
			Version version = new Version(versions);
			
			
		}
		for(int i = 0; i < 4; i++) {
			ArchiveEntry versionList = archive.getEntry(FILE_PREFIXES[i] + FILE_TYPES[0]);
			ReadOnlyBuffer buffer = versionList.getBuffer();
			int versionListSize = buffer.length() / 2;
		
			int versions[] = new int[versionListSize];
			
			for(int index = 0; index < versionListSize; index++) {
				versions[index] = buffer.getUnsignedShort();
			}
			Version version = new Version(versions);
			
		}
		
		return null;
	}
	
}