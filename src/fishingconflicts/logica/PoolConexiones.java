package fishingconflicts.logica;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import fishingconflicts.excepciones.LogicaException;
import fishingconflicts.logica.interfaces.IConexion;
import fishingconflicts.logica.interfaces.IPoolConexiones;

public class PoolConexiones implements IPoolConexiones {

	/**
	 * Driver.
	 */
	private String driver;
	
	/**
	 * URL de conexión.
	 */
	private String url;
	
	/**
	 * Usuario.
	 */
	private String usuario;
	
	/**
	 * Contraseña.
	 */
	private String contrasenia;
	
	/**
	 * Lista de conexiones.
	 */
	private ArrayList<IConexion> conexiones;
	
	/**
	 * Instancia.
	 */
	private static PoolConexiones instancia;
	
	/**
	 * Devuelve la instancia del pool.
	 * 
	 * @return PoolConexiones
	 */
	public static IPoolConexiones getInstancia() throws LogicaException {
		if (!(instancia instanceof PoolConexiones))
			instancia = new PoolConexiones();
		
		return instancia;
	}
	
	/**
	 * Constructor.
	 */
	private PoolConexiones() throws LogicaException {
		Properties p = new Properties();
		
		try {
			p.load(this.getClass().getResourceAsStream("config/sql.properties"));
		} catch(Exception e) {
			throw new LogicaException("Error: " + e.getMessage());
		}

		driver = p.getProperty("driver");
		url = p.getProperty("url");
		usuario = p.getProperty("usuario");
		contrasenia = p.getProperty("contrasenia");
		conexiones = new ArrayList<IConexion>();

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new LogicaException("Error: " + e.getMessage());
		}
	}
	
	/**
	 * Obtiene una nueva conexión.
	 */
	@Override
	public synchronized IConexion obtenerConexion() throws LogicaException {
		for(IConexion c : conexiones) {
			if (!c.asignada()) {
				c.asignar();
				return c;
			}
		}
		
		IConexion c = null;
		try {
			c = new Conexion(DriverManager.getConnection(url, usuario, contrasenia));
			c.getConnection().setAutoCommit(false);
		} catch (SQLException e) {
			throw new LogicaException("Error: " + e.getMessage());
		}
		
		conexiones.add(c);		
		return c;
	}

	/**
	 * Devuelve una conexión al pool.
	 * 
	 * @param conexion
	 * @param ok
	 */
	@Override
	public synchronized void liberarConexion(IConexion conexion, boolean ok) throws LogicaException {
		try {
			if (ok)
				conexion.getConnection().commit();
			else
				conexion.getConnection().rollback();
		} catch (Exception e) {
			throw new LogicaException("Error: " + e.getMessage());
		}
	}

}
