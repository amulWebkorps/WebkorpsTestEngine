
function startContest() {
	var contestId = $("#startContest").val();
	$.ajax({
		url: "/startContestPage",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		method: "POST",
		data: JSON.stringify(contestId),

		success: function(response) {
			console.log("response......"+response);
			debugger
			$("#problem1").text(response.question);
			if(response === "no_errors") location.href = "startContest.html"
		},

		error: function(error) {
			console.log(error);
		},

	});
}
