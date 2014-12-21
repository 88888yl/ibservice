/**
 * Created by myl on 14-11-19.
 */
if (window.ActiveXObject) {
    loadJsCssFile("./ie/html5shiv.js", "js");
    loadJsCssFile("./ie/respond.js", "js");
    loadJsCssFile("./bootstrap/js/bootstrap.min.js", "js");
    loadJsCssFile("./bootstrap/css/bootstrap.min.css", "css");
    loadJsCssFile("./ie/bootstrap-ie.css", "css");
}
else {
    loadJsCssFile("./bootstrap/js/bootstrap.min.js", "js");
    loadJsCssFile("./bootstrap/css/bootstrap.min.css", "css");
}

function loadJsCssFile(fileName, fileType) {
    var fileRef;
    if (fileType == "js") {
        fileRef = document.createElement('script');
        fileRef.setAttribute("type", "text/javascript");
        fileRef.setAttribute("src", fileName);
    }
    else if (fileType == "css") {
        fileRef = document.createElement("link");
        fileRef.setAttribute("rel", "stylesheet");
        fileRef.setAttribute("type", "text/css");
        fileRef.setAttribute("href", fileName);
    }
    if (typeof fileRef != "undefined")
        document.getElementsByTagName("head")[0].appendChild(fileRef);
}