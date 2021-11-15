package fishingconflicts.logica.interfaces;

import java.sql.Connection;

public interface IConexion {

	Connection getConnection();
	
	boolean asignada();
	
	void asignar();
	
	void desasignar();
}
