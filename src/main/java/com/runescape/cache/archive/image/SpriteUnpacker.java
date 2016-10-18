package com.runescape.cache.archive.image;

import com.runescape.cache.archive.Archive;
import com.runescape.cache.archive.image.Sprite.SpriteBuilder;
import com.runescape.io.ReadOnlyBuffer;

public class SpriteUnpacker {

	public Sprite unpackSprite(Archive archive, String entryName, int subEntryIndex) {
		ReadOnlyBuffer imageData = archive.getEntry(entryName + ".dat").getBuffer();
		ReadOnlyBuffer metaData = archive.getEntry("index.dat").getBuffer();
		
		SpriteBuilder builder = new SpriteBuilder();
		
		metaData.advance(imageData.getUnsignedShort());
		
		builder.resizeWidth(metaData.getUnsignedShort());
		builder.resizeHeight(metaData.getUnsignedShort());
		
		int colourCount = metaData.getUnsigned();
		int palette[] = new int[colourCount];
		for(int index = 0; index < colourCount - 1; index++) {
			int colour = metaData.getUnsigned24BitInt();
			palette[index + 1] = colour == 0 ? 1 : colour;
		}
		
		for(int index = 0; index < subEntryIndex; index++) {
			//skip x and y offsets
			metaData.advance(Byte.BYTES * 2);
			//skip image data (width * height)
			imageData.advance(Byte.BYTES * metaData.getUnsignedShort() * metaData.getUnsignedShort());
			//skip fill type
			metaData.advance(Byte.BYTES);
		}
		
		builder.xOffset(metaData.getUnsigned());
		builder.yOffset(metaData.getUnsigned());
		int width = builder.width(metaData.getUnsignedShort());
		int height = builder.height(metaData.getUnsignedShort());
		int fillType = metaData.getUnsigned();
		
		int area = width * height;
		int[] raster = new int[area];
		if(fillType == 0) {
			for(int index = 0; index < area; index++) { //linear
				raster[index] = palette[imageData.getUnsigned()];
			}
		} else if(fillType == 1) {
			for(int column = 0; column < width; column++) {
				for(int row = 0; row < height; row++) {
					raster[column + row * width] = palette[imageData.getUnsigned()];
				}
			}
		}
		builder.raster(raster);
		
		return builder.build();
	}

}