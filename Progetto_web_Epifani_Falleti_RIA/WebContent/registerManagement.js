/**
 * 
 */

(function() { // avoid variables ending up in the global scope

  document.getElementById("registerbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
    	  if (checkPassword(form.elements["pwdR"].value,form.elements["pwdripR"].value))
    	     {
    	    	 if (checkMail(form.elements["usernameR"].value))
    	{
      makeCall("POST", 'RegisterUser', e.target.closest("form"),
        function(req) {
          if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
            switch (req.status) {
              case 200:
            	document.getElementById("user").value = message;
            	document.getElementById("infomessage").textContent = "User already registered";
            	 document.getElementById("errormessageR").textContent = "";
                break;
              case 400: // bad request
                document.getElementById("errormessageR").textContent = message;
                break;
              case 401: // unauthorized
                  document.getElementById("errormessageR").textContent = message;
                  break;
              case 500: // server error
            	document.getElementById("errormessageR").textContent = message;
                break;
            }
          }
        }
      );
    }
    else {
    	document.getElementById("errormessageR").textContent = "Mail not valid";	 
    	  }
    }else{
    	 
   	  document.getElementById("errormessageR").textContent = "Passwords don't match";
    }
    	  
    } else {
    	 form.reportValidity();
    }
  });

})();