class Bala extends Phaser.GameObjects.Sprite {
	
	/**
	 * Constructor.
	 */
	constructor(id, escena, x, y, idBarco, tipoArma) {
		var imagen = (tipoArma == 'ametralladora') ? 'BalaAmetralladora' : 'BalaCanion';
		super(escena, x, y, imagen);
		this.id = id;
		this.idBarco = idBarco;
		escena.add.existing(this);
		this.graficos = escena.add.graphics();
		this.cuerpo = new Phaser.Geom.Circle(x, y, 2);
	}
	
	/**
	 * Setea la posición.
	 *
	 * @param x
	 * @param y
	 */
	setPosicion(x, y) {
		this.graficos.clear();
		this.graficos.lineStyle(2, 0xffff00);
		this.graficos.strokeCircleShape(this.cuerpo);
		this.cuerpo.setPosition(x, y);
		
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Elimina al barco.
	 */
	eliminar() {
		this.graficos.destroy();
		this.destroy();
	}
}