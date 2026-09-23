<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>

    <title>Simple Chat Application</title>

    <style>

        body {
            font-family: Arial;
            background: #f2f2f2;
            margin: 0;
        }

        .container {
            width: 600px;
            margin: 40px auto;
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 0 10px #aaa;
        }

        h1 {
            text-align: center;
        }

        .messages {
            height: 350px;
            overflow-y: auto;
            border: 1px solid #ccc;
            padding: 10px;
            margin-bottom: 15px;
        }

        .message {
            padding: 8px;
            margin-bottom: 8px;
            background: #eeeeee;
            border-radius: 5px;
        }

        input {
            padding: 10px;
            margin: 5px;
            width: 90%;
        }

        button {
            padding: 10px 20px;
            background: #333;
            color: white;
            border: none;
            cursor: pointer;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>Simple Chat Application</h1>

    <div class="messages">

        <%
            List<String[]> messages =
                    (List<String[]>) request.getAttribute("messages");

            if (messages != null) {

                for (String[] msg : messages) {
        %>

            <div class="message">
                <b><%= msg[0] %>:</b>
                <%= msg[1] %>
            </div>

        <%
                }
            }
        %>

    </div>

    <form action="chat" method="post">

        <input
            type="text"
            name="user"
            placeholder="Enter your name"
            required
        >

        <input
            type="text"
            name="message"
            placeholder="Enter your message"
            required
        >

        <button type="submit">Send</button>

    </form>

</div>

</body>
</html>