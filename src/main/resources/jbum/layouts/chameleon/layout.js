document.write('<script type="text/javascript" src="prototype-1.4.0.js"></script>');

function s(x){
	document.write(x);
}

s("<HEAD><TITLE>"+p.title+"</TITLE>");
s("</HEAD><BODY BGCOLOR='"+p.bgColor+"'>");
s("<CENTER><H1>"+p.title+"</H1>"+p.intro);

function getWidth(){
	if (parseInt(navigator.appVersion)>3) {
 		if (navigator.appName=="Netscape") {
  			return window.innerWidth;
  		}
  	}
 	if (navigator.appName.indexOf("Microsoft")!=-1) {
  		return document.body.offsetWidth;
 	}
 	return 800;
}

numPerRow=0;

 function createPhotoTable() {
     pt="<table cellpadding=5 cellspacing=10>"
	 numPerRow = parseInt(getWidth()/228);
	 col =0;
	 for(dex=0;dex<p.photos.length;dex++){
	    if ( col == 0 )
	    	pt += "<TR>";
	    ii = p.photos[dex];
	    pt += "<td valign='top' align='center'>";
	    
//	    pt += "<table cellpadding=5 cellspacing=10 width=210 >";
//	    pt += "<tr><TD valign='top' align='center' bgcolor=white>";
        pt += "<table bgcolor='white' cellpadding='0' cellspacing='0'>";
        pt += "<tr><td align='center' bgcolor=''><img src=ul.png></td><td width=100 background='up.png'><td><img src=ur.png>";
        pt += "<tr><td background=left.png width=14></td><td width=180>";
	    pt += "<a border=0 HREF='javascript:void newWin(\""+ii.fileName+"\", "+ii.mediumSize.width+", "+ii.mediumSize.height+")'>";
	    pt += "<img border=0 src=smaller/sm_"+ii.fileName+" width="+ii.smallSize.width+
    		" height="+ii.smallSize.height+">";
        pt += "</a>";
        if ( ii.comment.length > 1 )
            pt += ii.comment;
        pt += "<td width=14 backgroun\d='right.png'>";
        pt += "<tr><td><img src=dl.png></td><td width=100 background='down.png'><td><img src='dr.png'>";
		
		pt += "</table>";
	    if ( ++col >= numPerRow ){
	    	col = 0;
	    }
	 }
	 pt+="</table>";
	 return pt;
 }
 
 s("<p><small>("+(p.photos.length)+" photos)</small><div id='hasphotos'>");
 s(createPhotoTable());
 
 function handle_resize(event){
    if ( numPerRow != parseInt(getWidth()/228) )
    	Element.update('hasphotos', createPhotoTable());
 }
 
 window.onresize=handle_resize;
 
function newWin(img, w, h) {
	w+=30;
	h+=30;
	var left = (screen.width - w) / 2;
	var top = (screen.height - h) / 2;
	winprops = 'height='+h+',width='+w+',top='+top+',left='+left+',scrollbars=false,resizable';
	win = window.open('', "popimage", winprops)
	win.document.write("<center><a href='"+img+"' onclick='window.moveTo(0,0);window.resizeTo(screen.availWidth,screen.availHeight);'>");
	win.document.write("<img src='smaller/me_"+img+"''></a>");
	win.document.close();
	
	if (parseInt(navigator.appVersion) >= 4) { 
		win.window.focus();
	}
}
 s("</div>"+p.prolog);


