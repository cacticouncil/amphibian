document.getElementById('updateSelection').addEventListener('click', function(event){
	var select = document.getElementById("selectBox");
	console.log("UPDATE " + select.options[select.selectedIndex].value);
	});
	
getPalettePosition = function(selectedPalette){
	if(typeof selectedPalette === "string"){
		var select = document.getElementById("selectBox");
		for(i = 0; i < select.options.length; i++){
			var paletteData = selectedPalette.split('|');
			if(select[i].value === selectedPalette || select[i].text === selectedPalette || (paletteData[0] === select[i].text && paletteData[1] === select[i].value)){
				return i;
			}
		}
	}
	return false;
}
	
getPalettes = function(){
	var select = document.getElementById("selectBox");
	var outputString = "";
	for(i = 0; i < select.options.length; i++){
		outputString += select.options[i].text;
		outputString += '|';
		outputString += select.options[i].value;
		outputString += '\n';
	}
	return outputString;
}

setPalettes = function(newPalettes){
	var select = document.getElementById("selectBox");
	var optionCount = select.options.length;
	for(i = 0; i < optionCount; i++){
		select.removeChild(select.options[0]);
	}
	var palettes = newPalettes.split('\n');
	for(i = 0; i < palettes.length; i++){
		var paletteData = palettes[i].split('|');
		var opt = document.createElement('option');
		opt.appendChild(document.createTextNode(paletteData[0]));
		opt.value = paletteData[1];
		select.appendChild(opt);
	}
}

addPalette = function(newPalette){
	if(typeof newPalette === "string"){
		var select = document.getElementById("selectBox");
		var paletteData = newPalette.split('|');
		var opt = document.createElement('option');
		opt.appendChild(document.createTextNode(paletteData[0]));
		opt.value = paletteData[1];
		select.appendChild(opt);
		return true;
	}
	return false;
}

removePalette = function(PaletteToRemove){
	if(typeof PaletteToRemove === "string"){
		PaletteToRemove = getPalettePosition(PaletteToRemove);
	}
	if(typeof PaletteToRemove === "number"){
		var select = document.getElementById("selectBox");
		select.removeChild(select.options[PaletteToRemove]);
		return true;
	}
	return false;
}

setSelectedPalette = function(selectedPalette){
	var select = document.getElementById("selectBox");
	if(typeof selectedPalette === "string"){
		selectedPalette = getPalettePosition(selectedPalette);
	}
	if(typeof selectedPalette === "number"){
		select.selectedIndex = selectedPalette;
		return true;
	}
	return false;
}