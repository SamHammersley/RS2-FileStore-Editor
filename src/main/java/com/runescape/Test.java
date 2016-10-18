package com.runescape;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.runescape.cache.archive.Archive;
import com.runescape.cache.archive.ArchiveCodec;
import com.runescape.cache.archive.ArchiveEntry;

public class Test {

	public static void main(String... args) throws IOException, NoSuchAlgorithmException {
		Path archivePath = Paths.get("./archive.jag");
		byte[] buffer = Files.readAllBytes(archivePath);
		Archive archive = ArchiveCodec.decode(buffer);

		MessageDigest md = MessageDigest.getInstance("MD5");
		archive.getEntries().stream().map(ArchiveEntry::getBytes).forEach(md::update);
		System.out.println(encodeHex(md.digest()));
		
		byte[] reencode = ArchiveCodec.encode(archive);
		try (FileOutputStream fos = new FileOutputStream("archive.jag")) {
			fos.write(reencode);
		}
	}

	public static String encodeHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
	    for (byte b : bytes) {
	        sb.append(String.format("%02X", b));
	    }
	    return sb.toString().toUpperCase();
	}

}