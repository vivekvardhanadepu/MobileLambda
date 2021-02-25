import sqlite3

conn = sqlite3.connect('database.db')
print "Opened database successfully"
conn.execute('CREATE TABLE requests (name TEXT, addr TEXT, city TEXT, pin TEXT)')