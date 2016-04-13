package com.wuming.util;

import com.sun.image.codec.jpeg.ImageFormatException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractNetImage {
	private String url = null;

	public AbstractNetImage(String url) {
		this.url = url;
	}

	public AbstractNetImage() {
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	protected abstract void encode(FileOutputStream paramFileOutputStream, BufferedImage paramBufferedImage)
			throws ImageFormatException, IOException;

	protected abstract void encode(ByteArrayOutputStream paramFileOutputStream, BufferedImage paramBufferedImage)
			throws ImageFormatException, IOException;

	private void getImageImpl(String newFilePath, int sizeReduceRank) throws MalformedURLException, IOException {
		if (this.url == null) {
			return;
		}
		Image image = ImageIO.read(new URL(this.url));
		int width = image.getWidth(null) / sizeReduceRank;
		int height = image.getHeight(null) / sizeReduceRank;

		BufferedImage bufferedImage = new BufferedImage(width, height, 1);
		bufferedImage.getGraphics().drawImage(image, 0, 0, width, height, null);
		FileOutputStream out = new FileOutputStream(newFilePath);
		encode(out, bufferedImage);
		image.flush();
		bufferedImage.flush();
		out.close();
	}

	public final void getImage(String newFilePath, int sizeReduceRank) throws MalformedURLException, IOException {
		getImageImpl(newFilePath, sizeReduceRank);
	}

	public final void getImage(String newFilePath) throws MalformedURLException, IOException {
		getImageImpl(newFilePath, 1);
	}

	public void getImageFromUrl(String url, String newFilePath) throws MalformedURLException, IOException {
		setUrl(url);
		getImage(newFilePath);
	}

	public ByteArrayOutputStream getImageToOutStream(String url) throws MalformedURLException, IOException {
		setUrl(url);
		if (this.url == null) {
			return null;
		}
		Image image = ImageIO.read(new URL(this.url));
		int width = image.getWidth(null) / 1;
		int height = image.getHeight(null) / 1;

		BufferedImage bufferedImage = new BufferedImage(width, height, 1);
		bufferedImage.getGraphics().drawImage(image, 0, 0, width, height, null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		encode(out, bufferedImage);
		image.flush();
		bufferedImage.flush();
//		out.close();
		return out;
	}
}
