package me.franciscofl12.threestripes;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Game extends Canvas{
	
	private static Game instance = null;
	private static JFrame ventana = null;
	private static List<Actor> actores = new ArrayList<Actor>();
	static int[][] tablero = new int[3][3];
	static final int FPS = 60;
	static int turno = 1;
	static int contador = 0;
	
	// Creo un doble buffer que lo utilizare para que no muestre lagazos a la hora de procesar los ladrillos
	public BufferStrategy strategy;
	
	//Patron singelton
	public static Game getInstance () {
		if (instance == null) { // Si no está inicializada, se inicializa
			instance = new Game();
		}
		return instance;
	}
	
	public Game() {
		ventana = new JFrame("3-Stripes by franciscofl12");
		ventana.setIconImage(GameSprite.getInstance().getSprite("tableroicono.png"));
		ventana.setBounds(0, 0, 600, 600);
		// Para colocar objetos sobre la ventana debo asignarle un "layout" (plantilla) al panel principal de la ventana
		ventana.getContentPane().setLayout(new BorderLayout());
		// Creo una lista de actores que intervendrá en el juego.
		actores = creaTablero();	
		// Creo y agrego un canvas, es un objeto que permitirá dibujar sobre él
		
		ventana.getContentPane().add(this, BorderLayout.CENTER);
		// Consigo que la ventana no se redibuje por los eventos de Windows
		ventana.setIgnoreRepaint(true);
		ventana.requestFocus();
		// Hago que la ventana sea visible
		ventana.setVisible(true);
		//Hago que la ventana no se pueda reescalar, por lo que no me movera los objetos que yo cree
		ventana.setResizable(false);
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		//Añado un MouseListener para captar cuando el usuario hace click
		this.addMouseListener(new MouseAdapter() {
			@Override
		    public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				rellenarTablero(turno, e.getX(), e.getY());
				mostrarTablero();
			}
		});
	}
	
   
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		// Comienzo un bucle, que consistirá en el juego completo.
		Game.getInstance().juego();
	}
	
	/**
	 * Al cerrar la aplicación preguntaremos al usuario si está seguro de que desea salir.
	 */
	private static void cerrarAplicacion() {
		String [] opciones ={"Aceptar","Cancelar"};
		int eleccion = JOptionPane.showOptionDialog(ventana,"¿Desea cerrar la aplicación?","Salir de la aplicación",
		JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE, null, opciones, "Aceptar");
		if (eleccion == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
	
	@SuppressWarnings("static-access")
	public void pintaMundo() {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		
		g.drawImage(GameSprite.getInstance().getSprite("tablero.png"),0,0,null); // Fondo
		
		strategy.show(); 
	}
	
	public void juego() {
		do {
		pintaMundo();
		} while (true);
	}
	
	public static List<Actor> creaTablero() {
		
		
		return actores;
	}
	
	public static void rellenarTablero(int jugador, int x, int y) {
		System.out.println(x);
		System.out.println(y);
		if (x<=200) {
			x = 0;
		}
		else {
			if (x<=400) {
				x = 1;
			}
			else {
				if (x<=600) {
					x = 2;
				}
			}
		}
		if (y<=200) {
			y = 0;
		}
		else {
			if (y<=400) {
				y = 1;
			}
			else {
				if (y<=600) {
					y = 2;
				}
			}
		}
		x = x - 1;
		y = y - 1;
		for (int i = 0; i < tablero.length; i++) {
			for (int j = 0; j < tablero[i].length; j++) {
				if (x == i && y == j) {
					if (tablero[i][j] == 0) {
						tablero[i][j] = jugador;
						if (jugador == 1) {
							turno = 2;
						}
						if (jugador == 2) {
							turno = 1;
						}
					}
					else JOptionPane.showMessageDialog(null,"Jugador " + jugador + " vuelve a elegir el jugador y las coordenadas. Esa posicion esta ya ocupada");
				}
			}
		}

	}
	
	public static void mostrarTablero() {
		System.out.println("\nTablero\n");
		System.out.println("   X");
		System.out.print("Y ");
		for (int i = 0; i < tablero.length; i++) {
			for (int j = 0; j < tablero[i].length; j++) {
				System.out.print(" " + tablero[i][j] + "   ");
			}
			System.out.println("");
			System.out.print("  ");
		}
	}
	
	
}
