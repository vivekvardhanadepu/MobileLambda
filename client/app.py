import os
from flask import Flask
from flask import render_template, request, session, redirect, url_for, flash
import subprocess

# TEMPLATE_DIR = os.path.join('.', 'templates')
# STATIC_DIR = os.path.join('.', 'static')

app = Flask(__name__)
app.secret_key = os.urandom(12)
activeQueries = {}
answeredQueries = {}
queryId = 0 

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
        if request.form['username'] == '':
            # error = 'Invalid Credentials. Please try again.'
            flash('wrong password!')
        else:
            session['logged_in'] = True
            session['username'] = request.form['username']
            return redirect(url_for('home'))
    return render_template('login.html')

@app.route('/run')
def run(): 
    global activeQueries
    global answeredQueries
    global queryId 

    code = request.args.get('code')
    code_input = request.args.get('input')

    currId = str(queryId)
    activeQueries[currId] = {'code' : code, 'code_input' : code_input}
    queryId += 1

    print(str(activeQueries))
    while currId not in answeredQueries : 
        # print(currId, answeredQueries)
        pass

    data = answeredQueries.pop(currId)
    print(data['code'], data['code_input'], data['code_output'])
    assert data['code'] == code and data['code_input'] == code_input
    return {'error' : data['error'], 'output' : data['code_output']}

@app.route('/getCodes')
def getCodes(): 
    if not activeQueries : 
        return ''
    currId, data = activeQueries.popitem()
    return {'currId' : currId, 'code' : data['code'], 'input' : data['code_input']}

@app.route('/submitOutput')
def submitOutput(): 
    currId = request.args.get('currId')
    code = request.args.get('code')
    code_input = request.args.get('code_input')
    code_output = request.args.get('code_output') 
    error = request.args.get('error')

    print('code : ', code, '\ncode_input: ', code_input, '\ncode_output: ', code_output)
    
    answeredQueries[currId] = {'code' : code, 'code_input' : code_input, 'code_output' : code_output, 'error' : error}
    return ''

@app.route('/processing')
def processing():
    queryID = request.args.get('query_id')
    print("query ID :", queryID)
    return ''
# import time 
# NUM = 0 
# @app.route('/some_request')
# def some_request(): 
#     global NUM
#     NUM += 1 
#     if NUM == 1 : 
#         time.sleep(100)

#     return 'hello- ' + str(NUM) 
#     print(request.url)
#     return 'hello'

if __name__ == '__main__' : 
    app.secret_key = os.urandom(12)
    app.run(debug = True)
