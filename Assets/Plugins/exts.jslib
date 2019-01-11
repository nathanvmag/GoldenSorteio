mergeInto(LibraryManager.library, {  

  falar: function (tx) {
      if ('speechSynthesis' in window) {
        var msg = new SpeechSynthesisUtterance();        
        msg.voiceURI = "native";        
        msg.text = Pointer_stringify(tx);
        msg.lang = 'pt-BR';
        speechSynthesis.speak(msg);
    }
    
  },

  

});