package com.chat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {

    private MessageStore store;

    @Override
    public void init() throws ServletException {

        String path = getServletContext()
                .getRealPath("/WEB-INF/messages.xml");

        store = new MessageStore(path);
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        List<String[]> messages = store.getMessages();

        request.setAttribute("messages", messages);

        request.getRequestDispatcher("index.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

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