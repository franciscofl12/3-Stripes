package me.franciscofl12.threestripes;

import java.awt.BorderLayout;


import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Game extends Canvas{
	
	private static Game instance = null;
	private static JFrame ventana = null;
	static int[][] tablero = new int[3][3];
	static final int FPS = 60;
	private static int victoria = 0;
	static int turno = 1;
	static int contador = 0;
	static int posicionx = 0; // Variable para colocar la imagen
	static int posiciony = 0;
	private static boolean juegoTerminado = false;
	private static List<Actor> actores = new ArrayList<Actor>();
	private static List<Actor> actoresParaAgregar = new ArrayList<Actor>();
	
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
		ventana.setBounds(0, 0, 600, 650);
		// Para colocar objetos sobre la ventana debo asignarle un "layout" (plantilla) al panel principal de la ventana
		ventana.getContentPane().setLayout(new BorderLayout());
		// Creo y agrego un canvas, es un objeto que permitirá dibujar sobre él
		ventana.getContentPane().add(this, BorderLayout.CENTER);
		// Consigo que la ventana no se redibuje por los eventos de Windows
		ventana.setIgnoreRepaint(true);
		this.requestFocus();
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
				if (juegoTerminado== false ) {
					super.mousePressed(e);
					rellenarTablero(turno, e.getY(), e.getX());
				}
				//mostrarTablero();

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
	
	/****
	 * Pintamos el fondo del juego
	 */
	@SuppressWarnings("static-access")
	public void pintaMundo() {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		
		g.setColor(Color.white);
		g.fillRect(0, 0,this.getWidth(),this.getHeight()); 
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial",Font.BOLD,20));
		g.drawString("Turno del jugador " + turno, this.getWidth() / 2 - 100 , this.getHeight() - 20);
		
		g.drawImage(GameSprite.getInstance().getSprite("tablero.png"),0,0,null); // Fondo
		
		for (Actor a : this.actores) {
			a.paintImagen(g);
		}
		
		strategy.show(); 
	}
	
	/****
	 * Iniciamos el juego
	 */
	public void juego() {
		int millisPorCadaFrame = 1000 / FPS;
		do {
			// Redibujo la escena tantas veces por segundo como indique la variable FPS
			// Tomo los millis actuales
			long millisAntesDeProcesarEscena = new Date().getTime();
			// Redibujo la escena
			pintaMundo();
			
			agregarActores();
			
			if (victoria() != 0) {
				turno = victoria();
				juegoTerminado = true;
			}
			
			if (comprobarEmpate() == true) {
				juegoTerminado = true;
			}
			// Calculo los millis que debemos parar el proceso, generando 60 FPS.
			long millisDespuesDeProcesarEscena = new Date().getTime();
			int millisDeProcesamientoDeEscena = (int) (millisDespuesDeProcesarEscena - millisAntesDeProcesarEscena);
			int millisPausa = millisPorCadaFrame - millisDeProcesamientoDeEscena;
			millisPausa = (millisPausa < 0)? 0 : millisPausa;
			// "Duermo" el proceso principal durante los milllis calculados.
			try {
				Thread.sleep(millisPausa);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (juegoTerminado == false);
		pintaMundo();
		if (victoria() != 0) paintVictoria();
		if (comprobarEmpate() == true &&  victoria() == 0) paintEmpate();
	}

	public void agregarActores() {
		for (Actor a : this.actoresParaAgregar) {
			this.actores.add(a);
		}
		this.actoresParaAgregar.clear(); // Limpio la lista de actores a incorporar, ya están incorporados
		
	}
	
	public boolean comprobarEmpate() {
		int comprobador = 0;
		for (int i = 0; i < tablero.length; i++) {
			for (int j = 0; j < tablero[i].length; j++) {
					if (tablero[i][j] != 0) comprobador++;
			}
		}
		if (comprobador==9) return true;
		else return false;
	}

	public void paintVictoria() {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		Color miColor = new Color(0, 0, 0, 127);
		g.setColor(miColor);
		g.fillRect(0, 0,this.getWidth(),this.getHeight()); 
		g.setColor(Color.white);
		g.setFont(new Font("Arial",Font.BOLD,20));
		g.drawString("Fin. Ha ganado el jugador" + turno, this.getWidth() / 2 - 120 , this.getHeight() / 2);
		strategy.show();
	}
	
	public void paintEmpate() {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		Color miColor = new Color(0, 0, 0, 127);
		g.setColor(miColor);
		g.fillRect(0, 0,this.getWidth(),this.getHeight()); 
		g.setColor(Color.white);
		g.setFont(new Font("Arial",Font.BOLD,20));
		g.drawString("Fin. Empate táctico, y encima táctico.", this.getWidth() / 2 - 150 , this.getHeight() / 2);
		strategy.show();
	}
	
	public void rellenarTablero(int jugador, int x, int y) {
		//Comprobamos donde clickó el jugador
		if (x<=200) {
			x = 1;
		}
		else {
			if (x<=400) {
				x = 2;
			}
			else {
				if (x<=600) {
					x = 3;
				}
			}
		}
		if (y<=200) {
			y = 1;
		}
		else {
			if (y<=400) {
				y = 2;
			}
			else {
				if (y<=600) {
					y = 3;
				}
			}
		}
		x = x - 1;
		y = y - 1;
		//Agregamos la accion del jugador al tablero, o avisamos que no está disponible esa casilla
		for (int i = 0; i < tablero.length; i++) {
			for (int j = 0; j < tablero[i].length; j++) {
				if (y == j && x == i) {
					if (tablero[i][j] == 0) {
						tablero[i][j] = jugador;
						if (jugador == 1) {
							turno = 2;
						}
						if (jugador == 2) {
							turno = 1;
						}
						// Aquí obtendremos donde irán colocadas las imagenes
						if (y == 0) posicionx = 10; if (y == 1) posicionx = 205; if (y == 2) posicionx = 390; 
						if (x == 0) posiciony = 10; if (x == 1) posiciony = 200; if (x == 2) posiciony = 380;
						if (jugador == 1) {
							Cross equis = new Cross(posicionx,posiciony);
							equis.x = posicionx;
							equis.y = posiciony;
							contador++;
							actoresParaAgregar.add(equis);
						}
						if (jugador == 2) {
							Circle circulo = new Circle(posicionx,posiciony);
							circulo.x = posicionx;
							circulo.y = posiciony;
							contador++;
							actoresParaAgregar.add(circulo);
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
	
	public static int victoria() {

		if (tablero[0][0] == 1 && tablero[0][1] == 1 && tablero[0][2] == 1) victoria=1;
		if (tablero[1][0] == 1 && tablero[1][1] == 1 && tablero[1][2] == 1) victoria=1;
		if (tablero[2][0] == 1 && tablero[2][1] == 1 && tablero[2][2] == 1) victoria=1;
		if (tablero[0][0] == 2 && tablero[0][1] == 2 && tablero[0][2] == 2) victoria=2;
		if (tablero[1][0] == 2 && tablero[1][1] == 2 && tablero[1][2] == 2) victoria=2;
		if (tablero[2][0] == 2 && tablero[2][1] == 2 && tablero[2][2] == 2) victoria=2;
		if (tablero[0][0] == 1 && tablero[1][0] == 1 && tablero[2][0] == 1) victoria=1;
		if (tablero[0][1] == 1 && tablero[1][1] == 1 && tablero[2][1] == 1) victoria=1;
		if (tablero[0][2] == 1 && tablero[1][2] == 1 && tablero[2][2] == 1) victoria=1;
		if (tablero[0][0] == 2 && tablero[1][0] == 2 && tablero[2][0] == 2) victoria=2;
		if (tablero[0][1] == 2 && tablero[1][1] == 2 && tablero[2][1] == 2) victoria=2;
		if (tablero[0][2] == 2 && tablero[1][2] == 2 && tablero[2][2] == 2) victoria=2;
		if (tablero[0][0] == 1 && tablero[1][1] == 1 && tablero[2][2] == 1) victoria=1;
		if (tablero[0][0] == 2 && tablero[1][1] == 2 && tablero[2][2] == 2) victoria=2;
		if (tablero[0][2] == 1 && tablero[1][1] == 1 && tablero[2][0] == 1) victoria=1;
		if (tablero[0][2] == 2 && tablero[1][1] == 2 && tablero[2][0] == 2) victoria=2;


		return victoria;

	}
	
	
}
