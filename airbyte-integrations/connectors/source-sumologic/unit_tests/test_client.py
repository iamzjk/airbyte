#
# Copyright (c) 2021 Airbyte, Inc., all rights reserved.
#
import pytest
from requests import HTTPError
from requests.models import Response
from source_sumologic.client import Client


def test_check__success(mocker):
    resp = Response()
    resp.status_code = 404
    sumo = mocker.Mock()
    sumo.session.get.return_value = resp
    sumo.endpoint = "https://fakeurl"
    mocker.patch("source_sumologic.client.SumoLogic", return_value=sumo)
    assert Client("foo", "bar").check() is True


def test_check__fail(mocker):
    resp = Response()
    resp.status_code = 401
    sumo = mocker.Mock()
    sumo.session.get.return_value = resp
    sumo.endpoint = "https://fakeurl"
    mocker.patch("source_sumologic.client.SumoLogic", return_value=sumo)
    with pytest.raises(HTTPError):
        Client("foo", "bar").check()


def test_search(mocker):
    mocker.patch("time.sleep", return_value=None)
    sumo = mocker.Mock()
    search_job = mocker.Mock()
    sumo.search_job.return_value = search_job
    sumo.search_job_status.side_effect = [
        {"state": "x"},
        {"state": "DONE GATHERING RESULTS", "messageCount": 20000},
    ]
    sumo.search_job_messages.return_value = {
        "messages": [
            {"map": {"_messagetime": 100}},
            {"map": {"_messagetime": 200}},
        ]
    }
    mocker.patch("source_sumologic.client.SumoLogic", return_value=sumo)

    config = {
        "query": "xyz",
        "from_time": None,
        "to_time": None,
        "time_zone": "UTC",
        "by_receipt_time": False,
    }

    messages = list(Client("foo", "bar").search(config["query"]))

    sumo.search_job.assert_called_once_with(
        config["query"],
        config["from_time"],
        config["to_time"],
        config["time_zone"],
        config["by_receipt_time"],
    )
    assert sumo.search_job_status.call_count == 2
    assert len(messages) == 4
    sumo.search_job_messages.assert_has_calls(
        [
            mocker.call(search_job, limit=10000, offset=0),
            mocker.call(search_job, limit=10000, offset=10000),
        ]
    )
