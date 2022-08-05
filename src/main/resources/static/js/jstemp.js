let teditorc;

window.onload = function() {

	teditorc = ace.edit("editor");
	teditorc.setTheme("ace/theme/monokai");
	teditorc.session.setMode("ace/mode/c_cpp");
	codeEditor();
}
function codeEditor() {		
		teditorc.setValue("${qId}");	
}
