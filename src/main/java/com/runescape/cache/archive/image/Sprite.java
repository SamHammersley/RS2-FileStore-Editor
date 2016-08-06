package com.runescape.cache.archive.image;

public final class Sprite {

	private final int[] raster;
	
	private final int width;
	
	private final int height;
	
	private final int xOffset;
	
	private final int yOffset;
	
	private final int resizeWidth;
	
	private final int resizeHeight;
	
	private Sprite(int[] raster, int width, int height, int xOffset, int yOffset, int resizeWidth, int resizeHeight) {
		this.raster = raster;
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.resizeWidth = resizeWidth;
		this.resizeHeight = resizeHeight;
	}
	
	public static class SpriteBuilder {
		
		private int[] raster;
		
		private int width;
		
		private int height;
		
		private int xOffset;
		
		private int yOffset;
		
		private int resizeWidth;
		
		private int resizeHeight;
		
		public Sprite build() {
			return new Sprite(raster, width, height, xOffset, yOffset, resizeWidth, resizeHeight);
		}
		
		public void raster(int[] raster) {
			this.raster = raster;
		}
		
		public int width(int width) {
			return this.width = width;
		}
		
		public int height(int height) {
			return this.height = height;
		}
		
		public void xOffset(int xOffset) {
			this.xOffset = xOffset;
		}
		
		public void yOffset(int yOffset) {
			this.yOffset = yOffset;
		}
		
		public void resizeWidth(int resizeWidth) {
			this.resizeWidth = resizeWidth;
		}
		
		public void resizeHeight(int resizeHeight) {
			this.resizeHeight = resizeHeight;
		}
		
	}
	
}