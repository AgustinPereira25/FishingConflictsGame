class Partida extends Phaser.Scene {
	
	/**
	 * Constructor.
	 */
	constructor() {
		super('Partida');
	}
	
	/**
	 * Pre-carga de recursos.
	 */
	preload() {
		this.load.image('PesqueroFabricaUp', 'img/PesqueroFabricaUp.png');		
		this.load.image('PesqueroConvencionalUp', 'img/PesqueroConvencionalUp.png');		
		this.load.image('PatrullaPesadaUp', 'img/PatrullaPesadaUp.png');
		this.load.image('PatrullaLigeraUp', 'img/PatrullaLigeraUp.png');
		this.load.image('BoteLigero', 'img/BoteLigero.png');
		this.load.image('BalaAmetralladora', 'img/BalaAmetralladora.png');
		this.load.image('BalaCanion', 'img/BalaCanion.png');
		
		this.load.audio('m', [
	        'sound/Battle.mp3',
	        'sound/Battle.mp3'
	    ]);
	}
	
	/**
	 * Creación de escena.
	 */
	create() {			
		// Musica juego
		this.musica = this.sound.add('m');
		this.musica.play();
		
		contexto.escenaActual = 'Partida';
		
		// Fondo.
		this.add.sprite(0, 0, 'fondoJuego').setOrigin(0, 0);

		this.barcoJugador = contexto.partida.barcos.filter((barco) => barco.id == contexto.idBarco)[0];
		if (this.barcoJugador.tipo == 'patrulla') {
			// Nivel de combustible.
			this.textoCombustible = this.add.bitmapText(5, 5, 'fuentePartida', 'Combustible:' + (Math.round((contexto.partida.stockCombustiblePatrullas + Number.EPSILON) * 100) / 100), 18);
			
			// Armas disponibles.
			// Ametralladora.
			this.botonAmetralladora = this.add.existing(new Boton(this, 225, 9, 'Ametralladora', 
				{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '10px'},
				() => { if (contexto.partida.estado == 2) this.crearBala('ametralladora'); }
			));
			
			this.botonAmetralladora.setVisible(false);
			
			if (this.barcoJugador.subtipo == 'pesada') {
				this.botonCanion = this.add.existing(new Boton(this, 370, 9, 'Cañón', 
					{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '10px' },
					() => { if (contexto.partida.estado == 2) this.crearBala('canion'); }				
				));
				
				this.botonCanion.setVisible(false);
				
				// Bote ligero
				this.botonCapturar = this.add.existing(new Boton(this, 440, 9, 'Capturar', 
					{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '10px' },
					() => { if (contexto.partida.estado == 2) this.capturandoBarco = true; }
				));	
				
				this.botonCapturar.setVisible(false);
				
				if (!this.barcoJugador.helicopteroUtilizado) {
					// Helicoptero
					this.botonHelicoptero = this.add.existing(new Boton(this, 535, 9, 'Helicoptero', 
						{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '10px' },
						() => {
							if (contexto.partida.estado == 2) {
								contexto.funciones.enviarMensaje(contexto.mensajes.activarHelicoptero(this.barcoJugador.id));
								this.botonHelicoptero.setVisible(false);
								this.botonHelicoptero.destroy();
							}
						}
					));
				}
			}
		} else {
			// Vida.
			this.textoVida = this.add.bitmapText(240, 5, 'fuentePartida', 'Vida: ' + this.barcoJugador.vida, 18);
			
			// Nivel de combustible.
			this.textoCombustible = this.add.bitmapText(5, 5, 'fuentePartida', 'Combustible:' + (Math.round((contexto.partida.stockCombustiblePesqueros + Number.EPSILON) * 100) / 100), 18);
			
			// Nivel de peces.
			this.textoPeces = this.add.bitmapText(370, 5, 'fuentePartida', 'Peces:' + (Math.round((contexto.partida.stockPeces + Number.EPSILON) * 100) / 100), 18);
		}
		
		//Guardar partida
		this.botonGuardarPartida = this.add.existing(new Boton(this, 670, 9, 'Guardar partida',
			{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '10px' },
			() => { contexto.funciones.enviarMensaje(contexto.mensajes.guardarPartida()); }
		));
		
		//Vista lateral
		this.botonVistaLateral = this.add.existing(new Boton(this, 840, 9, 'Vista lateral',
			{ fill: '#fff', fontFamily: '"Press Start 2P"', fontSize: '10px' },
			() => {
				if (contexto.partida.estado == 2) {
					this.musica.stop();
					contexto.funciones.enviarMensaje(contexto.mensajes.activarVistaLateral());
					contexto.funciones.cambiarEscena('VistaLateral');
				}
			}
		));
		
		this.textoJugadorEnZonaDeAtaque = this.add.bitmapText(500, 250, 'fuentePartida', '¡Estás en zona de ataque!', 18);
		this.textoJugadorEnZonaDeAtaque.setVisible(false);
		
		this.textoContadorZonaDeAtaque = this.add.bitmapText(500, 180, 'fuentePartida', '', 18);
		this.textoContadorZonaDeAtaque.setVisible(false);
		
		this.textoPartida = this.add.bitmapText(500, 250, 'fuentePartida', 'Partida en espera', 24);
		this.textoGanador = null;
		
		this.textoIntervaloHelicoptero = this.add.bitmapText(500, 180, 'fuentePartida', '', 18);
		this.textoJugadorCapturado = this.add.bitmapText(500, 180, 'fuentePartida', '', 18);
		this.textoMuerto = this.add.bitmapText(500, 180, 'fuentePartida', '', 18);
		this.keys = this.input.keyboard.createCursorKeys();
		this.sprites = [];
		this.spritesBotesLigeros = [];
		this.spritesBalas = [];
		this.intervalo = 0;
		this.intervaloHelicoptero = 0;
		this.radarAmetralladoraYCaptura = null;
		this.radarCanion = null;
		this.graficosRadarAmetralladora = this.add.graphics();
		this.graficosRadarCanion = this.add.graphics();
		this.capturandoBarco = false;
		this.volverABase = false;
		this.barcoOponenteEnRadar = null;
		this.barcoOponenteEnRadarCanion = false;
		this.boteLigero = null;
		this.mensajeBoteLigero = false;
		this.contadorAvisoRadar = 0;
		this.armasHabilitadas = false;
		contexto.jugadorCapturado = false;
		contexto.jugadorMuerto = false;
	}
	
	/**
	 * Loop principal.
	 */
	update(time, delta) {
		if (contexto.jugadorCapturado) {
			this.textoJugadorCapturado.setText('Has sido capturado!');
			
			// Eliminamos los barcos y los botes ligeros de la pantalla.
			this.sprites.forEach((sprite, index) => {
				sprite.barco.eliminar();
				this.sprites.splice(index, 1);
			});
			
			this.spritesBotesLigeros.forEach((sprite, index) => {
				sprite.barco.eliminar();
				this.spritesBotesLigeros.splice(index, 1);
			});
			
			this.spritesBalas.forEach((sprite, index) => {
				sprite.bala.eliminar();
				sprite.destino.destroy();
				this.spritesBalas.splice(index, 1);
			});
		} else if (contexto.jugadorMuerto) {
			this.textoMuerto.setText('Has muerto!');
			
			// Eliminamos los barcos, los botes ligeros y las balas de la pantalla.
			this.sprites.forEach((sprite, index) => {
				sprite.barco.eliminar();
				this.sprites.splice(index, 1);
			});
			
			this.spritesBotesLigeros.forEach((sprite, index) => {
				sprite.barco.eliminar();
				this.spritesBotesLigeros.splice(index, 1);
			});
			
			this.spritesBalas.forEach((sprite, index) => {
				sprite.bala.eliminar();
				sprite.destino.destroy();
				this.spritesBalas.splice(index, 1);
			});
		}
		else {
			this.actualizarCombustible();
			this.actualizarBarcos();
			this.actualizarPeces();
			this.actualizarHelicoptero(delta);
			this.actualizarCaptura();
			this.actualizarRadar();
			this.actualizarBalas();
			this.actualizarArmas(delta);
			this.chequearInterseccionConPesquero();
			
			// Si el estado de la partida es "en espera".
			if (contexto.partida.estado == 1 && !this.textoPartida.visible) {
				this.textoPartida.setVisible(true);
			}
		
			// Si el estado de la partida es "en juego".
			if (contexto.partida.estado == 2) {
				this.textoPartida.setVisible(false);
				this.intervalo += delta;
				this.chequearMovimientoDeBarco();
				this.actualizarCombustible();
				this.actualizarVida();
				
				// Si el jugador está en zona de ataque, mostramos el texto.
				this.textoJugadorEnZonaDeAtaque.setVisible(contexto.hasOwnProperty('jugadorEnZonaDeAtaque') ?
					contexto.jugadorEnZonaDeAtaque : false);
				
				// En caso de que el barco sea pesquero y haya pasado un segundo, pescamos.
				if (this.intervalo >= 1000) {
					this.intervalo = 0;
					if (this.barcoJugador.posicionY < 356)
						contexto.funciones.enviarMensaje(contexto.mensajes.pescar(this.barcoJugador.id));
				}
			}
		}
		
		// Si el estado de la partida es "finalizada".
		if (contexto.partida.estado == 3) {
			if (this.textoGanador == null) {
				this.textoGanador = this.add.bitmapText(75, 250, 'fuentePartida', 'Equipo ganador: ' + contexto.partida.equipoGanador, 24);
			}
		}
	}
	
	/**
	 * Actualiza la vida.
	 */
	actualizarVida() {
		if (this.barcoJugador.tipo != 'pesquero')
			return;
		
		var vida = this.barcoJugador.vida < 0 ? 0 : this.barcoJugador.vida;
		this.textoVida.setText('Vida: ' + vida);
	}
	
	/**
	 * Actualiza el helicóptero.
	 */
	actualizarHelicoptero(delta) {
		if (this.barcoJugador.tipo != 'patrulla' || this.barcoJugador.subtipo != 'pesada'
			|| !this.barcoJugador.helicopteroActivado)
			return;
		
		if (this.intervaloHelicoptero <= 10000) {
			this.intervaloHelicoptero += delta;
			this.textoIntervaloHelicoptero.setText('Ampliando la visual del radar,\ntiempo restante: ' + parseInt(11 - this.intervaloHelicoptero * 0.001));
		} else {
			contexto.funciones.enviarMensaje(contexto.mensajes.desactivarHelicoptero(this.barcoJugador.id));
			this.textoIntervaloHelicoptero.setVisible(false);
			this.textoIntervaloHelicoptero.destroy();
		}
	}
	
	/**
	 * Actualiza el combustible.
	 */
	actualizarCombustible() {
		var combustible;
		
		if (this.barcoJugador.tipo == 'patrulla')
			combustible = (Math.round((contexto.partida.stockCombustiblePatrullas + Number.EPSILON) * 100) / 100);
		else
			combustible = (Math.round((contexto.partida.stockCombustiblePesqueros + Number.EPSILON) * 100) / 100);
		
		combustible = combustible < 0 ? 0 : combustible;
		this.textoCombustible.setText('Combustible: ' + combustible);
	}
	
	/**
	 * Actualiza el stock de peces.
	 */
	actualizarPeces() {
		var peces = (Math.round((contexto.partida.stockPeces + Number.EPSILON) * 100) / 100);
		peces = peces < 0 ? 0 : peces;
		
		// Nivel de peces.
		if (this.barcoJugador.tipo == 'pesquero')
			this.textoPeces.setText('Peces:' + peces);
	}
	
	/**
	 * Chequea el movimiento del barco del jugador.
	 */
	chequearMovimientoDeBarco() {			
		// Movimiento del barco.
		// Obtenemos el sprite del jugador.
		var sprite = this.sprites.filter((sprite) => sprite.barco.id == this.barcoJugador.id)[0].barco;
		var angle360 = (sprite.angle < 0) ? 360 + sprite.angle : sprite.angle;
		
		// Arriba.
		if (this.keys.up.isDown && this.barcoJugador.posicionY > 120) {			
			// Giramos el barco.
			if (this.keys.left.isDown && angle360 != 315) {
				sprite.angle = (angle360 > 315 || angle360 < 135) ? sprite.angle - 1 : sprite.angle + 1;
			} else if (this.keys.right.isDown && angle360 != 45) {
				sprite.angle = (angle360 < 225 && angle360 > 45) ? sprite.angle - 1 : sprite.angle + 1;
			} else if (angle360 != 360) {
				sprite.angle = (angle360 < 180) ? sprite.angle - 1 : sprite.angle + 1;
			}
			
			contexto.funciones.enviarMensaje(contexto.mensajes.moverBarco(this.barcoJugador.id, 'arriba', sprite.angle));
		}
		
		// Abajo.
		if (this.keys.down.isDown) {
			if ((this.barcoJugador.tipo == 'patrulla' && this.barcoJugador.posicionY < 356)
				|| (this.barcoJugador.tipo == 'pesquero' && this.barcoJugador.posicionY < 540)) {
				
				// Giramos al barco.
				if (this.keys.left.isDown && angle360 != 225) {
					sprite.angle = (angle360 < 45 || angle360 > 225) ? sprite.angle - 1 : sprite.angle + 1;
				} else if (this.keys.right.isDown && angle360 != 135) {
					sprite.angle = (angle360 > 135 && angle360 < 315) ? sprite.angle - 1 : sprite.angle + 1;
				} else if (angle360 != 180) {
					sprite.angle = (angle360 > 180) ? sprite.angle - 1 : sprite.angle + 1;
				}
				
				contexto.funciones.enviarMensaje(contexto.mensajes.moverBarco(this.barcoJugador.id, 'abajo', sprite.angle));
			}
		}
		
		// Izquierda.
		if (this.keys.left.isDown && this.barcoJugador.posicionX > 13) {
			// Giramos al barco.
			if (this.keys.up.isDown && angle360 != 315) {
				sprite.angle = (angle360 > 315 || angle360 < 135) ? sprite.angle - 1 : sprite.angle + 1;
			} else if (this.keys.down.isDown && angle360 != 225) {
				sprite.angle = (angle360 < 45 || angle360 > 225) ? sprite.angle - 1 : sprite.angle + 1;
			} else if (angle360 != 270) {
				sprite.angle = (angle360 < 90 || angle360 > 270) ? sprite.angle - 1 : sprite.angle + 1;
			}
			
			contexto.funciones.enviarMensaje(contexto.mensajes.moverBarco(this.barcoJugador.id, 'izquierda', sprite.angle));
		}
		
		// Derecha.
		if (this.keys.right.isDown && this.barcoJugador.posicionX < 1230) {			
			// Giramos el barco.
			if (this.keys.up.isDown && angle360 != 45) {
				sprite.angle = (angle360 < 225 && angle360 > 45) ? sprite.angle - 1 : sprite.angle + 1;
			} else if (this.keys.down.isDown && angle360 != 135) {
				sprite.angle = (angle360 > 135 && angle360 < 315) ? sprite.angle - 1 : sprite.angle + 1;
			} else if (angle360 != 90) {
				sprite.angle = (angle360 > 90 && angle360 < 270) ? sprite.angle - 1 : sprite.angle + 1;
			}
			
			contexto.funciones.enviarMensaje(contexto.mensajes.moverBarco(this.barcoJugador.id, 'derecha', sprite.angle));
		}
	}
	
	/**
	 * Actualiza los barcos de la partida.
	 */
	actualizarBarcos() {
		// Verificamos si los barcos están creados como sprites y en caso
		// negativo, los creamos.
		contexto.partida.barcos.forEach((barco) => {
			var existeSpriteBarco = this.sprites.filter((s) => s.barco.id == barco.id)[0] != null
			if (!existeSpriteBarco) {
				
				if (barco.subtipo == 'fabrica')
					var b = new Barco(barco.id, this, barco.posicionX, barco.posicionY, 'pesquero', 'fabrica');
				else if (barco.subtipo == 'convencional')
					var b = new Barco(barco.id, this, barco.posicionX, barco.posicionY, 'pesquero', 'convencional');
				else if (barco.subtipo == 'pesada')
					var b = new Barco(barco.id, this, barco.posicionX, barco.posicionY, 'patrulla', 'pesada');
				else if (barco.subtipo == 'ligera')
					var b = new Barco(barco.id, this, barco.posicionX, barco.posicionY, 'patrulla', 'ligera');
				
				this.sprites.push({
					barco: b,
				});
				
				this.physics.world.enable(b);
			}
			
			// Creamos los botes ligeros.
			if (barco.tipo == 'patrulla' && barco.subtipo == 'pesada' && barco.boteLigeroActivado
				&& this.spritesBotesLigeros.filter((s) => s.barco.id == barco.id)[0] == null) {
				
				var boteLigero = new Barco(barco.id, this, barco.boteLigeroPosicionX, barco.boteLigeroPosicionY, 'bote', 'ligero');
				this.spritesBotesLigeros.push({
					barco: boteLigero,
				});
				
				this.physics.world.enable(boteLigero);
			}
		});
		
		// Actualizamos la posición de los barcos, los botes ligeros y chequeamos si todos los sprites
		// existen en la colección de barcos.
		this.sprites.forEach((sprite, index) => {
			var barco = contexto.partida.barcos.filter((b) => b.id == sprite.barco.id)[0];

			if (barco == null) {
				sprite.barco.eliminar();
				this.sprites.splice(index, 1);
				this.spritesBotesLigeros.forEach((s, index) => {
					if (s.barco.id == sprite.barco.id)
						this.spritesBotesLigeros.splice(index, 1);
				});
			} else {
				// Actualizamos el barco.
				sprite.barco.setPosicion(barco.posicionX, barco.posicionY, barco.angulo);
				if (barco.tipo == 'patrulla' && barco.subtipo == 'pesada' && barco.boteLigeroActivado) {
					var boteLigero = this.spritesBotesLigeros.filter((sprite) => sprite.barco.id == barco.id)[0].barco;
					
					if (boteLigero.id != this.barcoJugador.id) {
						boteLigero.setPosicion(barco.boteLigeroPosicionX, barco.boteLigeroPosicionY, barco.boteLigeroAngulo);
					}
				}
			}
		});
		
		this.barcoJugador = contexto.partida.barcos.filter((barco) => barco.id == contexto.idBarco)[0];
	}
	
	/**
	 * Actualizar la posición del radar.
	 */
	actualizarRadar() {
		// Si el barco del jugador es una patrulla, seteamos el alcance.
		if (this.barcoJugador.tipo == 'patrulla') {
			if (this.radarAmetralladoraYCaptura == null) {
				this.radarAmetralladoraYCaptura = new Phaser.Geom.Circle(10, 10, this.barcoJugador.alcance);
			}
			
			if (this.barcoJugador.subtipo == 'pesada') {
				if (this.radarCanion == null) 
					this.radarCanion = new Phaser.Geom.Circle(10, 10, this.barcoJugador.alcance / 2);
				
				this.graficosRadarCanion.clear();
				this.graficosRadarCanion.lineStyle(2, 0xff0000);
				this.graficosRadarCanion.strokeCircleShape(this.radarCanion);
				this.radarCanion.setTo(this.barcoJugador.posicionX, this.barcoJugador.posicionY, this.barcoJugador.alcance / 2);
			}
			
			this.graficosRadarAmetralladora.clear();
			this.graficosRadarAmetralladora.lineStyle(2, 0xffff00);
			this.graficosRadarAmetralladora.strokeCircleShape(this.radarAmetralladoraYCaptura);
			this.radarAmetralladoraYCaptura.setTo(this.barcoJugador.posicionX, this.barcoJugador.posicionY, this.barcoJugador.alcance);
		}
	}
	
	/**
	 * Actualiza los textos de armas.
	 */
	actualizarArmas(delta) {
		// Si las armas ya están habilitadas, chequeamos si el jugador ingresó o se fue de
		// la zona de ataque del cañón.
		if (this.armasHabilitadas && this.barcoJugador.subtipo == 'pesada') {
			this.botonCanion.setVisible(this.barcoOponenteEnRadarCanion);
		}
		
		if (this.barcoOponenteEnRadar == null || this.barcoJugador.tipo != 'patrulla' || this.armasHabilitadas) {
			this.textoContadorZonaDeAtaque.setVisible(false);
			return;
		}
		
		if (this.contadorAvisoRadar == 0)
			contexto.funciones.enviarMensaje(contexto.mensajes.aJugador(this.barcoOponenteEnRadar, 'activarZonaDeAtaque'));
		
		this.contadorAvisoRadar += delta;
		
		this.textoContadorZonaDeAtaque.setText('Tiempo restante para poder atacar: ' + parseInt(4000 - this.contadorAvisoRadar) / 1000);
		this.textoContadorZonaDeAtaque.setVisible(true);
		
		if (this.contadorAvisoRadar >= 3000) {
			this.textoContadorZonaDeAtaque.setVisible(false);
			this.contadorAvisoRadar = 0;
			this.armasHabilitadas = true;
			this.botonAmetralladora.setVisible(true);
			
			if (this.barcoJugador.subtipo == 'pesada') {
				this.botonCapturar.setVisible(true);
				
				if (this.barcoOponenteEnRadarCanion)
					this.botonCanion.setVisible(true);
			}
		}
	}
	
	/**
	 * Chequea si hay intersección entre una patrulla y un pesquero.
	 */
	chequearInterseccionConPesquero() {
		if (this.barcoJugador.tipo != 'patrulla')
			return;
		
		// Chequeamos si hay intersección con algún barco pesquero.
		var pesqueros = contexto.partida.barcos.filter((barco) => barco.tipo == 'pesquero');
		pesqueros.forEach((pesquero) => {
			var spritePesquero = this.sprites.filter((sprite) => sprite.barco.id == pesquero.id)[0];
			
			// Intersecta con el radar de ametralladora y captura.
			if (spritePesquero.barco.y < 356 && Phaser.Geom.Intersects.CircleToRectangle(this.radarAmetralladoraYCaptura, spritePesquero.barco.cuerpo)) {
				if (!this.capturandoBarco && (this.barcoOponenteEnRadar == null || this.barcoOponenteEnRadar == spritePesquero.barco.id)) {
					this.barcoOponenteEnRadar = spritePesquero.barco.id;
					
					// Intersecta con el radar de cañón.
					if (this.barcoJugador.subtipo == 'pesada' && Phaser.Geom.Intersects.CircleToRectangle(this.radarCanion, spritePesquero.barco.cuerpo)) {
						this.barcoOponenteEnRadarCanion = true;
					} else {
						this.barcoOponenteEnRadarCanion = false;
					}
				}
			} else {
				if (this.barcoOponenteEnRadar != null && spritePesquero.barco.id == this.barcoOponenteEnRadar) {
					contexto.funciones.enviarMensaje(contexto.mensajes.aJugador(this.barcoOponenteEnRadar, 'desactivarZonaDeAtaque'));
					
					this.contadorAvisoRadar = 0;
					this.armasHabilitadas = false;
					this.barcoOponenteEnRadar = null;
					
					if (this.capturandoBarco) {
						this.capturandoBarco = false;
						this.volverABase = true;
					}
					
					this.botonAmetralladora.setVisible(false);
					
					if (this.barcoJugador.subtipo == 'pesada') {
						this.botonCanion.setVisible(false);
						this.botonCapturar.setVisible(false);
					}
				}
			}
		});
	}
	
	/**
	 * Crea una nueva bala para el barco del jugador, con el tipo de arma especificado.
	 *
	 * @param tipoArma
	 */
	crearBala(tipoArma) {
		var p = this.sprites.filter((sprite) => sprite.barco.id == this.barcoOponenteEnRadar)[0];
		contexto.funciones.enviarMensaje(contexto.mensajes.crearBala(this.barcoJugador.id, p.barco.x, p.barco.y, tipoArma));
	}
	
	/**
	 * Actualiza las balas del barco y chequea colisiones.
	 */
	actualizarBalas() {
		// Verificamos si las balas están creadas y en caso negativo, las creamos.
		contexto.partida.barcos.map((barco) => barco.tipo == 'patrulla' ? barco : null).forEach((barco) => {
			if (barco != null) {
				barco.balas.forEach((bala) => {
					var existeBala = this.spritesBalas.filter((b) => b.bala.id == bala.id)[0] != null;
					if (!existeBala) {
						var b = new Bala(bala.id, this, bala.posicionX, bala.posicionY, barco.id, bala.tipoArma);
						this.spritesBalas.push({ bala: b, destino: new Phaser.GameObjects.Sprite(this, bala.posicionDestinoX, bala.posicionDestinoY, 'bala') });
						this.physics.world.enable(b);
					}
				});
			}
		});
		
		// Actualizamos la posición de las balas.
		this.spritesBalas.forEach((sprite, index) => {
			var bala = contexto.partida.barcos.filter((barco) => barco.id == sprite.bala.idBarco)[0].balas.filter((bala) => bala.id == sprite.bala.id)[0];
			
			if (bala == null) {
				sprite.bala.eliminar();
				sprite.destino.destroy();
				this.spritesBalas.splice(index, 1);
			} else {
				if (sprite.bala.idBarco == this.barcoJugador.id) {
					this.physics.moveToObject(sprite.bala, sprite.destino, (bala.tipoArma == 'ametralladora') ? 500 : 100);
					contexto.funciones.enviarMensaje(contexto.mensajes.moverBala(this.barcoJugador.id, sprite.bala.id, sprite.bala.x, sprite.bala.y));
					
					var huboColision = false;
					// Chequeamos si colisiona con algún pesquero.
					contexto.partida.barcos.filter((b) => b.tipo == 'pesquero').forEach((barco, index) => {
						var barco = this.sprites.filter((s) => s.barco.id == barco.id)[0].barco;
						var colisionador = this.physics.add.overlap(sprite.bala, barco, (bala) => {
							contexto.funciones.enviarMensaje(contexto.mensajes.disparar(sprite.bala.idBarco, sprite.bala.id, barco.id));
							huboColision = true;
							this.physics.world.removeCollider(colisionador);
						});
					});
					
					// Si llegó a destino y no colisionó, la eliminamos.
					if (!huboColision && sprite.bala.x >= sprite.destino.x - 1 && sprite.bala.x <= sprite.destino.x + 1
						&& sprite.bala.y >= sprite.destino.y - 1 && sprite.bala.y <= sprite.destino.y + 1) {
						contexto.funciones.enviarMensaje(contexto.mensajes.eliminarBala(sprite.bala.idBarco, sprite.bala.id));
					}
				} else {
					sprite.bala.setPosicion(bala.posicionX, bala.posicionY);
				}
			}			
		});
	}
	
	/**
	 * Actualiza el bote ligero de captura.
	 */
	actualizarCaptura() {		
		if (this.capturandoBarco) {
			this.boteLigero = this.spritesBotesLigeros.filter((s) => s.barco.id == this.barcoJugador.id)[0];

			if (this.boteLigero == null) {
				contexto.funciones.enviarMensaje(contexto.mensajes.habilitarBoteLigero(this.barcoJugador.id));
				return;
			}
			
			// Si estamos yendo a capturar.
			if (!this.volverABase) {
				var barcoACapturar = this.sprites.filter((sprite) => sprite.barco.id == this.barcoOponenteEnRadar)[0].barco;
				this.physics.moveToObject(this.boteLigero.barco, barcoACapturar);
				
				// Calculamos el ángulo.				
				this.boteLigero.barco.angle = - Phaser.Math.Angle.Between(this.boteLigero.barco.x, this.boteLigero.barco.y,
					barcoACapturar.x, barcoACapturar.y) * 180 / 3.14;
				
				// Chequeamos si colisiona con el pesquero.
				var colisionador = this.physics.add.overlap(this.boteLigero.barco, barcoACapturar, (boteLigero) => {
					this.volverABase = true;					
					this.physics.world.removeCollider(colisionador);
					
					contexto.funciones.enviarMensaje(contexto.mensajes.capturar(this.barcoJugador.id, barcoACapturar.id));
				});
			} else {
				var spriteJugador = this.sprites.filter((sprite) => sprite.barco.id == this.barcoJugador.id)[0].barco;
				this.physics.moveToObject(this.boteLigero.barco, spriteJugador);
				
				// Calculamos el ángulo.						
				this.boteLigero.barco.angle = - Phaser.Math.Angle.Between(this.boteLigero.barco.x, this.boteLigero.barco.y,
					spriteJugador.x, spriteJugador.y) * 180 / 3.14;
				
				// Chequeamos colisión con el barco patrulla de donde salió
				var colisionador = this.physics.add.overlap(this.boteLigero.barco, spriteJugador, (boteLigero) => {
					this.barcoOponenteEnRadar = null;
					this.capturandoBarco = false;
					this.volverABase = false;
					this.spritesBotesLigeros.map((sprite, index) => {
						if (sprite.barco.id == this.barcoJugador.id)
							this.spritesBotesLigeros.splice(index, 1);
					});
					this.boteLigero.barco.eliminar();
					this.physics.world.removeCollider(colisionador);
					
					contexto.funciones.enviarMensaje(contexto.mensajes.deshabilitarBoteLigero(this.barcoJugador.id));
				});
			}
			
			contexto.funciones.enviarMensaje(contexto.mensajes.moverBoteLigero(this.barcoJugador.id, this.boteLigero.barco.x, 
				this.boteLigero.barco.y, this.boteLigero.barco.angle));
		}
	}
}