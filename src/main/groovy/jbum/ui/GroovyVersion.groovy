
package  jbum.ui;


class GroovyVersion {
	public String about = 'Groovy Version 1.1 beta 2'
	
	void startConsole(){
		try {
//		    Console console = new Console();
//		    console.setVariable("main", Main.myself);
//		    console.setVariable("vecii", Main.myself.centerP.vecii);
//		    console.run();
//		    console.getOutputArea().setText("Hi there.  'main' is the main jbum class.");
		} catch (Throwable t) {
			Main.error(t,"Creating groovy console");
			throw new RuntimeException(t);
		}
	}
}
