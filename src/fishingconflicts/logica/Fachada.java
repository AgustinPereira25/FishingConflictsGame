package fishingconflicts.logica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;

import javax.websocket.Session;

import fishingconflicts.excepciones.LogicaException;
import fishingconflicts.excepciones.PersistenciaException;
import fishingconflicts.logica.colecciones.Barcos;
import fishingconflicts.logica.colecciones.Partidas;
import fishingconflicts.logica.colecciones.interfaces.IBarcos;
import fishingconflicts.logica.colecciones.interfaces.IPartidas;
import fishingconflicts.logica.interfaces.IConexion;
import fishingconflicts.logica.interfaces.IFachada;
import fishingconflicts.logica.modelos.Bala;
import fishingconflicts.logica.modelos.Barco;
import fishingconflicts.logica.modelos.Partida;
import fishingconflicts.logica.modelos.Patrulla;
import fishingconflicts.logica.modelos.Pesquero;
import fishingconflicts.persistencia.daos.DAOBarcos;
import fishingconflicts.persistencia.daos.DAOPartidas;
import fishingconflicts.persistencia.daos.interfaces.IDAOBarcos;
import fishingconflicts.persistencia.daos.interfaces.IDAOPartidas;

public class Fachada implements IFachada {

	/**
	 * Instancia.
	 */
	private static Fachada instancia;
	
	/**
	 * Colección de partidas.
	 */
	private IPartidas partidas;
	
	/**
	 * DAO de partidas.
	 */
	private IDAOPartidas daoPartidas;
	
	/**
	 * DAO de barcos.
	 */
	private IDAOBarcos daoBarcos;	
	
	/**
	 * Mapeo Partida -> Barcos y sesiones.
	 * Ejemplo: Partida 1 -> [Barco 1 / sesión 1, Barco 2 / sesión 2]
	 */
	private HashMap<Integer, HashMap<Integer, Session>> sesionesPartida;
	
	/**
	 * @return the instancia
	 * @throws LogicaException 
	 * @throws PersistenciaException 
	 */
	public static Fachada getInstancia() throws PersistenciaException, LogicaException {
		if (!(instancia instanceof Fachada))
			instancia = new Fachada();
		
		return instancia;
	}
	
	/**
	 * Constructor.
	 * @throws LogicaException 
	 * @throws PersistenciaException 
	 */
	private Fachada() throws LogicaException, PersistenciaException {
		sesionesPartida = new HashMap<Integer, HashMap<Integer, Session>>();
		daoPartidas = new DAOPartidas();
		daoBarcos = new DAOBarcos();
		
		IConexion icon 	= null;
		// Obtenemos una nueva conexión del pool.
		icon = PoolConexiones.getInstancia().obtenerConexion();
		partidas = new Partidas(daoPartidas.maximoId(icon));
		PoolConexiones.getInstancia().liberarConexion(icon, true);
	}

	/**
	 * Inserta una nueva partida con la cantidad de patrullas y de pesqueros especificada.
	 * 
	 * @param cantidadPatrullas
	 * @param cantidadPesqueros
	 */
	@Override
	public Resultado nuevaPartida(int cantidadPatrullas, int cantidadPesqueros) {
		Resultado r = new Resultado();
		Properties p = new Properties();
		
		try {
			p.load(this.getClass().getResourceAsStream("config/partida.properties"));
		} catch(Exception e) {
			r.setExito(false, e.getMessage());
			return r;
		}
		
		// Creamos la partida.
		Partida partida = new Partida(partidas.obtenerNuevoId(),
				Integer.parseInt(p.getProperty("stockCombustiblePatrullas")),
				Integer.parseInt(p.getProperty("stockCombustiblePesqueros")), 
				Integer.parseInt(p.getProperty("stockPeces")),
				1, cantidadPatrullas, cantidadPesqueros, 0, "");

		// Verificamos si la partida pudo ser numerada.
		if (partida.getId() <= 0) {
			r.setExito(false, "Error al crear la partida");
			return r;
		}
		
		// Insertamos la partida
		partidas.insertar(partida);
		
		// Devolvemos el id de la partida en el resultado.
		r.agregarRespuesta("idPartida", String.valueOf(partida.getId()), "int");
		return r;
	}

	/**
	 * Conecta un jugador a una partida, con el barco especificado.
	 * 
	 * @param idPartida
	 * @param tipoBarco
	 * @param subtipoBarco
	 * @return Resultado
	 */
	@Override
	public Resultado conectarAPartida(int idPartida, String tipoBarco, String subtipoBarco, Session sesion) {
		Resultado r		= new Resultado();
		IConexion icon 	= null;
		
		// Obtenemos una nueva conexión del pool.
		try {
			icon = PoolConexiones.getInstancia().obtenerConexion();
		} catch (LogicaException e) {
			r.setExito(false, e.getMessage());
			return r;
		}
		
		// Cargamos la partida.
		Partida partida = null;
		if (partidas.existe(idPartida)) {
			// Existe en memoria, por lo tanto la cargamos directo.
			partida = partidas.obtener(idPartida);
		} else {
			try {
				// No existe en memoria, así que la buscamos en la BD.
				if (daoPartidas.existe(idPartida, icon)) {
					partida = daoPartidas.buscar(idPartida, icon);
					
					// Guardamos la partida en memoria, y la eliminamos de la BD.
					partidas.insertar(partida);
					partidas.actualizarNuevoId();
					
					// Guardamos los barcos de la partida, traídos desde la BD, y
					// los eliminamos de la misma.
					for(Barco b : daoBarcos.listarBarcosPorPartida(idPartida, icon)) {
						partida.getBarcos().insertar(b);
						daoBarcos.eliminar(b.getId(), icon);
					}
					
					partida.getBarcos().actualizarNuevoId();
				} else {
					r.setExito(false, "No existe la partida");
					return r;
				}
			} catch (PersistenciaException e) {
				// Liberamos la conexión.
				try {
					PoolConexiones.getInstancia().liberarConexion(icon, false);
				} catch (LogicaException e1) {
					r.setExito(false, e1.getMessage());
					return r;
				}
				
				r.setExito(false, e.getMessage());
				return r;
			}
		}
		
		// Liberamos la conexión.
		try {
			PoolConexiones.getInstancia().liberarConexion(icon, true);
		} catch (LogicaException e) {
			r.setExito(false, e.getMessage());
		}
		
		// Verificamos si la partida está finalizada.
		if (partida.getEstado() == 3) {
			r.setExito(false, "La partida ya está finalizada. Equipo ganador: " + partida.getEquipoGanador());
			return r;
		}
		
		// Chequeamos que haya barcos libres para asignar.
		if (tipoBarco.equalsIgnoreCase("patrulla")) {
			if (partida.getBarcos().cantidadPatrullasEnUso() == partida.getCantidadPatrullas()) {
				r.setExito(false, "No hay patrullas disponibles");
				return r;
			}
		} else {
			if (partida.getBarcos().cantidadPesquerosEnUso()
					+ partida.getCantidadPesquerosEliminados() == partida.getCantidadPesqueros()) {
				r.setExito(false, "No hay pesqueros disponibles");
				return r;
			}
		}
		
		// Creamos o asignamos un barco ya existente, y devolvemos el id.
		int lugaresDisponibles;
		if (tipoBarco.equalsIgnoreCase("patrulla"))
			lugaresDisponibles = partida.getCantidadPatrullas() - partida.getCantidadBarcosCreados(tipoBarco);
		else
			lugaresDisponibles = partida.getCantidadPesqueros() - partida.getCantidadBarcosCreados(tipoBarco)
				- partida.getCantidadPesquerosEliminados();
		
		int idBarco = partida.getBarcos().asignarOCrear(tipoBarco, subtipoBarco, lugaresDisponibles);
		// Si el id del barco vino en 0, es porque no se pueden crear más barcos y no hay
		// barcos disponibles del tipo y subtipo seleccionados. Devolvemos un mensaje de error al usuario.
		if (idBarco == 0) {
			r.setExito(false, "No hay lugares disponibles para el tipo de barco seleccionado");
			return r;
		}
		
		// Asignamos el id del barco a la respuesta.
		r.agregarRespuesta("idBarco", String.valueOf(idBarco), "int");
		
		// Agregamos la sesión a la partida.
		if (!sesionesPartida.containsKey(idPartida))
			sesionesPartida.put(idPartida, new HashMap<Integer, Session>());
		
		sesionesPartida.get(idPartida).put(idBarco, sesion);
		
		// Chequeamos si la partida puede comenzar.
		if (partida.getCantidadPatrullas() == partida.getBarcos().cantidadPatrullasEnUso() &&
				partida.getCantidadPesqueros() == partida.getBarcos().cantidadPesquerosEnUso())
			partida.setEstado(2);
		
		return r;
	}
	
	/**
	 * cambia estado a en espera si algun barco apreta el boton vista lateral
	 * @param idPartida
	 * @return Resultado
	 */
	public Resultado vistaLateral(int idPartida) {
		Resultado r = new Resultado();
		
		// Cargamos la partida.
		if (partidas.existe(idPartida)) {
			Partida partida = null;
			
			// Existe en memoria, por lo tanto la cargamos directo.
			partida = partidas.obtener(idPartida);
			
			//seteo el estado "en espera" asi todo se pausea
			partida.setEstado(1);
			
			// Devolvemos el resultado.
			r.agregarRespuesta("estado", 
					String.valueOf(partida.getEstado()), "int");
			return r;
		} else {
			r.setExito(false, "No existe la partida");
			return r;
		}
	}
	 
	/**
	 * cambia estado a iniciada si algun barco apreta el boton volver estando en la vista lateral
	 * @param idPartida
	 * @return Resultado
	 */
	public Resultado vistaLateralDesactivar(int idPartida) {
		Resultado r = new Resultado();
		
		// Cargamos la partida.
		Partida partida = null;
		if (partidas.existe(idPartida)) {
			// Existe en memoria, por lo tanto la cargamos directo.
			partida = partidas.obtener(idPartida);
			
			// seteo el estado "iniciada", si los dos equipos tienen barcos en uso.
			if (partida.getBarcos().cantidadPatrullasEnUso() > 0 && partida.getBarcos().cantidadPesquerosEnUso() > 0)
				partida.setEstado(2);
			
			// Devolvemos el resultado.
			r.agregarRespuesta("estado", 
					String.valueOf(partida.getEstado()), "int");
			return r;
		} else {
			r.setExito(false, "No existe la partida");
			return r;
		}
	}
	
	/**
	 * Obtiene la cantidad de barcos disponibles de la partida.
	 * 
	 * @param idPartida
	 * @return Resultado
	 */
	@Override
	public Resultado obtenerBarcosDisponibles(int idPartida) {
		Resultado r 	= new Resultado();
		IConexion icon 	= null;
		
		// Obtenemos una nueva conexión del pool.
		try {
			icon = PoolConexiones.getInstancia().obtenerConexion();
		} catch (LogicaException e) {
			r.setExito(false, e.getMessage());
			return r;
		}
		
		// Cargamos la partida.
		Partida partida = null;
		if (partidas.existe(idPartida)) {
			// Existe en memoria, por lo tanto la cargamos directo.
			partida = partidas.obtener(idPartida);
		} else {
			try {
				// No existe en memoria, así que la buscamos en la BD.
				if (daoPartidas.existe(idPartida, icon)) {
					partida = daoPartidas.buscar(idPartida, icon);
					
					// Guardamos la partida en memoria, y la eliminamos de la BD.
					partidas.insertar(partida);
					partidas.actualizarNuevoId();
					
					// Guardamos los barcos de la partida, traídos desde la BD, y
					// los eliminamos de la misma.
					for(Barco b : daoBarcos.listarBarcosPorPartida(idPartida, icon)) {
						partida.getBarcos().insertar(b);
						daoBarcos.eliminar(b.getId(), icon);
					}

					partida.getBarcos().actualizarNuevoId();
				} else {
					r.setExito(false, "No existe la partida");
					return r;
				}
			} catch (PersistenciaException e) {
				// Liberamos la conexión.
				try {
					PoolConexiones.getInstancia().liberarConexion(icon, false);
				} catch (LogicaException e1) {
					r.setExito(false, e1.getMessage());
					return r;
				}
				
				r.setExito(false, e.getMessage());
				return r;
			}
		}
		
		// Liberamos la conexión.
		try {
			PoolConexiones.getInstancia().liberarConexion(icon, true);
		} catch (LogicaException e) {
			r.setExito(false, e.getMessage());
		}
		
		// Chequeamos si la partida está finalizada.
		if (partida.getEstado() == 3) {
			r.setExito(false, "La partida está finalizada. Equipo ganador: " +
					partida.getEquipoGanador());
			return r;
		}
		
		// Calculamos la cantidad de barcos disponibles.
		int cantidadPatrullasDisponibles = partida.getCantidadPatrullas() - 
				partida.getBarcos().cantidadPatrullasEnUso();
		int cantidadPesquerosDisponibles = partida.getCantidadPesqueros() - 
				partida.getBarcos().cantidadPesquerosEnUso();
		
		// Devolvemos el resultado.
		r.agregarRespuesta("cantidadPatrullasDisponibles", 
				String.valueOf(cantidadPatrullasDisponibles), "int");
		r.agregarRespuesta("cantidadPesquerosDisponibles", 
				String.valueOf(cantidadPesquerosDisponibles), "int");
		
		return r;
	}

	/**
	 * Mueve un barco en la dirección dada.
	 * 
	 * @param idPartida
	 * @param idBarco
	 * @param direccion
	 * @param angulo
	 */
	@Override
	public void moverBarco(int idPartida, int idBarco, String direccion, int angulo) {
		// Verificamos si la partida existe.
		if (!partidas.existe(idPartida)) return;
		
		// Verificamos si el barco existe en la partida.
		Partida partida = partidas.obtener(idPartida);
		if (!partida.getBarcos().existe(idBarco)) return;
		
		// Obtenemos el barco.
		Barco barco = partida.getBarcos().obtener(idBarco);
		
		// Movemos el barco y consumimos el combustible.
		barco.mover(direccion, angulo);
		if (barco.getTipo().equalsIgnoreCase("patrulla"))
			partida.setStockCombustiblePatrullas(partida.getStockCombustiblePatrullas() -
					(barco.getConsumoCombustible() / 60));
		else
			partida.setStockCombustiblePesqueros(partida.getStockCombustiblePesqueros() -
					(barco.getConsumoCombustible() / 60));
		
		// Chequeamos si existe un ganador en la partida.
		partida.chequearSiHayGanador();
	}

	/**
	 * Dispara al barco oponente y devuelve un booleano indicando
	 * si el barco fue eliminado
	 * 
	 * @param idPartida
	 * @param idBarco
	 * @param idBala
	 * @param idBarcoOponente
	 * @return boolean
	 */
	@Override
	public boolean disparar(int idPartida, int idBarco, int idBala, int idBarcoOponente) {
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) return false;
		
		// Verificamos si el barco que dispara existe en la partida.
		Partida partida = partidas.obtener(idPartida);
		if (!partida.getBarcos().existe(idBarco)) return false;
		
		// Verificamos si el barco que dispara es una patrulla.
		Barco barco = partida.getBarcos().obtener(idBarco);
		if (!barco.getTipo().equalsIgnoreCase("patrulla")) return false;
		
		// Verificamos si el barco oponente existe.
		if (!partida.getBarcos().existe(idBarcoOponente)) return false;
		
		// Verificamos si el barco oponente es un pesquero.
		Barco oponente = partida.getBarcos().obtener(idBarcoOponente);
		if (!oponente.getTipo().equalsIgnoreCase("pesquero")) return false;
		
		// Verificamos si la bala existe.
		if (((Patrulla) barco).getBala(idBala) == null) return false;
		
		// Obtenemos la bala y generamos el daño en el oponente.
		Bala bala = ((Patrulla) barco).getBala(idBala);
		int vidaConDanio = ((Pesquero) oponente).getVida() - (bala.getTipoArma().equalsIgnoreCase("canion") ?
				((Patrulla) barco).getDanioBase() * 2 : ((Patrulla) barco).getDanioBase());
		((Pesquero) oponente).setVida(vidaConDanio);

		// Eliminamos la bala.
		((Patrulla) barco).eliminarBala(idBala);
		
		// Verificamos si el oponente murió.
		if (((Pesquero) oponente).getVida() <= 0) {
			partida.eliminarPesquero();
			partida.getBarcos().eliminar(idBarcoOponente);
			
			// Chequeamos si hay ganador.
			partida.chequearSiHayGanador();
			return true;
		}
		
		return false;
	}

	/**
	 * Captura el barco oponente.
	 * 
	 * @param idPartida
	 * @param idBarco
	 * @param idBarcoOponente
	 */
	@Override
	public void capturar(int idPartida, int idBarco, int idBarcoOponente) {
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) return;
		
		// Verificamos si el barco que captura existe en la partida.
		Partida partida = partidas.obtener(idPartida);
		if (!partida.getBarcos().existe(idBarco)) return;
		
		// Verificamos si el barco que dispara es una patrulla pesada.
		Barco barco = partida.getBarcos().obtener(idBarco);
		if (!barco.getTipo().equalsIgnoreCase("patrulla") ||
				!barco.getSubtipo().equalsIgnoreCase("pesada")) return;
		
		// Verificamos si el barco oponente existe.
		if (!partida.getBarcos().existe(idBarcoOponente)) return;
		
		// Verificamos si el barco oponente es un pesquero.
		Barco oponente = partida.getBarcos().obtener(idBarcoOponente);
		if (!oponente.getTipo().equalsIgnoreCase("pesquero")) return;
		
		// Eliminamos al oponente.
		partida.eliminarPesquero();
		partida.getBarcos().eliminar(idBarcoOponente);
		
		// Chequeamos si hay ganador.
		partida.chequearSiHayGanador();
	}

	/**
	 * Obtiene todos los datos de la partida.
	 * 
	 * @param idPartida
	 * @return Resultado
	 */
	@Override
	public Resultado obtenerPartidaJson(int idPartida) {
		Resultado r = new Resultado();
		
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) {
			r.setExito(false, "No existe la partida");
			return r;
		}
		
		// Obtenemos la partida.
		Partida partida = partidas.obtener(idPartida);
		
		// Agregamos la partida en formato string de json al resultado.
		r.agregarRespuesta("partida", partida.toJson().toString(), "json");
		
		return r;
	}

	/**
	 * Obtiene las sesiones conectadas una partida.
	 * 
	 * @param idPartida
	 * @return ArrayList<Session>
	 */
	@Override
	public HashMap<Integer, Session> obtenerSesionesDePartida(int idPartida) {
		return sesionesPartida.get(idPartida);
	}
	
	/**
	 * Obtener sesión de jugador.
	 *
	 * @param idPartida
	 * @param idJugador
	 * @return Session
	 */
	@Override
	public Session obtenerSesionBarco(int idPartida, int idBarco) {
		HashMap<Integer, Session> sesiones = sesionesPartida.get(idPartida);
		
		return sesiones.get(idBarco);
	}

	/**
	 * Desconecta el jugador de la partida.
	 * 
	 * @parma Session sesion
	 */
	@Override
	public int desconectarSesion(Session sesion) {
		IConexion icon	= null;
		int idPartida	= 0;
		int idBarco 	= 0;
		
		// Buscamos la sesión en las partidas.
		for(Entry<Integer, HashMap<Integer, Session>> partida : sesionesPartida.entrySet()) {
			for(Entry<Integer, Session> barco : partida.getValue().entrySet()) {
				if (barco.getValue().getId().equalsIgnoreCase(sesion.getId())) {
					idPartida = partida.getKey();
					idBarco = barco.getKey();
					break;
				}
			}
		}
		
		// Eliminamos la sesión.
		if (sesionesPartida.containsKey(idPartida) && sesionesPartida.get(idPartida).containsKey(idBarco)) {
			sesionesPartida.get(idPartida).remove(idBarco);
			
			// Eliminamos el barco de la partida.
			Partida partida = partidas.obtener(idPartida);
			if (partida.getBarcos().existe(idBarco)) {
				partida.getBarcos().obtener(idBarco).setEnUso(false);
			}

			// Verificamos si la partida tiene al menos un jugador por equipo.
			boolean esJugable = partida.getBarcos().cantidadPatrullasEnUso() >= 1 && partida.getBarcos().cantidadPesquerosEnUso() >= 1;
			if (!esJugable && partida.getEstado() != 3) {
				partida.setEstado(1);
			}
			
			// Verificamos si todos los jugadores se desconectaron y en ese caso eliminamos la partida de memoria.
			if (partida.getBarcos().cantidadPatrullasEnUso() == 0 && partida.getBarcos().cantidadPesquerosEnUso() == 0) {
				partidas.eliminar(idPartida);
				return 0;
			}
		}
		
		return idPartida;
	}

	/**
	 * Realiza el acto de pescar.
	 * 
	 * @param idPartida
	 * @param idBarco
	 */
	@Override
	public void pescar(int idPartida, int idBarco) {
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) return;
		
		// Verificamos si el barco que pesca existe en la partida.
		Partida partida = partidas.obtener(idPartida);
		if (!partida.getBarcos().existe(idBarco)) return;
		
		// Verificamos si el barco es un pesquero.
		Barco barco = partida.getBarcos().obtener(idBarco);
		if (!barco.getTipo().equalsIgnoreCase("pesquero")) return;
		
		// Seteamos el nuevo stock de peces.
		if (barco.getSubtipo().equalsIgnoreCase("fabrica"))
			partida.setStockPeces(partida.getStockPeces() - (100d / 60d));
		else
			partida.setStockPeces(partida.getStockPeces() - (50d / 60d));
		
		// Chequeamos si hay ganador.
		partida.chequearSiHayGanador();
	}

	/**
	 * Activa el helicóptero del barco dado.
	 * 
	 * @param idPartida
	 * @param idBarco
	 */
	@Override
	public void activarHelicoptero(int idPartida, int idBarco) {
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) return;
		
		// Verificamos si el barco existe en la partida.
		Partida partida = partidas.obtener(idPartida);
		if (!partida.getBarcos().existe(idBarco)) return;
		
		// Verificamos si el barco sea una patrulla pesada.
		Barco barco = partida.getBarcos().obtener(idBarco);
		if (!barco.getTipo().equalsIgnoreCase("patrulla") ||
				!barco.getSubtipo().equalsIgnoreCase("pesada")) return;
		
		// Verificamos si el barco ya utilizó el helicóptero.
		if (((Patrulla) barco).isHelicopteroUtilizado()) return;
		
		// Activamos el helicóptero.
		((Patrulla) barco).setHelicopteroActivado(true);
		((Patrulla) barco).setHelicopteroUtilizado(true);
	}
	
	/**
	 * Desactiva el helicóptero del barco dado.
	 * 
	 * @param idPartida
	 * @param idBarco
	 */
	@Override
	public void desactivarHelicoptero(int idPartida, int idBarco) {
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) return;
		
		// Verificamos si el barco existe en la partida.
		Partida partida = partidas.obtener(idPartida);
		if (!partida.getBarcos().existe(idBarco)) return;
		
		// Verificamos si el barco sea una patrulla pesada.
		Barco barco = partida.getBarcos().obtener(idBarco);
		if (!barco.getTipo().equalsIgnoreCase("patrulla") ||
				!barco.getSubtipo().equalsIgnoreCase("pesada")) return;
		
		// Verificamos si el helicóptero está activado.
		if (!((Patrulla) barco).isHelicopteroActivado()) return;
		
		// Desactivamos el helicóptero.
		((Patrulla) barco).setHelicopteroActivado(false);
	}

	/**
	 * Habilita el bote ligero de un barco.
	 * 
	 * @param idPartida
	 * @param idBarco
	 */
	@Override
	public void habilitarBoteLigero(int idPartida, int idBarco) {
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) return;
		
		// Verificamos si el barco existe en la partida.
		Partida partida = partidas.obtener(idPartida);
		if (!partida.getBarcos().existe(idBarco)) return;
		
		// Verificamos si el barco sea una patrulla pesada.
		Barco barco = partida.getBarcos().obtener(idBarco);
		if (!barco.getTipo().equalsIgnoreCase("patrulla") ||
				!barco.getSubtipo().equalsIgnoreCase("pesada")) return;
		
		// Verificamos si ya tiene el bote ligero activado.
		if (((Patrulla) barco).isBoteLigeroActivado()) return;
		
		// Activamos el bote ligero.
		((Patrulla) barco).setBoteLigeroPosicionX(barco.getPosicionX());
		((Patrulla) barco).setBoteLigeroPosicionY(barco.getPosicionY());
		((Patrulla) barco).setBoteLigeroActivado(true);
	}
	
	/**
	 * Deshabilita el bote ligero de un barco.
	 * 
	 * @param idPartida
	 * @param idBarco
	 */
	@Override
	public void deshabilitarBoteLigero(int idPartida, int idBarco) {
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) return;
		
		// Verificamos si el barco existe en la partida.
		Partida partida = partidas.obtener(idPartida);
		if (!partida.getBarcos().existe(idBarco)) return;
		
		// Verificamos si el barco sea una patrulla pesada.
		Barco barco = partida.getBarcos().obtener(idBarco);
		if (!barco.getTipo().equalsIgnoreCase("patrulla") ||
				!barco.getSubtipo().equalsIgnoreCase("pesada")) return;
		
		// Verificamos si no tiene el bote ligero activado.
		if (!((Patrulla) barco).isBoteLigeroActivado()) return;
		
		// Desactivamos el helicóptero.
		((Patrulla) barco).setBoteLigeroActivado(false);
	}

	/**
	 * Mueve el bote ligero a la posición dada.
	 * 
	 * @param idPartida
	 * @param idBarco
	 * @param posicionX
	 * @param posicionY
	 */
	@Override
	public void moverBoteLigero(int idPartida, int idBarco, int posicionX, int posicionY, int angulo) {
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) return;
		
		// Verificamos si el barco existe en la partida.
		Partida partida = partidas.obtener(idPartida);
		if (!partida.getBarcos().existe(idBarco)) return;
		
		// Verificamos si el barco sea una patrulla pesada.
		Barco barco = partida.getBarcos().obtener(idBarco);
		if (!barco.getTipo().equalsIgnoreCase("patrulla") ||
				!barco.getSubtipo().equalsIgnoreCase("pesada")) return;
		
		// Verificamos si no tiene el bote ligero activado.
		if (!((Patrulla) barco).isBoteLigeroActivado()) return;
		
		// Movemos el bote ligero.
		((Patrulla) barco).setBoteLigeroPosicionX(posicionX);
		((Patrulla) barco).setBoteLigeroPosicionY(posicionY);
		((Patrulla) barco).setBoteLigeroAngulo(angulo);
	}

	/**
	 * Crea una bala en el barco especificado.
	 * 
	 * @param idPartida
	 * @param idBarco
	 * @param posicionDestinoX
	 * @param posicionDestinoY
	 * @param tipoArma
	 */
	@Override
	public void crearBala(int idPartida, int idBarco, int posicionDestinoX, int posicionDestinoY, String tipoArma) {
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) return;
		
		// Verificamos si el barco existe en la partida.
		Partida partida = partidas.obtener(idPartida);
		if (!partida.getBarcos().existe(idBarco)) return;
		
		// Verificamos si el barco sea una patrulla.
		Barco barco = partida.getBarcos().obtener(idBarco);
		if (!barco.getTipo().equalsIgnoreCase("patrulla")) return;
		
		// Verificamos si el arma es un cañón y es una patrulla pesada.
		if (tipoArma.equalsIgnoreCase("canion") && !barco.getSubtipo().equalsIgnoreCase("pesada")) return;
		
		// Creamos la bala en la patrulla.
		((Patrulla) barco).insertarBala(new Bala(((Patrulla) barco).obtenerNuevoIdBala(), barco.getPosicionX(),
				barco.getPosicionY(), posicionDestinoX, posicionDestinoY, tipoArma));
	}

	/**
	 * Mueve una bala.
	 * 
	 * @param idPartida
	 * @param idBarco
	 * @param idBala
	 * @param posicionX
	 * @param posicionY
	 */
	@Override
	public void moverBala(int idPartida, int idBarco, int idBala, int posicionX, int posicionY) {
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) return;
		
		// Verificamos si el barco existe en la partida.
		Partida partida = partidas.obtener(idPartida);
		if (!partida.getBarcos().existe(idBarco)) return;
		
		// Verificamos si el barco sea una patrulla.
		Barco barco = partida.getBarcos().obtener(idBarco);
		if (!barco.getTipo().equalsIgnoreCase("patrulla")) return;
		
		// Verificamos si existe la bala.
		if (((Patrulla) barco).getBala(idBala) == null) return;
		
		// Movemos la bala
		Bala bala = ((Patrulla) barco).getBala(idBala);
		bala.setPosicionX(posicionX);
		bala.setPosicionY(posicionY);
	}

	/**
	 * Elimina una bala.
	 * 
	 * @param idPartida
	 * @param idBarco
	 * @param idBala
	 */
	@Override
	public void eliminarBala(int idPartida, int idBarco, int idBala) {
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) return;
		
		// Verificamos si el barco existe en la partida.
		Partida partida = partidas.obtener(idPartida);
		if (!partida.getBarcos().existe(idBarco)) return;
		
		// Verificamos si el barco sea una patrulla.
		Barco barco = partida.getBarcos().obtener(idBarco);
		if (!barco.getTipo().equalsIgnoreCase("patrulla")) return;
		
		// Verificamos si existe la bala.
		if (((Patrulla) barco).getBala(idBala) == null) return;
		
		// Eliminamos la bala.
		((Patrulla) barco).eliminarBala(idBala);
	}

	/**
	 * Guarda una partida.
	 * 
	 * @param idPartida
	 */
	@Override
	public Resultado guardarPartida(int idPartida) {
		Resultado r = new Resultado();
		IConexion icon 	= null;
		
		// Verificamos si existe la partida.
		if (!partidas.existe(idPartida)) {
			r.setExito(false, "No existe la partida");
			return r;
		}
		
		// Obtenemos una nueva conexión del pool y guardamos los datos.
		try {
			icon = PoolConexiones.getInstancia().obtenerConexion();
			
			// Guardamos la partida.
			Partida partida = partidas.obtener(idPartida);
			daoPartidas.eliminar(idPartida, icon);
			daoPartidas.insertar(partida, icon);
			
			// Guardamos los barcos de la partida.
			for(Barco b : partida.getBarcos().listarBarcos()) {
				daoBarcos.eliminar(b.getId(), icon);
				// Insertamos el barco.
				daoBarcos.insertar(partida.getId(), b, icon);
			}
		} catch (LogicaException | PersistenciaException e) {
			// Liberamos la conexión.
			try {
				PoolConexiones.getInstancia().liberarConexion(icon, false);
			} catch (LogicaException e1) {
				r.setExito(false, e1.getMessage());
				return r;
			}
			
			r.setExito(false, e.getMessage());
			return r;
		}	
		
		// Liberamos la conexión.
		try {
			PoolConexiones.getInstancia().liberarConexion(icon, true);
		} catch (LogicaException e) {
			r.setExito(false, e.getMessage());
			return r;
		}
		
		return r;
	}
}