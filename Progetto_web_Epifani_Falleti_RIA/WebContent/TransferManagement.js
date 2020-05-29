(function() { // avoid variables ending up in the global scope

  // page components
  var contoDetails, contiList, createTransferForm,
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
            contiDetails.show(e.target.getAttribute("contoid")); // the list must know the details container
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
	  
	  this.alert = _alert;
	  this.listcontainer = _listcontainer;
	  this.listcontainerbody = _listcontainerbody;
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
          destIDcell.textContent = trasferimento.DestContoID;
          row.appendChild(destIDcell);
          
          purposecell = document.createElement("td");
          purposecell.textContent = trasferimento.causale;
          row.appendChild(purposecell);
          
          datecell = document.createElement("td");
          datecell.textContent = trasferimento.data;
          row.appendChild(datecell);
          
          statuscell = document.createElement("td");
          if (self.contoID == trasferimento.DestContoID)
        	  statuscell.textContent = "INGRESSO";
          else{
        	  statuscell.textContent = "USCITA";
          }
          
          row.appendChild(idcell);
          self.listcontainerbody.appendChild(row);
        });
        this.listcontainer.style.visibility = "visible";
      }
    }
  }

  function createTransferForm(formID, alert) {
    
    this.transferForm = formID;
    this.alert = alert;


    
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

          if (valid) {
            var self = this;
            makeCall("POST", 'CreateTransfer', e.target.closest("form"),
              function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                  var message = req.responseText; 
                  if (req.status == 200) {
                    orchestrator.refresh(message); 
                  } else {
                    self.alert.textContent = message;
                    self.reset();
                  }
                }
              }
            );
          }
        });
      };

    this.reset = function() {
      var fieldsets = document.querySelectorAll("#" + this.wizard.id + " fieldset");
      fieldsets[0].hidden = false;
     }

 }

  function PageOrchestrator() {
    var alertContainer = document.getElementById("id_alert");
    this.start = function() {
      personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
        document.getElementById("id_username"));
      personalMessage.show();

      missionsList = new MissionsList(
        alertContainer,
        document.getElementById("id_listcontainer"),
        document.getElementById("id_listcontainerbody"));

      missionDetails = new MissionDetails({ // many parameters, wrap them in an
        // object
        alert: alertContainer,
        detailcontainer: document.getElementById("id_detailcontainer"),
        expensecontainer: document.getElementById("id_expensecontainer"),
        expenseform: document.getElementById("id_expenseform"),
        closeform: document.getElementById("id_closeform"),
        date: document.getElementById("id_date"),
        destination: document.getElementById("id_destination"),
        status: document.getElementById("id_status"),
        description: document.getElementById("id_description"),
        country: document.getElementById("id_country"),
        province: document.getElementById("id_province"),
        city: document.getElementById("id_city"),
        fund: document.getElementById("id_fund"),
        food: document.getElementById("id_food"),
        accomodation: document.getElementById("id_accomodation"),
        transportation: document.getElementById("id_transportation")
      });
      missionDetails.registerEvents(this);

      wizard = new Wizard(document.getElementById("id_createmissionform"), alertContainer);
      wizard.registerEvents(this);
    };


    this.refresh = function(currentConto) {
      missionsList.reset();
      missionDetails.reset();
      missionsList.show(function() {
        missionsList.autoclick(currentConto);
      }); // closure preserves visibility of this
      wizard.reset();
    };
  }
})();
