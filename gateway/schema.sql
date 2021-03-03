CREATE TABLE request(
    client_url INTEGER NOT NULL,
    request_id INTEGER PRIMARY KEY AUTOINCREMENT,
    status BOOLEAN DEFAULT '0' 
    code TEXT NOT NULL,
    code_input TEXT NOT NULL,
    lang TEXT 
);

CREATE TABLE server(
    socket_id INTEGER NOT NULL,
    request_id INTEGER,
    status BOOLEAN DEFAULT '1'
);