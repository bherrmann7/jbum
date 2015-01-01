
package jbum.ui;

import javax.swing.*;
import java.awt.event.ActionListener;

class GroovyUI {
	
	void init(JFrame frame){
		def menuItem;
		
		frame.getJMenuBar().getMenu(0).add(menuItem = new JMenuItem("Order images by filename"),4);
		menuItem.addActionListener({
				jbum.ui.Main.myself.centerP.vecii.vec.sort({a,b->a.fileName<=>b.fileName})
				jbum.ui.Main.myself.centerP.rebuildComponents()
		} as  ActionListener)

	}
}