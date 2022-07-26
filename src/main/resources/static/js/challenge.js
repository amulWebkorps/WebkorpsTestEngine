
function startContest() {
	var contestId = $("#startContest").val();
	$.ajax({
		url: "/startContestPage",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		method: "POST",
		data: JSON.stringify(contestId),

		success: function(response) {
			//console.log(response.status);
			console.log(response.output);
		},

		error: function(error) {
			console.log(error);
		},

	});
}
