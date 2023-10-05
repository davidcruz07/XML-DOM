import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Scanner;

public class SalesDOM {

    public static void main(String[] args) {

        try {

            File file = new File("sales.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList saleRecords = doc.getElementsByTagName("sale_record");

            Scanner scanner = new Scanner(System.in);
            double porcentaje;

            do { // validación para el incremento
                System.out.print("Porcentaje de incremento (entre 5% y 15%): ");
                porcentaje = scanner.nextDouble();
            } while (porcentaje < 5 || porcentaje > 15);

            scanner.nextLine();

            String departamento;
            boolean departamentoValido;

            do { // validación para el depaaaa

                System.out.print("Departamento: ");
                departamento = scanner.nextLine();
                departamentoValido = validarDepartamento(departamento, saleRecords);

                if (!departamentoValido) {

                    System.out.println("Departamento no encontrado en los registros. Intente nuevamente.");

                }

            } while (!departamentoValido);

            DocumentBuilderFactory newDbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder newDBuilder = newDbFactory.newDocumentBuilder();
            Document newDoc = newDBuilder.newDocument();
            Element rootElement = newDoc.createElement("sales_doc");
            newDoc.appendChild(rootElement);

            for (int i = 0; i < saleRecords.getLength(); i++) {

                Node saleRecordNode = saleRecords.item(i);

                if (saleRecordNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element saleRecord = (Element) saleRecordNode;

                    String departamentito = saleRecord.getElementsByTagName("department").item(0).getTextContent();

                    if (departamentito.equalsIgnoreCase(departamento)) {

                        Element newSaleRecord = newDoc.createElement("sale_record");

                        NodeList childNodes = saleRecord.getChildNodes();

                        for (int j = 0; j < childNodes.getLength(); j++) {

                            Node childNode = childNodes.item(j);

                            if (childNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element childElement = (Element) childNode;

                                Element newElement = newDoc.createElement(childElement.getNodeName());
                                newElement.appendChild(newDoc.createTextNode(childElement.getTextContent()));

                                newSaleRecord.appendChild(newElement);
                            }
                        }

                        // calcular el valor de las ventas con el incremento
                        double value = Double.parseDouble(saleRecord.getElementsByTagName("sales").item(0).getTextContent());
                        double incremento = value * (porcentaje / 100);
                        double finalSales = value + incremento;

                        Element newSales = newDoc.createElement("sales");
                        newSales.appendChild(newDoc.createTextNode(String.valueOf(finalSales)));

                        // poner el nuevo valor de sales
                        newSaleRecord.getElementsByTagName("sales").item(0).setTextContent(String.valueOf(finalSales));

                        rootElement.appendChild(newSaleRecord);
                    }
                }
            }

            // configura la indentacion del doccc
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Habilitar la indentación
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); // Número de espacios para la indentación

            DOMSource source = new DOMSource(newDoc);
            StreamResult result = new StreamResult(new File("new_sales.xml"));
            transformer.transform(source, result);

            System.out.println("Nuevo documento XML 'new_sales.xml' generado o modificado.");

        } catch (Exception e) {

            System.err.println(e);

        }
    }

    // pa validar el departamento
    private static boolean validarDepartamento(String departamento, NodeList saleRecords) {
        for (int i = 0; i < saleRecords.getLength(); i++) {
            Node saleRecordNode = saleRecords.item(i);

            if (saleRecordNode.getNodeType() == Node.ELEMENT_NODE) {
                Element saleRecord = (Element) saleRecordNode;

                String departamentito = saleRecord.getElementsByTagName("department").item(0).getTextContent();

                if (departamentito.equalsIgnoreCase(departamento)) {
                    return true;
                }
            }
        }
        return false;
    }
}

