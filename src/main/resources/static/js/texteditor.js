let editorc;
let flag;
window.onload = function() {
	editorc = ace.edit("editor");
	editorc.setTheme("ace/theme/monokai");
	editorc.session.setMode("ace/mode/c_cpp");
	codeEditor();
}
function codeEditor() {
	jQuery.get('js/sampleCProgram.txt', function(txt) {
		var abc = txt;
		editorc.setValue(abc)
	});
}

function setFlagForTest() {
	flag = $("#test").val();
	executeCode()
}

function setFlagForSubmit() {
	flag = $("#submit").val();
	executeCode()
}
function changeLanguage() {
debugger
	let language = $("#languages").val();
	$("#showResult").val("");
	console.log(language);
	if (language == "python") {
		console.log("python");
		jQuery.get('js/samplePythonProgram.txt', function(txt) {
			editorc.setValue(txt);

		});
	}
	if (language == "java") {
		console.log("java");
		jQuery.get('js/sampleJavaProgram.txt', function(txt) {
			editorc.setValue(txt)

		});
	}
	if (language == "cpp") {
		console.log("CPP");
		jQuery.get('js/sampleCPPProgram.txt', function(txt) {
			editorc.setValue(txt)
		});
	}
	if ((language) == "c") {
		console.log("C");
		jQuery.get('js/sampleCProgram.txt', function(txt) {
			editorc.setValue(txt)
		});
	}

	if (language == 'c' || language == 'cpp') editorc.session.setMode("ace/mode/c_cpp");
	else if (language == 'python') editorc.session.setMode("ace/mode/python");
	else if ((language) == 'java') editorc.session.setMode("ace/mode/java");
}

function executeCode() {
	let language = $("#languages").val();
	if (language == 'c') {
		executeCodeOfc()
	}
	if (language == 'cpp') {
		executeCodeOfcpp()
	}
	if (language == 'java') {
		executeCodeOfjava();
	}
	if (language == 'python') {
		executeCodeofpython()
	}
}

function executeCodeOfc() {
	console.log($("#languages").val());
	console.log(editorc.getSession().getValue());
	var questionId = $("#questionId").val();
	var d = { 'language': $("#languages").val(), 'code': editorc.getSession().getValue(), 'questionId': questionId, 'flag': flag  };
	$.ajax({
		url: "/ccompiler",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		method: "POST",
		data: JSON.stringify(d),
		success: function(response) {
			//console.log(response.status);
			console.log(response.totalsent);
			$(".output").text(response.totalSent)
		},
		error: function(error) {
			console.log(error);
		},
	});
}

function executeCodeOfcpp() {
	console.log($("#languages").val());
	console.log(editorc.getSession().getValue());
	var questionId = $("#questionId").val();
	var d = { 'language': $("#languages").val(), 'code': editorc.getSession().getValue(), 'questionId': questionId, 'flag': flag  };
	$.ajax({
		url: "/cppcompiler",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		method: "POST",
		data: JSON.stringify(d),
		success: function(response) {
			//console.log(response.status);
			console.log(response.totalSent);
			$(".output").text(response.totalSent)
		},
		error: function(error) {
			console.log(error);
		},
	});
}

function executeCodeOfjava() {
	console.log($("#languages").val());
	console.log(editorc.getSession().getValue());
	var questionId = $("#questionId").val();
	var d = { 'language': $("#languages").val(), 'code': editorc.getSession().getValue(), 'questionId': questionId, 'flag': flag };
	$.ajax({
		url: "/javacompiler",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		method: "POST",
		data: JSON.stringify(d),
		success: function(response) {
			//console.log(response.status);
			console.log(response.totalSent);
			$(".output").text(response.totalSent)
		},
		error: function(error) {
			console.log(error);
		},
	});
}

function executeCodeofpython() {
	console.log($("#languages").val());
	var questionId = $("#questionId").val();
	console.log(editorc.getSession().getValue());
	var d = { 'language': $("#languages").val(), 'code': editorc.getSession().getValue(), 'questionId': questionId, 'flag': flag  };
	$.ajax({
		url: "/pythoncompiler",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		method: "POST",
		data: JSON.stringify(d),
		success: function(response) {
			//console.log(response.status);
			console.log(response.totalSent);
			$(".output").text(response.totalSent)
		},
		error: function(error) {
			console.log(error);
		},
	});
}














