import sqlite3

def create_db():
    db = sqlite3.connect('database.db')
    print("Opened database successfully")

    with open('schema.sql', 'r') as sql_file:
        sql_script = sql_file.read()
    cursor = db.cursor()
    cursor.executescript(sql_script)
    db.commit()
    db.close()

def add_request(client_url, code, code_input):
    error = False
    try:
        db = sqlite3.connect('database.db')
        db.cursor().execute("INSERT INTO request(client_url, code, code_input)\
                VALUES (?,?,?,?)",(client_url, code, code_input) )
        db.commit()
    except:
        db.rollback()
        error = True
        print("db: error in inserting request") # print to console
    finally:
        db.close()
        return error

def add_server(socket_id):
    error = False
    try:
        db = sqlite3.connect('database.db')
        db.cursor().execute("INSERT INTO server(socket_id, status)\
                VALUES (?,?)",(socket_id, 1) )
        db.commit()
    except:
        db.rollback()
        error = True
        print("db: error in adding server") # print to console
    finally:
        db.close()
        return error
    
def remove_server(socket_id):
    error = False
    try:
        db = sqlite3.connect('database.db')
        db.cursor().execute("DELETE FROM server \
                             WHERE socket_id like ?", socket_id)
        db.commit()
    except:
        db.rollback()
        error = True
        print("db: error in removing server") # print to console
    finally:
        db.close()
        return error

def fetch_pending_req():
    error = False
    try:
        db = sqlite3.connect('database.db')
        req = db.cursor().execute("SELECT * FROM request\
                                   WHERE status == 0\
                                   ORDER BY request_id ASC\
                                   LIMIT 1")
        db.commit()
    except:
        db.rollback()
        error = True
        print("db: error in fetching pending request") # print to console
    finally:
        db.close()
        return error, req

def fetch_server():
    error = False
    try:
        db = sqlite3.connect('database.db')
        server = db.cursor().execute("SELECT * FROM server\
                                      WHERE status == 1\
                                      LIMIT 1")
        db.commit()
    except:
        db.rollback()
        error = True
        print("db: error in fetching pending request") # print to console
    finally:
        db.close()
        return error, server["socket_id"]

def set_busy(socket_id, request_id):
    error = False
    try:
        db = sqlite3.connect('database.db')
        db.cursor().execute("UPDATE server\
                            SET status = 0\
                            SET request_id = ?\
                            WHERE socket_id like ?",
                            request_id, socket_id)
        db.commit()
    except:
        db.rollback()
        error = True
        print("db: error in setting server: ", socket_id, " busy") # print to console
    finally:
        db.close()
        return error

def set_free(socket_id):
    error = False
    try:
        db = sqlite3.connect('database.db')
        db.cursor().execute("UPDATE server\
                            SET status = 1\
                            WHERE socket_id like ?",
                            socket_id)
        db.commit()
    except:
        db.rollback()
        error = True
        print("db: error in setting server : ", socket_id, " free") # print to console
    finally:
        db.close()
        return error

def remove_request(request_id):
    error = False
    try:
        db = sqlite3.connect('database.db')
        db.cursor().execute("DELETE FROM request\
                             WHERE request_id like ?",
                             request_id)
        db.commit()
    except:
        db.rollback()
        error = True
        print("db: error in removing request: ", request_id) # print to console
    finally:
        db.close()
        return error

if __name__ == '__main__' :
    create_db()