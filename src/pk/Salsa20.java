package pk;

import java.math.*;
import java.util.ArrayList;
import java.util.Random;

import java.nio.*;

public class Salsa20 {

	/* CONSTANTES */
	private final int RONDAS = 20;
	private final int TAM_BLOQUE = 64;

	// "expand 32-byte k" en ASCII, cada nro es la representación en decimal
	private final byte[] CONSTANTE = { 101, 120, 112, 97, 110, 100, 32, 51, 50, 45, 98, 121, 116, 101, 32, 107 };

	/* MATRICES */

	private int[][] matrizInicial = new int[4][4];
	private int[][] matrizEncriptadora = new int[4][4];
	private int[][] matrizAux = new int[4][4];

	/* ------------------------------- MÉTODOS ------------------------------- */

	/*
	 * Esta función se ejecuta SOLO UNA VEZ. Luego, por cada bloque de 64B de texto
	 * plano, se cambiará el valor del contador (offset).
	 */

	private void generarMatrizInicial(byte[] key) {
		// generamos los valores aleatorios (nonce)
		int nonce1, nonce2;
		Random r = new Random(23);
		nonce1 = r.nextInt(Integer.MAX_VALUE);
		nonce2 = r.nextInt(Integer.MAX_VALUE);

		// generamos la matriz (agregamos la key, la constante, los nonce y el offset

		matrizInicial[0][0] = cargarCelda(CONSTANTE, 0);
		matrizInicial[0][1] = cargarCelda(key, 0);
		matrizInicial[0][2] = cargarCelda(key, 4);
		matrizInicial[0][3] = cargarCelda(key, 8);
		matrizInicial[1][0] = cargarCelda(key, 12);
		matrizInicial[1][1] = cargarCelda(CONSTANTE, 4);
		matrizInicial[1][2] = nonce1;
		matrizInicial[1][3] = nonce2;
		matrizInicial[2][0] = 0;// Para una primera vez, el contador inicia en 0. luego, por cada bloque de 64B
								// del texto plano, este contador incrementará en 1
		matrizInicial[2][1] = 0;
		matrizInicial[2][2] = cargarCelda(CONSTANTE, 8);
		matrizInicial[2][3] = cargarCelda(key, 16);
		matrizInicial[3][0] = cargarCelda(key, 20);
		matrizInicial[3][1] = cargarCelda(key, 24);
		matrizInicial[3][2] = cargarCelda(key, 28);
		matrizInicial[3][3] = cargarCelda(CONSTANTE, 12);
	}

	private void generarMatrizEncriptadora(byte[] numeroBloque) {

		int i, j;

		// cambio el valor del contador, contenido en la matriz inicial
		matrizInicial[2][0] = cargarCelda(numeroBloque, 0);
		matrizInicial[2][1] = cargarCelda(numeroBloque, 4);
		matrizAux = matrizInicial.clone();

		// hago las 20 rondas
		for (i = 0; i < RONDAS; i += 2) {

			quarterRound(0, 0, 1, 0, 2, 0, 3, 0);// COLUMNA 1
			quarterRound(1, 1, 2, 1, 3, 1, 0, 1);// COLUMNA 2
			quarterRound(2, 2, 3, 2, 0, 2, 1, 2);// COLUMNA 3
			quarterRound(3, 3, 0, 3, 1, 3, 2, 3);// COLUMNA 4

			quarterRound(0, 0, 0, 1, 0, 2, 0, 3);// FILA 1
			quarterRound(1, 1, 1, 2, 1, 3, 1, 0);// FILA 2
			quarterRound(2, 2, 2, 3, 2, 0, 2, 1);// FILA 3
			quarterRound(3, 3, 3, 0, 3, 1, 3, 2);// FILA 4
		}

		// genero la matriz encriptadora, utilizando la matriz auxiliar y la inicial
		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				matrizEncriptadora[i][j] = matrizAux[i][j] + matrizInicial[i][j];
			}
		}
	}

	public byte[] encriptar(byte[] textoPlano, byte[] key) {

		// Variables a usar
		int numeroBloque = 0;
		int cantBytesTextoPlano = textoPlano.length;
		int i, j, k;
		byte[] block = new byte[TAM_BLOQUE];
		byte[] bloqueAux = new byte[4];
		byte[] criptograma;

		// Invertimos el texto plano
		ByteBuffer buffer = ByteBuffer.wrap(textoPlano.clone());
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		textoPlano = buffer.array();

		// Genero por primera vez la matriz inicial, con el numero de bloque en 0
		generarMatrizInicial(key);

		criptograma = new byte[cantBytesTextoPlano];

		// Aca se realiza el XOR
		while (cantBytesTextoPlano >= TAM_BLOQUE) {

			// Generamos la matriz encriptadora para esta pasada
			generarMatrizEncriptadora(ByteBuffer.allocate(8).putInt(numeroBloque).array());

			// Dividimos el texto plano, cargando un bloque de 64 Bytes
			for (i = 0; i < 64; i++) {
				block[i] = textoPlano[(int) (numeroBloque * 64) + i];
			}

			// Hacemos el XOR con el texto plano
			for (i = 0; i < 4; i++) {
				for (j = 0; j < 4; j++) {
					bloqueAux = ByteBuffer.allocate(4).putInt(matrizEncriptadora[i][j]).array();

					for (k = 0; k < 4; k++) {
						block[k + (i * 16 + j * 4)] ^= bloqueAux[k];
					}
				}
			}

			// Incorporamos el bloque cifrado al criptograma
			for (i = 0; i < TAM_BLOQUE; i++) {
				criptograma[i + (int) (numeroBloque * TAM_BLOQUE)] = block[i];
			}

			numeroBloque++;
			cantBytesTextoPlano -= TAM_BLOQUE;
		}

		
		// Esto se realiza en caso en que que la longitud del texto plano no es múltiplo de 6.
		if (cantBytesTextoPlano != 0) {
			
			int aux = cantBytesTextoPlano;

			// Generamos la matriz encriptadora para la última pasada
			generarMatrizEncriptadora(ByteBuffer.allocate(8).putInt(numeroBloque).array());

			// Cargamos los n úlitmos bytes que faltan cifrar
			for (i = 0; i < cantBytesTextoPlano; i++) {
				block[i] = textoPlano[(int) (numeroBloque * 64) + i];
			}

			// Hacemos el XOR con el texto plano
			for (i = 0; i < 4; i++) {
				for (j = 0; j < 4; j++) {
					bloqueAux = ByteBuffer.allocate(4).putInt(matrizEncriptadora[i][j]).array();

					for (k = 0; k < 4; k++) {

						if (cantBytesTextoPlano > 0) {
							block[k + (i * 16 + j * 4)] ^= bloqueAux[k];
							cantBytesTextoPlano--;
						}
					}
				}
			}

			// Incorporamos el bloque cifrado al criptograma
			for (i = 0; i < aux; i++) {
				criptograma[i + (int) (numeroBloque * TAM_BLOQUE)] = block[i];
			}

		}

		return criptograma;
	}

	public byte[] desencriptar(byte[] criptograma, byte[] key) {
		return encriptar(criptograma, key);
	}

	/*
	 * Para imprimir la matriz por pantalla.
	 */

	private void imprimirMatriz(int[][] m) {

		System.out.println("-------------------------------------------");
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.println("M[" + i + "][" + j + "] = " + m[i][j]);
			}
		}
		System.out.println("-------------------------------------------");
	}

	private int rotateL(int a, int b) {
		return (a << b) | (a >>> (32 - b)); // a<<b --> a= a.2^b
	}

	/*
	 * funcion de rotación que hace operaciones ARX, XOR rotate sobre 4 elementos.
	 */

	private void quarterRound(int xa, int ya, int xb, int yb, int xc, int yc, int xd, int yd) {

		matrizAux[xb][yb] ^= rotateL(matrizAux[xa][ya] + matrizAux[xd][yd], 7);
		matrizAux[xc][yc] ^= rotateL(matrizAux[xb][yb] + matrizAux[xa][ya], 9);
		matrizAux[xd][yd] ^= rotateL(matrizAux[xc][yc] + matrizAux[xb][yb], 13);
		matrizAux[xa][ya] ^= rotateL(matrizAux[xd][yd] + matrizAux[xc][yc], 18);

	}

	private int cargarCelda(byte[] x, int offset) {
		return ((int) (x[offset]) & 0xff) | ((((int) (x[offset + 1]) & 0xff)) << 8)
				| ((((int) (x[offset + 2]) & 0xff)) << 16) | ((((int) (x[offset + 3]) & 0xff)) << 24);
	}
}
