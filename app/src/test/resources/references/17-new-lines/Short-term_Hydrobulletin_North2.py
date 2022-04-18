#!/usr/bin/python3.6
# -*- coding: utf-8 -*-
#
# Copyright (c) 2019, KISTERS AG, Germany.
# All rights reserved.
# Modification, redistribution and use in source and binary
# forms, with or without modification, are not permitted
# without prior written approval by the copyright holder.
import cgi
import requests
from requests import Request
import json
from typing import Dict, List
from datetime import datetime as dt, time, timedelta as td
import isodate
import sys
import argparse

def printJsonHeader():
    print("Content-Type: application/json;charset=utf-8")
    print()

def printErrorHeader():
    print("Status: 500 An internal error has happened while running the Python script")
    print()

def readJson():
    file = open("wiski_reference.json", "r")
    return file.read()

def printArguments():
    arguments = cgi.FieldStorage()
    for key in arguments.keys():
        print(key, arguments[key].value)


###############################################################################################

class KiwisException(Exception):
    def __init__(self, message: str):
        super(Exception, self).__init__(message)


class KiWIS:

    def __init__(self, base_uri, datasource):
        self.base_uri = base_uri
        self.base_params = {
            "datasource": datasource,
            "service": "kisters",
            "type": "queryServices",
            "format": "objson"
        }
        self.headers = {
            "content-type": "application/json",
            "accept": "application/json"
        }
        return

    def _get(self, params):
        return requests.get(self.base_uri, params=params, verify=False, headers=self.headers)

    def _post(self, params):
        #TODO: Make it work
        return requests.post(self.base_uri, data=params, verify=False, headers=self.headers)

    def _request(self, params, prepare=False):
        url = Request("GET", self.base_uri, params=params).prepare()
        if prepare:
            return url
        elif len(url.url) > 8192:
            return self._post(params)
        else:
            return self._get(params)

    def get_station_list(self, stationgroup_id):
        params = self.base_params.copy()
        params["request"] = "getStationList"
        params["stationgroup_id"] = stationgroup_id
        res = self._request(params)
        if res.status_code != 200:
            raise KiwisException("Error getting Data from KiWIS")
        return res.json()

    def get_timeseries_list(self, station_id_list: List[str]):
        params = self.base_params.copy()
        params["request"] = "getTimeseriesList"
        params["station_id"] = ",".join(station_id_list)
        res = self._request(params)
        if res.status_code != 200:
            raise KiwisException("Error getting Data from KiWIS")
        return res.json()

    def get_timeseries_values(self, from_, to, ts_list, period):
        params = self.base_params.copy()
        params["request"] = "getTimeseriesValues"
        params["format"] = "json"
        params["ts_id"] = ",".join(ts_list)
        params["from"] = from_.strftime("%Y-%m-%d")
        # params["to"] = to.strftime("%Y-%m-%d")
        params["period"] = period
        params["metadata"] = True
        res = self._request(params)
        if res.status_code != 200:
            error = res.json()
            if error["code"] == "TooManyResults":
                res = []
                splitter = 5
                for i in range(0, int(len(ts_list) / splitter)):
                    if i*10 + 10 > len(ts_list):
                        splitted_list = ts_list[i*splitter:]
                    else:
                        splitted_list = ts_list[i*splitter:i*splitter + splitter]
                    params["ts_id"] = ",".join(splitted_list)
                    req = self._request(params)
                    res.append(req.json())
                return res
            raise KiwisException("Error getting Data from KiWIS.\nError: %s\nMessage: %s" % (error["code"], error["message"]))
        return res.json()

    def generate_get_graph_url(self, from_, station_id, template, period):
        params = self.base_params.copy()
        params["request"] = "getStationGraph"
        params["format"] = "png"
        params["from"] = from_
        params["period"] = period
        params["station_id"] = station_id
        params["template"] = template
        params["metadata"] = True
        return self._request(params, prepare=True).url

def remove_empty_keys(d):
    """Remove empty keys from the json, so that empty lists are not parsed"""
    for k in d.keys():
        if not d[k]:
            del d[k]
    return d


class Result:

    def __init__(self):
        self.input_parameters: Dict = {}
        self.red_river_graphs: List[WaterLevelStation] = []
        self.thai_binh_graphs: List = []
        self.water_hq_table: Dict = {
            "styles": [
                {
                    "column_style": {
                        "selector": ".field-r1",
                        "font-weight": "normal"
                    }
                }
            ],
            "columns": [],
            "data": []
        }

    def as_dict(self):
        res = {
            "input_parameters": self.input_parameters,
            "red_river_graphs": [wls.as_dict() for wls in self.red_river_graphs],
            "thai_binh_graphs": [wqs.as_dict() for wqs in self.thai_binh_graphs],
            "water_hq_table": {
                "styles": [
                    {
                        "column_style": {
                            "selector": ".field-r1",
                            "font-weight": "bold",
                            #"color": "red"
                        }
                    }
                ],
                "columns": [col.as_dict() for col in self.water_hq_table["columns"]],
                "data": self.water_hq_table["data"]
            }
        }
        return res


class WaterLevelStation:

    def __init__(self, station_id="", station_no="", station_label="", url=""):
        self.station_id = station_id
        self.station_no = station_no
        self.station_label = station_label
        self.url = url

    def as_dict(self):
        return {
            "station_id": self.station_id,
            "station_no": self.station_no,
            "station_label": self.station_label,
            "url": self.url
        }


class Columns:

    def __init__(self, field="", label="", children=None):
        if children is None:
            children = []
        self.field = field
        self.label = label
        self.children: List[Columns] = children

    def as_dict(self):
        if len(self.children):
            return {
                "field": self.field,
                "label": self.label,
                "children": [child.as_dict() for child in self.children]
            }
        else:
            return {
                "field": self.field,
                "label": self.label
            }


def up_or_down(a, b):
    if a and b:
        if a > b:
            return "down"
        elif a == b:
            return "neutral"
        elif b > a:
            return "up"
    else:
        return None


def get_closest_timestamp(data_ts, time: dt):
    data_dates = [isodate.parse_datetime(date[0]) for date in data_ts]
    if data_ts:
        closest_date = min(data_dates, key=lambda x: abs(x - time))
        for data in data_ts:
            if isodate.parse_datetime(data[0]) == closest_date:
                return data[1]
    else:
        return None


def create_json(station_group, ref_date, reference_time_start: time, reference_time_end: time, indent):
    """Params:
    station_group: Name of the station group
    ref_date: datetime object of the reference date
    reference_time_start: reference time (datetime.time object) for the first value
    reference_time_end: reference time (datetime.time object) for the second value
    indent: Boolean wether or not you want indentation in the output.
    """
    if indent:
        indent_ = 4
    else:
        indent_ = None
    # first: get all stations in the group
    kiwis = KiWIS("http://vm-gis-rasdaman.kisters.de/KiWIS/KiWIS", 0)
    result = Result()
    result.input_parameters = {
        "station_group": station_group,
        "reference_date": ref_date.isoformat(),
        "timeseries_filter": ["ts_name", "05 - Hour.Max'"]
    }
    stations = kiwis.get_station_list(station_group)
    from_ = dt(year=ref_date.year, day=ref_date.day, month=ref_date.month, hour=reference_time_start.hour,
               minute=reference_time_start.minute, tzinfo=ref_date.tzinfo)
    to = dt(year=ref_date.year, day=ref_date.day, month=ref_date.month, hour=reference_time_end.hour,
            minute=reference_time_end.minute, tzinfo=ref_date.tzinfo)
    station_ids = []
    for station in stations:
        url = kiwis.generate_get_graph_url(from_=from_.strftime("%Y-%m-%d"), station_id=station["station_id"],
                                           template="precip_h_week", period="P1W")
        wl_station = WaterLevelStation(station["station_id"], station["station_no"], station["station_name"], url=url)
        result.red_river_graphs.append(wl_station)
        result.thai_binh_graphs.append(wl_station)
        station_ids.append(station["station_id"])

    forecast_from_1 = from_ + td(days=1)
    forecast_to_1 = to + td(days=1)
    forecast_from_2 = from_ + td(days=1)
    forecast_to_2 = to + td(days=1)

    station_group_ts = kiwis.get_timeseries_list(station_ids)

    ts_filtered = [ts for ts in station_group_ts if ts[
        "ts_name"] == '05 - Hour.Max']  # Filter for certain parameters. Can be changed by changing the column name and value. 523: Precip

    ts_res = kiwis.get_timeseries_values(from_, ts_list=[ts["ts_id"] for ts in ts_filtered], period="P1M",
                                         to=forecast_to_2)
    if type(ts_res[0]) == dict:
        ts_res = [[ts] for ts in ts_res]

    river_col = Columns(label="River", field="r1")
    station_col = Columns(label="Station", field="s1")
    t_1_measured_col = Columns(label=from_.strftime("%Hh %d/%m"), field="t1_measured")
    t_2_measured_col = Columns(label=to.strftime("%Hh %d/%m"), field="t2_measured")
    tendency_measured_col = Columns(label="Tendency", field="tendency_measured")
    hq_meas_col = Columns(label="H Q measured", field="hqmeasured", children=[t_1_measured_col, t_2_measured_col, tendency_measured_col])
    t_1_forecast_col = Columns(label=forecast_from_1.strftime("%Hh %d/%m"), field="t1_forecast")
    t_2_forecast_col = Columns(label=forecast_to_1.strftime("%Hh %d/%m"), field="t2_forecast")
    t_3_forecast_col = Columns(label=forecast_from_2.strftime("%Hh %d/%m"), field="t3_forecast")
    t_4_forecast_col = Columns(label=forecast_to_2.strftime("%Hh %d/%m"), field="t4_forecast")
    hq_forecast_col = Columns(label="H Q forecast", field="hqforecast",
                              children=[t_1_forecast_col, t_2_forecast_col, t_3_forecast_col, t_4_forecast_col])

    result.water_hq_table["columns"] = [
        river_col,
        station_col,
        hq_meas_col,
        hq_forecast_col
    ]
    for ts_list in ts_res:
        for ts in ts_list:
            data = ts["data"]
            first_data = get_closest_timestamp(data, from_)
            second_data = get_closest_timestamp(data, to)
            change = up_or_down(first_data, second_data)
            data_ = {
                "r1": "River Placeholder",  # TODO: Actual River name
                "s1": ts["station_name"],
                "t1_measured": first_data,
                "t2_measured": second_data,
                "tendency_measured": change,
                "t1_forecast": "forecast_1_from_placeholder",  # TODO: Actual forecasts
                "t2_forecast": "forecast_1_to_placeholder",
                "tendency_forecast_1": "forecast_1_change_placeholder",
                "t3_forecast": "forecast_2__from_placeholder",
                "t4_forecast": "forecast_2_to_placeholder",
                "tendency_forecast_2": "forecast_2_change_placeholder",
            }
            result.water_hq_table["data"].append(data_)

    return json.dumps(result.as_dict(), indent=indent_)



def main(argv):
    parser = argparse.ArgumentParser(description="Create JSON file for the PortalApp export",
                                     add_help=False)
    req_args = parser.add_argument_group("Required")
    opt_args = parser.add_argument_group("Optional")

    opt_args.add_argument("-h", "--help", action="help", help="show this help message and exit")

    req_args.add_argument("--station-group-id", required=True, help="ID of the Station group to be considered")
    req_args.add_argument("--reference-date", required=True,
                          help="Reference Date for the creation of the JSON file in ISO-Format")

    opt_args.add_argument("--outfile", required=False, help="Path and name of the resulting JSON file")
    opt_args.add_argument("--no-indent", action="store_false", help="Use if you dont want indentation in the output")

    args = parser.parse_args(argv)

    sg_id = args.station_group_id
    ref_date = isodate.parse_datetime(args.reference_date)
    outfile = args.outfile

    reference_time_start = time(hour=7)
    reference_time_end = time(hour=19)

    if outfile:
        with open(outfile, "w") as out_f:
            out_f.write(create_json(sg_id, ref_date, reference_time_start, reference_time_end, indent=args.no_indent))
    else:
        print(create_json(sg_id, ref_date, reference_time_start, reference_time_end, indent=args.no_indent))

    return


###############################################################################################

def runLogic():
    try:
        argv = ['--station-group-id', '14444', '--reference-date', '2018-04-03T13:37:00Z']
        main(argv)
        return True
    except:
        printErrorHeader()
        raise Exception("An internal error has happened while running the Python script")
        return False

def printReferenceJson():
    file = open("Short-term_Hydrobulletin_North-reference-data.txt", "r")
    print(file.read())

def cgiMain():
    printJsonHeader()
    #runLogic()
    printReferenceJson()

cgiMain()
