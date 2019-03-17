function writeOutPage(p) {
    function s(x) {
        document.write(x);
    }

    s("<HEAD>");
    s('<script type="text/javascript" src="js/prototype.js"></script>');
    s('<script type="text/javascript" src="js/scriptaculous.js?load=effects"></script>');
    s('<script type="text/javascript" src="js/lightbox.js"></script>');
    s('<link rel="stylesheet" href="css/lightbox.css" type="text/css" media="screen" />');
    s("<TITLE>" + p.title + "</TITLE>");
    s("</HEAD><BODY BGCOLOR='" + p.bgColor + "' >");
    s("<CENTER><H1>" + p.title + "</H1>" + p.intro);

    sideMargin = 30;

    function getWidth() {
        if (parseInt(navigator.appVersion) > 3) {
            if (navigator.appName == "Netscape") {
                return window.innerWidth - sideMargin;
            }
        }
        if (navigator.appName.indexOf("Microsoft") != -1) {
            return document.body.offsetWidth - sideMargin;
        }
        return 800; // cell phone?
    }

    numPerRow = 0;
    imagePlusBorder = 300/*image width*/ + 5/*right border*/ + 5/*left border*/ + 10/*gap between images*/;

    function computeNumberPerRow() {
        return parseInt(getWidth() / imagePlusBorder);
    }

    function createPhotoTable() {
        pt = "<table>";
        numPerRow = computeNumberPerRow();
        col = 0;
        for (dex = 0; dex < p.photos.length; dex++) {
            if (col == 0)
                pt += "<TR>";
            ii = p.photos[dex];
            pt += "<td valign='top' align='center'><table cellpadding=5 cellspacing=5 width=310 >";
            pt += "<tr><TD valign='top' align='center' bgcolor='white'>";
            pt += "<a id='" + ii.fileName + "' title='" + ii.fileName + "' onclick='myLightbox.start(this);return false' rel='lightbox[1]' HREF='smaller/me_" + escape(ii.fileName) + "' >";
            pt += "<img src='smaller/sm_" + escape(ii.fileName) + "' width=" + ii.smallSize.width;
            pt += " height=" + ii.smallSize.height + " ></a><br>" + ii.comment;
            pt += "</TD></table>";
            if (++col >= numPerRow) {
                col = 0;
            }
        }
        pt += "</table>";
        return pt;
    }

    s("<p><small>" + (p.photos.length) + " photos</small>");
    s("<div id='hasphotos'>")
    s(createPhotoTable());
    s("</div>")

    s(p.prolog);
    document.close();

    function handle_resize(event) {
        if (numPerRow != computeNumberPerRow)
            Element.update('hasphotos', createPhotoTable());
    }

    window.onresize = handle_resize;

}
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function () {
        if (this.readyState == 4 /*&& this.status == 200*/) {
            var data = JSON.parse(this.responseText);
            writeOutPage(data);
        }
    };
    xmlhttp.open("GET", "jbum.json", true);
    xmlhttp.send();

