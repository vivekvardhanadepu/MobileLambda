from utils.db import *
from utils.req import *
# from db import *
from flask import Flask, request
from apscheduler.schedulers.background import BackgroundScheduler

app = Flask(__name__)

# req_sched = BackgroundScheduler(daemon=True)
# req_sched.add_job(run_req,'interval',seconds=1)
# req_sched.start()

@app.route('/')
def console():
    return "nada"

@app.route('/run_request', methods = ['POST'])
def run_request():
    if request.method == 'POST':
        client_url = request.url
        # lang = request.form['lang']
        code = request.form['code']
        code_input = request.form['input']
        print(code, 'code_input: ', code_input)

        if(add_request(client_url, code, code_input)):
            return "gateway error"  # send a failure msg

        # server_url = "app's"
        # data_dict = {'code': code, 'code_input': code_input}
        # response = requests.post(server_url, json= data_dict)
        # return "success"

@app.route('/conn_request', methods = ['GET'])
def conn_request():
    if request.method == 'GET':
        socket_id = request.url
        if(int(request.args.get('type'))):
            if(remove_server(socket_id)):
                return "server disconnected" # send proper response
            else:
                return "gateway error"
        else:
            if(add_server(socket_id)):
                return "server connected"
            else:
                return "gateway error"
        
if __name__ == '__main__' :
    app.run(debug = True)
    # db.close()
