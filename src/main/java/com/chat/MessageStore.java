package com.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MessageStore {

    private final File file;

    public MessageStore(String path) {
        file = new File(path);

        if (!file.exists()) {
            createFile();
        }
    }

    private void createFile() {
        try {
            Document doc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            Element root = doc.createElement("messages");
            doc.appendChild(root);

            save(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void addMessage(String user, String message) {

        try {
            Document doc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(file);

            Element root = doc.getDocumentElement();

            Element msg = doc.createElement("message");

            Element userElement = doc.createElement("user");
            userElement.setTextContent(user);

            Element textElement = doc.createElement("text");
            textElement.setTextContent(message);

            msg.appendChild(userElement);
            msg.appendChild(textElement);

            root.appendChild(msg);

            save(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String[]> getMessages() {

        List<String[]> messages = new ArrayList<>();

        try {
            Document doc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(file);

            NodeList list = doc.getElementsByTagName("message");

            for (int i = 0; i < list.getLength(); i++) {

                Element msg = (Element) list.item(i);

                String user = msg
                        .getElementsByTagName("user")
                        .item(0)
                        .getTextContent();

                String text = msg
                        .getElementsByTagName("text")
                        .item(0)
                        .getTextContent();

                messages.add(new String[]{user, text});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return messages;
    }

    private void save(Document doc) throws Exception {

        TransformerFactory factory = TransformerFactory.newInstance();

        var transformer = factory.newTransformer();

        transformer.setOutputProperty(
                OutputKeys.INDENT, "yes"
        );

        transformer.transform(
                new DOMSource(doc),
                new StreamResult(file)
        );
    }
}