class Barco extends Phaser.GameObjects.Sprite {
	
	/**
	 * Constructor.
	 */
	constructor(id, escena, x, y, tipo, subtipo, lateral = false) {
		var imagen;
		// Cargamos la imagen del barco, seguún el subtipo.
		switch(subtipo) {
			// Pesquero fábrica.
			case 'fabrica':
				imagen = lateral ? 'PesqueroFabricaLeft' : 'PesqueroFabricaUp';
				break;
				
			// Pesquero convencional.
			case 'convencional':
				imagen = lateral ? 'PesqueroConvencionalLeft' : 'PesqueroConvencionalUp';
				break;
				
			// Patrulla pesada.
			case 'pesada':
				imagen = lateral ? 'PatrullaPesadaRight' : 'PatrullaPesadaUp';
				break;
				
			// Patrulla ligera.
			case 'ligera':
				imagen = lateral ? 'PatrullaLigeraRight' : 'PatrullaLigeraUp';
				break;
			
			// Bote ligero.
			case 'ligero':
				imagen = 'BoteLigero';
				break;
				 
			default:
				imagen = 'bala';
				break;
		}

		// Constructor de Phaser.GameObjects.Sprite
		super(escena, x, y, imagen);
		
		// Cargamos las variables.
		escena.add.existing(this);
		this.id = id;
		this.tipo = tipo;
		this.subtipo = subtipo;
		
		// Creamos el cuerpo de colisión.
		switch(subtipo) {				
			case 'fabrica':
				this.cuerpo = new Phaser.Geom.Rectangle(x - 10, y - 45, 20, 90);
				break;
			
			case 'convencional':
				this.cuerpo = new Phaser.Geom.Rectangle(x - 15, y - 40, 30, 80);
				break;
			
			default:
				this.cuerpo = new Phaser.Geom.Rectangle(x - 10, y - 40, 20, 80);
				break;
		}
		
	}
	
	/**
	 * Setea la posición.
	 *
	 * @param x
	 * @param y
	 */
	setPosicion(x, y, angulo = null) {
		if (angulo != null) {
			this.angle = angulo;
		}
		
		this.cuerpo.angle = this.angle;
		
		switch(this.subtipo) {				
			case 'fabrica':
				this.cuerpo.setPosition(x - 10, y - 45);
				break;
			
			case 'convencional':
				this.cuerpo.setPosition(x - 15, y - 40);
				break;
			
			default:
				this.cuerpo.setPosition(x - 10, y - 40);
				break;
		}
		
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Elimina al barco.
	 */
	eliminar() {
		this.destroy();
	}
}