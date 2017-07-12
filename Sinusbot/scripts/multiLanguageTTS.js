registerPlugin({
    name: 'MultilanguageTTS',
    version: '1.0',
    description: 'This plugin enables switching back and forth between multiple languages',
    author: 'Kilian B https://github.com/KilianB',
}, function(sinusbot, config) {
    sinusbot.on('chat', function(ev) {
		sinusbot.log("Message Recieved");
		//String.prototype.indexOf()
		var message = ev.msg.toString();
		
		sinusbot.log(message);
		if(message.indexOf(".") == 0){
			
			var indexMessageBegin = message.indexOf(" ");
			var locale = message.substring(1,indexMessageBegin);
			
			if(locale == "e1"){
				locale = "en-us";
			}
			
			var rawmessage = message.substring(indexMessageBegin,message.length);
			say(rawmessage,locale);
		}else{
			
			if(ev.mode == 1){
				say(message);
			}
		}		
    });
});
