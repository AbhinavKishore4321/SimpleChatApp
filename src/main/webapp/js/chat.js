document.addEventListener("DOMContentLoaded", () => {

    const chatForm =
        document.getElementById("chatForm");

    const messageInput =
        document.getElementById("messageInput");

    const userInput =
        document.getElementById("userInput");

    const fileInput =
        document.getElementById("fileInput");

    const attachButton =
        document.getElementById("attachButton");

    const removeFileButton =
        document.getElementById("removeFileButton");

    const filePreview =
        document.getElementById("filePreview");

    const fileName =
        document.getElementById("selectedFileName");

    const fileSize =
        document.getElementById("selectedFileSize");

    const sendButton =
        document.getElementById("sendButton");

    const messagesContainer =
        document.getElementById("messagesContainer");

    const darkModeButton =
        document.getElementById("darkModeButton");


    /* =========================
       REMEMBER USERNAME
       ========================= */

    const savedUsername =
        localStorage.getItem("chatUsername");

    if (savedUsername && userInput) {

        userInput.value =
            savedUsername;

        userInput.readOnly =
            true;
    }


    /* =========================
       FILE SELECTION
       ========================= */

    if (attachButton && fileInput) {

        attachButton.addEventListener(
            "click",
            () => {

                fileInput.click();

            }
        );
    }


    if (fileInput) {

        fileInput.addEventListener(
            "change",
            () => {

                const file =
                    fileInput.files[0];

                if (!file) {
                    return;
                }

                if (fileName) {

                    fileName.textContent =
                        file.name;
                }

                if (fileSize) {

                    fileSize.textContent =
                        formatFileSize(
                            file.size
                        );
                }

                if (filePreview) {

                    filePreview.style.display =
                        "flex";
                }

            }
        );
    }


    /* =========================
       REMOVE SELECTED FILE
       ========================= */

    if (removeFileButton) {

        removeFileButton.addEventListener(
            "click",
            () => {

                if (fileInput) {

                    fileInput.value =
                        "";
                }

                if (filePreview) {

                    filePreview.style.display =
                        "none";
                }

                if (fileName) {

                    fileName.textContent =
                        "No file selected";
                }

                if (fileSize) {

                    fileSize.textContent =
                        "";
                }

            }
        );
    }


    /* =========================
       FORM SUBMISSION
       ========================= */

    if (chatForm) {

        chatForm.addEventListener(
            "submit",
            async (event) => {

                event.preventDefault();

                const user =
                    userInput.value.trim();

                const message =
                    messageInput.value.trim();

                const file =
                    fileInput.files[0];


                if (!user) {

                    alert(
                        "Please enter your name."
                    );

                    userInput.focus();

                    return;
                }


                localStorage.setItem(
                    "chatUsername",
                    user
                );


                /* =========================
                   FILE UPLOAD
                   ========================= */

                if (file) {

                    await uploadFile(
                        user,
                        file
                    );

                    return;
                }


                /* =========================
                   TEXT MESSAGE
                   ========================= */

                if (!message) {

                    alert(
                        "Please enter a message."
                    );

                    messageInput.focus();

                    return;
                }


                sendButton.disabled =
                    true;

                chatForm.submit();

            }
        );
    }


    /* =========================
       FILE UPLOAD
       ========================= */

    function uploadFile(
        user,
        file
    ) {

        return new Promise(
            (resolve, reject) => {

                const formData =
                    new FormData();

                formData.append(
                    "user",
                    user
                );

                formData.append(
                    "file",
                    file
                );


                const xhr =
                    new XMLHttpRequest();


                /* =========================
                   UPLOAD PROGRESS
                   ========================= */

                xhr.upload.addEventListener(
                    "progress",
                    (event) => {

                        if (!event.lengthComputable) {
                            return;
                        }

                        const percentage =
                            Math.round(
                                (event.loaded /
                                    event.total) *
                                100
                            );

                        showUploadProgress(
                            percentage
                        );

                    }
                );


                /* =========================
                   UPLOAD COMPLETE
                   ========================= */

                xhr.addEventListener(
                    "load",
                    () => {

                        if (
                            xhr.status >= 200 &&
                            xhr.status < 300
                        ) {

                            showUploadProgress(
                                100
                            );

                            window.location.href =
                                "chat";

                            resolve();

                        } else {

                            hideUploadProgress();

                            alert(
                                "File upload failed."
                            );

                            enableSendButton();

                            reject(
                                new Error(
                                    "Upload failed"
                                )
                            );
                        }

                    }
                );


                /* =========================
                   NETWORK ERROR
                   ========================= */

                xhr.addEventListener(
                    "error",
                    () => {

                        hideUploadProgress();

                        alert(
                            "Unable to upload the file."
                        );

                        enableSendButton();

                        reject(
                            new Error(
                                "Network error"
                            )
                        );

                    }
                );


                /* =========================
                   UPLOAD ABORTED
                   ========================= */

                xhr.addEventListener(
                    "abort",
                    () => {

                        hideUploadProgress();

                        enableSendButton();

                        reject(
                            new Error(
                                "Upload aborted"
                            )
                        );

                    }
                );


                xhr.open(
                    "POST",
                    "upload",
                    true
                );


                xhr.send(formData);

            }
        );
    }


    /* =========================
       UPLOAD PROGRESS
       ========================= */

    function showUploadProgress(
        percentage
    ) {

        let progressContainer =
            document.getElementById(
                "uploadProgress"
            );


        if (!progressContainer) {

            progressContainer =
                document.createElement(
                    "div"
                );

            progressContainer.id =
                "uploadProgress";

            progressContainer.className =
                "upload-progress";


            progressContainer.innerHTML = `
                <div class="upload-progress-info">
                    <span>Uploading...</span>
                    <span id="uploadPercentage">
                        0%
                    </span>
                </div>

                <div class="upload-progress-bar">
                    <div
                        id="uploadProgressFill"
                        class="upload-progress-fill">
                    </div>
                </div>
            `;


            if (chatForm) {

                chatForm.parentNode.insertBefore(
                    progressContainer,
                    chatForm
                );
            }
        }


        progressContainer.style.display =
            "block";


        const progressFill =
            document.getElementById(
                "uploadProgressFill"
            );

        const percentageText =
            document.getElementById(
                "uploadPercentage"
            );


        if (progressFill) {

            progressFill.style.width =
                percentage + "%";
        }


        if (percentageText) {

            percentageText.textContent =
                percentage + "%";
        }

    }


    function hideUploadProgress() {

        const progressContainer =
            document.getElementById(
                "uploadProgress"
            );

        if (progressContainer) {

            progressContainer.style.display =
                "none";
        }
    }


    /* =========================
       ENABLE SEND BUTTON
       ========================= */

    function enableSendButton() {

        if (sendButton) {

            sendButton.disabled =
                false;
        }
    }


    /* =========================
       ENTER TO SEND
       ========================= */

    if (messageInput) {

        messageInput.addEventListener(
            "keydown",
            (event) => {

                if (
                    event.key === "Enter" &&
                    !event.shiftKey
                ) {

                    event.preventDefault();

                    if (chatForm) {

                        chatForm.requestSubmit();
                    }
                }

            }
        );
    }


    /* =========================
       DARK MODE
       ========================= */

    if (darkModeButton) {

        darkModeButton.addEventListener(
            "click",
            () => {

                document.body.classList.toggle(
                    "dark-mode"
                );

                const enabled =
                    document.body.classList.contains(
                        "dark-mode"
                    );

                localStorage.setItem(
                    "darkMode",
                    enabled
                        ? "true"
                        : "false"
                );

            }
        );
    }


    if (
        localStorage.getItem("darkMode")
        === "true"
    ) {

        document.body.classList.add(
            "dark-mode"
        );
    }


    /* =========================
       AUTO REFRESH MESSAGES
       ========================= */

    let lastMessageCount = 0;


    async function refreshMessages() {

        try {

            const response =
                await fetch(
                    "chat?action=messages",
                    {
                        cache: "no-store"
                    }
                );


            if (!response.ok) {
                return;
            }


            const messages =
                await response.json();


            /*
             * Only update the chat when
             * the number of messages changes.
             */

            if (
                messages.length !==
                lastMessageCount
            ) {

                updateMessages(
                    messages
                );

                lastMessageCount =
                    messages.length;
            }

        } catch (error) {

            console.log(
                "Message refresh failed:",
                error
            );

        }
    }


    /* =========================
       UPDATE MESSAGE AREA
       ========================= */

    function updateMessages(
        messages
    ) {

        if (!messagesContainer) {
            return;
        }


        const currentUser =
            userInput
                ? userInput.value.trim()
                : "";


        messagesContainer.innerHTML =
            "";


        if (messages.length === 0) {

            return;
        }


        messages.forEach(
            (message) => {

                const messageElement =
                    document.createElement(
                        "div"
                    );


                messageElement.className =
                    "message " +
                    (
                        message.user ===
                        currentUser
                            ? "sent"
                            : "received"
                    );


                const content =
                    document.createElement(
                        "div"
                    );

                content.className =
                    "message-content";


                const user =
                    document.createElement(
                        "div"
                    );

                user.className =
                    "message-user";

                user.textContent =
                    message.user;


                content.appendChild(
                    user
                );


                /*
                 * Text message
                 */

                if (
                    message.type ===
                    "text"
                ) {

                    const text =
                        document.createElement(
                            "div"
                        );

                    text.className =
                        "message-text";

                    text.textContent =
                        message.text;

                    content.appendChild(
                        text
                    );
                }


                /*
                 * Image message
                 */

                else if (
                    message.type ===
                    "image"
                ) {

                    const image =
                        document.createElement(
                            "img"
                        );

                    image.className =
                        "chat-image";

                    image.src =
                        "download?file=" +
                        encodeURIComponent(
                            message.filepath
                        );

                    image.alt =
                        message.filename;

                    image.loading =
                        "lazy";


                    content.appendChild(
                        image
                    );


                    const name =
                        document.createElement(
                            "div"
                        );

                    name.className =
                        "file-name";

                    name.textContent =
                        message.filename;

                    content.appendChild(
                        name
                    );
                }


                /*
                 * Document/file message
                 */

                else {

                    const fileCard =
                        document.createElement(
                            "div"
                        );

                    fileCard.className =
                        "file-card";


                    const icon =
                        document.createElement(
                            "div"
                        );

                    icon.className =
                        "file-icon";

                    icon.textContent =
                        message.type ===
                        "document"
                            ? "📄"
                            : "📎";


                    const details =
                        document.createElement(
                            "div"
                        );

                    details.className =
                        "file-details";


                    const name =
                        document.createElement(
                            "div"
                        );

                    name.className =
                        "file-name";

                    name.textContent =
                        message.filename;


                    const size =
                        document.createElement(
                            "div"
                        );

                    size.className =
                        "file-size";

                    size.textContent =
                        formatFileSize(
                            Number(
                                message.filesize
                            )
                        );


                    details.appendChild(
                        name
                    );

                    details.appendChild(
                        size
                    );


                    const download =
                        document.createElement(
                            "a"
                        );

                    download.className =
                        "download-button";

                    download.href =
                        "download?file=" +
                        encodeURIComponent(
                            message.filepath
                        );

                    download.textContent =
                        "Download";

                    download.target =
                        "_blank";


                    fileCard.appendChild(
                        icon
                    );

                    fileCard.appendChild(
                        details
                    );

                    fileCard.appendChild(
                        download
                    );


                    content.appendChild(
                        fileCard
                    );
                }


                /*
                 * Timestamp
                 */

                const time =
                    document.createElement(
                        "div"
                    );

                time.className =
                    "message-time";

                time.textContent =
                    formatTimestamp(
                        message.timestamp
                    );


                content.appendChild(
                    time
                );


                messageElement.appendChild(
                    content
                );


                messagesContainer.appendChild(
                    messageElement
                );

            }
        );


        messagesContainer.scrollTop =
            messagesContainer.scrollHeight;
    }


    /* =========================
       FORMAT TIMESTAMP
       ========================= */

    function formatTimestamp(
        timestamp
    ) {

        const time =
            Number(timestamp);


        if (!time) {
            return "";
        }


        const date =
            new Date(time);


        return date.toLocaleTimeString(
            [],
            {
                hour: "2-digit",
                minute: "2-digit"
            }
        );
    }


    /* =========================
       FORMAT FILE SIZE
       ========================= */

    function formatFileSize(
        bytes
    ) {

        if (
            !bytes ||
            bytes === 0
        ) {

            return "0 Bytes";
        }


        const units = [
            "Bytes",
            "KB",
            "MB",
            "GB",
            "TB"
        ];


        const index =
            Math.floor(
                Math.log(bytes) /
                Math.log(1024)
            );


        return (
            (
                bytes /
                Math.pow(
                    1024,
                    index
                )
            ).toFixed(2)
            + " "
            + units[index]
        );
    }


    /* =========================
       INITIAL MESSAGE LOAD
       ========================= */

    refreshMessages();


    /* =========================
       AUTOMATIC MESSAGE CHECK
       ========================= */

    setInterval(
        refreshMessages,
        3000
    );


    /* =========================
       INITIAL SCROLL
       ========================= */

    if (messagesContainer) {

        messagesContainer.scrollTop =
            messagesContainer.scrollHeight;
    }

});