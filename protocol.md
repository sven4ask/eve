---
layout: default
title: Communication protocol
---

# Communication Protocol


Each Eve agent has its own, unique url. 
The agents communicate via regular HTTP POST requests, 
using the [JSON-RPC](http://en.wikipedia.org/wiki/JSON_RPC) protocol.
This is a simple and readable protocol, using JSON to format requests and responses. 
[JSON](http://www.json.org/) (JavaScript Object Notation) 
is a lightweight, flexible data-interchange format. 
It is easy for humans to read and write, 
and  easy for machines to parse and generate. 

This page describes:

- [Protocol](#protocol) describes the communication protocol for the agents.
- [Resources](#resources) describes standardized resources such as calendar 
  events and geolocations.
- [Documentation](#documentation) links to resources related to the communication
  protocol and defined resources.


## Protocol {#protocol}

The [JSON-RPC](http://en.wikipedia.org/wiki/JSON_RPC) is implemented in two ways:

- **Synchronous**  
  Agent X performs an HTTP POST request to agent Y,
  which returns the results in the response.
  This can cause timeout issues, especially when the
  request causes new, nested requests to other agents.
    
- **Asynchronous**  
  Agent X performs an HTTP POST request to agent Y.
  Agent Y does not respond, but schedules the request
  in its task queue. When after the task is executed,
  agent Y performs an HTTP request to agent X
  containing the response.

Note that only JSON-RPC 2.0 is supported.
In JSON-RPC 2.0, method parameters are defined as an object with *named* parameters,
unlike JSON-RPC 1.0 where method parameters are defined as an array with *unnamed*
parameters, which is much more ambiguous.

<!--
[Click here](json_rpc.html) to try JSON-RPC yourself in your browser.
-->

### Synchronous communication

A regular request from Agent X to agent Y can look like this. 
Agent X addresses method "add" from agent Y, and provides two values 
as parameters. 
Agent Y executes the method with the provided parameters, and returns the result.


<table class="example" summary="Synchronous request">
<tr>
<th class="example">Url</th><td class="example"><pre class="example">http://myserver.com/agent/Y</pre></td>
</tr>
<tr>
<th class="example">Request</th><td class="example"><pre class="example">{
  "id": 1,
  "method": "add",
  "params": {
    "a": 2.2, 
    "b": 4.5
  }
}</pre></td>
</tr>
<tr>
<th class="example">Response</th><td class="example"><pre class="example">{
  "id": 1,
  "result": 6.7,
  "error": null
}</pre></td>
</tr>
</table>



### Asynchronous communication

Agent X performs a request to agent Y. 
It calls method "add" and provides two values as parameters.
Agent X also provides a callback url and method.
When agent Y receives teh request, it does not execute the requested method 
imediately, but puts the request in its task queue and returns nothing.

<table class="example" summary="Asynchronous request 1/2">
<tr>
<th class="example">Url</th><td class="example"><pre class="example">http://myserver.com/agent/Y</pre></td>
</tr>
<tr>
<th class="example">Request</th><td class="example"><pre class="example">{ 
  "id": 1,
  "method": "add",
  "params": {
    "a": 2.2, 
    "b": 4.5
  },
  "callback": {
    "url": "http://myserver.com/agentX",
    "method": "addCallback"
  }
}</pre></td>
</tr>
<tr>
<th class="example">Response</th><td class="example"><pre class="example">{
  "id": 1,
  "result": null,
  "error": null
}</pre></td>
</tr>
</table>


Because agent X is not waiting for the response with the result, 
there is no problem when execution of the method takes a long time. 
As soon as agent Y has executed the task from the queue, it returns the result
via a new request, adressing the callback url and method of agent X:

<table class="example" summary="Asynchronous request 2/2">
<tr>
<th class="example">Url</th><td class="example"><pre class="example">http://myserver.com/agent/X</pre></td>
</tr>
<tr>
<th class="example">Request</th><td class="example"><pre class="example">{
  "id": 1,
  "method": "addCallback",
  "params": {
    "result: 6.7,
    "error": null
  }
}</pre></td>
</tr>
<tr>
<th class="example">Response</th><td class="example"><pre class="example">{
  "id": 1,
  "result": null,
  "error": null
}</pre></td>
</tr>
</table>


## Resources {#resources}

The JSON-RPC protocol does define a mechanism for invoking an agents methods.
It does not define the structure of any resources. 
To get interoperability between the agents,
it is important to use a single data-structure for resources
such as calendar events, geolocations, and others. 

The standardized resources for Eve agents are described in this section. 


### Calendar Event

For handling calendar events, an event is defined accordingly to the Google 
Calendar API. A Google Calendar Event is defined at

[https://developers.google.com/google-apps/calendar/v3/reference/events#resource](https://developers.google.com/google-apps/calendar/v3/reference/events#resource)

An example event looks like:

    {
      "kind": "calendar#event",
      "etag": "\"DtwDXDyBz5ZZP0Bus85WBYkv-64/Q1BqTjhPUHlKaEVBQUFBQUFBQUFBQT09\"",
      "id": "1u924fot5dda30tn6h23hbt6tg",
      "status": "confirmed",
      "htmlLink": "https://www.google.com/calendar/event?eid=MXU5MjRmb3Q1ZGRhMzB0bjZoMjNoYnQ2dGcgam9zQGFsbWVuZGUub3Jn",
      "created": "2012-05-08T12:42:18.000Z",
      "updated": "2012-05-08T12:46:03.000Z",
      "summary": "Presentation on Eve",
      "description": "Explain the basics of Eve",
      "location": "Rotterdam, Almende B.V.",
      "creator": {
        "email": "jos@almende.org",
        "displayName": "Jos de Jong"
      },
      "organizer": {
        "email": "jos@almende.org",
        "displayName": "Jos de Jong"
      },
      "start": {
        "dateTime": "2012-05-11T15:00:00+02:00"
      },
      "end": {
        "dateTime": "2012-05-11T17:00:00+02:00"
      },
      "iCalUID": "1u924fot5dda30tn6h23hbt6tg@google.com",
      "sequence": 0,
      "attendees": [
        {
          "email": "ludo@almende.org",
          "responseStatus": "needsAction"
        },
        {
          "email": "andries@almende.org",
          "responseStatus": "needsAction"
        },
        {
          "email": "jos@almende.org",
          "displayName": "Jos de Jong",
          "organizer": true,
          "self": true,
          "responseStatus": "accepted"
        }
      ],
      "guestsCanInviteOthers": true,
      "guestsCanSeeOtherGuests": true,
      "reminders": {
        "useDefault": true
      }
    }


### Geolocation

A geolocation is described by a latitude and longitude.

Example location (Rotterdam, the Netherlands):

    {
      "lat": 51.92298,
      "lng": 4.48287
    }


<!-- TODO: describe authentication

## Authentication

(to be documented)


To send an authentication token with a request, an HTTP Header "Authorization" 
must be provided with the HTTP POST request. The value of this header must 
start with "AgentLogin", followed by a space and a valid
authentication token.
The HTTP header typically looks like this:

    Content-Type: application/json
    **Authorization: AgentLogin 93cd3a24-f429-4bf9-a7d5-ad3a2a3ad227**


Agents automatically send the correct authentication token when making a
request to another agent, when they have a valid authentication token for this 
agent.
-->

## Documentation

Documentation on the JSON-RPC protocol can be found via the following links:

- [http://www.json.org](http://www.json.org)
- [http://json-rpc.org](http://json-rpc.org)
- [http://jsonrpc.org](http://jsonrpc.org)
- [http://en.wikipedia.org/wiki/Json](http://en.wikipedia.org/wiki/Json)
- [http://en.wikipedia.org/wiki/JSON_RPC](http://en.wikipedia.org/wiki/JSON_RPC)

Documentation on the Google Calendar Events can be found at:

- [https://developers.google.com/google-apps/calendar/v3/reference/events#resource](https://developers.google.com/google-apps/calendar/v3/reference/events#resource)

