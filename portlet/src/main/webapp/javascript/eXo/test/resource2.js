
function Humain(){
	this.name = "Noname00";
}

Humain.prototype.hello = function(){
	alert("Welcome " + this.name + " to CROSS world");
}

Humain.prototype.goodbye = function(){
	alert("Salut " + this.name + ". See you again!");
}

Humain = new Humain();