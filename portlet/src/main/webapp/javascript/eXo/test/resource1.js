
function Robot(){
	this.name = name;
}

Robot.prototype.hello = function(){
	alert("Hello " + _module.Robot.name);
}

Robot.prototype.goodbye = function(){
	alert("Goodbye " + _module.Robot.name);
}

_module.Robot = new Robot();
