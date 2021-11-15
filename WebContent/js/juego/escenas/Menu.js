class Menu extends Phaser.Scene {
	
	/**
	 * Constuctor.
	 */
	constructor() {
		super('Menu');
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
		contexto.escenaActual = 'Menu';
	
		// Fondo.
		this.add.sprite(0, 0, 'fondo').setOrigin(0, 0);
		
		// Título.
		this.add.bitmapText(400, 50, 'fuente', 'Fishing Conflicts', 40);
		
		// Botones del menú.
		// Nueva partida.
		this.add.existing(new Boton(this, 400, 150, 'Nueva partida', 
			{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '15px' },
			() => { contexto.funciones.cambiarEscena('NuevaPartida'); }
		));
			
		// Conectarse a partida.
		this.add.existing(new Boton(this, 400, 200, 'Conectarse a partida', 
			{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '15px' },
			() => { contexto.funciones.cambiarEscena('ConectarseAPartida'); }
		));
			
		// Acerca de.
		this.add.existing(new Boton(this, 400, 250, 'Acerca de', 
			{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '15px' },
			() => contexto.funciones.cambiarEscena('AcercaDe')
		));
	}
}