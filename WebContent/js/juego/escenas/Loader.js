class Loader extends Phaser.Scene {
	
	/**
	 * Constructor.
	 */
	constructor() {
		super('Loader');
	}
	
	/**
	 * Pre-carga de recursos.
	 */
	preload() {
		contexto.escenaActual = 'Loader';
		this.load.bitmapFont('fuente', 'fonts/font.png', 'fonts/font.fnt');
		this.load.bitmapFont('fuentePartida', 'fonts/font-partida.png', 'fonts/font-partida.fnt');
		this.load.image('fondo', 'img/fondo.jpg');
		this.load.image('fondoJuego', 'img/Costa.png');
		
		contexto.websocket.conectar();
	}
	
	/**
	 * Creación de la escena.
	 */
	create() {
		contexto.funciones.cambiarEscena('Menu');
	}
}