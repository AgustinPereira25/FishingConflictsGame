package fishingconflicts.logica.colecciones.interfaces;

import java.util.ArrayList;

import fishingconflicts.logica.modelos.Barco;

public interface IBarcos {
	
	void insertar(Barco barco);
	
	boolean existe(int idBarco);
	
	Barco obtener(int idBarco);
	
	void eliminar(int idBarco);
	
	int cantidadPatrullasEnUso();
	
	int cantidadPesquerosEnUso();
	
	int asignarOCrear(String tipoBarco, String subtipoBarco, int lugaresDisponibles);
	
	int obtenerNuevoId();
	
	void actualizarNuevoId();
	
	ArrayList<Barco> listarBarcosEnUso();
	
	ArrayList<Barco> listarBarcos();
}
