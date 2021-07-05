package pk;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.SystemColor;

public class Interface extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
	
	private static final String MSJ_ERROR_KEY_VACIA = "Ingrese un número como key";
	private static final String MSJ_ERROR_KEY_NO_NUMERICA = "La key debe ser un valor numérico";
	private static final String MSJ_ERROR_KEY_LONGITUD = "La key debe tener una longitud de 32 caracteres";
	
	private static final String MSJ_ERROR_FILE_NO_SELECCIONADO = "Eliga un archivo a encriptar";
	private static final String MSJ_ERROR_NO_SE_PUDO_ABRIR_FILE = "No se logro a acceder a ";
	private static final String MSJ_ERROR_FILE_NO_PNG = "Solo se pueden usar archivos PNG";
	
	private static final String MSJ_EXITO_OPERACION_REALIZADA = "Operación realizada!";
	
	private static final String MSJ_CONFIRNAR_CERRAR_VENTANA = "¿Deseas cerrar la ventana?";
	
	private static final String TITULO_ERROR = "Error!";
	private static final String TITULO_EXITO = "Éxito!";
	private static final String TITULO_CERRAR_VENTANA = "Cerrar Ventana";
	private static final String TITULO_APLICACION = "Salsa 20";

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

		/* INICIALIZACIÓN OBJETOS BÁSICOS */
		
		// Establecemos un look and feel
		try {
			
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			
		} catch (ClassNotFoundException e1) {
			
		} catch (InstantiationException e1) {
		
		} catch (IllegalAccessException e1) {

		} catch (UnsupportedLookAndFeelException e1) {

		}

		// Definimos que no realice ninguna acción en el cierre
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// Definimos el tamaño
		setSize(508, 440);

		// Establecemos que no se pueda modificar el tamaño
		setResizable(false);
		
		// Ponemos un título
		setTitle(TITULO_APLICACION);

		// Definición del ContentPane
		contentPane = new JPanel();
		contentPane.setBackground(Color.white);
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
		lblArchivoSel.setBounds(34, 201, 159, 35);
		lblArchivoSel.setFont(FONT_CAMPOS);
		contentPane.add(lblArchivoSel);

		JLabel lblKey = new JLabel("Key:");
		lblKey.setFont(FONT_CAMPOS);
		lblKey.setBounds(155, 247, 38, 35);
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
		btnCifrar.setFont(FONT_CAMPOS);
		btnCifrar.setBounds(10, 329, 216, 45);
		contentPane.add(btnCifrar);

		JButton btnDescifrar = new JButton("Descifrar");
		btnDescifrar.setFont(FONT_CAMPOS);
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
		return JOptionPane.showConfirmDialog(null, MSJ_CONFIRNAR_CERRAR_VENTANA, TITULO_CERRAR_VENTANA,
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
	}

	/*
	 * Imprime por pantalla un mensaje de error.
	 */

	private int mensajeError(String msj) {
		return JOptionPane.showConfirmDialog(null, msj, TITULO_ERROR, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
	}

	/*
	 * Imprime por pantalla un mensaje de éxito.
	 */

	private int mensajeExito(String msj) {
		return JOptionPane.showConfirmDialog(null, msj, TITULO_EXITO, JOptionPane.CLOSED_OPTION,
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

			// Verificamos que sea un png
			if(!file.getName().contains(".png")) {

				// Emitimos un mensaje de error y reseteamos la variable file
				mensajeError(MSJ_ERROR_FILE_NO_PNG);
				file = null;
				txtNombre.setText("");
				return;
			}
			
			
			// Establecemos el nombre
			txtNombre.setText(file.getName());

		}
	}

	/*
	 * Realiza la operación cifrar o descifrar
	 */

	private void realizarOperación(int op) {

		String keyString = txtKey.getText();
		BufferedImage img;
		byte[] key;
		byte[] mensajeClaro;
		byte[] criptograma;

		// Validaciones necesarias
		if (keyString == null || keyString.trim().isEmpty()) {
			mensajeError(MSJ_ERROR_KEY_VACIA);
			return;
		}

		if (!keyString.matches("^[0-9]+$")) {
			mensajeError(MSJ_ERROR_KEY_NO_NUMERICA);
			return;
		}
		
		if(keyString.length() != 32) {
			mensajeError(MSJ_ERROR_KEY_LONGITUD);
			return;
		}

		if (file == null) {
			mensajeError(MSJ_ERROR_FILE_NO_SELECCIONADO);
			return;
		}
		
		// Convertimos la key a bytes
		key = keyString.getBytes();

		if (op == OPERACION_CIFRAR) {
			try {

				// Leemos la imagen
				img = imgManager.leerImagen(file.getPath());
				
				// Inicializamos el mensajeClaro con los bytes
				mensajeClaro = imgManager.convertirBytes(img);

				// Ciframos el mensaje en claro
				criptograma = salsita.encriptar(mensajeClaro, key);

				// Guardamos el archivo
				imgManager.escribirImagen(criptograma, img.getWidth(), img.getHeight(), PATH_CIFRADO_OUT);

			} catch (IOException e) {

				// Mostramos por pantalla el error y finalizamso la ejecución
				mensajeError(MSJ_ERROR_NO_SE_PUDO_ABRIR_FILE + file.getPath());
				return;
			}

		} else if (op == OPERACION_DESCIFRAR) {

			try {
				
				// Leemos la imagen
				img = imgManager.leerImagen(file.getPath());

				// Inicializamos el criptograma con los bytes
				criptograma = imgManager.convertirBytes(img);

				// Desciframos el mensaje
				mensajeClaro = salsita.desencriptar(criptograma, key);

				// Guardamos el resultado
				imgManager.escribirImagen(mensajeClaro, img.getWidth(), img.getHeight(), PATH_DESCIFRADO_OUT);

			} catch (IOException e) {

				// Mostramos por pantalla el error y finalizamso la ejecución
				mensajeError(MSJ_ERROR_NO_SE_PUDO_ABRIR_FILE + file.getPath());
				return;
			}
		}
		
		// Mostramos un mensaje de éxito
		mensajeExito(MSJ_EXITO_OPERACION_REALIZADA);

		// Reseteamos el file
		file = null;
		txtNombre.setText("");
	}
}
