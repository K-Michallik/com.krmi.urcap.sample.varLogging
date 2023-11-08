#!/usr/bin/env python

import sys
import logging
import threading
import time
import csv
import socket

from SimpleXMLRPCServer import SimpleXMLRPCServer, SimpleXMLRPCRequestHandler
from SocketServer import ThreadingMixIn

title = ""
var_list = []
var_max = 1000
path_to_file = ""

# Set the logging level to Warning.
logging.basicConfig(level=logging.INFO)


def get_file_path():
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.settimeout(2)
    s.connect(("127.0.0.1", 29999))
    s.recv(1024)
    s.sendall(("get serial number\n").encode())
    # Receive the serial number from the robot and remove the newline character
    serial_number = s.recv(1024).decode().rstrip("\n")
    s.close()
    if serial_number == "20195599999":
        path = "/ursim/programs/"
        return path
    else:
        path = "/programs/"
        return path


def get_path():
    # return the full path this file is running from as a string.
    return sys.path[0]


def send_var(var_name, var_value):
    global var_list
    # append a timestamp using isotime, variable name, and variable value to the list
    if len(var_list) >= var_max:
        # Call write_to_csv() if the list is full and reset var_list to an empty list
        write_to_csv()
        clear_vars()
    else:
        var_list.append([time.strftime("%Y-%m-%d-%H:%M:%S", time.localtime()), var_name, var_value])
    # return the length of var_list as a string
    return len(var_list)


def clear_vars():
    global var_list
    var_list = []
    return "Variables cleared"


def write_to_csv():
    file_path = get_file_path() + time.strftime("%Y-%m-%d-%H_%M_%S", time.localtime()) + ".csv"
    # Use csv.writer to write var_list to a csv file. Var_list is a list of tuples. The filename is the current time.
    with open(file_path, "wb") as f:
        writer = csv.writer(f)
        writer.writerow(["Time", "Variable Name", "Variable Value"])
        # writer should iterate through the list of tuples and write each tuple to a row in the csv file.
        for row in var_list:
            writer.writerow(row)
    return "CSV file written"


logging.info("Var logging daemon started")


class LoggingSimpleXMLRPCRequestHandler(SimpleXMLRPCRequestHandler):
    def handle_one_request(self):
        try:
            # First, call the superclass's handle_one_request to parse headers
            SimpleXMLRPCRequestHandler.handle_one_request(self)

            # Now it's safe to access the headers
            connection_header = self.headers.getheader('Connection', 'Not Specified')
            logging.debug("Connection header: %s", connection_header)
        except Exception as e:
            logging.exception("Exception in request handler: %s", e)


class LoggingSimpleXMLRPCServer(SimpleXMLRPCServer):
    def _dispatch(self, method, params):
        try:
            # Attempt to get the function from the server's function table
            func = self.funcs[method]
        except KeyError:
            # If the method is not found, raise an exception
            raise Exception('method "%s" is not supported' % method)

        # Log the method call and its arguments
        logging.debug("Method call: %s(%s)", method, ', '.join(repr(p) for p in params))

        # Call the method as usual
        return func(*params)


class MultithreadedSimpleXMLRPCServer(ThreadingMixIn, LoggingSimpleXMLRPCServer):
    def __init__(self, *args, **kwargs):
        LoggingSimpleXMLRPCServer.__init__(self, *args, **kwargs)
        self.requestHandler = LoggingSimpleXMLRPCRequestHandler
        logging.debug("Init Server thread created: %s", threading.current_thread().name)

    def __del__(self):
        logging.debug("Server thread destroyed: %s", threading.current_thread().name)

    def process_request(self, request, client_address):
        logging.debug("Processing request from %s", client_address)
        ThreadingMixIn.process_request(self, request, client_address)

    def finish_request(self, request, client_address):
        logging.debug("Finishing request from %s", client_address)
        LoggingSimpleXMLRPCServer.finish_request(self, request, client_address)

    def process_request_thread(self, request, client_address):
        logging.debug("Server thread created: %s", threading.current_thread().name)
        try:
            ThreadingMixIn.process_request_thread(self, request, client_address)
        finally:
            logging.debug("Server thread completed: %s", threading.current_thread().name)


server = MultithreadedSimpleXMLRPCServer(("127.0.0.1", 48010), requestHandler=LoggingSimpleXMLRPCRequestHandler)
server.RequestHandlerClass.protocol_version = "HTTP/1.1"
server.register_function(get_path, "get_path")
server.register_function(send_var, "send_var")
server.register_function(write_to_csv, "write_to_csv")
server.register_function(clear_vars, "clear_vars")
server.register_function(get_file_path, "get_file_path")
server.serve_forever()
