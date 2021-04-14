import os
from flask_login import current_user, login_user, logout_user, login_required, UserMixin
from flask import render_template, request, redirect, url_for, flash, Response
from models import * 
from wsgi import db, app
from flask import Flask
from flask import render_template, request, session, redirect, url_for, flash
# import subprocess
from datetime import datetime


# TEMPLATE_DIR = os.path.join('.', 'templates')
# STATIC_DIR = os.path.join('.', 'static')

activeQueries = {}
answeredQueries = {}
processingQueries = {}
WAITING_TIME = 3    #seconds
queryId = 0 


@app.route('/login', methods = ['GET', 'POST'])
def login(): 
    next_url = request.args.get('next', default = '/codebooks')
    if current_user.is_authenticated: 
        return redirect(next_url)

    if request.method == 'POST' : 
        username, password = request.form.get('username'), request.form.get('password')
        user = User.query.filter_by(username = username).first()
        if user and (user.password == password) : 
            login_user(user)
            flash(f"Logged in as {user.username}", 'success')
            return redirect(next_url)
        else:
            flash("Login failed. Please enter valid credentials and try again, or signup for a new account.", 'danger')
    return render_template('login.j2', title = 'Login')

@app.route('/signup', methods = ['GET', 'POST'])
def signup():
    if current_user.is_authenticated: 
        return redirect('/')

    if request.method == 'POST':        # only for students
        form = request.form
        user = User(
            username = form.get('username'), 
            password = form.get('password')
        )
        db.session.add(user) 
        db.session.commit()
        login_user(user)

        flash(f"Logged in as {user.username}", 'success')
        redirect('/codebooks')
    return render_template('signup.j2', title = 'Signup')

@app.route('/logout')
@login_required
def logout(): 
    logout_user()
    return redirect('/')

@app.route('/')
def home(): 
    return render_template('home.j2', title = 'Home')

@app.route('/codebooks', methods = ['POST', 'GET'])
@login_required
def codebooks(): 
    if request.method == 'POST':
        name = request.form.get('name')
        if name : 
            code = Code(name = name, user_id = current_user.id)
            db.session.add(code)
            db.session.commit()

            return redirect('code/' + str(code.id))
    return render_template('codebooks.j2', codebooks = [], title = 'Codebooks')

@app.route('/code/<int:id>')
@login_required
def code(id): 
    code = Code.query.get(id)
    return render_template('code.j2', title = code.name, code = code)

@app.route('/run/<int:id>')
def run(id): 
    global activeQueries
    global answeredQueries
    global queryId 

    code = request.args.get('code')
    code_input = request.args.get('input')


    existing_code = Code.query.get(id)
    existing_code.code = code
    db.session.commit()

    currId = str(queryId)
    activeQueries[currId] = {'code' : code, 'code_input' : code_input}
    queryId += 1
    print(activeQueries)

    return currId

@app.route('/checkStatus/<string:currId>')
def checkStatus(currId): 
    if currId not in answeredQueries : 
        curr = processingQueries.get(currId)
        if curr is None: 
            message, category = "Hold Tight! We're finding a server for your code!", 'info'

        elif (datetime.now() - curr['last_time']).total_seconds() > WAITING_TIME:
            message, category = f"Some problem occured with {curr['user']}'s server. We're transporting  your code!", 'danger'

            activeQueries[currId] = {'code' : curr['code'], 'code_input' : curr['code_input']}
            processingQueries.pop(currId)
        
        else: 
            message, category = f"Your code is running on {curr['user']}'s server", 'success'
        return {'status' : 'Incomplete', 'message' : message, 'category' : category}
    data = answeredQueries.pop(currId)
    print('Run: ', data)

    return {'status': 'Complete', 'error' : data['error'], 'output' : data['code_output']}

@app.route('/getCodes')
def getCodes(): 
    global activeQueries 
    global processingQueries 

    username = 'pshishod2645'

    if not activeQueries : 
        return ''
    
    currId, data = activeQueries.popitem()

    processingQueries[currId] = data
    processingQueries[currId]['last_time'] = datetime.now()
    processingQueries[currId]['user'] = username

    return {'currId' : currId, 'code' : data['code'], 'input' : data['code_input']}

@app.route('/submitOutput')
def submitOutput(): 
    currId = request.args.get('currId')
    code = request.args.get('code')
    code_input = request.args.get('code_input')
    code_output = request.args.get('code_output') 
    error = request.args.get('error')

    processingQueries.pop(currId)
    answeredQueries[currId] = {'code' : code, 'code_input' : code_input, 'code_output' : code_output, 'error' : error}
    print('submitOutput: ', answeredQueries[currId])
    return ''

@app.route('/processing')
def processing():
    queryID = request.args.get('query_id', type = str)
    if queryId in processingQueries: 
        print(queryId)
        processingQueries[queryId]['last_time'] = datetime.now()

    return ''