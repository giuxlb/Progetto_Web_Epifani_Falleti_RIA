(function() { // avoid variables ending up in the global scope
//rimane una options
	
  // page components
  var bankAccountDetails, bankAccountsList, createTransferForm, confirmTransfer
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
  
  function confirmTransfer(options) {
	  this.destBankAccount = options["destBankAccount"];
	  this.destUser = options["destUser"];
	  this.bankAccount = options["bankAccount"];
	  this.user = options["user"];
	  this.balance = options["balance"];
	  this.tdConfirm = options["tdConfirm"];
	  this.okButton = options["ok"];
	  
	  this.registerEvents = function(orchestrator) {
	    	// Manage submit button
	       this.okButton.addEventListener('click', (e) => {
	            var self = this;
	            makeCall("POST", 'CreateContact?contattoID=' + self.destUser.textContent, e.target.closest("form"),
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
	
		  this.destBankAccount.textContent = message.split(" ")[0];
		  this.destUser.textContent = message.split(" ")[1];
		  this.bankAccount.textContent = message.split(" ")[2];
		  this.user.textContent = message.split(" ")[3];
		  this.balance.textContent = message.split(" ")[4];
		  var putItHidden = false;
		  for(var i = 0;i<createTransferForm.listUser.length;i++)
			 {
			 
			  if (this.destUser.textContent === createTransferForm.listUser[i].toString())
				  {
					putItHidden = true;
				  	break;
				  	
				  }
				  
			 }
		  if (putItHidden)
			  {
			    this.okButton.style.visibility = "hidden";
			  	document.getElementById("messageC").style.visibility = "hidden";
			  }
		  else{
			  this.okButton.style.visibility = "visible";
			  	document.getElementById("messageC").style.visibility = "visible";
		  }
		  this.tdConfirm.style.visibility = "visible";
		  
	  }
	  
	  this.reset = function()
	  {
		  this.tdConfirm.style.visibility = "hidden";
		  document.getElementById("messageC").style.visibility = "hidden";
		  this.okButton.style.visibility = "hidden";
		  makeCall("GET", 'GetContacts',document.getElementById("id_createtransferform"),
          		function(req) {
          		if (req.readyState == XMLHttpRequest.DONE) {
          		var message = req.responseText; 
          		if (req.status == 200) {
          			createTransferForm.listUser = JSON.parse(req.responseText);
          	   } else {
          		   
          	    }
              }
              },false);
		  

	  }
	  
	  
  }

  function bankAccountsList(_alert, _listcontainer, _listcontainerbody) {
    this.alert = _alert;
    this.listcontainer = _listcontainer;
    this.listcontainerbody = _listcontainerbody;

    this.reset = function() {
      this.listcontainer.style.visibility = "hidden";
    }

    this.show = function(next) {
      var self = this; // quando entro nella funzione, il this diventa un'altra cosa
      makeCall("GET", "GetBankAccounts", null,
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


    this.update = function(arrayBankAccounts) {
      var l = arrayBankAccounts.length,
        elem, i, row, idcell, balanceCell, linkcell, anchor;
      if (l == 0) {
        alert.textContent = "No bank accounts yet!";
      } else {
        this.listcontainerbody.innerHTML = ""; // table body with id="id_conticontainerbody"
        // build updated list
        var self = this;
        arrayBankAccounts.forEach(function(bankAccount) { // self visible here, not this
          row = document.createElement("tr");
          idcell = document.createElement("td");
          idcell.textContent = bankAccount.id;
          row.appendChild(idcell);
          balanceCell = document.createElement("td");
          balanceCell.textContent = bankAccount.balance;
          row.appendChild(balanceCell);
          linkcell = document.createElement("td");
          anchor = document.createElement("a");
          linkcell.appendChild(anchor);
          linkText = document.createTextNode("Show");
          anchor.appendChild(linkText);
         // make list item clickable
          anchor.setAttribute('bankAccountID', bankAccount.id); // set a custom HTML attribute
          anchor.addEventListener("click", (e) => {
            // dependency via module parameter
        	  bankAccountDetails.show(e.target.getAttribute("bankAccountID")); // the list must know the details container
          }, false);
          anchor.href = "#";
          row.appendChild(linkcell);
          self.listcontainerbody.appendChild(row);
        });
        this.listcontainer.style.visibility = "visible";
      }
    }

    this.autoclick = function(bankAccountID) {
      var e = new Event("click");
      var selector = "a[bankAccountID='" + bankAccountID + "']";
      var allanchors = this.listcontainerbody.querySelectorAll("a");
      var myAnchor = document.querySelector(selector);
      var anchorToClick =
        (bankAccountID) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];// se contoid non è nullo, fa vedere quell'oggetto altrimenti fa vedere il primo
      anchorToClick.dispatchEvent(e);
    }

  }

  function bankAccountDetails(options) {
	  
	  this.alert = options["alert"];
	  this.listcontainer = options["listcontainer"];
	  this.listcontainerbody = options["listcontainerbody"];
	  this.bankAccountID = 0;

    this.show = function(bankAccountID) {
      var self = this;
      this.bankAccountID = bankAccountID;
      makeCall("GET", "GetBankAccountDetail?contoid=" + bankAccountID, null,
        function(req) {
          if (req.readyState == 4) {
            var message = req.responseText;
            if (req.status == 200) {
              var transfers = JSON.parse(req.responseText);   
              self.update(transfers); // self is the object on which the function           
             
            } else {
              self.alert.textContent = message;
              self.listcontainerbody.innerHTML = "";
              

            }
          }
        }
      );
    };


    this.reset = function() {
        this.listcontainer.style.visibility = "hidden";
      }


    this.update = function(transfers) {
    	var l = transfers.length,
        elem, i, row, idcell, amountcell, destIDcell,purposecell,datecell,statuscell;
      if (l == 0) {
        alert.textContent = "No transfers yet!";
        
      } else {
    	  this.alert.textContent = "";
        this.listcontainerbody.innerHTML = ""; // table body with id="id_trasferimenticontainerbody"
        // build updated list
        var self = this;
        transfers.forEach(function(transfer) { // self visible here, not this
          row = document.createElement("tr");
          
          idcell = document.createElement("td");
          idcell.textContent = transfer.transferID;
          row.appendChild(idcell);
          
          amountcell = document.createElement("td");
          amountcell.textContent = transfer.amount;
          row.appendChild(amountcell);
          
          destIDcell = document.createElement("td");
          destIDcell.textContent = transfer.destBankAccountId;
          row.appendChild(destIDcell);
          
          purposecell = document.createElement("td");
          purposecell.textContent = transfer.causal;
          row.appendChild(purposecell);
          
          datecell = document.createElement("td");
          datecell.textContent = transfer.data;
          row.appendChild(datecell);
          
          statuscell = document.createElement("td");
          if (self.bankAccountID == transfer.destBankAccountId)
        	  statuscell.textContent = "IN";
          else{
        	  statuscell.textContent = "OUT";
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
    this.balance = 0;
    this.listUser = [0];
    this.listBankAccounts = [0];
    var self = this;
    makeCall("GET", 'GetContacts', self.transferForm,
		function(req) {
		if (req.readyState == XMLHttpRequest.DONE) {
		var message = req.responseText; 
		if (req.status == 200) {
			self.listUser = JSON.parse(req.responseText);
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
          
          if (eventfieldset.elements["amount"].valueAsNumber <= 0)
        	  {
        	  
        	  document.getElementById("id_alertTransfer").textContent = "Negative Amount isn't valid";
        	  valid = false;
        	  }
          if (valid) {
            var self = this;
            makeCall("POST", 'CreateTransfer', e.target.closest("form"),
              function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                  var message = req.responseText; 
                  if (req.status == 200) {
                    orchestrator.refreshConfirmTransfer(message); 
                    self.alert.textContent = "";
                    bankAccountsList.show();
                    document.getElementById("id_alertTransfer").textContent = "";
                    bankAccountDetails.show(bankAccountDetails.bankAccountID);
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
        
        
        
        inputUser.addEventListener("keydown",(e) => {
        	//qua finisce la makeCall
        var datalistUser = self.transferForm.querySelector("#userList");
        datalistUser.innerHTML = "";
        var written = "";
        if (e.keyCode != 8)
        {
        	written = inputUser.value + e.key;
        }
        else{
        	written = inputUser.value.slice(0,-1);
        }
       ;
        
        //ora devo prendere tutti gli userId che mi escono e che iniziano con quel carattere e aggiungerli come nodi options figli di datalistaUser 
       console.log(self.listUser.length);
        for (var i = 0; i<self.listUser.length; i++){
        	var c = self.listUser[i].toString();
        	if (c.indexOf(written) == 0)   //ciao //i ritorna 1
        	{
        		var node = document.createElement("option"); 
                var val = document.createTextNode(c); 
                node.appendChild(val);
                datalistUser.appendChild(node);
        	}
        		
        }
       
          
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

      bankAccountsList = new bankAccountsList(
        alertContainer,
        document.getElementById("id_conticontainer"),
        document.getElementById("id_conticontainerbody"));
      
      confirmTransfer = new confirmTransfer({
    	  tdConfirm : document.getElementById("id_confermaTrasferimento"),
    	  destBankAccount : document.getElementById("id_contoAddr"),
    	  destUser :document.getElementById("id_userAddr"),
    	  bankAccount : document.getElementById("id_conto"),
    	  user : document.getElementById("id_user"),
    	  balance : document.getElementById("id_amount"),
    	  ok: document.getElementById("id_ok")
      });

      bankAccountDetails = new bankAccountDetails({ 
       
        alert:alertContainer,
        listcontainer:document.getElementById("id_trasferimenticontainer"),
        listcontainerbody:document.getElementById("id_trasferimenticontainerbody")
      });
      

      createTransferForm = new createTransferForm(document.getElementById("id_createtransferform"), alertContainer);
      createTransferForm.registerEvents(this);
      confirmTransfer.registerEvents(this);
    };
    
    this.refreshConfirmTransfer = function(message)
    {
    	confirmTransfer.reset();
    	confirmTransfer.show(message);
    };

    this.refresh = function(currentBankAccount) {
      bankAccountsList.reset();
      confirmTransfer.reset();
      bankAccountDetails.reset();
      bankAccountsList.show(function() {
    	  bankAccountsList.autoclick(currentBankAccount);
      }); 
      
      createTransferForm.reset(currentBankAccount);
    };
  }
})();
