$().ready(function(){
  var exampleSocket = new WebSocket("192.168.1.4");

  $("button#test").on("click", function() {
    sendText(1, [255, 64, 48], 1, 1);
  });

});

function sendText(id, colors, status = 1, change = 1) {
  // Construct a msg object containing the data the server needs to process the message from the chat client.
  var msg = {
    id: id,
    colors: colors,
    status: status,
    change: change
  };

  // Send the msg object as a JSON-formatted string.
  exampleSocket.send(JSON.stringify(msg));

  // Blank the text input element, ready to receive the next line of text from the user.
  document.getElementById("text").value = "";
}
