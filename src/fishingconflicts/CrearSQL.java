package fishingconflicts;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import fishingconflicts.excepciones.LogicaException;
import fishingconflicts.excepciones.PersistenciaException;
import fishingconflicts.logica.Fachada;
import fishingconflicts.logica.PoolConexiones;
import fishingconflicts.logica.interfaces.IConexion;
import fishingconflicts.persistencia.Consultas;

public class CrearSQL {

	public static void main(String[] args) {
		Connection con = null;
		Properties prop = new Properties();
		
		try {
			// Cargamos el archivo de configuración del sistema y
			// el driver de conexión a la base de datos.
			prop.load(PoolConexiones.class.getResourceAsStream("config/sql.properties"));
			Class.forName(prop.getProperty("driver"));
			
			// Creamos la conexión y seteamos la configuración inicial.
			con = DriverManager.getConnection(prop.getProperty("url_conn"),
					prop.getProperty("usuario"), prop.getProperty("contrasenia"));
			con.setAutoCommit(false);
			con.setTransactionIsolation(con.TRANSACTION_SERIALIZABLE);
			
			// Creamos la base de datos
			System.out.println("1. Creando base de datos...");
			
			Statement stmt = con.createStatement();
			stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS fishing_conflicts;");
			stmt.executeQuery("USE fishing_conflicts;");
			System.out.println("Utilizando la base de datos fishing_conflicts");
			
			// Creamos las tablas
			System.out.println("2. Creando las tablas necesarias...");
			
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS partidas(" + 
					"  id int NOT NULL," + 
					"  stock_combustible_patrullas double NOT NULL," + 
					"  stock_combustible_pesqueros double NOT NULL," + 
					"  stock_peces double NOT NULL," + 
					"  estado int NOT NULL," + 
					"  cantidad_patrullas int NOT NULL," + 
					"  cantidad_pesqueros int NOT NULL," + 
					"  cantidad_pesqueros_eliminados int NOT NULL," + 
					"  equipo_ganador text NOT NULL," +
					"PRIMARY KEY (id));");
			System.out.println("Tabla partidas...");
			
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS barcos(" + 
					"  id int NOT NULL," + 
					"  velocidad double NOT NULL," + 
					"  consumo_combustible double NOT NULL," + 
					"  posicion_x double NOT NULL," + 
					"  posicion_y double NOT NULL," + 
					"  tipo text NOT NULL," + 
					"  subtipo text NOT NULL," + 
					"  id_partida int NOT NULL," +
					"PRIMARY KEY (id), FOREIGN KEY (id_partida) REFERENCES partidas(id));");
			System.out.println("Tabla barcos...");
			
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS patrullas(" + 
					"  id int NOT NULL," + 
					"  alcance int NOT NULL," + 
					"  danio_base int NOT NULL," + 
					"  helicoptero_utilizado tinyint NOT NULL," +
					"PRIMARY KEY (id));");
			System.out.println("Tabla patrullas...");
			
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS pesqueros(" + 
					"  id int(8) NOT NULL," + 
					"  vida int(8) NOT NULL," + 
					"  capacidad_carga int(8) NOT NULL," +
					"PRIMARY KEY (id));");
			System.out.println("Tabla pesqueros...");
			
			System.out.println("Proceso correcto.");
			
			stmt.close();
			con.commit();
		} catch (IOException | ClassNotFoundException | SQLException | NumberFormatException e) {
			if (con != null) {
				try {
					con.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
