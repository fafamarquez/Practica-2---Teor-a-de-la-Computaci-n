package mx.ipn.escom.practica2tdlc;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

import java.awt.Color;
import java.util.*;

public class GraphViewer {

    public static mxGraphComponent createGraphComponent(Automata automata, Set<String> estadosResaltados) {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();

        Map<String, Object> vertices = new HashMap<>();

        try {
            // Estilos
            Map<String, Object> styleAceptacion = new HashMap<>();
            styleAceptacion.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
            styleAceptacion.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
            styleAceptacion.put(mxConstants.STYLE_FILLCOLOR, "#C3D9FF");
            styleAceptacion.put(mxConstants.STYLE_FONTCOLOR, "#000000");
            styleAceptacion.put(mxConstants.STYLE_STROKEWIDTH, "3");
            styleAceptacion.put(mxConstants.STYLE_STROKECOLOR, "#0000FF");
            graph.getStylesheet().putCellStyle("ACEPTACION", styleAceptacion);

            Map<String, Object> styleNormal = new HashMap<>();
            styleNormal.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
            styleNormal.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
            styleNormal.put(mxConstants.STYLE_FILLCOLOR, "#EFEFEF");
            styleNormal.put(mxConstants.STYLE_FONTCOLOR, "#000000");
            graph.getStylesheet().putCellStyle("NORMAL", styleNormal);

            Map<String, Object> styleResaltado = new HashMap<>();
            styleResaltado.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
            styleResaltado.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
            styleResaltado.put(mxConstants.STYLE_FILLCOLOR, "#FFFF00"); // Amarillo
            styleResaltado.put(mxConstants.STYLE_FONTCOLOR, "#000000");
            graph.getStylesheet().putCellStyle("RESALTADO", styleResaltado);
            
            // Añadir Múltiples aristas entre mismos nodos
            graph.setMultigraph(true);
            graph.setAllowDanglingEdges(false);
            graph.setCellsEditable(false);

            // 1. Agregar nodos
            Set<String> todos = automata.obtenerTodosLosEstados();
            for (String q : todos) {
                String estilo = "NORMAL";
                if (estadosResaltados != null && estadosResaltados.contains(q)) {
                    estilo = "RESALTADO";
                    if (automata.getEstadosAceptacion().contains(q)) {
                        estilo += ";" + mxConstants.STYLE_STROKEWIDTH + "=3;" + mxConstants.STYLE_STROKECOLOR + "=#0000FF";
                    }
                } else if (automata.getEstadosAceptacion().contains(q)) {
                    estilo = "ACEPTACION";
                }
                
                String label = q;
                if (q.equals(automata.getEstadoInicial())) {
                    label = "-> " + q;
                }
                
                Object v = graph.insertVertex(parent, q, label, 0, 0, 60, 60, estilo);
                vertices.put(q, v);
            }

            // 2. Transiciones (Agrupar las de mismo origen y destino)
            Map<String, Map<String, Set<String>>> matrix = new HashMap<>();
            for (String src : automata.getTransiciones().keySet()) {
                matrix.putIfAbsent(src, new HashMap<>());
                Map<String, Set<String>> rutas = automata.getTransiciones().get(src);
                for (String simbolo : rutas.keySet()) {
                    for (String dst : rutas.get(simbolo)) {
                        matrix.get(src).putIfAbsent(dst, new TreeSet<>());
                        matrix.get(src).get(dst).add(simbolo);
                    }
                }
            }

            for (String src : matrix.keySet()) {
                for (String dst : matrix.get(src).keySet()) {
                    String labels = String.join(", ", matrix.get(src).get(dst));
                    String styleEdge = "";
                    if (labels.contains("λ") || labels.contains("ε")) {
                        styleEdge = "dashed=1;strokeColor=red;"; // Transiciones lambda en rojo
                    }
                    graph.insertEdge(parent, null, labels, vertices.get(src), vertices.get(dst), styleEdge);
                }
            }

        } finally {
            graph.getModel().endUpdate();
        }

        // Diseño circular bonito
        mxCircleLayout layout = new mxCircleLayout(graph);
        layout.setRadius(150);
        layout.execute(graph.getDefaultParent());
        
        // Diseño de aristas paralelas para evitar superposición
        com.mxgraph.layout.mxParallelEdgeLayout parallelLayout = new com.mxgraph.layout.mxParallelEdgeLayout(graph);
        parallelLayout.execute(graph.getDefaultParent());

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.setConnectable(false);
        graphComponent.getViewport().setBackground(Color.WHITE);

        return graphComponent;
    }
}
