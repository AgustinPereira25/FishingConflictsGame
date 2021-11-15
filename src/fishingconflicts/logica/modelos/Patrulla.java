package fishingconflicts.logica.modelos;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Patrulla extends Barco {

	/**
	 * Alcance del radar.
	 */
	private int alcance;
	
	/**
	 * Daño base.
	 */
	private int danioBase;
	
	/**
	 * Helicóptero activado.
	 */
	private boolean helicopteroActivado;
	
	/**
	 * Helicóptero ya utilizado.
	 */
	private boolean helicopteroUtilizado;
	
	/**
	 * Bote ligero activado.
	 */
	private boolean boteLigeroActivado;
	
	/**
	 * Posición X del bote ligero.
	 */
	private double boteLigeroPosicionX;
	
	/**
	 * Posición Y del bote ligero.
	 */
	private double boteLigeroPosicionY;
	
	/**
	 * Ángulo bote ligero.
	 */
	private int boteLigeroAngulo;
	
	/**
	 * Balas que disparó la patrulla.
	 */
	private ArrayList<Bala> balas;
	
	/**
	 * Cantidad de balas.
	 */
	private int cantidadBalas;
	
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
	 * @param alcance
	 * @param danioBase
	 */
	public Patrulla(int id, double velocidad, double consumoCombustible, double posicionX, double posicionY,
			String subtipo, int alcance, int danioBase, boolean helicopteroUtilizado) {
		super(id, velocidad, consumoCombustible, posicionX, posicionY, "patrulla", subtipo);
		this.alcance = alcance;
		this.danioBase = danioBase;
		this.helicopteroActivado = false;
		this.helicopteroUtilizado = helicopteroUtilizado;
		this.boteLigeroActivado = false;
		this.boteLigeroPosicionX = posicionX;
		this.boteLigeroPosicionY = posicionY;
		this.boteLigeroAngulo = 0;
		this.balas = new ArrayList<Bala>();
		this.cantidadBalas = 0;
	}

	/**
	 * @return the helicopteroActivado
	 */
	public boolean isHelicopteroActivado() {
		return helicopteroActivado;
	}

	/**
	 * @param helicopteroActivado the helicopteroActivado to set
	 */
	public void setHelicopteroActivado(boolean helicopteroActivado) {
		this.helicopteroActivado = helicopteroActivado;
	}

	/**
	 * @return the helicopteroUtilizado
	 */
	public boolean isHelicopteroUtilizado() {
		return helicopteroUtilizado;
	}

	/**
	 * @param helicopteroUtilizado the helicopteroUtilizado to set
	 */
	public void setHelicopteroUtilizado(boolean helicopteroUtilizado) {
		this.helicopteroUtilizado = helicopteroUtilizado;
	}

	/**
	 * @return the alcance
	 */
	public int getAlcance() {
		return helicopteroActivado ? (int) Math.ceil(alcance * 1.25) : alcance;
	}

	/**
	 * @return the danioBase
	 */
	public int getDanioBase() {
		return danioBase;
	}
	
	/**
	 * @return the boteLigeroActivado
	 */
	public boolean isBoteLigeroActivado() {
		return boteLigeroActivado;
	}

	/**
	 * @param boteLigeroActivado the boteLigeroActivado to set
	 */
	public void setBoteLigeroActivado(boolean boteLigeroActivado) {
		this.boteLigeroActivado = boteLigeroActivado;
	}

	/**
	 * @return the boteLigeroPosicionX
	 */
	public double getBoteLigeroPosicionX() {
		return boteLigeroPosicionX;
	}

	/**
	 * @param boteLigeroPosicionX the boteLigeroPosicionX to set
	 */
	public void setBoteLigeroPosicionX(double boteLigeroPosicionX) {
		this.boteLigeroPosicionX = boteLigeroPosicionX;
	}

	/**
	 * @return the boteLigeroPosicionY
	 */
	public double getBoteLigeroPosicionY() {
		return boteLigeroPosicionY;
	}

	/**
	 * @param boteLigeroPosicionY the boteLigeroPosicionY to set
	 */
	public void setBoteLigeroPosicionY(double boteLigeroPosicionY) {
		this.boteLigeroPosicionY = boteLigeroPosicionY;
	}
	
	/**
	 * @return the boteLigeroAngulo
	 */
	public int getBoteLigeroAngulo() {
		return boteLigeroAngulo;
	}

	/**
	 * @param boteLigeroAngulo the boteLigeroAngulo to set
	 */
	public void setBoteLigeroAngulo(int boteLigeroAngulo) {
		this.boteLigeroAngulo = boteLigeroAngulo;
	}

	/**
	 * Inserta una bala en la lista de balas.
	 * 
	 * @param Bala
	 */
	public void insertarBala(Bala bala) {
		balas.add(bala);
	}
	
	/**
	 * Elimina una bala.
	 * 
	 * @param idBala
	 */
	public void eliminarBala(int idBala) {
		int idx = 0;
		for(Bala b : balas) {
			if (b.getId() == idBala) {
				balas.remove(idx);
				break;
			}
			
			idx++;
		}
	}
	
	/**
	 * Devuelve un identificador para una nueva bala.
	 * 
	 * @return int
	 */
	public int obtenerNuevoIdBala() {
		return ++cantidadBalas;
	}

	/**
	 * @param idbala
	 * @return the balas
	 */
	public Bala getBala(int idBala) {
		for(Bala b : balas) {
			if (b.getId() == idBala)
				return b;
		}
		
		return null;
	}

	/**
	 * Convierte el barco a un objeto JSON.
	 * 
	 * @return JSONObject
	 */
	public JSONObject toJson() {
		JSONObject barco = this.toJsonBarco();
		barco.put("alcance", this.getAlcance());
		barco.put("danioBase", danioBase);
		barco.put("helicopteroActivado", helicopteroActivado);
		barco.put("helicopteroUtilizado", helicopteroUtilizado);
		barco.put("boteLigeroActivado", boteLigeroActivado);
		barco.put("boteLigeroPosicionX", boteLigeroPosicionX);
		barco.put("boteLigeroPosicionY", boteLigeroPosicionY);
		barco.put("boteLigeroAngulo", boteLigeroAngulo);
		
		JSONArray listaBalas = new JSONArray();
		for(Bala b : balas)
			listaBalas.put(b.toJson());
		
		barco.put("balas", listaBalas);
		
		return barco;
	}
}
