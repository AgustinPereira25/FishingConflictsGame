package fishingconflicts.logica.colecciones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import fishingconflicts.logica.colecciones.interfaces.IBarcos;
import fishingconflicts.logica.modelos.Barco;
import fishingconflicts.logica.modelos.Patrulla;
import fishingconflicts.logica.modelos.Pesquero;

public class Barcos implements IBarcos {

	/**
	 * Colección.
	 */
	private HashMap<Integer, Barco> barcos;
	
	/**
	 * Cantidad de barcos.
	 */
	private int cantidadBarcos;

	/**
	 * Constructor. 
	 */
	public Barcos() {
		cantidadBarcos = 0;
		barcos = new HashMap<Integer, Barco>();
	}

	/**
	 * Inserta un barco a la colección.
	 * 
	 * @param barco
	 */	
	@Override
	public void insertar(Barco barco) {
		barcos.put(barco.getId(), barco);
	}

	/**
	 * Verifica si el barco con el id dado existe.
	 * 
	 * @param idBarco
	 * @return boolean
	 */
	@Override
	public boolean existe(int idBarco) {
		return barcos.containsKey(idBarco);
	}

	/**
	 * Obtiene el barco con el id dado.
	 * 
	 * @param idBarco
	 * @return boolean
	 */
	@Override
	public Barco obtener(int idBarco) {
		return barcos.get(idBarco);
	}

	/**
	 * Devuelve la cantidad de patrullas asignadas.
	 * 
	 * @return int
	 */
	@Override
	public int cantidadPatrullasEnUso() {
		int cantidad = 0;
		
		for(Barco barco : barcos.values())
			cantidad += (barco.getTipo().equalsIgnoreCase("patrulla") && barco.isEnUso()) ? 1 : 0; 
		
		return cantidad;
	}

	/**
	 * Devuelve la cantidad de pesqueros asignados.
	 * 
	 * @return int
	 */
	@Override
	public int cantidadPesquerosEnUso() {
		int cantidad = 0;
		
		for(Barco barco : barcos.values())
			cantidad += (barco.getTipo().equalsIgnoreCase("pesquero") && barco.isEnUso()) ? 1 : 0;
		
		return cantidad;
	}

	/**
	 * Asigna o crea el barco del tipo y subtipo dado.
	 * 
	 * @param tipoBarco
	 * @param subtipoBarco
	 */
	@Override
	public int asignarOCrear(String tipoBarco, String subtipoBarco, int lugaresDisponibles) {		
		for(Barco barco : barcos.values()) {
			if (barco.getTipo().equalsIgnoreCase(tipoBarco) && barco.getSubtipo().equalsIgnoreCase(subtipoBarco) &&
				!barco.isEnUso()) {
				barco.setEnUso(true);
				return barco.getId();
			}
		}
		
		// Si ya están todos los barcos creados, y no hay barcos de ese tipo, devolvemos id 0, y en la fachada
		// avisamos al usuario que no hay barcos de ese tipo disponibles.
		if (lugaresDisponibles == 0)
			return 0;
		
		// Si llegamos a este punto, es porque vamos necesitamos crear un
		// barco nuevo (para una partida recién creada).
		Barco barco = null;
		
		if (tipoBarco.equalsIgnoreCase("patrulla")) {
			int x = ThreadLocalRandom.current().nextInt(13, 1230);
			int y = ThreadLocalRandom.current().nextInt(120, 356);
			
			if (subtipoBarco.equalsIgnoreCase("pesada")) {				
				// Patrulla pesada.
				barco = new Patrulla(this.obtenerNuevoId(), 0.3, 2, x, y, subtipoBarco, 130, 2, false);
			} else {
				// Patrulla ligera.
				barco = new Patrulla(this.obtenerNuevoId(), 0.6, 1, x, y, subtipoBarco, 65, 2, false);
			}
		} else {
			int x = ThreadLocalRandom.current().nextInt(13, 1230);
			int y = ThreadLocalRandom.current().nextInt(424, 540);
			
			if (subtipoBarco.equalsIgnoreCase("fabrica")) {
				// Pesquero fábrica.
				barco = new Pesquero(this.obtenerNuevoId(), 0.35, 2, x, y, subtipoBarco, 100, 5000);
			} else {
				// Pesquero convencional.
				barco = new Pesquero(this.obtenerNuevoId(), 0.7, 2, x, y, subtipoBarco, 100, 2500);
			}
		}
		
		barco.setEnUso(true);
		insertar(barco);
		
		return barco.getId();
	}

	/**
	 * Devuelve un id para generar un barco.
	 * 
	 * @return int
	 */
	@Override
	public int obtenerNuevoId() {
		cantidadBarcos++;
		
		return cantidadBarcos;
	}
	
	/**
	 * Actualiza el id.
	 */
	@Override
	public void actualizarNuevoId() {
		if (!barcos.isEmpty())
			cantidadBarcos = Collections.max(barcos.keySet());
	}

	/**
	 * Devuelve una lista de barcos en uso.
	 * 
	 * @return ArrayList<Barco>
	 */
	@Override
	public ArrayList<Barco> listarBarcosEnUso() {
		ArrayList<Barco> lista = new ArrayList<Barco>();
		
		for(Barco barco : barcos.values()) {
			if (barco.isEnUso())
				lista.add(barco);
		}
		
		return lista;
	}
	
	/**
	 * Devuelve una lista de barcos.
	 * 
	 * @return ArrayList<Barco>
	 */
	@Override
	public ArrayList<Barco> listarBarcos() {
		ArrayList<Barco> lista = new ArrayList<Barco>();
		
		for(Barco barco : barcos.values()) {
			lista.add(barco);
		}
		
		return lista;
	}


	/**
	 * Elimina un barco de la colección.
	 * 
	 * @param idBarco
	 */
	@Override
	public void eliminar(int idBarco) {
		barcos.remove(idBarco);
	}
}
