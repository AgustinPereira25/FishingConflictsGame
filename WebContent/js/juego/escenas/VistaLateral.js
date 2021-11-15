class VistaLateral extends Phaser.Scene {
	
	/**
	 * Constructor.
	 */
	constructor() {
		super('VistaLateral');
	}
	
	/**
	 * Pre-carga de recursos.
	 */
	preload() {
		this.load.image('PatrullaLigeraRight', 'img/lateral-PatrullaLigeraRight.png');
		this.load.image('PatrullaPesadaRight', 'img/lateral-PatrullaPesadaRight.png');
		
		this.load.image('PesqueroFabricaRight', 'img/lateral-PesqueroFabricaRight.png');
		this.load.image('PesqueroFabricaLeft', 'img/lateral-PesqueroFabricaLeft.png');
		this.load.image('PesqueroConvencionalRight', 'img/lateral-PesqueroConvencionalRight.png');
		this.load.image('PesqueroConvencionalLeft', 'img/lateral-PesqueroConvencionalLeft.png');
		
		// Carga de imagenes
		this.load.image('fondoLateral', 'img/fondoLateral.png');
	}
	
	/**
	 * Creación de escena.
	 */
	create() {			
		contexto.escenaActual = 'VistaLateral';
		
		// Fondo.
		this.add.sprite(0, 0, 'fondoLateral').setOrigin(0, 0);
		
		//Vista lateral
		this.botonVistaLateral = this.add.existing(new Boton(this, 10, 10, 'Volver al juego',
				{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '10px' },
				() => { contexto.funciones.enviarMensaje(contexto.mensajes.desactivarVistaLateral());
						contexto.funciones.cambiarEscena('Partida');	
						}
				));
		
		this.sprites = null;
	}
	
	/**
	 * Loop principal.
	 */
	update(time, delta) {
		this.actualizarBarcos();
	}
	
	/**
	 * Actualiza los barcos de la partida.
	 */
	actualizarBarcos() {
		// Creamos los sprites de los barcos.
		if (this.sprites == null) {
			this.sprites = [];
			
			contexto.partida.barcos.forEach((barco) => {
				var img;
				var posicionX = barco.posicionY * 1250 / 530 - (150 * 1250 / 590);
				var posicionY = 530 - barco.posicionX * 350 / 1250;
				
				if (posicionY < 250)
					posicionY = 250;
				
				var sprite = new Barco(barco.id, this, posicionX, posicionY, barco.tipo, barco.subtipo, true);
				
				// Escalamos la imágen
				sprite.setScale(1 + (posicionY - 240) / 100);
				
				this.sprites.push({
					barco: sprite,
				});
			});
		}
	}
}