document.addEventListener("DOMContentLoaded", () => {

    const chatForm = document.getElementById("chatForm");
    const messageInput = document.getElementById("messageInput");
    const userInput = document.getElementById("userInput");

    const fileInput = document.getElementById("fileInput");
    const attachButton = document.getElementById("attachButton");
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

        userInput.value = savedUsername;

        userInput.readOnly = true;

    }


    /* =========================
       FILE SELECTION
       ========================= */

    if (attachButton && fileInput) {

        attachButton.addEventListener("click", () => {

            fileInput.click();

        });

    }


    if (fileInput) {

        fileInput.addEventListener("change", () => {

            const file = fileInput.files[0];

            if (!file) {
                return;
            }

            if (fileName) {

                fileName.textContent =
                    file.name;

            }

            if (fileSize) {

                fileSize.textContent =
                    formatFileSize(file.size);

            }

            if (filePreview) {

                filePreview.style.display =
                    "flex";

            }

        });

    }


    /* =========================
       REMOVE SELECTED FILE
       ========================= */

    if (removeFileButton) {

        removeFileButton.addEventListener(
            "click",
            () => {

                if (fileInput) {
                    fileInput.value = "";
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

                    fileSize.textContent = "";

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


                /*
                 * User must provide a name.
                 */

                if (!user) {

                    alert(
                        "Please enter your name."
                    );

                    userInput.focus();

                    return;
                }


                /*
                 * Save username in browser.
                 */

                localStorage.setItem(
                    "chatUsername",
                    user
                );


                /*
                 * If a file is selected,
                 * upload the file.
                 */

                if (file) {

                    await uploadFile(
                        user,
                        file
                    );

                    return;
                }


                /*
                 * If there is no file,
                 * send a normal text message.
                 */

                if (!message) {

                    alert(
                        "Please enter a message."
                    );

                    messageInput.focus();

                    return;
                }


                sendButton.disabled = true;

                chatForm.submit();

            }
        );

    }


    /* =========================
       FILE UPLOAD
       ========================= */

    async function uploadFile(
        user,
        file
    ) {

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


        /*
         * XMLHttpRequest is used instead of
         * fetch because it provides upload
         * progress information.
         */

        const xhr =
            new XMLHttpRequest();


        /*
         * Upload progress
         */

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


        /*
         * Upload completed
         */

        xhr.addEventListener(
            "load",
            () => {

                if (
                    xhr.status >= 200 &&
                    xhr.status < 300
                ) {

                    showUploadProgress(100);

                    window.location.href =
                        "chat";

                } else {

                    hideUploadProgress();

                    alert(
                        "File upload failed."
                    );

                    enableSendButton();

                }

            }
        );


        /*
         * Network/server error
         */

        xhr.addEventListener(
            "error",
            () => {

                hideUploadProgress();

                alert(
                    "Unable to upload the file."
                );

                enableSendButton();

            }
        );


        /*
         * Upload cancelled
         */

        xhr.addEventListener(
            "abort",
            () => {

                hideUploadProgress();

                enableSendButton();

            }
        );


        xhr.open(
            "POST",
            "upload",
            true
        );


        xhr.send(formData);

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
                    enabled ? "true" : "false"
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
       SCROLL TO LATEST MESSAGE
       ========================= */

    if (messagesContainer) {

        messagesContainer.scrollTop =
            messagesContainer.scrollHeight;

    }


    /* =========================
       FORMAT FILE SIZE
       ========================= */

    function formatFileSize(bytes) {

        if (bytes === 0) {

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
                Math.pow(1024, index)
            ).toFixed(2)
            + " "
            + units[index]
        );

    }

});