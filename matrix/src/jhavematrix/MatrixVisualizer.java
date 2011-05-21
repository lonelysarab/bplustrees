/* 
This file is part of JHAVE -- Java Hosted Algorithm Visualization
Environment, developed by Tom Naps, David Furcy (both of the
University of Wisconsin - Oshkosh), Myles McNally (Alma College), and
numerous other contributors who are listed at the http://jhave.org
site

JHAVE is free software: you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free
Software Foundation, either version 3 of the License, or (at your
option) any later version.

JHAVE is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with the JHAVE. If not, see:
<http://www.gnu.org/licenses/>.
*/

/**
 * 
 */
package jhavematrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jhave.core.JHAVETranslator;
import jhave.core.Visualizer;
import jhave.event.DocumentEvent;
import matrix.animation.Animator;
import matrix.uitools.StructurePanel;
import matrix.util.configuration.MatrixConfiguration;
import matrixpro.animation.CountingAnimator;
import matrixpro.util.ExerciseDescriptionParser;
import applications.exerciseapplet.MyModelAnswerFrame;
import content.assessment.Judgement;
import content.assessment.Jury;
import content.interfaces.SimulationExerciseModel;

/**
 * @author vkaravir
 *
 */
public class MatrixVisualizer extends Visualizer {
	private JhaveExercisePanel jep;
	private CountingAnimator studentAnimator;
	private StructurePanel modelSolution;
	private CountingAnimator modelAnimator;
	private CountingAnimator currAnimator;
	private boolean modelAnswerVisible = false;
	private JLabel studentLabel = new JLabel("Your solution");
	private JLabel modelLabel = new JLabel("Model solution");

	/**
	 * @param script
	 * @throws IOException
	 */
	public MatrixVisualizer(InputStream script) throws IOException {
		super(script);
		MatrixConfiguration.getInstance();
		studentAnimator = new CountingAnimator();
		currAnimator = studentAnimator;
        setCapabilities(CAP_CONTROLLABLE + CAP_STEP_FORWARD + CAP_STEP_BACKWARD + CAP_GOTO_FRAME +
        		CAP_MODEL + CAP_FINISH);
        Properties properties = new Properties();
        properties.load(script);
		jep = new JhaveExercisePanel(studentAnimator, 
				createInstance(properties.getProperty("exercise"), properties));
		modelAnimator = new CountingAnimator();
		MyModelAnswerFrame mmaf = new MyModelAnswerFrame(modelAnimator, (SimulationExerciseModel) jep.getExercise());
		mmaf.setVisible(false);
		modelSolution = mmaf.getStructurePanel();
		initDocumentationTabs(properties);
	}
	
	/**
	 * initialize the documentation info and pseudo code tabs in jhave if
	 * the exercise description contains it.
	 * @param properties Properties where the descriptions are searched from.
	 */
	private void initDocumentationTabs(Properties properties) {
        try {
        	String lang = JHAVETranslator.getLocale().getLanguage();
			String task = null;
        	if (properties.containsKey("task_" + lang)) {
        		task = properties.getProperty("task_" + lang);
        	} else if (properties.containsKey("task_en")) {
        		task = properties.getProperty("task_en");
        	} else {
        		task = properties.getProperty("task");
        	}
        	// this could be done better if the ExerciseDescriptionParser was changed to allow
        	// it to be passed strings
        	ExerciseDescriptionParser parser = new ExerciseDescriptionParser(new BufferedReader(new StringReader(task)));
        	if (parser.getNumberOfCodeBlocks() > 0) {
        		String code = "";
        		for (int i = 0; i < parser.getNumberOfCodeBlocks(); i++) {
        			String name = parser.getCodeBlockName(i);
        			code += "<h3>" + parser.getCodeBlockName(i) + "</h3>" + parser.getCodeBlock(name);
        		}
    			fireDocumentationEvent(new URI("str", code, ""), DocumentEvent.TYPE_PSEUDOCODE_PAGE);
        	}
        	task = "<h3>Task</h3>" + parser.getTask() + "<h3>Instructions</h3>" + parser.getInstructions();
        	if (jep.getExercise().getDescription() != null) {
        		task += "<h3>Data</h3><pre>" + jep.getExercise().getDescription() + "</pre>"; 
        	}
        	fireDocumentationEvent(new URI("str", task, ""), DocumentEvent.TYPE_INFORMATION_PAGE);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    private static SimulationExerciseModel createInstance(String exerciseClassName, Properties properties) {
        try {
            Class exerciseClass = Class.forName(exerciseClassName);

            //If the exercise class has a constructor with a properties parameter
            //give the properties. Otherwise use the parameterless constructor.
            SimulationExerciseModel exer;
            try{
                Constructor c = exerciseClass.getConstructor(new Class[]{Properties.class});                        
                exer = (SimulationExerciseModel)c.newInstance(new Object[]{ properties });
                //return (SimulationExerciseModel)c.newInstance(new Object[]{});
            }catch(NoSuchMethodException f){
                Constructor c = exerciseClass.getConstructor(new Class[0]);                        
                exer = (SimulationExerciseModel)c.newInstance(new Object[0]);                
            }
            if (exer != null) {
            	exer.setSeed(new Date().getTime());
            	return exer;
            }
        } catch (Exception e) {
            System.err.println("Can't get object of class " + exerciseClassName);
            e.printStackTrace(System.err);
        }
        return null;
    }

	public int getCurrentFrame() {
		if (currAnimator != null) {
			try {
				return Integer.parseInt(currAnimator.getStep()) - 1;
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}

	public int getFrameCount() {
		if (currAnimator == null || currAnimator.getMaxStep() == 0) {
			System.out.println("getFrameCount() 0");
			return 1;
		}
		return currAnimator.getMaxStep();
	}

	private JPanel renderPane;
	public JComponent getRenderPane() {
		if (renderPane == null) {
			renderPane = new JPanel();
			renderPane.add(jep);
		}
		return renderPane;
	}

   /**
     * Step back one frame, animating if necessary. This method must block until
     * the animation or frame change is complete.
     */
    public void stepBackward() {
    	if (currAnimator.hasPreviousOperation()) {
    		currAnimator.undo();
    		renderPane.repaint();
    	}
    }
    
    /**
     * Step forward one frame, animating if necessary. This method must block until
     * the animation or frame change is complete.
     */
    public void stepForward() {
    	if (currAnimator.hasNextOperation()) {
    		currAnimator.redo();
    		renderPane.repaint();
    	}
    }
    
    public void gotoFrame(int frame) {
    	currAnimator.rewind();
    	while (frame > 0) {
    		currAnimator.redo();
    		frame--;
    	}
    	renderPane.repaint();
    }
    
    public void toggleModelAnswerVisible() {
    	modelAnswerVisible = !modelAnswerVisible;
		renderPane.removeAll();
    	if (modelAnswerVisible) {
    		renderPane.add(modelLabel);
			renderPane.add(modelSolution);
    		currAnimator = modelAnimator;
    	} else {
    		renderPane.add(studentLabel);
    		renderPane.add(jep);
    		currAnimator = studentAnimator;
    	}
		Animator.setActiveAnimator(currAnimator);
    	jep.repaint();
    	modelSolution.repaint();
    	renderPane.repaint();
    }
    
    public int[] finish() {
    	Judgement j = Jury.getInstance().judge(jep.getExercise().getAnswer(),
                    jep.getAnimator(), (SimulationExerciseModel)jep.getExercise());
    	toggleModelAnswerVisible();
    	// to get the highlighting working, we have to do some rewinding
    	modelAnimator.rewind();
    	modelAnimator.end();
    	modelAnimator.rewind();
    	return new int[] {j.getPointSum(), j.getMax()};
    }
}