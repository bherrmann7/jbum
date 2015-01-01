
(new File("/home/bob/ws-rc/jbum/src/jbum/layouts")).eachFile { File file ->
	if ( file.name == ".svn" )
		return;
	if ( ! file.isDirectory() )
		return;
	
	//println "Working on $file"
	new File(file,'resources.txt').withWriter(){ w->
	    def recurse;
	    recurse = { base, current ->
			current.eachFile { rFile ->		
				if (rFile.name == ".svn" || rFile.name == "resources.txt")
					return;
				if ( rFile.isDirectory())
					recurse( base, rFile)
				else
					w.write rFile.toString().substring(base.toString().length()+1)+'\n';
			}
		}
	    recurse(file,file)
	}
}