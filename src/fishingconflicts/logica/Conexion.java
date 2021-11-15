package fishingconflicts.logica;

import java.sql.Connection;

import fishingconflicts.logica.interfaces.IConexion;

public class Conexion implements IConexion {

	/**
	 * Conexi�n.
	 */
	private Connection con;
	
	/**
	 * Est� asignada?
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
	 * Devuelve la conexi�n.
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
	 * Asigna una conexi�n.
	 */
	@Override
	public void asignar() {
		asignada = true;
	}
	
	/**
	 * Desasigna una conexi�n.
	 */
	@Override
	public void desasignar() {
		asignada = false;
	}
}
