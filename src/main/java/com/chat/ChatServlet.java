package com.chat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {

    private MessageStore store;

    @Override
    public void init() throws ServletException {

        String dataDirectory = System.getenv("CHAT_DATA_DIR");

        if (dataDirectory == null || dataDirectory.trim().isEmpty()) {
            dataDirectory =
                    System.getProperty("user.home")
                    + File.separator
                    + "SimpleChatAppData";
        }

        File dataDir = new File(dataDirectory);

        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        String messagePath =
                new File(dataDir, "messages.xml").getAbsolutePath();

        store = new MessageStore(messagePath);
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("messages".equals(action)) {

            sendMessagesAsJson(response);

            return;
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        List<String[]> messages = store.getMessages();

        request.setAttribute(
                "messages",
                messages
        );

        request.getRequestDispatcher("index.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");

        String user = request.getParameter("user");
        String message = request.getParameter("message");

        if (user != null && message != null &&
                !user.trim().isEmpty() &&
                !message.trim().isEmpty()) {

            store.addMessage(
                    user.trim(),
                    message.trim()
            );
        }

        response.sendRedirect("chat");
    }

    private void sendMessagesAsJson(
            HttpServletResponse response)
            throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType(
                "application/json; charset=UTF-8"
        );

        List<String[]> messages =
                store.getMessages();

        PrintWriter out =
                response.getWriter();

        out.print("[");

        for (int i = 0;
                i < messages.size();
                i++) {

            String[] message =
                    messages.get(i);

            if (i > 0) {
                out.print(",");
            }

            out.print("{");

            out.print(
                    "\"user\":\"" +
                    escapeJson(message[0]) +
                    "\","
            );

            out.print(
                    "\"type\":\"" +
                    escapeJson(message[1]) +
                    "\","
            );

            out.print(
                    "\"text\":\"" +
                    escapeJson(message[2]) +
                    "\","
            );

            out.print(
                    "\"filename\":\"" +
                    escapeJson(message[3]) +
                    "\","
            );

            out.print(
                    "\"filepath\":\"" +
                    escapeJson(message[4]) +
                    "\","
            );

            out.print(
                    "\"filesize\":\"" +
                    escapeJson(message[5]) +
                    "\","
            );

            out.print(
                    "\"timestamp\":\"" +
                    escapeJson(message[6]) +
                    "\""
            );

            out.print("}");
        }

        out.print("]");
    }

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}