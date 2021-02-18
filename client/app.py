import os
from flask import Flask
from flask import render_template, request, session, redirect, url_for, flash
import subprocess

# TEMPLATE_DIR = os.path.join('.', 'templates')
# STATIC_DIR = os.path.join('.', 'static')

app = Flask(__name__)

@app.route('/')
def home():
    if not session.get('logged_in'):
        return render_template('login.html')
    else:
        return render_template('home.j2', title = 'Online IDE')

@app.route('/login', methods=['GET', 'POST'])
def login():
    # error = None
    if request.method == 'POST':
        if request.form['username'] != 'admin' or request.form['password'] != 'admin':
            # error = 'Invalid Credentials. Please try again.'
            flash('wrong password!')
        else:
            session['logged_in'] = True
            return redirect(url_for('home'))
    return render_template('login.html')

@app.route('/run')
def run(): 
    code = request.args.get('code')
    code_input = request.args.get('input')
    print(code, 'code_input: ', code_input)

    with open('demo.py', 'w+') as f: 
        f.write(code)
    proc = subprocess.run(['python3', 'demo.py'], input = code_input, capture_output = True,
                                     text = True)
    # output, error = proc.stdout.decode('utf-8'), proc.stderr.decode('utf-8')
    output = proc.stdout
    error  = proc.stderr
    print(proc)
    print(output, error)
    return {'output': output, 'error' : error}

if __name__ == '__main__' : 
    app.secret_key = os.urandom(12)
    app.run(debug = True)
