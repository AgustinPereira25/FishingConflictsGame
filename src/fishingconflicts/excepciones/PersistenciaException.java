package fishingconflicts.excepciones;

public class PersistenciaException extends Exception {

	/**
	 * Mensaje de error.
	 */
	private String mensaje;
	
	/**
	 * Constructor.
	 * 
	 * @param mensaje
	 */
	public PersistenciaException(String mensaje) {
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
