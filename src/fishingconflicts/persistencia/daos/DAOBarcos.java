package fishingconflicts.persistencia.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import fishingconflicts.excepciones.LogicaException;
import fishingconflicts.excepciones.PersistenciaException;
import fishingconflicts.logica.PoolConexiones;
import fishingconflicts.logica.interfaces.IConexion;
import fishingconflicts.logica.modelos.Barco;
import fishingconflicts.logica.modelos.Patrulla;
import fishingconflicts.logica.modelos.Pesquero;
import fishingconflicts.persistencia.Consultas;
import fishingconflicts.persistencia.daos.interfaces.IDAOBarcos;

public class DAOBarcos implements IDAOBarcos {
	
	/**
	 * Indica si un barco existe.
	 * @param idBarco
	 * @param icon
	 * @return boolean
	 */
	public boolean existe(int idBarco, IConexion icon) throws PersistenciaException{
		boolean existe = false;
		Connection con = icon.getConnection();
		
		try {
			PreparedStatement pstmt = con.prepareStatement(Consultas.obtenerBarco());
			pstmt.setInt(1, idBarco);
			ResultSet rs = pstmt.executeQuery();
			existe = rs.next();
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			throw new PersistenciaException("Error SQL: " + e.getMessage());
		}
		
		return existe;		
	}

	/**
	 * Inserta un barco en la BD, así como también inserta la patrulla o el pesquero en caso de ser necesario.
	 * 
	 * @param barco
	 * @param icon
	 */	
	public void insertar(int idPartida, Barco barco, IConexion icon) throws PersistenciaException{
		Connection con = icon.getConnection();

		// Insertamos el barco.
		try {
			PreparedStatement pstmt = con.prepareStatement(Consultas.insertarBarco());
			pstmt.setInt(1, barco.getId());
			pstmt.setDouble(2, barco.getVelocidad());
			pstmt.setDouble(3, barco.getConsumoCombustible());
			pstmt.setDouble(4, barco.getPosicionX());
			pstmt.setDouble(5, barco.getPosicionY());
			pstmt.setString(6, barco.getTipo());
			pstmt.setString(7, barco.getSubtipo());
			pstmt.setInt(8, idPartida);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			try {
				PoolConexiones.getInstancia().liberarConexion(icon, false);
			} catch (LogicaException e1) {
				throw new PersistenciaException("Error SQL: " + e1.getMessage());
			}
			
			throw new PersistenciaException("Error SQL: " + e.getMessage());
		}	
		
		if (barco.getTipo().equalsIgnoreCase("patrulla")) {
			// Insertamos la patrulla.
			try { 
				PreparedStatement pstmt = con.prepareStatement(Consultas.insertarPatrulla());
				pstmt.setInt(1, barco.getId()); 
				pstmt.setInt(2, ((Patrulla) barco).getAlcance());
				pstmt.setInt(3, ((Patrulla) barco).getDanioBase());
				pstmt.setBoolean(4, ((Patrulla) barco).isHelicopteroUtilizado());
				pstmt.executeUpdate();
				pstmt.close(); 
			} catch (SQLException e) {
				try {
					PoolConexiones.getInstancia().liberarConexion(icon, false);
				} catch (LogicaException e1) {
					throw new PersistenciaException("Error SQL: " + e1.getMessage());
				}
				
				throw new PersistenciaException("Error SQL: " + e.getMessage()); 
			}
		} else {
			// Insertamos el pesquero.
			try { 
				PreparedStatement pstmt = con.prepareStatement(Consultas.insertarPesquero());
				pstmt.setInt(1, barco.getId()); 
				pstmt.setInt(2, ((Pesquero) barco).getVida());
				pstmt.setInt(3, ((Pesquero) barco).getCapacidadCarga()); pstmt.executeUpdate();
				pstmt.close(); 
			} catch (SQLException e) {
				try {
					PoolConexiones.getInstancia().liberarConexion(icon, false);
				} catch (LogicaException e1) {
					throw new PersistenciaException("Error SQL: " + e1.getMessage());
				}
				
				throw new PersistenciaException("Error SQL: " + e.getMessage());
			}
		}
		
		try {
			PoolConexiones.getInstancia().liberarConexion(icon, true);
		} catch (LogicaException e1) {
			throw new PersistenciaException("Error SQL: " + e1.getMessage());
		}
	}

	/**
	 * Busca un barco en la BD.
	 * 
	 * @param idBarco
	 * @param icon
	 */
	@Override
	public Barco buscar(int idBarco, IConexion icon) throws PersistenciaException {
		Barco barco 	= null;
		Connection con 	= icon.getConnection();
		
		// Obtenemos los datos del barco.
		try {
			PreparedStatement pstmt = con.prepareStatement(Consultas.obtenerBarco());
			pstmt.setInt(1, idBarco);
			ResultSet datosBarco = pstmt.executeQuery();
			if (datosBarco.next()) {
				pstmt.close();
				
				if (datosBarco.getString("tipo").equalsIgnoreCase("patrulla")) {
					// Obtenemos los datos de la patrulla.
					PreparedStatement pstmt2 = con.prepareStatement(Consultas.obtenerPatrulla());
					pstmt2.setInt(1, idBarco);
					ResultSet datosPatrulla = pstmt2.executeQuery();
										
					if (datosPatrulla.next()) {
						barco = new Patrulla(datosBarco.getInt("id"), datosBarco.getDouble("velocidad"),
								datosBarco.getDouble("consumo_combustible"), datosBarco.getDouble("posicion_x"),
								datosBarco.getDouble("posicion_y"), datosBarco.getString("subtipo"), 
								datosPatrulla.getInt("alcance"), datosPatrulla.getInt("danio_base"),
								datosPatrulla.getBoolean("helicoptero_utilizado"));
					}
					
					datosPatrulla.close();
				} else {
					// Obtenemos los datos del pesquero.
					PreparedStatement pstmt2 = con.prepareStatement(Consultas.obtenerPesquero());
					pstmt2.setInt(1, idBarco);
					ResultSet datosPesquero = pstmt2.executeQuery();
										
					if (datosPesquero.next()) {
						barco = new Pesquero(datosBarco.getInt("id"), datosBarco.getDouble("velocidad"),
								datosBarco.getDouble("consumo_combustible"), datosBarco.getDouble("posicion_x"),
								datosBarco.getDouble("posicion_y"), datosBarco.getString("subtipo"), 
								datosPesquero.getInt("vida"), datosPesquero.getInt("capacidad_carga"));
					}
					
					datosPesquero.close();
				}
				
			}
			
			datosBarco.close();
			pstmt.close();
		} catch (SQLException e) {
			try {
				PoolConexiones.getInstancia().liberarConexion(icon, false);
			} catch (LogicaException e1) {
				throw new PersistenciaException("Error SQL: " + e1.getMessage());
			}
			
			throw new PersistenciaException("Error SQL: " + e.getMessage());
		}
		
		return barco;
	}
	
	/**
	 * Elimina un barco.
	 */
	public void eliminar(int idBarco, IConexion icon) throws PersistenciaException{
		Connection con 	= icon.getConnection();
		String tipoBarco= null;
		
		// Eliminamos el barco.
		try {
			// Primero traemos el tipo de barco.
			PreparedStatement pstmt = con.prepareStatement(Consultas.obtenerBarco());
			pstmt.setInt(1, idBarco);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				tipoBarco = rs.getString("tipo");
			
			rs.close();
			pstmt.close();			
			
			// Eliminamos el barco.
			pstmt = con.prepareStatement(Consultas.eliminarBarco());
			pstmt.setInt(1, idBarco);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			try {
				PoolConexiones.getInstancia().liberarConexion(icon, false);
			} catch (LogicaException e1) {
				throw new PersistenciaException("Error SQL: " + e1.getMessage());
			}
			
			throw new PersistenciaException("Error SQL: " + e.getMessage());
		}
		
		// Si es patrulla, la eliminamos.
		if (tipoBarco != null && tipoBarco.equalsIgnoreCase("patrulla")) {
			try {
				PreparedStatement pstmt = con.prepareStatement(Consultas.eliminarPatrulla());
				pstmt.setInt(1, idBarco);
				pstmt.executeUpdate();
				pstmt.close();
			} catch (SQLException e) {
				try {
					PoolConexiones.getInstancia().liberarConexion(icon, false);
				} catch (LogicaException e1) {
					throw new PersistenciaException("Error SQL: " + e1.getMessage());
				}
				
				throw new PersistenciaException("Error SQL: " + e.getMessage());
			}
		} else if (tipoBarco != null) {
			// Si es pesquero, lo eliminamos.
			try {
				PreparedStatement pstmt = con.prepareStatement(Consultas.eliminarPesquero());
				pstmt.setInt(1, idBarco);
				pstmt.executeUpdate();
				pstmt.close();
			} catch (SQLException e) {
				try {
					PoolConexiones.getInstancia().liberarConexion(icon, false);
				} catch (LogicaException e1) {
					throw new PersistenciaException("Error SQL: " + e1.getMessage());
				}
				
				throw new PersistenciaException("Error SQL: " + e.getMessage());
			}
		}
	}

	/**
	 * Devuelve una lista con los barcos de una partida.
	 * 
	 * @param idPartida
	 * @param icon
	 */
	@Override
	public ArrayList<Barco> listarBarcosPorPartida(int idPartida, IConexion icon) throws PersistenciaException {
		ArrayList<Barco> lista	= new ArrayList<Barco>();
		Connection con 			= icon.getConnection();
		
		// Obtenemos los datos del barco.
		try {
			PreparedStatement pstmt = con.prepareStatement(Consultas.obtenerBarcosDePartida());
			pstmt.setInt(1, idPartida);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				
				if (rs.getString("tipo").equalsIgnoreCase("patrulla")) {
					// Obtenemos los datos de la patrulla.
					PreparedStatement pstmt2 = con.prepareStatement(Consultas.obtenerPatrulla());
					pstmt2.setInt(1, rs.getInt("id"));
					ResultSet datosPatrulla = pstmt2.executeQuery();
										
					if (datosPatrulla.next()) {
						lista.add(new Patrulla(rs.getInt("id"), rs.getDouble("velocidad"),
								rs.getDouble("consumo_combustible"), rs.getDouble("posicion_x"),
								rs.getDouble("posicion_y"), rs.getString("subtipo"), 
								datosPatrulla.getInt("alcance"), datosPatrulla.getInt("danio_base"),
								datosPatrulla.getBoolean("helicoptero_utilizado")));
					}
					
					datosPatrulla.close();
				} else {
					// Obtenemos los datos del pesquero.
					PreparedStatement pstmt2 = con.prepareStatement(Consultas.obtenerPesquero());
					pstmt2.setInt(1, rs.getInt("id"));
					ResultSet datosPesquero = pstmt2.executeQuery();
										
					if (datosPesquero.next()) {
						lista.add(new Pesquero(rs.getInt("id"), rs.getDouble("velocidad"),
								rs.getDouble("consumo_combustible"), rs.getDouble("posicion_x"),
								rs.getDouble("posicion_y"), rs.getString("subtipo"), 
								datosPesquero.getInt("vida"), datosPesquero.getInt("capacidad_carga")));
					}
					
					datosPesquero.close();
				}
				
			}
			
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			try {
				PoolConexiones.getInstancia().liberarConexion(icon, false);
			} catch (LogicaException e1) {
				e1.printStackTrace();
				throw new PersistenciaException("Error SQL: " + e1.getMessage());
			}
			
			e.printStackTrace();
			throw new PersistenciaException("Error SQL: " + e.getMessage());
		}
		
		return lista;
	}
}
	