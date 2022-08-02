let teditorc;

window.onload = function() {
	teditorc = ace.edit("editor");
	teditorc.setTheme("ace/theme/monokai");
	teditorc.session.setMode("ace/mode/c_cpp");
	codeEditor();
}
function codeEditor() {
	jQuery.get('js/TextTemp.txt', function(txt) {
		var abc = txt;
		teditorc.setValue(abc)
	});
}
