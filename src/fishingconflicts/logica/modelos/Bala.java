package fishingconflicts.logica.modelos;

import org.json.JSONObject;

public class Bala {

	/**
	 * Identificador.
	 */
	private int id;
	
	/**
	 * Posición X.
	 */
	private double posicionX;
	
	/**
	 * Posición Y.
	 */
	private double posicionY;
	
	/**
	 * Posición de destino X.
	 */
	private double posicionDestinoX;
	
	/**
	 * Posición de destino Y.
	 */
	private double posicionDestinoY;

	/**
	 * Tipo de arma que disparó la bala.
	 */
	private String tipoArma;
	
	/**
	 * Connstructor.
	 * 
	 * @param id
	 * @param posicionX
	 * @param posicionY
	 * @param posicionDestinoX
	 * @param posicionDestinoY
	 * @param tipoArma
	 */
	public Bala(int id, double posicionX, double posicionY, double posicionDestinoX, double posicionDestinoY, String tipoArma) {
		super();
		this.id = id;
		this.posicionX = posicionX;
		this.posicionY = posicionY;
		this.posicionDestinoX = posicionDestinoX;
		this.posicionDestinoY = posicionDestinoY;
		this.tipoArma = tipoArma;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the posicionX
	 */
	public double getPosicionX() {
		return posicionX;
	}

	/**
	 * @param posicionX the posicionX to set
	 */
	public void setPosicionX(double posicionX) {
		this.posicionX = posicionX;
	}

	/**
	 * @return the posicionY
	 */
	public double getPosicionY() {
		return posicionY;
	}

	/**
	 * @param posicionY the posicionY to set
	 */
	public void setPosicionY(double posicionY) {
		this.posicionY = posicionY;
	}

	/**
	 * @return the posicionDestinoX
	 */
	public double getPosicionDestinoX() {
		return posicionDestinoX;
	}

	/**
	 * @param posicionDestinoX the posicionDestinoX to set
	 */
	public void setPosicionDestinoX(double posicionDestinoX) {
		this.posicionDestinoX = posicionDestinoX;
	}

	/**
	 * @return the posicionDestinoY
	 */
	public double getPosicionDestinoY() {
		return posicionDestinoY;
	}

	/**
	 * @param posicionDestinoY the posicionDestinoY to set
	 */
	public void setPosicionDestinoY(double posicionDestinoY) {
		this.posicionDestinoY = posicionDestinoY;
	}

	/**
	 * @return the tipoArma
	 */
	public String getTipoArma() {
		return tipoArma;
	}
	
	/**
	 * Convierte la bala a JSON.
	 * 
	 * @return JSONObject
	 */
	public JSONObject toJson() {
		JSONObject r = new JSONObject();
		r.put("id", id);
		r.put("posicionX", posicionX);
		r.put("posicionY", posicionY);
		r.put("posicionDestinoX", posicionDestinoX);
		r.put("posicionDestinoY", posicionDestinoY);
		r.put("tipoArma", tipoArma);
		
		return r;
	}
}
