$(document).ready(function(e) {

  let leds = getLeds();

  $.each(leds, function(key,value) {
    let status = value.status;
    console.log(status, key+1);
    if(status == 1) {
      $(".switch#"+(key+1)).removeClass("switchLightOff"),
      $(".switch#"+(key+1)).addClass("switchLightOn"),
      $(".switch#"+(key+1)+" > .hole").removeClass("holeLightOff"),
      $(".switch#"+(key+1)+" > .hole").addClass("holeLightOn"),
      $(".switch#"+(key+1)+" > .hole > .handle").removeClass("handleLightOff"),
      $(".switch#"+(key+1)+" > .hole > .handle").addClass("handleLightOn"),
      $(".switch#"+(key+1)+" > .hole > .handle > .sk").addClass("on"),
      $(".switch#"+(key+1)+" > .hole > .handle > .handleTop").removeClass("handleTopLightOff"),
      $(".switch#"+(key+1)+" > .hole > .handle > .handleTop").addClass("handleTopLightOn"),
      $(".switch#"+(key+1)+" > .hole > .handle > .handleBottom").removeClass("handleBottomLightOff"),
      $(".switch#"+(key+1)+" > .hole > .handle > .handleBottom").addClass("handleBottomLightOn");
    }
  });
});

function getLeds() {
  // TODO

  //temp dummy
  let leds = [];
  leds.push({"id" : 1, "status" : 1,"color" : {"red" : 255, "green" : 64, "blue" : 48}});
  leds.push({"id" : 2, "status" : 0,"color" : {"red" : 255, "green" : 255, "blue" : 48}});
  leds.push({"id" : 3, "status" : 1,"color" : {"red" : 134, "green" : 64, "blue" : 255}});
  leds.push({"id" : 4, "status" : 1,"color" : {"red" : 126, "green" : 64, "blue" : 48}});

  return leds;
}

$(function() {
 $('.switch').click(function() {
  if( $(".switch#"+(this.id)).hasClass("switchLightOff") ) {
    $(".switch#"+(this.id)).removeClass("switchLightOff"),
    $(".switch#"+(this.id)).addClass("switchLightOn"),
    $(".switch#"+(this.id)+" > .hole").removeClass("holeLightOff"),
    $(".switch#"+(this.id)+" > .hole").addClass("holeLightOn"),
    $(".switch#"+(this.id)+" > .hole > .handle").removeClass("handleLightOff"),
    $(".switch#"+(this.id)+" > .hole > .handle").addClass("handleLightOn"),
    $(".switch#"+(this.id)+" > .hole > .handle > .sk").addClass("on"),
    $(".switch#"+(this.id)+" > .hole > .handle > .handleTop").removeClass("handleTopLightOff"),
    $(".switch#"+(this.id)+" > .hole > .handle > .handleTop").addClass("handleTopLightOn"),
    $(".switch#"+(this.id)+" > .hole > .handle > .handleBottom").removeClass("handleBottomLightOff"),
    $(".switch#"+(this.id)+" > .hole > .handle > .handleBottom").addClass("handleBottomLightOn");
  }
  else
  {
   $(".switch#"+(this.id)).addClass("switchLightOff"),
   $(".switch#"+(this.id)+" > .hole").removeClass("holeLightOn"),
   $(".switch#"+(this.id)+" > .hole").addClass("holeLightOff"),
   $(".switch#"+(this.id)+" > .hole > .handle").removeClass("handleLightOn"),
   $(".switch#"+(this.id)+" > .hole > .handle").addClass("handleLightOff"),
   $(".switch#"+(this.id)+" > .hole > .handle > .sk").removeClass("on"),
   $(".switch#"+(this.id)+" > .hole > .handle > .handleTop").removeClass("handleTopLightOn"),
   $(".switch#"+(this.id)+" > .hole > .handle > .handleTop").addClass("handleTopLightOff"),
   $(".switch#"+(this.id)+" > .hole > .handle > .handleBottom").removeClass("handleBottomLightOn"),
   $(".switch#"+(this.id)+" > .hole > .handle > .handleBottom").addClass("handleBottomLightOff");
  }
  });
 });
