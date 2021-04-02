import sqlite3

db = sqlite3.connect('database.db')
print("Opened database successfully")

with open('schema.sql', 'r') as sql_file:
    sql_script = sql_file.read()
cursor = db.cursor()
cursor.executescript(sql_script)
db.commit()
db.close()