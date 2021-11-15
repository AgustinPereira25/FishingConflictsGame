package fishingconflicts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;

import fishingconflicts.excepciones.LogicaException;
import fishingconflicts.excepciones.PersistenciaException;
import fishingconflicts.logica.Fachada;
import fishingconflicts.logica.Resultado;

@ServerEndpoint("/websocket")
public class WebSocket {
	
	/**
	 * Fachada.
	 */
	private Fachada fachada;
	
	/**
	 * Esta función es ejecutada al detectar
	 * una nueva conexión al servidor.
	 * 
	 * @param sesion
	 */
	@OnOpen
	public void onOpen(Session sesion) {
		try {
			fachada = Fachada.getInstancia();
		} catch (PersistenciaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LogicaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Esta función se ejecuta al detectar que un
	 * cliente se desconecta del servidor.
	 * 
	 * @param sesion
	 */
	@OnClose
	public void onClose(Session sesion, CloseReason reason) {
		int idPartida = fachada.desconectarSesion(sesion);
		
		if (idPartida != 0) {
			try {
				sincronizarPartida(idPartida);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Esta función se ejecuta al recibir un nuevo mensaje
	 * desde algún cliente.
	 * 
	 * @param mensaje
	 * @param sesion
	 */
	@OnMessage
	public void onMessage(String mensaje, Session sesion) {	
		try {
			// Convertimos el mensaje a objetos JSON.
			JSONObject operacion = (new JSONObject(mensaje)).getJSONObject("operacion");
			JSONObject parametros = operacion.has("parametros") ? operacion.getJSONObject("parametros") : null;
			
			// Despachamos la operación.
			despacharOperacion(operacion, parametros, sesion);	
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Despacha la operación que vino en el mensaje.
	 * 
	 * @param operacion
	 * @param parametros
	 * @param sesion
	 */
	public void despacharOperacion(JSONObject operacion, JSONObject parametros, Session sesion) {
		Resultado resultado = null;
		
		try {
			// Nueva partida
			if (operacion.getString("nombre").equalsIgnoreCase("nuevaPartida")) {
				resultado = fachada.nuevaPartida(parametros.getInt("cantidadPatrullas"),
						parametros.getInt("cantidadPesqueros"));
				
				// Seteamos la operación al resultado y enviamos el mensaje
				// al cliente.
				resultado.setOperacion(operacion);
				sesion.getBasicRemote().sendText(resultado.toParsedString());
			}
			
			// Obtener los barcos disponibles para la partida.
			if (operacion.getString("nombre").equalsIgnoreCase("obtenerBarcosDisponibles")) {
				resultado = fachada.obtenerBarcosDisponibles(parametros.getInt("idPartida"));
				
				// Seteamos la operación al resultado y enviamos el mensaje
				// al cliente.
				resultado.setOperacion(operacion);
				sesion.getBasicRemote().sendText(resultado.toParsedString());
			}
			
			// Conectar el jugador a la partida.
			if (operacion.getString("nombre").equalsIgnoreCase("conectarAPartida")) {
				resultado = fachada.conectarAPartida(parametros.getInt("idPartida"),
						parametros.getString("tipoBarco"), parametros.getString("subtipoBarco"), sesion);
				
				// Seteamos la operación al resultado y enviamos el mensaje
				// al cliente.
				resultado.setOperacion(operacion);
				sesion.getBasicRemote().sendText(resultado.toParsedString());
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Sincroniza la partida con el cliente. Obtiene el objeto Partida en
			// formato JSON y se lo manda al jugador.
			if (operacion.getString("nombre").equalsIgnoreCase("sincronizarPartida")) {
				resultado = fachada.obtenerPartidaJson(parametros.getInt("idPartida"));
				
				// Seteamos la operación al resultado y enviamos el mensaje
				// al cliente.
				resultado.setOperacion(operacion);
				sesion.getBasicRemote().sendText(resultado.toParsedString());
			}
			
			// Mueve el barco en la dirección dada.
			if (operacion.getString("nombre").equalsIgnoreCase("moverBarco")) {
				fachada.moverBarco(parametros.getInt("idPartida"), parametros.getInt("idBarco"),
						parametros.getString("direccion"), parametros.getInt("angulo"));
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Pesca.
			if (operacion.getString("nombre").equalsIgnoreCase("pescar")) {
				fachada.pescar(parametros.getInt("idPartida"), parametros.getInt("idBarco"));
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Activa el helicóptero del barco.
			if (operacion.getString("nombre").equalsIgnoreCase("activarHelicoptero")) {
				fachada.activarHelicoptero(parametros.getInt("idPartida"), parametros.getInt("idBarco"));
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Desactiva el helicóptero del barco.
			if (operacion.getString("nombre").equalsIgnoreCase("desactivarHelicoptero")) {
				fachada.desactivarHelicoptero(parametros.getInt("idPartida"), parametros.getInt("idBarco"));
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Activa vista lateral
			if (operacion.getString("nombre").equalsIgnoreCase("vistaLateral")) {
				resultado = fachada.vistaLateral(parametros.getInt("idPartida"));
				
				// Seteamos la operación al resultado y enviamos el mensaje
				// al cliente.
				resultado.setOperacion(operacion);
				sesion.getBasicRemote().sendText(resultado.toParsedString());
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Desactiva vista lateral
			if (operacion.getString("nombre").equalsIgnoreCase("vistaLateralDesactivar")) {
				resultado = fachada.vistaLateralDesactivar(parametros.getInt("idPartida"));
				
				// Seteamos la operación al resultado y enviamos el mensaje
				// al cliente.
				resultado.setOperacion(operacion);
				sesion.getBasicRemote().sendText(resultado.toParsedString());
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}			
			
			// Activa un bote ligero.
			if (operacion.getString("nombre").equalsIgnoreCase("habilitarBoteLigero")) {
				fachada.habilitarBoteLigero(parametros.getInt("idPartida"), parametros.getInt("idBarco"));
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Desactiva un bote ligero.
			if (operacion.getString("nombre").equalsIgnoreCase("deshabilitarBoteLigero")) {
				fachada.habilitarBoteLigero(parametros.getInt("idPartida"), parametros.getInt("idBarco"));
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Mueve un bote ligero.
			if (operacion.getString("nombre").equalsIgnoreCase("moverBoteLigero")) {
				fachada.moverBoteLigero(parametros.getInt("idPartida"), parametros.getInt("idBarco"),
						parametros.getInt("x"), parametros.getInt("y"), parametros.getInt("angulo"));
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Captura un bote.
			if (operacion.getString("nombre").equalsIgnoreCase("capturar")) {
				fachada.capturar(parametros.getInt("idPartida"), parametros.getInt("idBarco"),
						parametros.getInt("idBarcoOponente"));
				
				// Enviamos el mensaje de eliminación al jugador correspondiente.
				Resultado r = new Resultado();
				r.setOperacion((new JSONObject()).put("nombre", "jugadorCapturado"));
				Session s = fachada.obtenerSesionBarco(parametros.getInt("idPartida"), parametros.getInt("idBarcoOponente"));
				
				s.getBasicRemote().sendText(r.toParsedString());
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Crea una nueva bala (cuando alguien dispara).
			if (operacion.getString("nombre").equalsIgnoreCase("crearBala")) {
				fachada.crearBala(parametros.getInt("idPartida"), parametros.getInt("idBarco"),
						parametros.getInt("posicionDestinoX"), parametros.getInt("posicionDestinoY"),
						parametros.getString("tipoArma"));
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Mueve una bala.
			if (operacion.getString("nombre").equalsIgnoreCase("moverBala")) {				
				fachada.moverBala(parametros.getInt("idPartida"), parametros.getInt("idBarco"), parametros.getInt("idBala"),
						parametros.getInt("x"), parametros.getInt("y"));
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Dispara contra un jugador.
			if (operacion.getString("nombre").equalsIgnoreCase("disparar")) {
				boolean murio = fachada.disparar(parametros.getInt("idPartida"), parametros.getInt("idBarco"), parametros.getInt("idBala"),
						parametros.getInt("idBarcoOponente"));
				
				// Le avisamos al jugador que murió.
				if (murio) {
					Resultado r = new Resultado();
					r.setOperacion(((new JSONObject()).put("nombre", "jugadorMuerto")));
					Session s = fachada.obtenerSesionBarco(parametros.getInt("idPartida"), parametros.getInt("idBarcoOponente"));
					s.getBasicRemote().sendText(r.toParsedString());
				}
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Elimina una bala.
			if (operacion.getString("nombre").equalsIgnoreCase("eliminarBala")) {				
				fachada.eliminarBala(parametros.getInt("idPartida"), parametros.getInt("idBarco"),
						parametros.getInt("idBala"));
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Guarda una partida.
			if (operacion.getString("nombre").equalsIgnoreCase("guardarPartida")) {
				resultado = fachada.guardarPartida(parametros.getInt("idPartida"));
				
				resultado.setOperacion(operacion);
				sesion.getBasicRemote().sendText(resultado.toParsedString());
				
				sincronizarPartida(parametros.getInt("idPartida"));
			}
			
			// Desconecta una sesión de la partida.
			if (operacion.getString("nombre").equalsIgnoreCase("desconectarSesion")) {
				int idPartida = fachada.desconectarSesion(sesion);
				
				if (idPartida != 0) {
					sincronizarPartida(idPartida);
				}
			}
			
			// Envía un mensaje de un jugador a otro.
			if (operacion.getString("nombre").equalsIgnoreCase("aJugador")) {
				Session sesionJugador = fachada.obtenerSesionBarco(parametros.getInt("idPartida"),
						parametros.getInt("idJugador"));
				
				Resultado r = new Resultado();
				r.setOperacion(((new JSONObject()).put("nombre", parametros.getString("mensaje"))));
				sesionJugador.getBasicRemote().sendText(r.toParsedString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sincroniza la partida con los jugadores.
	 * 
	 * @param idPartida
	 * @throws IOException 
	 */
	public void sincronizarPartida(int idPartida) throws IOException {
		// Obtenemos la partida como JSON y agregamos el nombre de la operación.
		Resultado resultado = fachada.obtenerPartidaJson(idPartida);
		resultado.setOperacion((new JSONObject()).put("nombre", "sincronizarPartida"));
		
		HashMap<Integer, Session> sesiones = fachada.obtenerSesionesDePartida(idPartida);
		if (sesiones != null) {
			for(Session s : sesiones.values())
				s.getBasicRemote().sendText(resultado.toParsedString());
		}
	}

	/**
	 * Función ejecutada al detectar un error de algún tipo
	 * con algún cliente.
	 * 
	 * @param error
	 */
	@OnError
	public void onError(Throwable e) {
		e.printStackTrace();
	}
}
