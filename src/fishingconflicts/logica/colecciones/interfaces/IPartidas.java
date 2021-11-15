package fishingconflicts.logica.colecciones.interfaces;

import fishingconflicts.logica.modelos.Partida;

public interface IPartidas {
	
	void insertar(Partida partida);
	
	boolean existe(int idPartida);
	
	Partida obtener(int idPartida);
	
	int obtenerNuevoId();
	
	void actualizarNuevoId();
	
	void eliminar(int idPartida);
}
