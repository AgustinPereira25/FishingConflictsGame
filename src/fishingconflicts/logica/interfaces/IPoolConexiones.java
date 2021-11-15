package fishingconflicts.logica.interfaces;

import fishingconflicts.excepciones.LogicaException;
import fishingconflicts.excepciones.PersistenciaException;

public interface IPoolConexiones {

	IConexion obtenerConexion() throws LogicaException;
	
	void liberarConexion(IConexion conexion, boolean ok) throws LogicaException;
}
