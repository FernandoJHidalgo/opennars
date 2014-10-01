package nars.gui.output.graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import nars.core.EventEmitter.Observer;
import nars.core.Events.FrameEnd;
import nars.core.NAR;
import nars.gui.NPanel;
import nars.gui.NSlider;


public class ProcessingGraphPanel extends NPanel implements Observer {

    
    ProcessingGraphCanvas app = null;
    private final NAR nar;

    public ProcessingGraphPanel(NAR n, ProcessingGraphCanvas graphCanvas) {
        super(new BorderLayout());

        this.app = graphCanvas;
        this.nar = n;
    }

    protected void init() {        

        this.setSize(1000, 860);//initial size of the window
        this.setVisible(true);

        JPanel menu = new JPanel(new FlowLayout(FlowLayout.LEFT));

        final JComboBox modeSelect = new JComboBox();
        modeSelect.addItem("GridSort");
        modeSelect.addItem("Circle");
        modeSelect.addItem("Grid");
        modeSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.mode = modeSelect.getSelectedIndex();
                app.setUpdateNext();
            }
        });
        menu.add(modeSelect);

        final JCheckBox beliefsEnable = new JCheckBox("Beliefs");
        beliefsEnable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.showBeliefs = (beliefsEnable.isSelected());
                app.setUpdateNext();
            }
        });
        menu.add(beliefsEnable);

        /*
         final JCheckBox syntaxEnable = new JCheckBox("Syntax");
         syntaxEnable.addActionListener(new ActionListener() {
         @Override public void actionPerformed(ActionEvent e) {
         app.showSyntax = (syntaxEnable.isSelected());        
         app.setUpdateNext();
         }
         });
         menu.add(syntaxEnable);        
         */
        NSlider nodeSize = new NSlider(app.nodeSize, 1, app.maxNodeSize) {
            @Override
            public void onChange(float v) {
                app.nodeSize = (float) v;
                app.drawn = false;
            }
        };
        nodeSize.setPrefix("Node Size: ");
        nodeSize.setPreferredSize(new Dimension(125, 25));
        menu.add(nodeSize);

        //final int numLevels = ((Bag<Concept>)n.memory.concepts).levels;
        NSlider maxLevels = new NSlider(1, 0, 1) {
            @Override
            public void onChange(float v) {
                app.minPriority = (float) (1.0 - v);
                app.setUpdateNext();
            }
        };
        maxLevels.setPrefix("Min Level: ");
        maxLevels.setPreferredSize(new Dimension(125, 25));
        menu.add(maxLevels);

        NSlider nodeSpeed = new NSlider(app.nodeSpeed, 0.001f, 0.99f) {
            @Override
            public void onChange(float v) {
                app.nodeSpeed = (float) v;
                app.drawn = false;
            }
        };
        nodeSpeed.setPrefix("Node Speed: ");
        nodeSpeed.setPreferredSize(new Dimension(125, 25));
        menu.add(nodeSpeed);

        NSlider blur = new NSlider(0, 0, 1.0f) {
            @Override
            public void onChange(float v) {
                app.motionBlur = (float) v;
                app.drawn = false;
            }
        };
        blur.setPrefix("Blur: ");
        blur.setPreferredSize(new Dimension(85, 25));
        menu.add(blur);

        add(menu, BorderLayout.NORTH);
        add(app, BorderLayout.CENTER);

    }

    @Override
    protected void onShowing(boolean showing) {
        if (showing) {
            init();
            nar.memory.event.on(FrameEnd.class, this);
        } else {
            nar.memory.event.off(FrameEnd.class, this);

            app.stop();
            app.destroy();
            removeAll();
            app = null;
        }
    }

    @Override
    public void event(Class event, Object[] arguments) {
        if (app != null) {
            app.updateGraph();
        }
    }

}