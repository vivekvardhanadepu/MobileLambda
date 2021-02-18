import os
from flask import Flask
from flask import render_template, request
import subprocess

TEMPLATE_DIR = os.path.join('.', 'templates')
STATIC_DIR = os.path.join('.', 'static')

app = Flask(__name__, template_folder = TEMPLATE_DIR, 
static_folder = STATIC_DIR)

@app.route('/')
def home(): 
    return render_template('home.j2', title = 'Online IDE')

@app.route('/run')
def run(): 
    code = request.args.get('code')
    code_input = request.args.get('input')
    print(code, 'code_input: ', code_input)

    with open('demo.py', 'w+') as f: 
        f.write(code)
    proc = subprocess.run(['python', 'demo.py'], input = code_input, capture_output = True)
    output, error = proc.stdout.decode('utf-8'), proc.stderr.decode('utf-8')
    print(proc)
    print(output, error)
    return {'output': output, 'error' : error}

if __name__ == '__main__' : 
    app.run(debug = True)
