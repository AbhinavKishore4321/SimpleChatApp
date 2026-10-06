package com.chat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@WebServlet("/upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024L * 1024L * 1024L,
        maxRequestSize = 1024L * 1024L * 1024L + 1024L * 1024L
)
public class FileUploadServlet extends HttpServlet {

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
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String user = request.getParameter("user");

        Part filePart = request.getPart("file");

        if (user == null || user.trim().isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Username is required."
            );
            return;
        }

        if (filePart == null ||
                filePart.getSize() == 0) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "No file selected."
            );
            return;
        }

        String originalFilename = Paths
                .get(filePart.getSubmittedFileName())
                .getFileName()
                .toString();

        if (originalFilename.isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid filename."
            );
            return;
        }

        String extension = getExtension(originalFilename);

        String type;

        if (isImage(extension)) {
            type = "image";
        } else if (isDocument(extension)) {
            type = "document";
        } else {
            type = "file";
        }

        String folderName;

        if ("image".equals(type)) {
            folderName = "images";
        } else if ("document".equals(type)) {
            folderName = "documents";
        } else {
            folderName = "other";
        }

        String dataDirectory = getDataDirectory();

        File uploadDirectory =
                new File(
                        dataDirectory,
                        "uploads" + File.separator + folderName
                );

        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        String safeFilename =
                System.currentTimeMillis()
                        + "_"
                        + originalFilename;

        File destination =
                new File(uploadDirectory, safeFilename);

        filePart.write(destination.getAbsolutePath());

        String relativePath =
                folderName + "/" + safeFilename;

        store.addFileMessage(
                user.trim(),
                type,
                originalFilename,
                relativePath,
                filePart.getSize()
        );

        response.sendRedirect("chat");
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

    private String getExtension(String filename) {

        int dot =
                filename.lastIndexOf('.');

        if (dot == -1) {
            return "";
        }

        return filename
                .substring(dot + 1)
                .toLowerCase();
    }

    private boolean isImage(String extension) {

        return extension.equals("jpg")
                || extension.equals("jpeg")
                || extension.equals("png")
                || extension.equals("gif")
                || extension.equals("webp");
    }

    private boolean isDocument(String extension) {

        return extension.equals("pdf")
                || extension.equals("doc")
                || extension.equals("docx")
                || extension.equals("xls")
                || extension.equals("xlsx")
                || extension.equals("ppt")
                || extension.equals("pptx")
                || extension.equals("txt")
                || extension.equals("csv");
    }
}