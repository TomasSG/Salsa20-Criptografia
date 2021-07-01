package pk;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class ImageManager {

	public byte[] leerImagen(String path) throws IOException {
		return Files.readAllBytes(new File(path).toPath());
	}

	public void escribirImagen(byte[] imageInByte, String path) throws IOException {
		BufferedOutputStream bs = new BufferedOutputStream(new FileOutputStream(new File(path)));
		bs.write(imageInByte);
		bs.close();

	}
}