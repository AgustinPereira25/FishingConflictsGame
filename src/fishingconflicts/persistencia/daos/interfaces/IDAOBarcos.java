package fishingconflicts.persistencia.daos.interfaces;

import java.util.ArrayList;

import fishingconflicts.excepciones.PersistenciaException;
import fishingconflicts.logica.interfaces.IConexion;
import fishingconflicts.logica.modelos.Barco;

public interface IDAOBarcos {
	
	boolean existe(int idBarco, IConexion icon) throws PersistenciaException;
	
	void insertar(int idPartida, Barco barco, IConexion icon) throws PersistenciaException;
	
	Barco buscar(int idBarco, IConexion icon) throws PersistenciaException;
	
	ArrayList<Barco> listarBarcosPorPartida(int idPartida, IConexion icon) throws PersistenciaException;
	
	void eliminar(int idBarco, IConexion icon) throws PersistenciaException;
}
