(function() { // avoid variables ending up in the global scope
//controllo lato client della email e delle password
//controllo lato client dei campi della form
  // page components
  var contoDetails, contiList, createTransferForm, confermaTransfer
    pageOrchestrator = new PageOrchestrator(); // main controller

  window.addEventListener("load", () => {
    pageOrchestrator.start(); // initialize the components
    pageOrchestrator.refresh(); // display initial content
  }, false);


  // Constructors of view components
  
  function PersonalMessage(_username, messagecontainer) {//messagecontainer sarà l'elemento con id = id_username in Home.html
    this.username = _username;
    this.show = function() {
      messagecontainer.textContent = this.username;
    }
  }
  
  function confermaTransfer(options) {
	  this.destConto = options["destConto"];
	  this.destUser = options["destUser"];
	  this.conto = options["conto"];
	  this.user = options["user"];
	  this.balance = options["balance"];
	  this.tdConferma = options["tdConferma"];
	  this.okButton = options["ok"];
	  
	  this.registerEvents = function(orchestrator) {
	    	// Manage submit button
	       this.okButton.addEventListener('click', (e) => {
	            var self = this;
	            makeCall("POST", 'CreateContatto?contattoID=' + self.destUser.textContent, e.target.closest("form"),
	              function(req) {
	                if (req.readyState == XMLHttpRequest.DONE) {
	                  var message = req.responseText; 
	                  if (req.status == 200) {
	                    self.reset();
	                  } else {
	                    self.alert.textContent = message;
	                  }
	                }
	              }
	            );
	           
	          
	        });
	      };
	  
	  this.show = function(message)
	  {
	
		  this.destConto.textContent = message.split(" ")[0];
		  this.destUser.textContent = message.split(" ")[1];
		  this.conto.textContent = message.split(" ")[2];
		  this.user.textContent = message.split(" ")[3];
		  this.balance.textContent = message.split(" ")[4];
		  for(var i = 0;i<createTransferForm.listaUser.length;i++)
			 {
			  console.log(this.destUser.textContent);
			  console.log(createTransferForm.listaUser[i]);
			  if (this.destUser.textContent === createTransferForm.listaUser[i].toString())
				  {
				  	this.okButton.style.visibility = "hidden";
				  	
				  }
				  
			 }
		  this.tdConferma.style.visibility = "visible";
		  
	  }
	  
	  this.reset = function()
	  {
		  this.tdConferma.style.visibility = "hidden";
		  makeCall("GET", 'GetContatti?user=1',document.getElementById("id_createtransferform"),
          		function(req) {
          		if (req.readyState == XMLHttpRequest.DONE) {
          		var message = req.responseText; 
          		if (req.status == 200) {
          			createTransferForm.listaUser = JSON.parse(req.responseText);
          	   } else {
          		   
          	    }
              }
              },false);
		  

	  }
	  
	  
  }

  function contiList(_alert, _listcontainer, _listcontainerbody) {
    this.alert = _alert;
    this.listcontainer = _listcontainer;
    this.listcontainerbody = _listcontainerbody;

    this.reset = function() {
      this.listcontainer.style.visibility = "hidden";
    }

    this.show = function(next) {
      var self = this; // quando entro nella funzione, il this diventa un'altra cosa
      makeCall("GET", "GetConti", null,
        function(req) {
          if (req.readyState == 4) {
            var message = req.responseText;
            if (req.status == 200) {
              self.update(JSON.parse(req.responseText)); // self visible by
              // closure
              if (next) next(); // show the first element of the list
            } else {
              self.alert.textContent = message;
            }
          }
        }
      );
    };


    this.update = function(arrayConti) {
      var l = arrayConti.length,
        elem, i, row, idcell, saldocell, linkcell, anchor;
      if (l == 0) {
        alert.textContent = "No bank accounts yet!";
      } else {
        this.listcontainerbody.innerHTML = ""; // table body with id="id_conticontainerbody"
        // build updated list
        var self = this;
        arrayConti.forEach(function(conto) { // self visible here, not this
          row = document.createElement("tr");
          idcell = document.createElement("td");
          idcell.textContent = conto.id;
          row.appendChild(idcell);
          saldocell = document.createElement("td");
          saldocell.textContent = conto.saldo;
          row.appendChild(saldocell);
          linkcell = document.createElement("td");
          anchor = document.createElement("a");
          linkcell.appendChild(anchor);
          linkText = document.createTextNode("Show");
          anchor.appendChild(linkText);
         // make list item clickable
          anchor.setAttribute('contoid', conto.id); // set a custom HTML attribute
          anchor.addEventListener("click", (e) => {
            // dependency via module parameter
            contoDetails.show(e.target.getAttribute("contoid")); // the list must know the details container
          }, false);
          anchor.href = "#";
          row.appendChild(linkcell);
          self.listcontainerbody.appendChild(row);
        });
        this.listcontainer.style.visibility = "visible";
      }
    }

    this.autoclick = function(contoid) {
      var e = new Event("click");
      var selector = "a[contoid='" + contoid + "']";
      var allanchors = this.listcontainerbody.querySelectorAll("a");
      var myAnchor = document.querySelector(selector);
      var anchorToClick =
        (contoid) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];// se contoid non è nullo, fa vedere quell'oggetto altrimenti fa vedere il primo
      anchorToClick.dispatchEvent(e);
    }

  }

  function contoDetails(options) {
	  
	  this.alert = options["alert"];
	  this.listcontainer = options["listcontainer"];
	  this.listcontainerbody = options["listcontainerbody"];
	  this.contoID = 0;

    this.show = function(contoid) {
      var self = this;
      this.contoID = contoid;
      makeCall("GET", "GetContoDetail?contoid=" + contoid, null,
        function(req) {
          if (req.readyState == 4) {
            var message = req.responseText;
            if (req.status == 200) {
              var trasferimenti = JSON.parse(req.responseText);   
              self.update(trasferimenti); // self is the object on which the function           
             
            } else {
              self.alert.textContent = message;
              self.listcontainerbody.innerHTML = "";
              console.log(message);

            }
          }
        }
      );
    };


    this.reset = function() {
        this.listcontainer.style.visibility = "hidden";
      }


    this.update = function(trasferimenti) {
    	var l = trasferimenti.length,
        elem, i, row, idcell, importocell, destIDcell,purposecell,datecell,statuscell;
      if (l == 0) {
        alert.textContent = "No transfers yet!";
        
      } else {
    	  this.alert.textContent = "";
        this.listcontainerbody.innerHTML = ""; // table body with id="id_trasferimenticontainerbody"
        // build updated list
        var self = this;
        trasferimenti.forEach(function(trasferimento) { // self visible here, not this
          row = document.createElement("tr");
          
          idcell = document.createElement("td");
          idcell.textContent = trasferimento.trasferimentoID;
          row.appendChild(idcell);
          
          importocell = document.createElement("td");
          importocell.textContent = trasferimento.importo;
          row.appendChild(importocell);
          
          destIDcell = document.createElement("td");
          destIDcell.textContent = trasferimento.DestContoId;
          row.appendChild(destIDcell);
          
          purposecell = document.createElement("td");
          purposecell.textContent = trasferimento.causale;
          row.appendChild(purposecell);
          
          datecell = document.createElement("td");
          datecell.textContent = trasferimento.data;
          row.appendChild(datecell);
          
          statuscell = document.createElement("td");
          if (self.contoID == trasferimento.DestContoId)
        	  statuscell.textContent = "INGRESSO";
          else{
        	  statuscell.textContent = "USCITA";
          }
          
          row.appendChild(statuscell);
          self.listcontainerbody.appendChild(row);
        });
        this.listcontainer.style.visibility = "visible";
      }
    }
  }

  function createTransferForm(formID, alert) {
    
    this.transferForm = formID;
    this.alert = alert;
    this.saldo = 0;
    this.listaUser = [0];
    this.listaConti = [0];
    var self = this;
    makeCall("GET", 'GetContatti?user=1', self.transferForm,
		function(req) {
		if (req.readyState == XMLHttpRequest.DONE) {
		var message = req.responseText; 
		if (req.status == 200) {
			self.listaUser = JSON.parse(req.responseText);
	   } else {
		   
	    }
    }
    },false);


    
    this.registerEvents = function(orchestrator) {
    	// Manage submit button
        this.transferForm.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
          var eventfieldset = e.target.closest("fieldset"),
            valid = true;
          for (i = 0; i < eventfieldset.elements.length; i++) {
            if (!eventfieldset.elements[i].checkValidity()) {
              eventfieldset.elements[i].reportValidity();
              valid = false;
              break;
            }
          }
          console.log(eventfieldset.elements["amount"].valueAsNumber);
          if (eventfieldset.elements["amount"].valueAsNumber < 0)
        	  {
        	  console.log("importo non valido");
        	  document.getElementById("id_alertTransfer").textContent = "Importo negativo non valido";
        	  valid = false;
        	  }
          if (valid) {
            var self = this;
            makeCall("POST", 'CreateTransfer', e.target.closest("form"),
              function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                  var message = req.responseText; 
                  if (req.status == 200) {
                    orchestrator.refreshConfermaTrasferimento(message); 
                    self.alert.textContent = "";
                    contiList.show();
                    document.getElementById("id_alertTransfer").textContent = "";
                    contoDetails.show(contoDetails.contoID);
                  } else {
                    self.alert.textContent = message;
                    self.reset();
                  }
                }
              }
            );
          }
        });
        
         
        var inputUser =  document.getElementById("destUserID");
        var inputConto =  document.getElementById("destContoID");
        
        
        inputUser.addEventListener("keypress",(e) => {
        	//qua finisce la makeCall
        var datalistaUser = self.transferForm.querySelector("#userList");
        datalistaUser.innerHTML = "";
        var digitato = inputUser.value;
        console.log(digitato);
        
        //ora devo prendere tutti gli userId che mi escono e che iniziano con quel carattere e aggiungerli come nodi options figli di datalistaUser 
        console.log("inizio il for");
        for (var i = 0; i<self.listaUser.length; i++){
        	var c = self.listaUser[i].toString();
        	if (c.indexOf(digitato) > -1)   //ciao //i ritorna 1
        	{
        		var node = document.createElement("option"); 
                var val = document.createTextNode(c); 
                node.appendChild(val);
                datalistaUser.appendChild(node);
        	}
        		
        }
        console.log("finisco il for");
          
      });
        
        
        
        
      };

    this.reset = function() {
      var fieldsets = document.querySelectorAll("#" + this.transferForm.id + " fieldset");
      fieldsets[0].hidden = false;
     }

 }

  function PageOrchestrator() {
    var alertContainer = document.getElementById("id_alert");
    
    this.start = function() {
      personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
        document.getElementById("id_username"));
      personalMessage.show();

      contiList = new contiList(
        alertContainer,
        document.getElementById("id_conticontainer"),
        document.getElementById("id_conticontainerbody"));
      
      confermaTransfer = new confermaTransfer({
    	  tdConferma : document.getElementById("id_confermaTrasferimento"),
    	  destConto : document.getElementById("id_contoAddr"),
    	  destUser :document.getElementById("id_userAddr"),
    	  conto : document.getElementById("id_conto"),
    	  user : document.getElementById("id_user"),
    	  balance : document.getElementById("id_amount"),
    	  ok: document.getElementById("id_ok")
      });

      contoDetails = new contoDetails({ 
       
        alert:alertContainer,
        listcontainer:document.getElementById("id_trasferimenticontainer"),
        listcontainerbody:document.getElementById("id_trasferimenticontainerbody")
      });
      

      createTransferForm = new createTransferForm(document.getElementById("id_createtransferform"), alertContainer);
      createTransferForm.registerEvents(this);
      confermaTransfer.registerEvents(this);
    };
    
    this.refreshConfermaTrasferimento = function(message)
    {
    	confermaTransfer.reset();
    	confermaTransfer.show(message);
    };

    this.refresh = function(currentConto) {
      contiList.reset();
      confermaTransfer.reset();
      contoDetails.reset();
      contiList.show(function() {
        contiList.autoclick(currentConto);
      }); 
      
      createTransferForm.reset(currentConto);
    };
  }
})();
