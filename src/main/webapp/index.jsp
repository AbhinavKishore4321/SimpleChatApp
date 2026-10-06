<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Simple Chat Application</title>

    <link rel="stylesheet" href="css/style.css">

</head>

<body>

<div class="chat-app">

    <!-- =========================
         SIDEBAR
         ========================= -->

    <aside class="sidebar">

        <div class="sidebar-header">

            <h2>Chats</h2>

            <button
                id="darkModeButton"
                class="dark-mode-button"
                title="Toggle dark mode">
                &#127769;
            </button>

        </div>

        <div class="search-container">

            <input
                type="text"
                id="searchInput"
                placeholder="Search messages..."
            >

        </div>

        <div class="chat-list">

            <div class="chat-user active">

                <div class="avatar">
                    &#128172;
                </div>

                <div class="chat-user-info">

                    <strong>Simple Chat</strong>

                    <span>Chat room</span>

                </div>

            </div>

        </div>

    </aside>


    <!-- =========================
         MAIN CHAT AREA
         ========================= -->

    <main class="chat-container">

        <!-- Chat Header -->

        <header class="chat-header">

            <div class="chat-header-user">

                <div class="avatar">
                    &#128172;
                </div>

                <div>

                    <h3>Simple Chat</h3>

                    <span class="online-status">
                        &#9679; Online
                    </span>

                </div>

            </div>

            <div class="chat-actions">

                <button
                    type="button"
                    title="Search">
                    &#128269;
                </button>

                <button
                    type="button"
                    title="More options">
                    &#8942;
                </button>

            </div>

        </header>


        <!-- =========================
             MESSAGES
             ========================= -->

        <div class="messages-container" id="messagesContainer">

            <%
                List<String[]> messages =
                        (List<String[]>) request.getAttribute("messages");

                if (messages != null && !messages.isEmpty()) {

                    for (String[] msg : messages) {

                        String user = msg[0];
                        String type = msg.length > 1 ? msg[1] : "text";
                        String text = msg.length > 2 ? msg[2] : "";
                        String filename = msg.length > 3 ? msg[3] : "";
                        String filepath = msg.length > 4 ? msg[4] : "";
                        String filesize = msg.length > 5 ? msg[5] : "";
                        String timestamp = msg.length > 6 ? msg[6] : "";
            %>

            <div class="message received">

                <div class="message-content">

                    <div class="message-user">
                        <%= user %>
                    </div>

                    <%
                        if ("text".equals(type)) {
                    %>

                        <div class="message-text">
                            <%= text %>
                        </div>

                    <%
                        } else if ("image".equals(type)) {
                    %>

                        <div class="message-text">
                            <%= text %>
                        </div>

                        <%
                            if (filepath != null && !filepath.isEmpty()) {
                        %>

                            <img
                                src="download?file=<%= filepath %>"
                                class="chat-image"
                                alt="<%= filename %>"
                            >

                        <%
                            }
                        %>

                        <div class="file-message">

                            <div class="file-card">

                                <div class="file-icon">
                                    &#128444;
                                </div>

                                <div class="file-details">

                                    <div class="file-name">
                                        <%= filename %>
                                    </div>

                                    <div class="file-size">
                                        <%= filesize %> bytes
                                    </div>

                                </div>

                                <a
                                    href="download?file=<%= filepath %>"
                                    class="download-button">
                                    Download
                                </a>

                            </div>

                        </div>

                    <%
                        } else {
                    %>

                        <div class="file-message">

                            <div class="file-card">

                                <div class="file-icon">
                                    &#128196;
                                </div>

                                <div class="file-details">

                                    <div class="file-name">
                                        <%= filename %>
                                    </div>

                                    <div class="file-size">
                                        <%= filesize %> bytes
                                    </div>

                                    <%
                                        if (text != null && !text.isEmpty()) {
                                    %>

                                        <div class="message-text">
                                            <%= text %>
                                        </div>

                                    <%
                                        }
                                    %>

                                </div>

                                <a
                                    href="download?file=<%= filepath %>"
                                    class="download-button">
                                    Download
                                </a>

                            </div>

                        </div>

                    <%
                        }
                    %>

                    <div class="message-time">

                        <%
                            if (timestamp != null && !timestamp.isEmpty()) {
                        %>

                            <%= timestamp %>

                        <%
                            }
                        %>

                    </div>

                </div>

            </div>

            <%
                    }

                } else {
            %>

                <div class="empty-chat">

                    <div class="empty-chat-icon">
                        &#128172;
                    </div>

                    <h3>No messages yet</h3>

                    <p>
                        Start the conversation by sending a message.
                    </p>

                </div>

            <%
                }
            %>

        </div>


        <!-- =========================
             FILE PREVIEW
             ========================= -->

        <div
            class="file-preview"
            id="filePreview"
            style="display: none;">

            <div class="file-preview-info">

                <span class="file-preview-icon">
                    &#128206;
                </span>

                <div>

                    <strong id="selectedFileName">
                        No file selected
                    </strong>

                    <span id="selectedFileSize">
                    </span>

                </div>

            </div>

            <button
                type="button"
                id="removeFileButton">
                &#10005;
            </button>

        </div>


        <!-- =========================
             MESSAGE INPUT
             ========================= -->

        <form
            action="chat"
            method="post"
            id="chatForm"
            class="input-area">

            <input
                type="text"
                name="user"
                id="userInput"
                class="user-input"
                placeholder="Your name"
                required
            >

            <div class="message-input-container">

                <button
                    type="button"
                    id="attachButton"
                    class="attach-button"
                    title="Attach file">
                    &#128206;
                </button>

                <input
                    type="file"
                    id="fileInput"
                    class="hidden-file-input"
                >

                <input
                    type="text"
                    name="message"
                    id="messageInput"
                    class="message-input"
                    placeholder="Type a message..."
                >

                <button
                    type="button"
                    class="emoji-button"
                    title="Emoji">
                    &#128522;
                </button>

                <button
                    type="submit"
                    id="sendButton"
                    class="send-button">
                    &#10148;
                </button>

            </div>

        </form>

    </main>

</div>


<script src="js/chat.js"></script>

</body>

</html>