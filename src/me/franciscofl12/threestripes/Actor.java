package me.franciscofl12.threestripes;

import java.awt.Graphics;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class Actor {

	// Propiedades protegidas (visibles en la propia clase y en los subtipos) de
	// cada actor
	protected int x , y; // Coordenadas x e y del actor
	protected String img; // Imagen del actor
	protected List<BufferedImage> sprites = new ArrayList<BufferedImage>(); // Lista de archivos de imagen utilizado para representarse en pantalla
	protected BufferedImage spriteActual = null;
	protected int velocidadDeCambioDeSprite = 0;  // Esta propiedad indica cada cuantas "unidades de tiempo" debemos mostrar el siguiente sprite del actor
	

	/**
	 * Constructor sin parámetros de entrada
	 */
	public Actor() {
	}

	/**
	 * Constructor con parámetros de entrada
	 * 
	 * @param x
	 * @param y
	 * @param img
	 */
	public Actor(int x, int y, String img) {
		this.x = x;
		this.y = y;
		this.img = img;
	}
	
	/**
	 * Constructor usado cuando el actor solo tiene un unico sprite
	 * @param spriteName
	 */
	public Actor (String spriteName) {
		this.velocidadDeCambioDeSprite = 1;
		cargarImagenesDesdeSpriteNames(new String[] {spriteName});
	}
	
	/**
	 * Constructor ampliamente utilizado, indicando los nombres de los sprites a utilizar para mostrar este actor
	 * @param spriteName
	 */
	public Actor (String spriteNames[]) {
		this.velocidadDeCambioDeSprite = 1;
		cargarImagenesDesdeSpriteNames(spriteNames);
	}
	
	public List<BufferedImage> getSprites() {
		return sprites;
	}

	public void setSprites(List<BufferedImage> sprites) {
		this.sprites = sprites;
	}

	public void paintImagen(Graphics2D g){
		g.drawImage(this.spriteActual, this.x, this.y, null);
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	// Getters y setters

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the img
	 */
	public String getImg() {
		return img;
	}

	/**
	 * @param img the img to set
	 */
	public void setImg(String img) {
		this.img = img;
	}
	
	/**
	 * A partir de un array de String, cargamos en memoria la lista de imagenes que constituyen los sprites del actor
	 * @param spriteNames
	 */
	private void cargarImagenesDesdeSpriteNames(String spriteNames[]) {
		// Obtengo las imagenes de este actor, a partir del patron de diseno Singleton con el que se encuentra
		// el ArkanoidSprite
		for (String sprite : spriteNames) {
			this.sprites.add(GameSprite.getInstance().getSprite(sprite));
		}
		// ajusto el primer sprite del actor
		if (this.sprites.size() > 0) {
			this.spriteActual = this.sprites.get(0);
		}
	}
	
}
