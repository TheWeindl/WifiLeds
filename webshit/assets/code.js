$(function() {
 $('.switch').click(function() {
  if( $(".switch").hasClass("switchLightOff") ) {
   $(".switch").removeClass("switchLightOff"),
   $(".switch").addClass("switchLightOn"),
   $("#Wrapper").removeClass("dark"),
   $(".hole").removeClass("holeLightOff"),
   $(".hole").addClass("holeLightOn"),
   $(".handle").removeClass("handleLightOff"),
   $(".handle").addClass("handleLightOn"),
   $(".sk").addClass("on"),
   $(".handleTop").removeClass("handleTopLightOff"),
   $(".handleTop").addClass("handleTopLightOn"),
   $(".handleBottom").removeClass("handleBottomLightOff"),
   $(".handleBottom").addClass("handleBottomLightOn");
  }
  else
  {
   $(".switch").addClass("switchLightOff"),
   $("#Wrapper").addClass("dark"),
   $(".hole").removeClass("holeLightOn"),
   $(".hole").addClass("holeLightOff"),
   $(".handle").removeClass("handleLightOn"),
   $(".handle").addClass("handleLightOff"),
   $(".sk").removeClass("on"),
   $(".handleTop").removeClass("handleTopLightOn"),
   $(".handleTop").addClass("handleTopLightOff"),
     $(".handleBottom").removeClass("handleBottomLightOn"),
   $(".handleBottom").addClass("handleBottomLightOff");
  }
  });
 });
