package com.chat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
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
}