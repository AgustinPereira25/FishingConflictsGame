package fishingconflicts.logica.modelos;

import org.json.JSONObject;

public abstract class Barco {

	/**
	 * Identificador.
	 */
	protected int id;
	
	/**
	 * Velocidad.
	 */
	protected double velocidad;
	
	/**
	 * Consumo de combustible por minuto.
	 */
	protected double consumoCombustible;
	
	/**
	 * Posición en el eje X.
	 */
	protected double posicionX;
	
	/**
	 * Posición en el eje Y.
	 */
	protected double posicionY;
	
	/**
	 * Tipo de embarcación.
	 */
	protected String tipo;
	
	/**
	 * Subtipo de embarcación.
	 */
	protected String subtipo;

	/**
	 * Si el barco está en uso.
	 */
	protected boolean enUso;
	
	/**
	 * Ángulo de rotación.
	 */
	protected int angulo;

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param velocidad
	 * @param consumoCombustible
	 * @param posicionX
	 * @param posicionY
	 * @param tipo
	 * @param subtipo
	 */
	protected Barco(int id, double velocidad, double consumoCombustible, double posicionX, double posicionY, String tipo,
			String subtipo) {
		this.id = id;
		this.velocidad = velocidad;
		this.consumoCombustible = consumoCombustible;
		this.posicionX = posicionX;
		this.posicionY = posicionY;
		this.tipo = tipo;
		this.subtipo = subtipo;
		this.enUso = false;
		this.angulo = 0; 
	}
	
	/**
	 * Mueve el barco en la dirección dada.
	 * 
	 * @param direccion
	 */
	public void mover(String direccion, int angulo) {
		switch(direccion) {
			case "arriba":
				posicionY -= velocidad;
				break;
				
			case "abajo":
				posicionY += velocidad;
				break;
				
			case "derecha":
				posicionX += velocidad;
				break;
				
			case "izquierda":
				posicionX -= velocidad;
				break;
		}
		
		this.angulo = angulo;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the velocidad
	 */
	public double getVelocidad() {
		return velocidad;
	}

	/**
	 * @return the consumoCombustible
	 */
	public double getConsumoCombustible() {
		return consumoCombustible;
	}

	/**
	 * @return the posicionX
	 */
	public double getPosicionX() {
		return posicionX;
	}

	/**
	 * @return the posicionY
	 */
	public double getPosicionY() {
		return posicionY;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}
	
	/**
	 * @return the sbutipo
	 */
	public String getSubtipo() {
		return subtipo;
	}
	
	/**
	 * @return the subtipo
	 */
	public boolean isEnUso() {
		return enUso;
	}

	/**
	 * @param enUso the enUso to set
	 */
	public void setEnUso(boolean enUso) {
		this.enUso = enUso;
	}
	
	/**
	 * Convierte el barco en un objeto JSON.
	 * 
	 * @return JSONObject
	 */
	protected JSONObject toJsonBarco() {
		JSONObject barco = new JSONObject();
		barco.put("id", id);
		barco.put("velocidad", velocidad);
		barco.put("consumoCombustible", consumoCombustible);
		barco.put("posicionX", posicionX);
		barco.put("posicionY", posicionY);
		barco.put("tipo", tipo);
		barco.put("subtipo", subtipo);
		barco.put("angulo", angulo);
		
		return barco;
	}
}
