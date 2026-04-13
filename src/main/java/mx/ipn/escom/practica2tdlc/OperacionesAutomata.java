package mx.ipn.escom.practica2tdlc;

import java.util.*;

public class OperacionesAutomata {

    /**
     * Convierte un AFN con transiciones lambda a un AFN sin transiciones lambda.
     */
    public static Automata afnlAfn(Automata original, StringBuilder log) {
        if (log != null) log.append("\n--- INICIO CONVERSIÓN AFN-λ a AFND ---\n");
        Set<String> alfabeto = new HashSet<>(original.getAlfabeto());
        alfabeto.remove("λ");
        alfabeto.remove("ε");

        String estadoInicial = original.getEstadoInicial();
        Set<String> todosEstados = original.obtenerTodosLosEstados();
        
        Set<String> nuevosEstadosAceptacion = new HashSet<>();
        Map<String, Map<String, Set<String>>> nuevasTransiciones = new HashMap<>();

        // Para cada estado, si su clausura lambda contiene un estado final, 
        // este estado también se vuelve final.
        for (String q : todosEstados) {
            Set<String> cl = original.calcularClausuraLambda(new HashSet<>(Arrays.asList(q)));
            if(log != null) log.append("Clausura-λ(").append(q).append(") = ").append(cl).append("\n");
            for (String f : original.getEstadosAceptacion()) {
                if (cl.contains(f)) {
                    nuevosEstadosAceptacion.add(q);
                    if(log != null) log.append("-> El estado '").append(q).append("' se vuelve final porque alcanza a '").append(f).append("' mediante transiciones λ.\n");
                    break;
                }
            }
        }

        // Para calcular delta'(q, a):
        for (String q : todosEstados) {
            nuevasTransiciones.putIfAbsent(q, new HashMap<>());
            Set<String> clausuraOrigen = original.calcularClausuraLambda(new HashSet<>(Arrays.asList(q)));
            
            for (String a : alfabeto) {
                Set<String> destinosDirectos = new HashSet<>();
                // Ver a donde viajan todos los estados de clausuraOrigen con 'a'
                for (String c : clausuraOrigen) {
                    Map<String, Set<String>> t = original.getTransiciones().get(c);
                    if (t != null && t.containsKey(a)) {
                        destinosDirectos.addAll(t.get(a));
                    }
                }
                
                if (!destinosDirectos.isEmpty()) {
                    // Clausura lambda de los destinos
                    Set<String> destinosFinales = original.calcularClausuraLambda(destinosDirectos);
                    if (!destinosFinales.isEmpty()) {
                        nuevasTransiciones.get(q).put(a, destinosFinales);
                    }
                }
            }
        }

        if(log != null) log.append("--- FIN CONVERSIÓN ---\n");
        return new Automata(alfabeto, estadoInicial, nuevosEstadosAceptacion, nuevasTransiciones);
    }
    
    public static Automata afnlAfn(Automata original) {
        return afnlAfn(original, null);
    }

    /**
     * Convierte un AFN a AFD construyendo subconjuntos.
     */
    public static Automata afnAfd(Automata afn, StringBuilder log) {
        if (log != null) log.append("\n--- INICIO CONVERSIÓN A AFD (Por Subconjuntos) ---\n");
        // Asegurarse de que no tenga lambda
        Automata sinLambda = afn;
        if (afn.determinarTipo() == Automata.Tipo.AFNL) {
            if(log != null) log.append("Detectado AFN-λ. Eliminando transiciones λ previamente...\n");
            sinLambda = afnlAfn(afn, log);
        }

        Set<String> alfabeto = new HashSet<>(sinLambda.getAlfabeto());
        
        // Mapa de alias: Set de estados -> Nombre representativo (ej: "q0,q1")
        Map<Set<String>, String> nombreConjuntos = new HashMap<>();
        
        Set<String> inicialSet = new HashSet<>(Arrays.asList(sinLambda.getEstadoInicial()));
        Queue<Set<String>> porExplorar = new LinkedList<>();
        Set<Set<String>> explorados = new HashSet<>();

        porExplorar.add(inicialSet);
        explorados.add(inicialSet);
        
        // Creamos el nombre para el inicial
        String idInicial = unificarNombre(inicialSet);
        nombreConjuntos.put(inicialSet, idInicial);

        Set<String> nuevosEstadosAceptacion = new HashSet<>();
        Map<String, Map<String, Set<String>>> nuevasTransiciones = new HashMap<>();

        while (!porExplorar.isEmpty()) {
            Set<String> actualSet = porExplorar.poll();
            String idActual = nombreConjuntos.get(actualSet);

            if(log != null) log.append("\nCalculando derivaciones para el Subconjunto: [").append(idActual).append("] -> { ").append(String.join(", ", actualSet)).append(" }\n");

            nuevasTransiciones.putIfAbsent(idActual, new HashMap<>());

            // Es de aceptacion si contiene al menos uno de aceptación original
            boolean esAcep = false;
            for (String q : actualSet) {
                if (sinLambda.getEstadosAceptacion().contains(q)) esAcep = true;
            }
            if (esAcep) nuevosEstadosAceptacion.add(idActual);

            for (String a : alfabeto) {
                Set<String> destinoSet = new HashSet<>();
                for (String q : actualSet) {
                    Map<String, Set<String>> t = sinLambda.getTransiciones().get(q);
                    if (t != null && t.containsKey(a)) {
                        destinoSet.addAll(t.get(a));
                    }
                }

                if (!destinoSet.isEmpty()) {
                    if (!explorados.contains(destinoSet)) {
                        explorados.add(destinoSet);
                        porExplorar.add(destinoSet);
                        nombreConjuntos.put(destinoSet, unificarNombre(destinoSet));
                        if(log != null) log.append("  > [NUEVO SUBCONJUNTO CREADO]: ").append(nombreConjuntos.get(destinoSet)).append(" originado por transición '").append(a).append("'\n");
                    }
                    String idDestino = nombreConjuntos.get(destinoSet);
                    if(log != null) log.append("  --(").append(a).append(")--> [").append(idDestino).append("]\n");
                    nuevasTransiciones.get(idActual).put(a, new HashSet<>(Arrays.asList(idDestino)));
                }
            }
        }

        if (log != null) log.append("--- FIN CONVERSIÓN A AFD ---\n");
        return new Automata(alfabeto, idInicial, nuevosEstadosAceptacion, nuevasTransiciones);
    }
    
    public static Automata afnAfd(Automata afn) {
        return afnAfd(afn, null);
    }

    private static String unificarNombre(Set<String> conjunto) {
        List<String> ordenados = new ArrayList<>(conjunto);
        Collections.sort(ordenados);
        return String.join("-", ordenados); // Ej "q0-q1"
    }

    /**
     * Minimiza un AFD eliminando inalcanzables y juntando equivalentes (Distinguishability table).
     */
    public static Automata minimizarAfd(Automata afdParams) {
        // Asegurarse estrictamente que es AFD
        Automata afd = afdParams;
        if(afd.determinarTipo() != Automata.Tipo.AFD) {
            afd = afnAfd(afd);
        }

        // Paso 1: Eliminar inalcanzables
        Set<String> alcanzables = new HashSet<>();
        Queue<String> cola = new LinkedList<>();
        cola.add(afd.getEstadoInicial());
        alcanzables.add(afd.getEstadoInicial());

        while (!cola.isEmpty()) {
            String q = cola.poll();
            Map<String, Set<String>> trans = afd.getTransiciones().get(q);
            if (trans != null) {
                for (Set<String> dests : trans.values()) {
                    for (String d : dests) {
                        if (!alcanzables.contains(d)) {
                            alcanzables.add(d);
                            cola.add(d);
                        }
                    }
                }
            }
        }

        // Paso 2: Pares distinguibles (Hopcroft table method)
        List<String> listAlc = new ArrayList<>(alcanzables);
        int n = listAlc.size();
        boolean[][] distinguibles = new boolean[n][n];

        Set<String> acep = afd.getEstadosAceptacion();

        // Marcar aquellos donde uno es final y otro no
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    boolean iAcep = acep.contains(listAlc.get(i));
                    boolean jAcep = acep.contains(listAlc.get(j));
                    if (iAcep != jAcep) {
                        distinguibles[i][j] = true;
                    }
                }
            }
        }

        // Iterar hasta que ya no haya cambios
        boolean cambios;
        do {
            cambios = false;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j && !distinguibles[i][j]) {
                        // Revisar transiciones para cada símbolo
                        for (String a : afd.getAlfabeto()) {
                            String destI = getUnicoDestino(afd, listAlc.get(i), a);
                            String destJ = getUnicoDestino(afd, listAlc.get(j), a);

                            if (destI == null && destJ != null || destI != null && destJ == null) {
                                distinguibles[i][j] = true;
                                cambios = true;
                                break;
                            } else if (destI != null && destJ != null) {
                                int idxI = listAlc.indexOf(destI);
                                int idxJ = listAlc.indexOf(destJ);
                                if (distinguibles[idxI][idxJ]) {
                                    distinguibles[i][j] = true;
                                    cambios = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } while (cambios);

        // Agrupar los no distinguibles
        boolean[] visitado = new boolean[n];
        List<Set<String>> clasesEquivalencia = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!visitado[i]) {
                Set<String> clase = new HashSet<>();
                clase.add(listAlc.get(i));
                visitado[i] = true;

                for (int j = i + 1; j < n; j++) {
                    if (!distinguibles[i][j]) {
                        clase.add(listAlc.get(j));
                        visitado[j] = true;
                    }
                }
                clasesEquivalencia.add(clase);
            }
        }

        // Construir nuevo automata minimizado
        Map<String, String> viejoANuevo = new HashMap<>();
        for (Set<String> clase : clasesEquivalencia) {
            String nuevoNombre = unificarNombre(clase);
            for (String viejo : clase) {
                viejoANuevo.put(viejo, nuevoNombre);
            }
        }

        String nuevoInicial = viejoANuevo.get(afd.getEstadoInicial());
        Set<String> nuevosAceptacion = new HashSet<>();
        Map<String, Map<String, Set<String>>> nuevasTransiciones = new HashMap<>();

        for (Set<String> clase : clasesEquivalencia) {
            String nom = unificarNombre(clase);
            String reps = clase.iterator().next(); // Tomar cualquiera
            
            if (afd.getEstadosAceptacion().contains(reps)) {
                nuevosAceptacion.add(nom);
            }

            nuevasTransiciones.putIfAbsent(nom, new HashMap<>());
            Map<String, Set<String>> treps = afd.getTransiciones().get(reps);
            if(treps != null) {
                for (String a : afd.getAlfabeto()) {
                    if (treps.containsKey(a)) {
                        String destReps = getUnicoDestino(afd, reps, a);
                        String nuevoDest = viejoANuevo.get(destReps);
                        if (nuevoDest != null) { // Por si el destino era inalcanzable, o algo (no deberia)
                            nuevasTransiciones.get(nom).put(a, new HashSet<>(Arrays.asList(nuevoDest)));
                        }
                    }
                }
            }
        }

        return new Automata(afd.getAlfabeto(), nuevoInicial, nuevosAceptacion, nuevasTransiciones);
    }

    private static String getUnicoDestino(Automata afd, String estado, String simbolo) {
        Map<String, Set<String>> t = afd.getTransiciones().get(estado);
        if (t != null && t.containsKey(simbolo)) {
            Set<String> dests = t.get(simbolo);
            if (!dests.isEmpty()) return dests.iterator().next();
        }
        return null;
    }
}
