package fishingconflicts.persistencia;

public class Consultas {
	
	public static String crearBD() {
		return	"CREATE TABLE IF NOT EXISTS partidas (" + 
				"  id int NOT NULL," + 
				"  stock_combustible_patrullas double NOT NULL," + 
				"  stock_combustible_pesqueros double NOT NULL," + 
				"  stock_peces double NOT NULL," + 
				"  estado int NOT NULL," + 
				"  cantidad_patrullas int NOT NULL," + 
				"  cantidad_pesqueros int NOT NULL," + 
				"  cantidad_pesqueros_eliminados int NOT NULL," + 
				"  equipo_ganador text NOT NULL," + 
				"  PRIMARY KEY (id)" + 
				");" + 
				"CREATE TABLE IF NOT EXISTS barcos (" + 
				"  id int NOT NULL," + 
				"  velocidad double NOT NULL," + 
				"  consumo_combustible double NOT NULL," + 
				"  posicion_x double NOT NULL," + 
				"  posicion_y double NOT NULL," + 
				"  tipo text NOT NULL," + 
				"  subtipo text NOT NULL," + 
				"  id_partida int NOT NULL," + 
				"  PRIMARY KEY (id), FOREIGN KEY (id_partida) REFERENCES partidas(id)" + 
				");" + 
				"CREATE TABLE IF NOT EXISTS patrullas (" + 
				"  id int NOT NULL," + 
				"  alcance int NOT NULL," + 
				"  danio_base int NOT NULL," + 
				"  helicoptero_utilizado tinyint NOT NULL," + 
				"  PRIMARY KEY (id)" + 
				");" + 
				"CREATE TABLE IF NOT EXISTS pesqueros (" + 
				"  id int(8) NOT NULL," + 
				"  vida int(8) NOT NULL," + 
				"  capacidad_carga int(8) NOT NULL," + 
				"  PRIMARY KEY (id)" + 
				");";
	}
	
	/*****************************		
	 * 							 *
	 * 			PARTIDAS		 *
	 * 							 *
	 *****************************
	 */
	
	/**
	 * Consulta para insertar una nueva partida.
	 * 
	 * @return String
	 */
	public static String insertarPartida() {
		return "INSERT INTO partidas VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}
	
	/**
	 * Consulta que devuelve la información de una partida.
	 * 
	 * @return String
	 */
	public static String obtenerPartida() {
		return "SELECT * FROM partidas WHERE id = ?";
	}
	
	/**
	 * Consulta para eliminar una partida.
	 * 
	 * @return String
	 */
	public static String eliminarPartida() {
		return "DELETE FROM partidas WHERE id = ?";
	}
	
	/**
	 * Consulta para obtener el máximo id de partida.
	 * 
	 * @return String
	 */
	public static String maximoIdPartida() {
		return "SELECT MAX(id) AS maximoId FROM partidas";
	}
	
	/*****************************		
	 * 							 *
	 * 			BARCOS			 *
	 * 							 *
	 *****************************
	 */
	
	/**
	 * Consulta para insertar un nuevo barco.
	 * 
	 * @return String
	 */
	public static String insertarBarco() {
		return "INSERT INTO barcos VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	}
	
	/**
	 * Consulta que devuelve la información de un Barco.
	 * 
	 * @return String
	 */
	public static String obtenerBarco() {
		return "SELECT * FROM barcos WHERE id = ?";
	}
	
	/**
	 * Consulta que devuelve los barcos de una partida.
	 * 
	 * @return String
	 */
	public static String obtenerBarcosDePartida() {
		return "SELECT * FROM barcos WHERE id_partida = ?";
	}
	
	/**
	 * Consulta para eliminar un barco.
	 * 
	 * @return String
	 */
	public static String eliminarBarco() {
		return "DELETE FROM barcos WHERE id = ?";
	}
	
	/*****************************		
	 * 							 *
	 * 			PATRULLAS		 *
	 * 							 *
	 *****************************
	 */
	/**
	 * Consulta para insertar una nueva patrulla.
	 * 
	 * @return String
	 */
	public static String insertarPatrulla() {
		return "INSERT INTO patrullas VALUES (?, ?, ?, ?)";
	}	

	/**
	 * Consulta para obtener una patrulla.
	 * 
	 * @return String
	 */
	public static String obtenerPatrulla() {
		return "SELECT * from patrullas WHERE id = ?";
	}
	
	/**
	 * Consulta para eliminar una patrulla.
	 * 
	 * @return String
	 */
	public static String eliminarPatrulla() {
		return "DELETE FROM patrullas WHERE id = ?";
	}
	
	/*****************************		
	 * 							 *
	 * 			PESQUEROS		 *
	 * 							 *
	 *****************************
	 */	
	
	/**
	 * Consulta para insertar un nuevo pesquero.
	 * 
	 * @return String
	 */
	public static String insertarPesquero() {
		return "INSERT INTO pesqueros VALUES (?, ?, ?)";
	}	
	
	/**
	 * Consulta para obtener un pesquero.
	 * 
	 * @return String
	 */
	public static String obtenerPesquero() {
		return "SELECT * from pesqueros WHERE id = ?";
	}	
	
	/**
	 * Consulta para eliminar un pesquero.
	 * 
	 * @return String
	 */
	public static String eliminarPesquero() {
		return "DELETE FROM pesqueros WHERE id = ?";
	}
}
