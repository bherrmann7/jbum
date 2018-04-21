
document.write('<script type="text/javascript" src="js/prototype.js"></script>');
document.write('<script type="text/javascript" src="js/scriptaculous.js?load=effects"></script>');
document.write('<script type="text/javascript" src="js/lightbox.js"></script>');

document.write('<link rel="stylesheet" href="css/lightbox.css" type="text/css" media="screen" />');

function s(x){
	document.write(x);
}

s("<HEAD>");
s("<style>");
s(".silent { display:none }");
s("</style>");
s("<TITLE>"+p.title+"</TITLE>");
s("</HEAD><BODY BGCOLOR='"+p.bgColor+"' onkeypress='gotKey(event)'>");
s("<CENTER><H1>"+p.title+"</H1>"+p.intro);

s('<form action=http://jadn.com:7070/fotomail/submit.jsp>')
s('<table><tr><td><div class="silent" style="border: dotted; padding: 20px;">Email all photos to ');
s('<input type=text name=to size=20 value="save@mywalgreens.com"> from ');
s('<input type=text name=from size=20 value="bob@jadn.com">');
base = document.location.href.replace("/index.html","");
s('<input type=hidden name=url value="'+base+'" />');
s('<input type=submit name="email" value="email">');
s('<br>Message: <textarea rows=5 cols=50 name="body"></textarea>');
s('<br><input type=checkbox name=linksOnly> Links only');

s('</div></table>');

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
     pt="<table>"
	 numPerRow = parseInt(getWidth()/228);
	 col =0;
	 for(dex=0;dex<p.photos.length;dex++){
	    if ( col == 0 )
	    	pt += "<TR>";
	    ii = p.photos[dex];	    
	    pt += "<td valign='top' align='center'><table cellpadding=5 cellspacing=10 width=210 >";
	    pt += "<tr><TD valign='top' align='center' bgcolor='white'>";
	    pt += "<a id='"+ii.fileName+"' title='"+ii.fileName+"' onclick='myLightbox.start(this);return false' rel='lightbox[1]' HREF='smaller/me_"+escape(ii.fileName)+"' >";
	    pt += "<img title='"+ii.date+"' src='smaller/sm_"+escape(ii.fileName)+"' width="+ii.smallSize.width;
	    pt += " height="+ii.smallSize.height+" ></a><br>"+ii.comment;
	    pt += "<br><input class='silent' type=checkbox onChange='checkChange(this)' name=foto value='"+escape(ii.fileName)+"'/>";
		pt +=  "</TD></table>";
	    if ( ++col >= numPerRow ){
	    	col = 0;
	    }
	 }
	 pt+="</table>";
	 return pt;
 }
 
 function checkChange(inputElem){
    if ( ! inputElem.next ) {
        new Insertion.After(inputElem, "<input type=text name='text"+inputElem.value+"' size='30' />");
    }
 }
 
 s("<p><small>("+(p.photos.length)+" photos)</small><div id='hasphotos'>");
 s(createPhotoTable());
 
 s('</form>');

 s("</div>"+p.prolog);
 document.close();

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
 

function gotKey(event){
   if( event.which != 5/*e*/ || !event.ctrlKey )
    return;
    // toggle emailing of photos
     document.getElementsByClassName( "silent").each(function(item) {
        if ( item.style.display == 'block' )
            item.style.display = 'none'
        else
            item.style.display = 'block'
        
     });
}
