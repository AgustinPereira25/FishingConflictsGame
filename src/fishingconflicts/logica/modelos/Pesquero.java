package fishingconflicts.logica.modelos;

import org.json.JSONObject;

public class Pesquero extends Barco {

	/**
	 * Vida.
	 */
	private int vida;
	
	/**
	 * Capacidad de carga.
	 */
	private int capacidadCarga;
	
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
	 * @param vida
	 * @param capacidadCarga
	 */
	public Pesquero(int id, double velocidad, double consumoCombustible, double posicionX, double posicionY,
			String subtipo, int vida, int capacidadCarga) {
		super(id, velocidad, consumoCombustible, posicionX, posicionY, "pesquero", subtipo);
		this.vida = vida;
		this.capacidadCarga = capacidadCarga;
	}

	/**
	 * @return the vida
	 */
	public int getVida() {
		return vida;
	}

	/**
	 * @return the capacidadCarga
	 */
	public int getCapacidadCarga() {
		return capacidadCarga;
	}

	/**
	 * @param vida the vida to set
	 */
	public void setVida(int vida) {
		this.vida = vida;
	}
	
	/**
	 * Convierte el barco a un objeto JSON.
	 * 
	 * @return JSONObject
	 */
	public JSONObject toJson() {
		JSONObject barco = this.toJsonBarco();
		barco.put("vida", vida);
		barco.put("capacidadCarga", capacidadCarga);
		
		return barco;
	}
}
