package fishingconflicts.excepciones;

public class LogicaException extends Exception {

	/**
	 * Mensaje de error.
	 */
	private String mensaje;
	
	/**
	 * Constructor.
	 * 
	 * @param mensaje
	 */
	public LogicaException(String mensaje) {
		this.mensaje = mensaje;
	}
	
	/**
	 * Devuelve el mensaje de error.
	 * 
	 * @return String
	 */
	public String getMessage() {
		return this.mensaje;
	}
}
