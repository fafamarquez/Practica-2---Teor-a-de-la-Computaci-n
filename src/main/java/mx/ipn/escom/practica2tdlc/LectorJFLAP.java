/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.ipn.escom.practica2tdlc;

/**
 *
 * @author rafael-marquez
 */
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.*;

public class LectorJFLAP {

    public static Automata cargarDesdeXML(String rutaArchivo) throws Exception {
        File archivo = new File(rutaArchivo);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(archivo);
        doc.getDocumentElement().normalize();

        Set<String> alfabeto = new HashSet<>();
        Set<String> estados = new HashSet<>();
        Set<String> estadosAceptacion = new HashSet<>();
        String estadoInicial = "";
        Map<String, Map<String, Set<String>>> transiciones = new HashMap<>();

        // 1. Leer Estados
        NodeList listaEstados = doc.getElementsByTagName("state");
        for (int i = 0; i < listaEstados.getLength(); i++) {
            Node nodo = listaEstados.item(i);
            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                Element elemento = (Element) nodo;
                String idEstado = "q" + elemento.getAttribute("id"); 
                estados.add(idEstado);

                if (elemento.getElementsByTagName("initial").getLength() > 0) {
                    estadoInicial = idEstado;
                }
                if (elemento.getElementsByTagName("final").getLength() > 0) {
                    estadosAceptacion.add(idEstado);
                }
            }
        }

        // 2. Leer Transiciones
        NodeList listaTransiciones = doc.getElementsByTagName("transition");
        for (int i = 0; i < listaTransiciones.getLength(); i++) {
            Node nodo = listaTransiciones.item(i);
            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                Element elemento = (Element) nodo;
                String origen = "q" + elemento.getElementsByTagName("from").item(0).getTextContent();
                String destino = "q" + elemento.getElementsByTagName("to").item(0).getTextContent();
                
                String simbolo = "";
                if (elemento.getElementsByTagName("read").getLength() > 0 && elemento.getElementsByTagName("read").item(0).getTextContent() != null) {
                    simbolo = elemento.getElementsByTagName("read").item(0).getTextContent();
                }

                if (simbolo.isEmpty()) simbolo = "λ"; 
                if (!simbolo.equals("λ") && !simbolo.equals("ε")) {
                    alfabeto.add(simbolo);
                }

                transiciones.putIfAbsent(origen, new HashMap<>());
                transiciones.get(origen).putIfAbsent(simbolo, new HashSet<>());
                transiciones.get(origen).get(simbolo).add(destino);
            }
        }
        return new Automata(alfabeto, estadoInicial, estadosAceptacion, transiciones);
    }
}