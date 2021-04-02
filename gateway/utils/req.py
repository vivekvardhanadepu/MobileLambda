import requests
from requests.exceptions import HTTPError
from utils.db import *
from flask import request


def run_req():
    error, pending_req = fetch_pending_req()
    if(error):
        return
    if(~pending_req):
        print("no pending requests!!")
        return
    
    error, server_id = fetch_server() 
    if(error):
        return
    if(~server_id):
        print("servers are busy: ", pending_req["request_id"], "!!")
        return
    
    error = set_busy(server_id, pending_req["request_id"])
    if(error):
        return

    dict2send = {'code': pending_req["code"], 'input': pending_req["code_input"]}

    try:
        response = requests.post(server_id, json=dict2send)
        # If the response was successful, no Exception will be raised
        response.raise_for_status()
    except HTTPError as http_err:
        print(f'HTTP error occurred: {http_err}')
        error = set_free(server_id)
        if(error):
            return
    except Exception as err:
        print(f'Other error occurred: {err}')
        error = set_free(server_id)
        if(error):
            return
    else:
        print("Request Successful: ", pending_req["request_id"], "!!")
        response_client = requests.post(server_id, json=response)
        error = remove_request(pending_req["request_id"])
        if(error):
            return

        error = set_free(server_id)
        if(error):
            return
