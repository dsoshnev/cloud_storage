package common;

public enum CommandType {
    AUTH,
    UPLOAD,             // Upload file from the client to the server
    DOWNLOAD,           // Download file from the server to the client
    LS,                 // List information about the FILEs
    CD,                 // Change current server directory
    RM,                 // Remove file from server
    MKDIR,              // Make server directory
    UPDATE_USERS_LIST,
    MESSAGE,
    ERROR,
    END
}
