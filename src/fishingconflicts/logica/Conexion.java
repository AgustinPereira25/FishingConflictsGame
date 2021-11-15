package fishingconflicts.logica;

import java.sql.Connection;

import fishingconflicts.logica.interfaces.IConexion;

public class Conexion implements IConexion {

	/**
	 * Conexión.
	 */
	private Connection con;
	
	/**
	 * Está asignada?
	 */
	private boolean asignada;
	
	/**
	 * Constructor
	 * 
	 * @param c
	 */
	public Conexion(Connection c) {
		this.con = c;
		this.asignada = false;
	}
	
	/**
	 * Devuelve la conexión.
	 * 
	 * @return Connection
	 */
	@Override
	public Connection getConnection() {
		return con;
	}

	/**
	 * @return the asignada
	 */
	@Override
	public boolean asignada() {
		return asignada;
	}

	/**
	 * Asigna una conexión.
	 */
	@Override
	public void asignar() {
		asignada = true;
	}
	
	/**
	 * Desasigna una conexión.
	 */
	@Override
	public void desasignar() {
		asignada = false;
	}
}
