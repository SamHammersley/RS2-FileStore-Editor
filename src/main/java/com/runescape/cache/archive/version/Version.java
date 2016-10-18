package com.runescape.cache.archive.version;

public final class Version {

	private final int[] fileVersions;

	public Version(int[] fileVersions) {
		this.fileVersions = fileVersions;
	}

	public int[] getVersions() {
		return fileVersions;
	}

}