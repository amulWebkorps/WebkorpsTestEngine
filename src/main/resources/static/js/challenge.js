function submitCode(questionId) {
	debugger
	var language = $("#languages").val();
	var contestId = $("#qContestId").val();
	var studentId = $("#studentId").val();
	var flag = $(".submit").val();
	var code = editorc.getSession().getValue();
	console.log(language);
	console.log(code);
	var d = { 'language': language, 'code': editorc.getSession().getValue(), 'questionId': questionId, 'contestId': contestId, 'studentId': studentId, 'flag': flag };

	//if (language == 'java') {
	$.ajax({
		url: "/javacompiler",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		method: "POST",
		data: JSON.stringify(d),

		success: function(response) {
			var showTestCases = "";
			if (!response.testCasesSuccess.length) {
				showTestCases += "<div> <h4 class='px-4'>" + response.complilationMessage + "</h4></div>"
				$("#showResult").html(showTestCases);
			}
			else {
				$("#showResult").html("");
				for (var i = 0; i < response.testCasesSuccess.length; i++) {
					if (response.testCasesSuccess[i] === "Pass") {
						showTestCases = "<div> <h4 class='px-4'> Test Case " + (i + 1) + "  <div class='success'></div></h4></div>"
						$("#showResult").append(showTestCases);
					} else {
						showTestCases = "<div> <h4 class='px-4'> Test Case " + (i + 1) + "  <div class='fail'></div></h4></div>"
						$("#showResult").append(showTestCases);
					}

				}
				if (response.successMessage !== "") {
					setTimeout(
						function() {
							alert("Question submitted");
						}
						, 100 /// milliseconds = 10 seconds
					);
				}
			}
		},
		error: function(error) {
			console.log(error);
		},

	});
}
/*}
if (language == 'python') {
	$.ajax({
		url: "/pythoncompiler",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		method: "POST",
		data: JSON.stringify(d),

		success: function(response) {
			var showTestCases = "";
			if (!response.testCasesSuccess.length) {
				showTestCases += "<div> <h4 class='px-4'>" + response.complilationMessage + "</h4></div>"
				$("#showResult").html(showTestCases);
			}
			else {
				$("#showResult").html("");
				for (var i = 0; i < response.testCasesSuccess.length; i++) {
					if (response.testCasesSuccess[i] === "Pass") {
						showTestCases = "<div> <h4 class='px-4'> Test Case " + (i + 1) + "  <div class='success'></div></h4></div>"
						$("#showResult").append(showTestCases);
					} else {
						showTestCases = "<div> <h4 class='px-4'> Test Case " + (i + 1) + "  <div class='fail'></div></h4></div>"
						$("#showResult").append(showTestCases);
					}

				}
			}
		},

		error: function(error) {
			console.log(error);
		},

	});
}
if (language == 'cpp') {
	$.ajax({
		url: "/cppcompiler",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		method: "POST",
		data: JSON.stringify(d),

		success: function(response) {
			//console.log(response.status);
			console.log(response.output);
			$(".output1").text(response.output)
		},

		error: function(error) {
			console.log(error);
		},

	});
}
if (language == 'c') {
	$.ajax({
		url: "/ccompiler",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		method: "POST",
		data: JSON.stringify(d),

		success: function(response) {
			//console.log(response.status);
			console.log(response.output);
			$(".output1").text(response.output)
		},

		error: function(error) {
			console.log(error);
		},

	});
}
}*/

function runCode(questionId) {
	debugger
	var language = $("#languages").val();
	var contestId = $("#qContestId").val();
	var studentId = $("#studentId").val();
	var flag = $(".run").val();
	var code = editorc.getSession().getValue();
	console.log(language);
	console.log(code);
	var d = { 'language': language, 'code': editorc.getSession().getValue(), 'questionId': questionId, 'contestId': contestId, 'studentId': studentId, 'flag': flag };

	//if (language == 'java') {
	$.ajax({
		url: "/javacompiler",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		method: "POST",
		data: JSON.stringify(d),

		success: function(response) {
			var showTestCases = "";
			if (!response.testCasesSuccess.length) {
				showTestCases += "<div> <h4 class='px-4'>" + response.complilationMessage + "</h4></div>"
				$("#showResult").html(showTestCases);
			}
			else {
				$("#showResult").html("");
				for (var i = 0; i < response.testCasesSuccess.length; i++) {
					if (response.testCasesSuccess[i] === "Pass") {
						showTestCases = "<div> <h4 class='px-4'> Test Case " + (i + 1) + "  <div class='success'></div></h4></div>"
						$("#showResult").append(showTestCases);
					} else {
						showTestCases = "<div> <h4 class='px-4'> Test Case " + (i + 1) + "  <div class='fail'></div></h4></div>"
						$("#showResult").append(showTestCases);
					}

				}
			}
		},
		error: function(error) {
			console.log(error);
		},

	});
}

function nextQuestion(value) {

}