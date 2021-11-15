package fishingconflicts.logica.interfaces;

import java.util.HashMap;

import javax.websocket.Session;

import fishingconflicts.logica.Resultado;

public interface IFachada {

	Resultado nuevaPartida(int cantidadPatrullas, int cantidadPesqueros);
	
	Resultado obtenerBarcosDisponibles(int idPartida);
	
	Resultado conectarAPartida(int idPartida, String tipoBarco, String subtipoBarco, Session sesion);
	
	Resultado obtenerPartidaJson(int idPartida);
	
	void moverBarco(int idPartida, int idBarco, String direccion, int angulo);
	
	boolean disparar(int idPartida, int idBarco, int idBala, int idBarcoOponente);
	
	void capturar(int idPartida, int idBarco, int idBarcoOponente);
	
	void pescar(int idPartida, int idBarco);
	
	HashMap<Integer, Session> obtenerSesionesDePartida(int idPartida);
	
	int desconectarSesion(Session sesion);
	
	Session obtenerSesionBarco(int idPartida, int idBarco);
	
	void activarHelicoptero(int idPartida, int idBarco);
	
	void desactivarHelicoptero(int idPartida, int idBarco);
	
	void habilitarBoteLigero(int idPartida, int idBarco);
	
	void deshabilitarBoteLigero(int idPartida, int idBarco);
	
	void moverBoteLigero(int idPartida, int idBarco, int posicionX, int posicionY, int angulo);
	
	void crearBala(int idPartida, int idBarco, int posicionDestinoX, int posicionDestinoY, String tipoArma);
	
	void moverBala(int idPartida, int idBarco, int idBala, int posicionX, int posicionY);
	
	void eliminarBala(int idPartida, int idBarco, int idBala);
	
	Resultado guardarPartida(int idPartida);
}
