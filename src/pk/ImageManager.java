package pk;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

public class ImageManager {

	public BufferedImage leerImagen(String path) throws IOException {

		// Variables a usar
		BufferedImage bf;

		// Leemos la imagen
		bf = ImageIO.read(new File(path));
		
		// Retornamos el BufferedImage
		return bf;

	}

	public byte[] convertirBytes(BufferedImage bf) {

		// Variables a usar
		int alto, ancho;
		byte[] pixels;

		// Obtenemos las dimensiones
		alto = bf.getHeight();
		ancho = bf.getWidth();

		// Inicializamos el vector donde guardamos los resultados
		pixels = new byte[alto * ancho];

		// Recorremos toda la imagen guardando los pixeles
		for (int i = 0; i < alto; i++) {
			for (int j = 0; j < ancho; j++) {

				Integer pixel = new Integer(bf.getRGB(j, i));
				pixels[i * ancho + j] = pixel.byteValue();
			}
		}

		return pixels;
	}

	public void escribirImagen(byte[] pixels, int width, int height, ColorModel type, String path) throws IOException {

		BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {

				bf.setRGB(j, i, pixels[i * width + j]);
			}
		}
		
		ImageIO.write(bf, "png", new File(path));
	}
}