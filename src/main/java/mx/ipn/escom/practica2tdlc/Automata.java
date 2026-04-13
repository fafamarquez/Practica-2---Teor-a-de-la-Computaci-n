package mx.ipn.escom.practica2tdlc;

import java.util.*;

public class Automata {
    public enum Tipo { AFD, AFND, AFNL }
    
    private Set<String> alfabeto;
    private String estadoInicial;
    private Set<String> estadosAceptacion;
    // Mapa de transiciones: EstadoOrigen -> (Símbolo -> ConjuntoDeEstadosDestino)
    private Map<String, Map<String, Set<String>>> transiciones;

    public Automata(Set<String> alfabeto, String estadoInicial, 
                    Set<String> estadosAceptacion, 
                    Map<String, Map<String, Set<String>>> transiciones) {
        this.alfabeto = alfabeto;
        this.estadoInicial = estadoInicial;
        this.estadosAceptacion = estadosAceptacion;
        this.transiciones = transiciones;
        
        // Asegurar la existencia de mapas vacios para los estados sin salidas
        for (String q : obtenerTodosLosEstados()) {
            this.transiciones.putIfAbsent(q, new HashMap<>());
        }
    }

    public Tipo determinarTipo() {
        boolean tieneLambda = false;
        boolean esNoDeterminista = false;
        
        // Un AFD estricto requiere que cada estado defina una transición para CADA símbolo de su alfabeto.
        // Pero para diferenciar rápidamente entre nuestra GUI:
        for (String origen : transiciones.keySet()) {
            Map<String, Set<String>> rutas = transiciones.get(origen);
            for (String sim : rutas.keySet()) {
                if (sim.equals("λ") || sim.equals("ε")) {
                    tieneLambda = true;
                }
                if (rutas.get(sim).size() > 1) {
                    esNoDeterminista = true;
                }
            }
            if (rutas.size() < alfabeto.size() && !tieneLambda) {
                // Si faltan caminos explícitos, entonces estrictamente es un AFND que lleva al rechazo (o "pozo")
                // Sin embargo, por simplificación podemos tratarlo como AFND si no define todo, o AFND.
                // Lo definiremos como AFND si faltan.
            }
        }
        
        if (tieneLambda) return Tipo.AFNL;
        // if (esNoDeterminista) 
        // Incluso si no hay múltiples para la misma letra, si falta alguna explícita sin un pozo, es AFND.
        return (esNoDeterminista) ? Tipo.AFND : Tipo.AFD;
    }

    public Set<String> obtenerTodosLosEstados() {
        Set<String> qEstados = new HashSet<>();
        qEstados.add(estadoInicial);
        qEstados.addAll(estadosAceptacion);
        for(String src : transiciones.keySet()){
            qEstados.add(src);
            for(Set<String> dests : transiciones.get(src).values()) {
                qEstados.addAll(dests);
            }
        }
        return qEstados;
    }

    public Set<String> calcularClausuraLambda(Set<String> estados) {
        Set<String> clausura = new HashSet<>(estados);
        Stack<String> pila = new Stack<>();
        pila.addAll(estados);
        
        while (!pila.isEmpty()) {
            String actual = pila.pop();
            Map<String, Set<String>> trans = transiciones.get(actual);
            if (trans != null && (trans.containsKey("λ") || trans.containsKey("ε"))) {
                Set<String> destinosLambda = new HashSet<>();
                if(trans.containsKey("λ")) destinosLambda.addAll(trans.get("λ"));
                if(trans.containsKey("ε")) destinosLambda.addAll(trans.get("ε"));
                
                for (String destino : destinosLambda) {
                    if (!clausura.contains(destino)) {
                        clausura.add(destino);
                        pila.push(destino);
                    }
                }
            }
        }
        return clausura;
    }

    public String validarCadena(String cadena) {
        StringBuilder traza = new StringBuilder();
        traza.append("--- INICIO DE SIMULACIÓN ---\n");
        traza.append("Cadena a evaluar: '").append(cadena).append("'\n");
        
        Set<String> activos = new HashSet<>();
        activos.add(estadoInicial);
        activos = calcularClausuraLambda(activos);
        
        traza.append("Estado Inicial (con λ-clausura): ").append(activos).append("\n\n");
        
        // Recorrer letra por letra
        for (char c : cadena.toCharArray()) {
            String simbolo = String.valueOf(c);
            
            if (!alfabeto.contains(simbolo)) {
                traza.append("\nERROR CRÍTICO: El símbolo '").append(simbolo).append("' no pertenece al alfabeto Σ.\n");
                traza.append(">>> CADENA RECHAZADA <<<");
                return traza.toString();
            }

            Set<String> siguientes = new HashSet<>();
            traza.append("Evaluando '").append(simbolo).append("' desde activos ").append(activos).append("\n");
            
            for(String estadoActual : activos) {
                Map<String, Set<String>> trans = transiciones.get(estadoActual);
                if (trans != null && trans.containsKey(simbolo)) {
                    Set<String> dest = trans.get(simbolo);
                    siguientes.addAll(dest);
                    traza.append("   (").append(estadoActual).append(") --").append(simbolo).append("--> ").append(dest).append("\n");
                }
            }
            
            if (siguientes.isEmpty()) {
                traza.append("\nLos estados murieron. No hay más caminos posibles.\n");
                traza.append(">>> CADENA RECHAZADA <<<");
                return traza.toString();
            }

            Set<String> siguientesConLambda = calcularClausuraLambda(siguientes);
            if (siguientesConLambda.size() > siguientes.size()) {
                traza.append("   λ-clausura a: ").append(siguientesConLambda).append("\n");
            }
            activos = siguientesConLambda;
        }

        traza.append("\n--- FIN DE CADENA ---\n");
        traza.append("Estados finales activos: ").append(activos).append("\n");
        
        boolean aceptada = false;
        List<String> activosAceptacion = new ArrayList<>();
        for (String q : activos) {
            if (estadosAceptacion.contains(q)) {
                aceptada = true;
                activosAceptacion.add(q);
            }
        }

        if (aceptada) {
            traza.append("Los estados ").append(activosAceptacion).append(" SON de aceptación.\n");
            traza.append(">>> CADENA ACEPTADA <<<");
        } else {
            traza.append("Ninguno de los estados finales es de aceptación.\n");
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

    public Map<String, Map<String, Set<String>>> getTransiciones() {
        return transiciones;
    }
}