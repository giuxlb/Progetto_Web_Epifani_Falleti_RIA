/**
 * 
 */

(function() { // avoid variables ending up in the global scope

  document.getElementById("registerbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      makeCall("POST", 'RegisterUser', e.target.closest("form"),
        function(req) {
          if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
            switch (req.status) {
              case 200:
            	document.getElementById("user").value = message;
            	document.getElementById("infomessage").textContent = "Utente registrato";
              
                break;
              case 400: // bad request
                document.getElementById("errormessageR").textContent = message;
                break;
              case 500: // server error
            	document.getElementById("errormessageR").textContent = message;
                break;
            }
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

})();