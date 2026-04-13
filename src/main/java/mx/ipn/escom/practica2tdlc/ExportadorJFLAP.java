package mx.ipn.escom.practica2tdlc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

public class ExportadorJFLAP {

    public static void exportar(Automata automata, String rute) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // Elemento raíz
        Element rootElement = doc.createElement("structure");
        doc.appendChild(rootElement);

        Element type = doc.createElement("type");
        type.appendChild(doc.createTextNode("fa"));
        rootElement.appendChild(type);

        Element automaton = doc.createElement("automaton");
        rootElement.appendChild(automaton);

        // Mapeo de nombre de estado a ID numérico para JFLAP
        Map<String, String> stateIds = new HashMap<>();
        int idCounter = 0;

        Set<String> todos = automata.obtenerTodosLosEstados();
        for (String q : todos) {
            stateIds.put(q, String.valueOf(idCounter));
            
            Element state = doc.createElement("state");
            state.setAttribute("id", String.valueOf(idCounter));
            state.setAttribute("name", q);
            
            // X and Y coords visually pleasing
            Element x = doc.createElement("x");
            x.appendChild(doc.createTextNode(String.valueOf(100 + (idCounter * 50))));
            Element y = doc.createElement("y");
            y.appendChild(doc.createTextNode(String.valueOf(100 + (idCounter * 30))));
            state.appendChild(x);
            state.appendChild(y);

            if (q.equals(automata.getEstadoInicial())) {
                state.appendChild(doc.createElement("initial"));
            }
            if (automata.getEstadosAceptacion().contains(q)) {
                state.appendChild(doc.createElement("final"));
            }

            automaton.appendChild(state);
            idCounter++;
        }

        // Transiciones
        for (Map.Entry<String, Map<String, Set<String>>> entry : automata.getTransiciones().entrySet()) {
            String qFrom = entry.getKey();
            for (Map.Entry<String, Set<String>> transition : entry.getValue().entrySet()) {
                String simbolo = transition.getKey();
                String lectura = (simbolo.equals("λ") || simbolo.equals("ε")) ? "" : simbolo;

                for (String qTo : transition.getValue()) {
                    Element trans = doc.createElement("transition");
                    
                    Element from = doc.createElement("from");
                    from.appendChild(doc.createTextNode(stateIds.get(qFrom)));
                    
                    Element to = doc.createElement("to");
                    to.appendChild(doc.createTextNode(stateIds.get(qTo)));
                    
                    Element read = doc.createElement("read");
                    read.appendChild(doc.createTextNode(lectura));

                    trans.appendChild(from);
                    trans.appendChild(to);
                    trans.appendChild(read);
                    
                    automaton.appendChild(trans);
                }
            }
        }

        // Escribir el contenido a XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(rute));

        transformer.transform(source, result);
    }
}
