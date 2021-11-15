package fishingconflicts.logica.modelos;

import java.util.ArrayList;

import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import fishingconflicts.logica.colecciones.Barcos;
import fishingconflicts.logica.colecciones.interfaces.IBarcos;

public class Partida {

	/**
	 * Id de la partida.
	 */
	private int id;
	
	/**
	 * Stock de combustible de las patrullas.
	 */
	private double stockCombustiblePatrullas;
	
	/**
	 * Stock de combustible de los pesqueros.
	 */
	private double stockCombustiblePesqueros;
	
	/**
	 * Stock de peces
	 */
	private double stockPeces;
	
	/**
	 * Estado de la partida (Iniciada, en espera, finalizada)
	 */
	private int estado;
	
	/**
	 * Cantidad de patrullas.
	 */
	private int cantidadPatrullas;
	
	/**
	 * Cantidad de pesqueros.
	 */
	private int cantidadPesqueros;
	
	/**
	 * Cantidad de pesqueros eliminados.
	 */
	private int cantidadPesquerosEliminados;
	
	/**
	 * Barcos de la partida.
	 */
	private IBarcos barcos;
	
	/**
	 * Sesiones conectadas.
	 */
	private ArrayList<Session> sesiones;
	
	/**
	 * Equipo ganador.
	 */
	private String equipoGanador;

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param stockCombustiblePatrullas
	 * @param stockCombustiblePesqueros
	 * @param stockPeces
	 * @param estado
	 * @param cantidadPatrullas
	 * @param cantidadPesqueros

	 */
	public Partida(int id, double stockCombustiblePatrullas, double stockCombustiblePesqueros, double stockPeces, int estado, int cantidadPatrullas,
			int cantidadPesqueros, int cantidadPesquerosEliminados, String equipoGanador) {
		this.id = id;
		this.stockCombustiblePatrullas = stockCombustiblePatrullas;
		this.stockCombustiblePesqueros = stockCombustiblePesqueros;
		this.stockPeces = stockPeces;
		this.estado = estado;
		this.cantidadPatrullas = cantidadPatrullas;
		this.cantidadPesqueros = cantidadPesqueros;
		this.barcos = new Barcos(); 
		this.sesiones = new ArrayList<Session>();
		this.equipoGanador = equipoGanador;
		this.cantidadPesquerosEliminados = cantidadPesquerosEliminados;
	}
	
	/**
	 * @return the cantidadPesquerosEliminados
	 */
	public int getCantidadPesquerosEliminados() {
		return cantidadPesquerosEliminados;
	}

	/**
	 * @return the equipoGanador
	 */
	public String getEquipoGanador() {
		return equipoGanador;
	}

	/**
	 * @param equipoGanador the equipoGanador to set
	 */
	public void setEquipoGanador(String equipoGanador) {
		this.equipoGanador = equipoGanador;
	}

	/**
	 * Conecta una nueva sesión a la partida.
	 * 
	 * @param sesion
	 */
	public void insertarSesion(Session sesion) {
		sesiones.add(sesion);
	}
	
	/**
	 * Elimina una sesión.
	 * 
	 * @param sesion
	 */
	public void eliminarSesion(Session sesion) {
		sesiones.remove(sesiones.indexOf(sesion));
	}
	
	/**
	 * Devuelve las sesiones conectadas a la partida.
	 * 
	 * @return ArrayList<Session>
	 */
	public ArrayList<Session> getSesiones() {
		return sesiones;
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
	 * @return the stockCombustiblePatrullas
	 */
	public double getStockCombustiblePatrullas() {
		return stockCombustiblePatrullas;
	}

	/**
	 * @param stockCombustiblePatrullas the stockCombustiblePatrullas to set
	 */
	public void setStockCombustiblePatrullas(double stockCombustiblePatrullas) {
		this.stockCombustiblePatrullas = stockCombustiblePatrullas;
	}

	/**
	 * @return the stockCombustiblePesqueros
	 */
	public double getStockCombustiblePesqueros() {
		return stockCombustiblePesqueros;
	}

	/**
	 * @param stockCombustiblePesqueros the stockCombustiblePesqueros to set
	 */
	public void setStockCombustiblePesqueros(double stockCombustiblePesqueros) {
		this.stockCombustiblePesqueros = stockCombustiblePesqueros;
	}

	/**
	 * @param stockPeces the stockPeces to set
	 */
	public void setStockPeces(double stockPeces) {
		this.stockPeces = stockPeces;
	}
	
	/**
	 * @param estado the estado to set
	 */
	public void estado(int estado) {
		this.estado = estado;
	}
	
	
	/**
	 * @return the sotckPeces
	 */
	public double getStockPeces() {
		return stockPeces;
	}
	
	/**
	 * @return the estado
	 */
	public int getEstado() {
		return estado;
	}
	
	/**
	 * @return the barcos
	 */
	public IBarcos getBarcos() {
		return barcos;
	}

	/**
	 * @return the cantidadPatrullas
	 */
	public int getCantidadPatrullas() {
		return cantidadPatrullas;
	}

	/**
	 * @return the cantidadPesqueros
	 */
	public int getCantidadPesqueros() {
		return cantidadPesqueros;
	}
	
	/**
	 * @param estado the estado to set
	 */
	public void setEstado(int estado) {
		this.estado = estado;
	}
	
	/**
	 * Aumenta en 1 el contador de pesqueros eliminados.
	 */
	public void eliminarPesquero() {
		this.cantidadPesquerosEliminados++;
	}
	
	/**
	 * Chequea si hay un ganador para la partida.
	 */
	public void chequearSiHayGanador() {
		if (stockCombustiblePatrullas <= 0 || stockPeces <= 0) {
			estado = 3;
			equipoGanador = "Pesqueros";
		}
		
		if (stockCombustiblePesqueros <= 0 || cantidadPesquerosEliminados == cantidadPesqueros) {
			estado = 3;
			equipoGanador = "Patrullas";
		}
	}

	/**
	 * Convierte la partida en un Json.
	 * 
	 * @return JSONObject
	 */
	public JSONObject toJson() {
		JSONObject partida = new JSONObject();
		
		partida.put("id", id);
		partida.put("stockCombustiblePatrullas", stockCombustiblePatrullas);
		partida.put("stockCombustiblePesqueros", stockCombustiblePesqueros);
		partida.put("stockPeces", stockPeces);
		partida.put("estado", estado);
		partida.put("cantidadPatrullas", cantidadPatrullas);
		partida.put("cantidadPesqueros", cantidadPesqueros);
		partida.put("equipoGanador", equipoGanador);
		
		JSONArray listaBarcos = new JSONArray();
		
		for(Barco barco : barcos.listarBarcosEnUso()) {
			if (barco.tipo.equalsIgnoreCase("patrulla"))
				listaBarcos.put(((Patrulla) barco).toJson());
			else
				listaBarcos.put(((Pesquero) barco).toJson());
		}
		
		partida.put("barcos", listaBarcos);
		
		return partida;
	}
	
	/**
	 * Devuelve la cantidad de barcos creados para el tipo y subtipo dados.
	 * 
	 * @param tipo
	 * @param subtipo
	 * @return int
	 */
	public int getCantidadBarcosCreados(String tipo) {
		int total = 0;
		
		for(Barco b : barcos.listarBarcos()) {
			if (b.getTipo().equalsIgnoreCase(tipo))
				total++;
		}
		
		return total;
	}
}
