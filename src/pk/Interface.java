package pk;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JTextField;

public class Interface extends JFrame {

	// Lógica
	private File file;
	private Salsa20 salsita;
	private ImageManager imgManager;

	// Algunas constantes
	private static final Font FONT_TITULOS = new Font("Arial", Font.BOLD, 20);
	private static final Font FONT_SUBTITULOS = new Font("Arial", Font.BOLD, 18);
	private static final Font FONT_CAMPOS = new Font("Arial", Font.PLAIN, 16);

	private static final int OPERACION_CIFRAR = 1;
	private static final int OPERACION_DESCIFRAR = 2;

	private static final String PATH_CIFRADO_OUT = "./cifrado.png";
	private static final String PATH_DESCIFRADO_OUT = "./descifrado.png";

	// Otros
	private JPanel contentPane;
	private JTextField txtKey;
	private JTextField txtNombre;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface frame = new Interface();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Interface() {
		setBackground(Color.LIGHT_GRAY);

		/* INICIALIZACIÓN OBJETOS BÁSICOS */

		// Definimos que no realice ninguna acción en el cierre
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// Definimos el tamaño
		setSize(508, 440);

		// Establecemos que no se pueda modificar el tamaño
		setResizable(false);

		// Definición del ContentPane
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(null);
		setContentPane(contentPane);

		// Inicialización dle cifrador en flujo
		salsita = new Salsa20();
		imgManager = new ImageManager();

		/* AGREGAR ELEMENTOS AL LAYOUT */

		JLabel lblTitulo = new JLabel();
		lblTitulo.setBounds(162, 11, 159, 45);
		lblTitulo.setText(":-: SALSA 20 :-:");
		lblTitulo.setFont(FONT_TITULOS);
		contentPane.add(lblTitulo);

		JLabel lblTitulo_1 = new JLabel();
		lblTitulo_1.setBackground(Color.LIGHT_GRAY);
		lblTitulo_1.setBounds(11, 70, 463, 22);
		lblTitulo_1.setText(":-: Seleccione una imágen y realice una operación :-:");
		lblTitulo_1.setFont(FONT_SUBTITULOS);
		contentPane.add(lblTitulo_1);

		JButton btnArchivo = new JButton("Seleccionar archivo");
		btnArchivo.setFont(FONT_CAMPOS);
		btnArchivo.setBounds(134, 120, 216, 45);
		contentPane.add(btnArchivo);

		JLabel lblArchivoSel = new JLabel("Archivo Seleccionado:");
		lblArchivoSel.setBounds(11, 201, 188, 35);
		lblArchivoSel.setFont(new Font("Arial", Font.BOLD, 16));
		contentPane.add(lblArchivoSel);

		JLabel lblKey = new JLabel("Key:");
		lblKey.setFont(new Font("Arial", Font.BOLD, 16));
		lblKey.setBounds(155, 247, 44, 35);
		contentPane.add(lblKey);

		txtKey = new JTextField();
		txtKey.setBounds(202, 247, 255, 35);
		contentPane.add(txtKey);
		txtKey.setColumns(10);

		txtNombre = new JTextField();
		txtNombre.setEditable(false);
		txtNombre.setColumns(10);
		txtNombre.setBounds(202, 201, 255, 35);
		contentPane.add(txtNombre);

		JButton btnCifrar = new JButton("Cifrar");
		btnCifrar.setFont(new Font("Arial", Font.PLAIN, 16));
		btnCifrar.setBounds(10, 329, 216, 45);
		contentPane.add(btnCifrar);

		JButton btnDescifrar = new JButton("Descifrar");
		btnDescifrar.setFont(new Font("Arial", Font.PLAIN, 16));
		btnDescifrar.setBounds(241, 329, 216, 45);
		contentPane.add(btnDescifrar);

		/* DEFINIR ACCIONES */

		// Para preguntar si se desea cerrar la ventana
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {

				// Preguntamos al usuario si desea realizar la acción
				int resultado = mensajeCerrarVentana();

				// Si apreto SI salimos, en caso contrario nos quedamos
				if (resultado == JOptionPane.YES_OPTION) {
					cerrarVentana();
				}
			}

		});

		btnArchivo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				buscarArchivo();
			}
		});

		btnCifrar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				realizarOperación(OPERACION_CIFRAR);
			}
		});

		btnDescifrar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				realizarOperación(OPERACION_DESCIFRAR);
			}
		});
	}

	/*
	 * Imprime por la pantalla un mensaje para confirmar cerrar la ventana.
	 */

	private int mensajeCerrarVentana() {
		return JOptionPane.showConfirmDialog(null, "¿Deseas cerrar la aplicación?", "Cerrar aplicaicón",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
	}

	/*
	 * Imprime por pantalla un mensaje de error.
	 */

	private int mensajeError(String msj) {
		return JOptionPane.showConfirmDialog(null, msj, "Error", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
	}

	/*
	 * Imprime por pantalla un mensaje de éxito.
	 */

	private int mensajeExito(String msj) {
		return JOptionPane.showConfirmDialog(null, msj, "ÉXito!", JOptionPane.CLOSED_OPTION,
				JOptionPane.INFORMATION_MESSAGE);
	}

	/*
	 * Cierra la ventana y finaliza la aplicaicón
	 */

	private void cerrarVentana() {
		this.dispose();
	}

	/*
	 * Abre un buscador para buscar un archivo de terminación requerida.
	 */

	private void buscarArchivo() {

		JFileChooser chooser = new JFileChooser();

		// Especificamos que solo se pueden seleccionar archivos
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// Abrir la ventana para buscar archivos
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

			// Obtenemos el archivo seleccionado
			file = chooser.getSelectedFile();

			// Establecemos el nombre
			txtNombre.setText(file.getName());

			// TODO: verificar que sea un PNG o JPG
		}
	}

	/*
	 * Realiza la operación cifrar o descifrar
	 */

	private void realizarOperación(int op) {

		String keyString = txtKey.getText();
		byte[] key;
		byte[] mensajeClaro;
		byte[] criptograma;

		// Validaciones necesarias
		if (keyString == null || keyString.trim().isEmpty()) {
			mensajeError("Ingrese un número como Key");
			return;
		}

		if (!keyString.matches("^[0-9]+$")) {
			mensajeError("La key debe ser un valor numérico");
			return;
		}
		
		if(keyString.length() > 32) {
			mensajeError("La Key no puede tener una longitud mayor a 32 caracteres");
			return;
		}

		if (file == null) {
			mensajeError("Eliga un archivo a encriptar");
			return;
		}


		// Verificamos la longitud de la cadena
		if(keyString.length() < 32) {
			
			// Como la key debe tener una longitud de 32 caracteres, la rellenamos hasta llegar a esa longitud
			for(int i = keyString.length(); i < 32; i++) {
				keyString = keyString.concat("0");
			}
			
		}
		
		// Convertimos la key a bytes
		key = keyString.getBytes();

		if (op == OPERACION_CIFRAR) {
			try {

				// Inicializamos el mensajeClaro con los bytes
				mensajeClaro = imgManager.leerImagen(file.getPath());

				// Ciframos el mensaje en claro
				criptograma = salsita.encriptar(mensajeClaro, key);

				// Guardamos el archivo
				imgManager.escribirImagen(criptograma, PATH_CIFRADO_OUT);

			} catch (IOException e) {

				// Mostramos por pantalla el error y finalizamso la ejecución
				mensajeError("No se logro acceder a: " + file.getPath());
				return;
			}

		} else if (op == OPERACION_DESCIFRAR) {

			try {

				// Inicializamos el criptograma con los bytes
				criptograma = imgManager.leerImagen(file.getPath());

				// Desciframos el mensaje
				mensajeClaro = salsita.desencriptar(criptograma, key);

				// Guardamos el resultado
				imgManager.escribirImagen(mensajeClaro, PATH_DESCIFRADO_OUT);

			} catch (IOException e) {

				// Mostramos por pantalla el error y finalizamso la ejecución
				mensajeError("No se logro acceder a: " + file.getPath());
				return;
			}
		}
		
		// Mostramos un mensaje de éxito
		mensajeExito("Operación Completada!");

		// Reseteamos el file
		file = null;
		txtNombre.setText("");
	}
}
