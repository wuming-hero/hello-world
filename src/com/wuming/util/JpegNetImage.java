package com.wuming.util;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class JpegNetImage extends AbstractNetImage {
	public JpegNetImage(String _url) {
		super(_url);
	}

	public JpegNetImage() {
	}

	protected void encode(FileOutputStream out, BufferedImage bufferedImage) throws ImageFormatException, IOException {
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		encoder.encode(bufferedImage);
	}

	@Override
	protected void encode(ByteArrayOutputStream out, BufferedImage bufferedImage)
			throws ImageFormatException, IOException {
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		encoder.encode(bufferedImage);

	}
}
