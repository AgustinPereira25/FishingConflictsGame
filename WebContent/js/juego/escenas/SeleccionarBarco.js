class SeleccionarBarco extends Phaser.Scene {
	
	/**
	 * Constructor.
	 */
	constructor() {
		super('SeleccionarBarco');
	}
	
	/**
	 * Pre-carga de recursos.
	 */
	preload() {
		
	}

	/**
	 * Creación de la escena.
	 */
	create() {
		contexto.escenaActual = 'SeleccionarBarco';
		
		// Fondo.
		this.add.sprite(0, 0, 'fondo').setOrigin(0, 0);
		
		// Título.
		this.add.bitmapText(470, 50, 'fuente', 'Partida: ' + contexto.partida.id, 40);
		this.add.bitmapText(415, 90, 'fuente', 'Seleccionar barco', 40);
		
		// Volver.
		this.add.existing(new Boton(this, 550, 450, 'Volver', 
			{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '15px' },
			() => { contexto.funciones.cambiarEscena('Menu'); }
		));
		
		// Botones del menú.
		if (contexto.partida.cantidadPatrullasDisponibles > 0) {
			// Patrulla ligera.
			this.add.existing(new Boton(this, 470, 160, 'Patrulla ligera', 
				{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '15px' },
				() => {
					contexto.funciones.enviarMensaje(
						contexto.mensajes.conectarAPartida('patrulla', 'ligera')
					);
				}
			));
				
			// Patrulla pesada.
			this.add.existing(new Boton(this, 470, 210, 'Patrulla pesada', 
				{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '15px' },
				() => {
					contexto.funciones.enviarMensaje(
						contexto.mensajes.conectarAPartida('patrulla', 'pesada')
					);
				}
			));
		}
			
		if (contexto.partida.cantidadPesquerosDisponibles > 0) {
			// Pesquero fábrica.
			this.add.existing(new Boton(this, 470, 260, 'Pesquero fábrica', 
				{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '15px' },
				() => { 
					contexto.funciones.enviarMensaje(
						contexto.mensajes.conectarAPartida('pesquero', 'fabrica')
					);
				}
			));
				
			// Pesquero convencional.
			this.add.existing(new Boton(this, 470, 310, 'Pesquero convencional', 
				{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '15px' },
				() => { 
					contexto.funciones.enviarMensaje(
						contexto.mensajes.conectarAPartida('pesquero', 'convencional')
					);
				}
			));
		}
	}
}