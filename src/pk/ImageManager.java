package pk;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

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
		byte[] pixelsByte;
		int [] pixelsInt;

		// Obtenemos las dimensiones
		alto = bf.getHeight();
		ancho = bf.getWidth();

		// Inicializamos el vector donde guardamos los resultados
		pixelsInt = new int[alto * ancho];

		// Recorremos toda la imagen guardando los pixeles
		for (int i = 0; i < alto; i++) {
			for (int j = 0; j < ancho; j++) {
				pixelsInt[i * ancho + j] = bf.getRGB(j, i);
			}
		}
		
		// Convertimes de int[] a byte[]
		ByteBuffer byteBf = ByteBuffer.allocate(pixelsInt.length * 4);
		IntBuffer intBf = byteBf.asIntBuffer();
		intBf.put(pixelsInt);
		pixelsByte = byteBf.array();
	
		return pixelsByte;
	}
	
	private byte[] obtener4Bytes(byte[] pixels, int inicio) {
		
		// Inicializamos el resultado
		byte[] resultado = new byte[4];
		
	
		// Recorremos la entrada y vamos copiando
		for(int i = 0; i < 4; i++) {
			resultado[i] = pixels[inicio + i];
		}

		return resultado;
	}

	public void escribirImagen(byte[] pixelsByte, int width, int height, String path) throws IOException {

		// Inicializamos las variables a usar
		BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] pixelsInt = new int[pixelsByte.length / 4];
		int len = pixelsInt.length;
		
		
		// Convertimos de byte[] a int[]
		for(int i = 0; i < len; i++) {
			pixelsInt[i] = ByteBuffer.wrap(obtener4Bytes(pixelsByte, i * 4)).getInt();
		}
		
		// Reccorremos y vamos seteando en la imagen
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {

				bf.setRGB(j, i, pixelsInt[i * width + j]);
			}
		}
		
		// Escribimos el resultado
		ImageIO.write(bf, "png", new File(path));
	}
}