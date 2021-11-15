class NuevaPartida extends Phaser.Scene {
	
	/**
	 * Constructor.
	 */
	constructor() {
		super('NuevaPartida');
	}
	
	/**
	 * Pre-carga de recursos.
	 */
	preload() {
		this.load.html('formularioNuevaPartida', 'js/juego/formularios/nuevaPartida.html');
	}
	
	/**
	 * Creación de la escena.
	 */
	create() {
		contexto.escenaActual = 'NuevaPartida';
		
		// Fondo.
		this.add.sprite(0, 0, 'fondo').setOrigin(0, 0);
		
		// Título.
		this.add.bitmapText(450, 50, 'fuente', 'Nueva partida', 40);
		
		// Volver.
		this.add.existing(new Boton(this, 560, 450, 'Volver', 
			{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '15px' },
			() => { contexto.funciones.cambiarEscena('Menu'); }
		));
		
		// Agregamos el formulario.
		var formulario = this.add.dom(600, 250).createFromCache('formularioNuevaPartida')
			.addListener('click').on('click', function(evento) {
				if (evento.target.name != 'submit')
					return;
				
				var cantidadPatrullas = this.getChildByName('cantidad_patrullas').value;
				var cantidadPesqueros = this.getChildByName('cantidad_pesqueros').value;
				
				if (cantidadPatrullas < 1 || cantidadPesqueros < 1) {
					alert('Ingresa al menos un jugador de cada equipo');
					return;
				} else if (cantidadPatrullas > 2 || cantidadPesqueros > 2) {
					alert('Ingresa 2 jugadores por equipo como máximo');
					return;
				}
				
				// Enviamos el mensaje al servidor.
				contexto.funciones.enviarMensaje(
					contexto.mensajes.nuevaPartida(cantidadPatrullas, cantidadPesqueros)
				);
			});
	}
}