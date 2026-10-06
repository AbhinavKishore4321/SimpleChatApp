package com.chat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@WebServlet("/download")
public class FileDownloadServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String fileParameter =
                request.getParameter("file");

        if (fileParameter == null ||
                fileParameter.trim().isEmpty()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "File path is required."
            );
            return;
        }

        String dataDirectory =
                getDataDirectory();

        File uploadsDirectory =
                new File(
                        dataDirectory,
                        "uploads"
                );

        File requestedFile =
                new File(
                        uploadsDirectory,
                        fileParameter
                );

        String uploadsPath =
                uploadsDirectory
                        .getCanonicalPath();

        String requestedPath =
                requestedFile
                        .getCanonicalPath();

        if (!requestedPath.startsWith(
                uploadsPath + File.separator)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Access denied."
            );
            return;
        }

        if (!requestedFile.exists() ||
                !requestedFile.isFile()) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "File not found."
            );
            return;
        }

        Path filePath =
                requestedFile.toPath();

        String contentType =
                Files.probeContentType(filePath);

        if (contentType == null) {
            contentType =
                    "application/octet-stream";
        }

        response.setContentType(contentType);

        response.setContentLengthLong(
                requestedFile.length()
        );

        boolean isImage =
                contentType.startsWith("image/");

        if (isImage) {

            response.setHeader(
                    "Content-Disposition",
                    "inline; filename=\"" +
                            requestedFile.getName() +
                            "\""
            );

        } else {

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"" +
                            requestedFile.getName() +
                            "\""
            );
        }

        try (
                InputStream input =
                        Files.newInputStream(filePath);

                OutputStream output =
                        response.getOutputStream()
        ) {

            byte[] buffer =
                    new byte[8192];

            int bytesRead;

            while ((bytesRead =
                    input.read(buffer)) != -1) {

                output.write(
                        buffer,
                        0,
                        bytesRead
                );
            }
        }
    }

    private String getDataDirectory() {

        String dataDirectory =
                System.getenv("CHAT_DATA_DIR");

        if (dataDirectory == null ||
                dataDirectory.trim().isEmpty()) {

            dataDirectory =
                    System.getProperty("user.home")
                    + File.separator
                    + "SimpleChatAppData";
        }

        return dataDirectory;
    }
}