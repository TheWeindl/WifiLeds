$(document).ready(function(){
  var ws = new WebSocket("ws://192.168.1.4:5045");

  ws.onerror = function(event) {
    console.log(event, "error");
  }

  // Construct a msg object containing the data the server needs to process the message from the chat client.
  var msg = {
    id: 1,
    colors: [255,1,150],
    status: 1,
    change: 1
  };

  ws.onopen = function(event) {
    console.log(event);
    // Send the msg object as a JSON-formatted string.
    ws.send(JSON.stringify(msg));
  }
});
