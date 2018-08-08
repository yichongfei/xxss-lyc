function formatDateTime(inputTime) {
	var date = new Date(inputTime);
	var y = date.getFullYear();
	var m = date.getMonth() + 1;
	m = m < 10 ? ('0' + m) : m;
	var d = date.getDate();
	d = d < 10 ? ('0' + d) : d;
	var h = date.getHours();
	h = h < 10 ? ('0' + h) : h;
	var minute = date.getMinutes();
	var second = date.getSeconds();
	minute = minute < 10 ? ('0' + minute) : minute;
	second = second < 10 ? ('0' + second) : second;
	return y + '-' + m + '-' + d;
};
function AddFavorite(sURL, sTitle) {
	try {
		window.external.addFavorite(sURL, sTitle);
	} catch (e) {
		try {
			window.sidebar.addPanel(sTitle, sURL, "");
		} catch (e) {
			alert("加入收藏失败，请使用Ctrl+D进行添加");
		}
	}
}
function isEmail(str) {
	var re = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
	if (re.test(str) != true) {
		return false;
	} else {
		return true;
	}
}


$(document).ready(function(){
	$("img").addClass("img-rounded");
	if($("#accountInformation").text()!=='游客'&&$("#accountInformation").text()!==null){
		$("#registerButton").css("display","none");
		$("#loginButton").css("display","none");
		$("#exit").css("display","inline");
	}
	
});
/**
 * 退出登录
 * @returns
 */
function exit(){
	window.location.href = "/exit";
}
function alertUrl() {
	alert("请记住本站永久域名,ctrl + d 添加收藏夹,本站永久域名 www.xxss2018.com");
}

