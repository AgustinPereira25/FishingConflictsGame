package fishingconflicts.logica.colecciones;

import java.util.Collections;
import java.util.HashMap;

import fishingconflicts.logica.colecciones.interfaces.IPartidas;
import fishingconflicts.logica.modelos.Partida;

public class Partidas implements IPartidas {
	
	/**
	 * Colección.
	 */
	private HashMap<Integer, Partida> partidas;
	
	/**
	 * Cantidad de partidas.
	 */
	private int cantidadPartidas;
	
	/**
	 * Constructor. 
	 */
	public Partidas(int cantidadPartidas) {
		this.cantidadPartidas = cantidadPartidas;
		partidas = new HashMap<Integer, Partida>();
	}

	/**
	 * Inserta una partida a la colección.
	 * 
	 * @param partida
	 */
	@Override
	public void insertar(Partida partida) {
		partidas.put(partida.getId(), partida);
	}
	
	/**
	 * Actualiza el id.
	 */
	@Override
	public void actualizarNuevoId() {
		cantidadPartidas = Collections.max(partidas.keySet());
	}

	/**
	 * Verifica si la partida con el id dado existe.
	 * 
	 * @param idPartida
	 * @return boolean
	 */
	@Override
	public boolean existe(int idPartida) {
		return partidas.containsKey(idPartida);
	}

	/**
	 * Obtiene la partida con el id dado.
	 * 
	 * @param idPartida
	 * @return boolean
	 */
	@Override
	public Partida obtener(int idPartida) {
		return partidas.get(idPartida);
	}

	/**
	 * Devuelve un id para generar una partida.
	 * 
	 * @return int
	 */
	@Override
	public int obtenerNuevoId() {
		cantidadPartidas++;
		
		return cantidadPartidas;
	}

	/**
	 * Elimina la partida.
	 * 
	 * @param idPartida
	 */
	@Override
	public void eliminar(int idPartida) {
		partidas.remove(idPartida);
	}
}
