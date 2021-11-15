class ClienteWebSocket {
	
	/**
	 * Constructor.
	 *
	 * @param host
	 * @param puerto
	 * @param endpoint
	 */
	constructor(host, puerto, endpoint) {
		this.url = `ws://${host}:${puerto}${endpoint}`;
	}
	
	/**
	 * Establece la conexión con el servidor.
	 */
	conectar() {
		try {
			this.webSocket = new WebSocket(this.url);
			contexto.funciones.inicializarWebSocket(this.webSocket);
		} catch (excepcion) {		
			alert('No se pudo establecer una conexión con el servidor.\nRevisa la consola del navegador para conocer el error.');
			console.log(excepcion);
		}
	}
	
	/**
	 * Envía un mensaje al servidor.
	 *
	 * @param mensaje
	 */
	enviarMensaje(mensaje) {
		if (this.webSocket.readyState == WebSocket.OPEN) {
			this.webSocket.send(mensaje);
		}
	}
	
	/**
	 * Cierra la conexión con el servidor.
	 */
	desconectar() {
		if (this.webSocket.readyState == WebSocket.OPEN) {
			this.webSocket.close();
		}
	}
}