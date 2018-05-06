package tinker

import javax.swing.*
import javax.swing.border.Border
import javax.swing.border.EmptyBorder
import java.awt.*

def f = new JFrame()

Box jp// = new  JPanel() //(BoxLayout.X_AXIS)
jp = new Box(BoxLayout.X_AXIS)
f.setBackground(Color.WHITE)

jp.setBorder(new EmptyBorder(5,0,0,0))

def blueBox = new JPanel(){
    @Override
    Dimension getPreferredSize() {
        return new Dimension(100,100)
    }
    {
        setBackground(Color.BLUE)
    }

    @Override
    Dimension getMaximumSize(){
        return getPreferredSize()
    }

}

def redBox = new JPanel(){
    @Override
    Dimension getPreferredSize() {
        return new Dimension(200,200)
    }
    {
        setBackground(Color.RED)
    }

    @Override
    Dimension getMaximumSize(){
        return getPreferredSize()
    }
}

redBox.setAlignmentY(0)
blueBox.setAlignmentY(0)
//redBox.setAlignmentX(0)
//blueBox.setAlignmentX(0)

jp.add(blueBox) //, "vtop tab")
jp.add(Box.createRigidArea(new Dimension(5,0)))
//jp.add(jp.createHorizontalStrut(5))
jp.add(redBox) //, "vtop tab")

f.setContentPane(jp)

f.setSize(400,400)

f.setVisible(true)
