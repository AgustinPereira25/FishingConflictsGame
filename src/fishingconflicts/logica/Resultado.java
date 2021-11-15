package fishingconflicts.logica;

import org.json.JSONArray;
import org.json.JSONObject;

public class Resultado {

	/**
	 * Operación.
	 */
	private JSONObject operacion;
	
	/**
	 * Éxito de la ejecución.
	 */
	private JSONObject exito;
	
	/**
	 * Otros parámetros de respuesta.
	 */
	private JSONArray respuestas;
	
	/**
	 * Constructor.
	 * 
	 * @param operacion
	 */
	public Resultado() {
		this.respuestas = new JSONArray();
		this.setExito(true, "");
	}
	
	/**
	 * @param operacion the operacion to set
	 */
	public void setOperacion(JSONObject operacion) {
		this.operacion = operacion;
	}

	/**
	 * @param exito the exito to set
	 */
	public void setExito(boolean estado, String mensaje) {
		this.exito = new JSONObject();
		this.exito.put("estado", estado);
		this.exito.put("mensaje", mensaje);
	}
	
	/**
	 * Agrega un valor a la respuesta.
	 * 
	 * @param mixed
	 */
	public void agregarRespuesta(String nombre, String valor, String tipo) {
		JSONObject respuesta = new JSONObject();
		respuesta.put("nombre", nombre);
		respuesta.put("valor", valor);
		respuesta.put("tipo", tipo);
		
		this.respuestas.put(respuesta);
	}
	
	/**
	 * Elimina un valor de una respuesta.
	 * 
	 * @param String nombre
	 */
	public void eliminarRespuesta(String nombre) {
		for (int i = 0; i < this.respuestas.length(); i++) {
			if (((JSONObject) this.respuestas.get(i)).getString("nombre")
					.equalsIgnoreCase(nombre)) {
				
				this.respuestas.remove(i);
				break;
			}
		}
	}
	
	/**
	 * Convierte el resultado a un String parsead.
	 * 
	 * @return String
	 */
	public String toParsedString() {
		JSONObject resultado = new JSONObject();
		resultado.put("operacion", this.operacion);
		resultado.put("exito", this.exito);
		resultado.put("respuestas", this.respuestas);
		
		return resultado.toString();
	}
}
