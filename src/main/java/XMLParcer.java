import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XMLParcer {

    public static Map<String, ArrayList<String>> parse(String file) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        XMLHandler handler = new XMLHandler();
        //parser.parse(new File("src/TestMy.xml"), handler);
        parser.parse(new File(file), handler);
        //for (ArrayList situations:phrases.values()){
        //    for (Object word:situations)
        //        System.out.println(word);
        //}
        //System.out.println(phrases.get("Sitting").get(0));
        return phrases;
    }

    public static Map<String, ArrayList<String>> phrases = new HashMap<>();
    private static String currentSituatuion;

    private static class XMLHandler extends DefaultHandler {
        @Override
        public void startDocument() throws SAXException {
            // Тут будет логика реакции на начало документа
        }

        @Override
        public void endDocument() throws SAXException {
            // Тут будет логика реакции на конец документа
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("Situation")) {
                phrases.put(attributes.getValue("name"), new ArrayList<String>());
                currentSituatuion = attributes.getValue("name");
                //String phrase = attributes.getValue("name");
                //System.out.println(phrase);
            }
            if (qName.equals("phrase"))
            {
                phrases.get(currentSituatuion).add(attributes.getValue("text"));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            // Тут будет логика реакции на конец элемента
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            // Тут будет логика реакции на текст между элементами
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            // Тут будет логика реакции на пустое пространство внутри элементов (пробелы, переносы строчек и так далее).
        }
    }
}
