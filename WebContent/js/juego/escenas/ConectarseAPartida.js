class ConectarseAPartida extends Phaser.Scene {
	
	/**
	 * Constructor.
	 */
	constructor() {
		super('ConectarseAPartida');
	}

	/**
	 * Pre-carga de recursos.
	 */
	preload() {
		this.load.html('formularioConectarseAPartida', 'js/juego/formularios/conectarseAPartida.html');
	}	
	
	/**
	 * Creación de escena.
	 */
	create() {
		contexto.escenaActual = 'ConectarseAPartida';
		
		// Fondo.
		this.add.sprite(0, 0, 'fondo').setOrigin(0, 0);
		
		// Título.
		this.add.bitmapText(400, 50, 'fuente', 'Conectarse a partida', 40);
		
		// Volver.
		this.add.existing(new Boton(this, 570, 450, 'Volver', 
			{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '15px' },
			() => { contexto.funciones.cambiarEscena('Menu'); }
		));
		
		// Agregamos el formulario.
		var formulario = this.add.dom(620, 250).createFromCache('formularioConectarseAPartida')
			.addListener('click').on('click', function(evento) {
				if (evento.target.name != 'submit')
					return;
				
				var idPartida = this.getChildByName('id_partida').value;
				
				if (idPartida < 0) {
					alert('Ingresa el n° de la partida');
					return;
				}
				
				// Enviamos el mensaje al servidor.
				contexto.partida.id = idPartida;
				contexto.funciones.enviarMensaje(contexto.mensajes.obtenerBarcosDisponibles());
			});
	}
}