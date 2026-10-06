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

        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

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

    public synchronized void addMessage(
            String user,
            String message) {

        try {
            Document doc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(file);

            Element root = doc.getDocumentElement();

            Element msg = doc.createElement("message");

            addElement(doc, msg, "user", user);
            addElement(doc, msg, "type", "text");
            addElement(doc, msg, "text", message);

            addElement(
                    doc,
                    msg,
                    "timestamp",
                    String.valueOf(System.currentTimeMillis())
            );

            root.appendChild(msg);

            save(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void addFileMessage(
            String user,
            String type,
            String filename,
            String filepath,
            long filesize) {

        try {
            Document doc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(file);

            Element root = doc.getDocumentElement();

            Element msg = doc.createElement("message");

            addElement(doc, msg, "user", user);
            addElement(doc, msg, "type", type);
            addElement(doc, msg, "text", "");
            addElement(doc, msg, "filename", filename);
            addElement(doc, msg, "filepath", filepath);

            addElement(
                    doc,
                    msg,
                    "filesize",
                    String.valueOf(filesize)
            );

            addElement(
                    doc,
                    msg,
                    "timestamp",
                    String.valueOf(System.currentTimeMillis())
            );

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

            NodeList list =
                    doc.getElementsByTagName("message");

            for (int i = 0;
                    i < list.getLength();
                    i++) {

                Element msg =
                        (Element) list.item(i);

                String user =
                        getElementText(msg, "user");

                String type =
                        getElementText(msg, "type");

                String text =
                        getElementText(msg, "text");

                String filename =
                        getElementText(msg, "filename");

                String filepath =
                        getElementText(msg, "filepath");

                String filesize =
                        getElementText(msg, "filesize");

                String timestamp =
                        getElementText(msg, "timestamp");

                if (type.isEmpty()) {
                    type = "text";
                }

                if (filename.isEmpty()) {
                    filename = "";
                }

                if (filepath.isEmpty()) {
                    filepath = "";
                }

                if (filesize.isEmpty()) {
                    filesize = "0";
                }

                if (timestamp.isEmpty()) {
                    timestamp = "0";
                }

                messages.add(
                        new String[]{
                                user,
                                type,
                                text,
                                filename,
                                filepath,
                                filesize,
                                timestamp
                        }
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return messages;
    }

    private void addElement(
            Document doc,
            Element parent,
            String name,
            String value) {

        Element element =
                doc.createElement(name);

        element.setTextContent(
                value == null ? "" : value
        );

        parent.appendChild(element);
    }

    private String getElementText(
            Element parent,
            String name) {

        NodeList nodes =
                parent.getElementsByTagName(name);

        if (nodes.getLength() == 0) {
            return "";
        }

        return nodes.item(0)
                .getTextContent();
    }

    private void save(Document doc)
            throws Exception {

        TransformerFactory factory =
                TransformerFactory.newInstance();

        var transformer =
                factory.newTransformer();

        transformer.setOutputProperty(
                OutputKeys.INDENT,
                "yes"
        );

        transformer.transform(
                new DOMSource(doc),
                new StreamResult(file)
        );
    }
}