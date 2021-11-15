class Boton extends Phaser.GameObjects.Text {
	constructor(escena, x, y, texto, estilo, funcion) {
		super(escena, x, y, texto, estilo);
		
		this.setInteractive({ useHandCursor: true })
			.on('pointerover', () => this.setStyle({ fill: '#ff0' }))
			.on('pointerout', () => this.setStyle({ fill: '#fff', }))
			.on('pointerdown', () => this.setStyle({ fill: '#0ff' }))
			.on('pointerup', () => {
				this.setStyle({ fill: '#ff0' });
				funcion();
			});
	}
}