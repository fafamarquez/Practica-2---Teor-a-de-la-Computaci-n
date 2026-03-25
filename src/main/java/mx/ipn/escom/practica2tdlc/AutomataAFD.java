/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package mx.ipn.escom.practica2tdlc;

/**
 *
 * @author rafael-marquez
 */
import java.util.Map;
import java.util.Set;

public class AutomataAFD {
    private Set<String> alfabeto;
    private String estadoInicial;
    private Set<String> estadosAceptacion;
    // Mapa de transiciones: EstadoOrigen -> (Símbolo -> EstadoDestino)
    private Map<String, Map<String, String>> transiciones;

    // Constructor
    public AutomataAFD(Set<String> alfabeto, String estadoInicial, 
                       Set<String> estadosAceptacion, 
                       Map<String, Map<String, String>> transiciones) {
        this.alfabeto = alfabeto;
        this.estadoInicial = estadoInicial;
        this.estadosAceptacion = estadosAceptacion;
        this.transiciones = transiciones;
    }

    // Método principal que valida la cadena y devuelve el texto para tu JTextArea
    public String validarCadena(String cadena) {
        String estadoActual = estadoInicial;
        StringBuilder traza = new StringBuilder();
        
        traza.append("--- INICIO DE SIMULACIÓN ---\n");
        traza.append("Estado Inicial: [ ").append(estadoActual).append(" ]\n");
        traza.append("Cadena a evaluar: '").append(cadena).append("'\n\n");

        // Caso especial: Cadena vacía (épsilon)
        if (cadena.isEmpty()) {
            traza.append("Evaluando cadena vacía (ε)...\n");
            if (estadosAceptacion.contains(estadoActual)) {
                traza.append("\nEl estado inicial [").append(estadoActual).append("] ES de aceptación.\n>>> CADENA ACEPTADA <<<");
            } else {
                traza.append("\nEl estado inicial [").append(estadoActual).append("] NO es de aceptación.\n>>> CADENA RECHAZADA <<<");
            }
            return traza.toString();
        }

        // Recorrer letra por letra
        for (char c : cadena.toCharArray()) {
            String simbolo = String.valueOf(c);

            // 1. Validar que el símbolo pertenezca al alfabeto
            if (!alfabeto.contains(simbolo)) {
                traza.append("\nERROR CRÍTICO: El símbolo '").append(simbolo).append("' no pertenece al alfabeto Σ.\n");
                traza.append(">>> CADENA RECHAZADA <<<");
                return traza.toString();
            }

            // 2. Buscar las transiciones disponibles para el estado actual
            Map<String, String> transicionesDelEstado = transiciones.get(estadoActual);
            
            // 3. Validar si existe un camino con ese símbolo
            if (transicionesDelEstado == null || !transicionesDelEstado.containsKey(simbolo)) {
                traza.append("\nERROR: No hay transición definida para el símbolo '").append(simbolo)
                     .append("' desde el estado [ ").append(estadoActual).append(" ].\n");
                traza.append(">>> CADENA RECHAZADA <<<");
                return traza.toString();
            }

            // 4. Moverse al siguiente estado
            String estadoSiguiente = transicionesDelEstado.get(simbolo);
            traza.append("[ ").append(estadoActual).append(" ]")
                 .append(" --( ").append(simbolo).append(" )--> ")
                 .append("[ ").append(estadoSiguiente).append(" ]\n");
                 
            estadoActual = estadoSiguiente;
        }

        // 5. Verificamos si terminamos en un estado de aceptación
        traza.append("\n--- FIN DE CADENA ---\n");
        if (estadosAceptacion.contains(estadoActual)) {
            traza.append("Estado final [").append(estadoActual).append("] ES de aceptación.\n");
            traza.append(">>> CADENA ACEPTADA <<<");
        } else {
            traza.append("Estado final [").append(estadoActual).append("] NO es de aceptación.\n");
            traza.append(">>> CADENA RECHAZADA <<<");
        }

        return traza.toString();
    }

    public Set<String> getAlfabeto() {
        return alfabeto;
    }

    public String getEstadoInicial() {
        return estadoInicial;
    }

    public Set<String> getEstadosAceptacion() {
        return estadosAceptacion;
    }

    public Map<String, Map<String, String>> getTransiciones() {
        return transiciones;
    }
}