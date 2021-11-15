package fishingconflicts.persistencia.daos.interfaces;

import fishingconflicts.excepciones.PersistenciaException;
import fishingconflicts.logica.interfaces.IConexion;
import fishingconflicts.logica.modelos.Partida;

public interface IDAOPartidas {
	
	boolean existe(int idPartida, IConexion icon) throws PersistenciaException;
	
	void insertar(Partida partida, IConexion icon) throws PersistenciaException;
	
	Partida buscar(int idPartida, IConexion icon) throws PersistenciaException;
	
	void eliminar(int idPartida, IConexion icon) throws PersistenciaException;
	
	int maximoId(IConexion icon) throws PersistenciaException;
}
