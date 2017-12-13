// tour

relative = function(list, item, rel) {

    // hack to make it work with the old team page
    if (item == '/team.html' && rel == -1) return '/';
    if (item == '/team.html' && rel == 1) return '/technologies.html';
    // end hack

    var index = list.indexOf(item);
    if (index == -1) {
	console.warn("Not part of the tour: "+item);
	return null;
    }
    var len = list.length;
    index = (index + rel + len) % len;
    return list[index];
}

document.onkeydown = function(event) {
    var path = window.location.pathname;
    var code = event.keyCode;
    var target = null;
    switch(code) {
    case 39: // right
	target = relative(tour, path, 1);
	break;
    case 37: // left
	target = relative(tour, path, -1);
	break;
    default:
	console.warn("No action registered for: "+code);
    }
    if (target != null) {
	window.location.pathname = target;
    }
}
