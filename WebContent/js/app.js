/**
 * Este archivo es el punto de inicio de la aplicación
 * a nivel del cliente. En él, se definen variables globales
 * y todo lo que la app vaya a utilizar.
 */

// Lista con los scripts necesarios que va a utilizar
// la aplicación.
var scripts = [
	
	// Scripts generales.
	'js/utils/ClienteWebSocket.js',
	
	// Escenas.
	'js/juego/escenas/Loader.js',
	'js/juego/escenas/Menu.js',
	'js/juego/escenas/NuevaPartida.js',
	'js/juego/escenas/SeleccionarBarco.js',
	'js/juego/escenas/Partida.js',
	'js/juego/escenas/ConectarseAPartida.js',
	'js/juego/escenas/AcercaDe.js',
	'js/juego/escenas/VistaLateral.js',
	
	// Objetos.
	'js/juego/objetos/Boton.js',
	'js/juego/objetos/Barco.js',
	'js/juego/objetos/Bala.js',
];

// Cargamos los scripts.
scripts.forEach(function(script, index) {
	var s = document.createElement('script'); s.src = script;
	document.getElementsByTagName('head')[0].append(s);
});

// Cargamos el juego.
var contexto;
window.onload = () => {
	// Contexto
	contexto = {
		// Escenas.
		escenas: [{
			nombre: 'AcercaDe',
			objeto: AcercaDe,
		}, {
			nombre: 'ConectarseAPartida',
			objeto: ConectarseAPartida,
		}, {
			nombre: 'Menu',
			objeto: Menu,
		}, {
			nombre: 'NuevaPartida',
			objeto: NuevaPartida,
		}, {
			nombre: 'Partida',
			objeto: Partida,
		}, {
			nombre: 'SeleccionarBarco',
			objeto: SeleccionarBarco,
		}, {
			nombre: 'VistaLateral',
			objeto: VistaLateral,
		}],
		
		// Instancia de phaser.
		phaser: new Phaser.Game({
			scale: {
				mode: Phaser.Scale.FIT,
				autoCenter: Phaser.Scale.CENTER_BOTH,
				width: 1250,
				height: 590,
			},
			scene: [
				Loader
			],
			parent: 'app',
			dom: {
				createContainer: true
			},
			physics: {
				default: 'arcade',
				arcade: {
					//gravity: { y: 300 },
					debug: false
				}
			},
			type: Phaser.AUTO,
		}),
		
		// Cliente Web Socket.
		websocket: new ClienteWebSocket('localhost', 8080, '/Fishing_Conflicts/websocket'),
		
		// Partida.
		partida: {},
		
		// Barco del jugador.
		barco: {},
		
		// Pesquero del jugador.
		pesquero: {},
		
		// Identificador del barco del jugador.
		idBarco: 0,
	};
	
	// Funciones.
	contexto.funciones = {
		/**
		 * Cambia la escena.
		 */
		cambiarEscena: (siguiente) => {
			contexto.phaser.scene.remove(contexto.escenaActual);
			
			var siguienteEscena = contexto.escenas.filter((item) => item.nombre == siguiente)[0];
			
			contexto.phaser.scene.add(siguienteEscena.nombre, siguienteEscena.objeto);
			contexto.phaser.scene.start(siguiente);
		},
		
		/**
		 * Envia el mensaje al servidor.
		 */
		enviarMensaje: (mensaje) => {
			contexto.websocket.enviarMensaje(mensaje);
		},
		
		/**
		 * Inicializa el websocket. En esta función se despachan
		 * los mensajes recibidos desde el servidor.
		 */
		inicializarWebSocket: (webSocket) => {		
			webSocket.onmessage = (evento) => {				
				respuesta = JSON.parse(evento.data);

				if (!respuesta.exito.estado) {
					alert(respuesta.exito.mensaje);
					return;
				}
				
				/**
				 * Operación: nuevaPartida
				 */
				if (respuesta.operacion.nombre == 'nuevaPartida') {
					contexto.partida.id = parseInt(respuesta.respuestas[0].valor);
					contexto.funciones.enviarMensaje(contexto.mensajes.obtenerBarcosDisponibles());
				}
				
				/**
				 * Operación: obtenerBarcosDisponibles
				 */
				if (respuesta.operacion.nombre == 'obtenerBarcosDisponibles') {
					contexto.partida.cantidadPatrullasDisponibles = parseInt(respuesta.respuestas[0].valor);
					contexto.partida.cantidadPesquerosDisponibles = parseInt(respuesta.respuestas[1].valor);
					contexto.funciones.cambiarEscena('SeleccionarBarco');
				}
				
				/**
				 * Operación: conectarseAPartida
				 */
				if (respuesta.operacion.nombre == 'conectarAPartida') {
					contexto.idBarco = parseInt(respuesta.respuestas[0].valor);
					contexto.funciones.enviarMensaje(contexto.mensajes.sincronizarPartida());
					contexto.funciones.cambiarEscena('Partida');
				}
				
				/**
				 * Operación: sincronizarPartida
				 */
				if (respuesta.operacion.nombre == 'sincronizarPartida') {
					contexto.partida = JSON.parse(respuesta.respuestas[0].valor);
				}
				
				/**
				 * Operación: jugadorCapturado
				 */
				if (respuesta.operacion.nombre == 'jugadorCapturado') {
					contexto.jugadorCapturado = true;
				}
				
				/**
				 * Operación: jugadorMuerto
				 */
				if (respuesta.operacion.nombre == 'jugadorMuerto') {
					contexto.jugadorMuerto = true;
				}
				
				/**
				 * Operación: vistaLateral
				 */
				if (respuesta.operacion.nombre == 'vistaLateral') {
					//Recibimos la respuesta del servidor (2 = en espera).
					contexto.partida.estado = parseInt(respuesta.respuestas[0].valor);
				}
				
				/**
				 * Operación: vistaLateralDesactivar 
				 */
				if (respuesta.operacion.nombre == 'vistaLateralDesactivar') {
					//Recibimos la respuesta del servidor (1 = iniciada).
					contexto.partida.estado = parseInt(respuesta.respuestas[0].valor);
				}

				/**
				 * Operación: activarZonaDeAtaque
				 */
				if (respuesta.operacion.nombre == 'activarZonaDeAtaque') {
					contexto.jugadorEnZonaDeAtaque = true;
				}
				
				/**
				 * Operación: desactivarZonaDeAtaque
				 */
				if (respuesta.operacion.nombre == 'desactivarZonaDeAtaque') {
					contexto.jugadorEnZonaDeAtaque = false;
				}
			}
		},
	};
	
	// Mensajes.
	contexto.mensajes = {
		nuevaPartida: (cantidadPatrullas, cantidadPesqueros) => {
			return JSON.stringify({
				operacion: {
					nombre: 'nuevaPartida',
					parametros: {
						cantidadPatrullas: cantidadPatrullas,
						cantidadPesqueros: cantidadPesqueros,
					}
				}
			})
		},
		
		guardarPartida: () => {
			return JSON.stringify({
				operacion: {
					nombre: 'guardarPartida',
					parametros: {
						idPartida: contexto.partida.id
					}
				}
			})
		},		
		
		obtenerBarcosDisponibles: () => {
			return JSON.stringify({
				operacion: {
					nombre: 'obtenerBarcosDisponibles',
					parametros: {
						idPartida: contexto.partida.id
					}
				}
			})
		},
		
		conectarAPartida: (tipo, subtipo) => {
			return JSON.stringify({
				operacion: {
					nombre: 'conectarAPartida',
					parametros: {
						idPartida: contexto.partida.id,
						tipoBarco: tipo,
						subtipoBarco: subtipo,
					}
				}
			})
		},
		
		sincronizarPartida: () => {
			return JSON.stringify({
				operacion: {
					nombre: 'sincronizarPartida',
					parametros: {
						idPartida: contexto.partida.id,
					}
				}
			})
		},
		
		moverBarco: (idBarco, direccion, angulo) => {
			return JSON.stringify({
				operacion: {
					nombre: 'moverBarco',
					parametros: {
						idPartida: contexto.partida.id,
						idBarco: idBarco,
						direccion: direccion,
						angulo: angulo,
					}
				}
			})
		},
		
		activarHelicoptero: (idBarco) => {
			return JSON.stringify({
				operacion: {
					nombre: 'activarHelicoptero',
					parametros: {
						idPartida: contexto.partida.id,
						idBarco: idBarco,
					}
				}
			})
		},
		
		desactivarHelicoptero: (idBarco) => {
			return JSON.stringify({
				operacion: {
					nombre: 'desactivarHelicoptero',
					parametros: {
						idPartida: contexto.partida.id,
						idBarco: idBarco,
					}
				}
			})
		},
		
		pescar: (idBarco) => {
			return JSON.stringify({
				operacion: {
					nombre: 'pescar',
					parametros: {
						idPartida: contexto.partida.id,
						idBarco: idBarco
					}
				}
			})
		},
		
		habilitarBoteLigero: (idBarco) => {
			return JSON.stringify({
				operacion: {
					nombre: 'habilitarBoteLigero',
					parametros: {
						idPartida: contexto.partida.id,
						idBarco: idBarco,
					}
				}
			})
		},
		
		deshabilitarBoteLigero: (idBarco) => {
			return JSON.stringify({
				operacion: {
					nombre: 'deshabilitarBoteLigero',
					parametros: {
						idPartida: contexto.partida.id,
						idBarco: idBarco,
					}
				}
			})
		},

		activarVistaLateral: () => {
			return JSON.stringify({
				operacion: {
					nombre: 'vistaLateral',
					parametros: {
						idPartida: contexto.partida.id,
					}
				}
			})
		},
		
		desactivarVistaLateral: () => {
			return JSON.stringify({
				operacion: {
					nombre: 'vistaLateralDesactivar',
					parametros: {
						idPartida: contexto.partida.id,
					}
				}
			})
		},		
		
		capturar: (idBarco, idBarcoOponente) => {
			return JSON.stringify({
				operacion: {
					nombre: 'capturar',
					parametros: {
						idPartida: contexto.partida.id,
						idBarco: idBarco,
						idBarcoOponente: idBarcoOponente,
					}
				}
			})
		},
		
		moverBoteLigero: (idBarco, x, y, angulo) => {
			return JSON.stringify({
				operacion: {
					nombre: 'moverBoteLigero',
					parametros: {
						idPartida: contexto.partida.id,
						idBarco: idBarco,
						x: x,
						y: y,
						angulo: angulo
					}
				}
			})
		},
		
		crearBala: (idBarco, posicionDestinoX, posicionDestinoY, tipoArma) => {
			return JSON.stringify({
				operacion: {
					nombre: 'crearBala',
					parametros: {
						idPartida: contexto.partida.id,
						idBarco: idBarco,
						posicionDestinoX: posicionDestinoX,
						posicionDestinoY: posicionDestinoY,
						tipoArma: tipoArma
					}
				}
			})
		},
		
		moverBala: (idBarco, idBala, x, y) => {
			return JSON.stringify({
				operacion: {
					nombre: 'moverBala',
					parametros: {
						idPartida: contexto.partida.id,
						idBarco: idBarco,
						idBala: idBala,
						x: x,
						y: y,
					}
				}
			})
		},
		
		disparar: (idBarco, idBala, idBarcoOponente) => {
			return JSON.stringify({
				operacion: {
					nombre: 'disparar',
					parametros: {
						idPartida: contexto.partida.id,
						idBarco: idBarco,
						idBala: idBala,
						idBarcoOponente: idBarcoOponente,
					}
				}
			})
		},
		
		eliminarBala: (idBarco, idBala) => {
			return JSON.stringify({
				operacion: {
					nombre: 'eliminarBala',
					parametros: {
						idPartida: contexto.partida.id,
						idBarco: idBarco,
						idBala: idBala
					}
				}
			})
		},
		
		aJugador: (idJugador, mensaje) => {
			return JSON.stringify({
				operacion: {
					nombre: 'aJugador',
					parametros: {
						idPartida: contexto.partida.id,
						idJugador: idJugador,
						mensaje: mensaje
					}
				}
			})
		}
	};
}