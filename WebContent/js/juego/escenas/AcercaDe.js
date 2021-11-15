class AcercaDe extends Phaser.Scene {
	
	/**
	 * Constructor.
	 */
	constructor() {
		super('AcercaDe');
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
		contexto.escenaActual = 'AcercaDe';
		this.add.sprite(0, 0, 'fondo').setOrigin(0, 0);
		this.add.text(450, 60, 'Desarrollado por', { fontFamily: '"Press Start 2P"', fontSize: "20px", fill: '#000000'});
		this.add.text(400, 130, 'Nicolás Adaime, Santiago Cajal,Rodrigo Gordano,', { fontFamily: '"Press Start 2P"', fontSize: "10px", fill: '#000000'});
		this.add.text(400, 150, 'Jonas Mattos, Agustín Pereira.', { fontFamily: '"Press Start 2P"', fontSize: "10px", fill: '#000000'});
		
		// Volver.
		this.add.existing(new Boton(this, 550, 450, 'Volver', 
			{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '15px' },
			() => { contexto.funciones.cambiarEscena('Menu'); }
		));
	}
}