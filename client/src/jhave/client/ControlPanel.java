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

/*
 * ControlPanel.java
 *
 * Created on August 3, 2004, 1:27 PM
 */

package jhave.client;

// import java.applet.Applet;	// For audio files
// import java.applet.AudioClip;
import javax.sound.sampled.*;	// For audio streamified

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.speech.AudioException;
import javax.speech.EngineCreate;
import javax.speech.EngineException;
import javax.speech.EngineList;
import javax.speech.EngineStateError;
import javax.speech.synthesis.JSMLException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

import jhave.client.misc.CommandQueue;
import jhave.core.JHAVETranslator;
import jhave.core.Visualizer;
import jhave.Algorithm;
import jhave.event.AudioTextEvent;
import jhave.event.AudioTextListener;
import jhave.event.DocumentationListener;
import jhave.event.QuizEvent;
import jhave.event.QuizListener;
import jhave.question.QuestionView;

/**
 *
 * @author  Chris Gaffney (AudioText -- Tom Naps)
 */
public class ControlPanel extends JPanel implements QuizListener {
    /** Icon for play. */
    private static final String ICON_PLAY = "jhave/client/graphics/Play24.gif";
    /** Icon for stop. */
    private static final String ICON_STOP = "jhave/client/graphics/Stop24.gif";
    /** Icon for pause. */
    private static final String ICON_PAUSE = "jhave/client/graphics/Pause24.gif";
    /** Icon for step backward. */
    private static final String ICON_STEP_BACKWARD = "jhave/client/graphics/StepBack24.gif";
    /** Icon for step forward. */
    private static final String ICON_STEP_FORWARD = "jhave/client/graphics/StepForward24.gif";
    /** Icon for zoom in. */
    private static final String ICON_ZOOM_IN = "jhave/client/graphics/ZoomIn24.gif";
    /** Icon for zoom out. */
    private static final String ICON_ZOOM_OUT = "jhave/client/graphics/ZoomOut24.gif";
    
    /** */
    private static final int TAB_CODE = 0;
    /** */
    private static final int TAB_INFO = 1;
    
    /** */
    private static final int STATE_INITIAL = 0;
    /** */
    private static final int STATE_PLAY = 1;
    /** */
    private static final int STATE_STOPPED = 2;
    /** */
    private static final int STATE_PAUSED = 3;
    /** */
    private static final int STATE_STEP = 4;
    /** */
    private int state = -1;
    
    /** Map of icon for the various buttons. */
    private static Map icons = null;
    
    /** Play button. */
    private JButton playButton = null;
    /** Stop button. */
    private JButton stopButton = null;
    /** Step backward button. */
    private JButton stepBackward = null;
    /** Step forward button. */
    private JButton stepForward = null;
    /** Zoom in button. */
    private JButton zoomIn = null;
    /** Zoom out button. */
    private JButton zoomOut = null;
    /** Toggle model answer button. */
    private JButton toggleModelAnswer = null;
    /** Finish button. */
    private JButton finishButton = null;
    /** Slider for going to a specific frame. */
    private JSlider gotoSlider = null;
    /** Label for the frame number. */
    private JLabel frameNumber = null;
    /** */
    private JSplitPane splitPane = null;
    
    /* 
     * William Clements
     * Aug 28, 2010
     * jvmbytecodes project - used to return the current running visualization
     */
    private String algoName;
    private JTabbedPane byteCodeTab;
    private JTabbedPane sourceCodeTab;

    /** */
    private JTabbedPane documentTabs;
    /** */
    private JEditorPane infoPage = null;
    /** */
    private JEditorPane codePage = null;
    /** */
    private DocumentHandler docHandler = null;
    /** */
    private AudioTextHandler audioTextHandler = null;
    /** */
    private boolean isFirstPaint = true;
    
    /** */
    private Visualizer visualizer;
    /** */
    private CommandQueue queue;
    
    /** Reference to current quiz results to aid quizmode */
    private QuizInfo quizInfo;
    /** If we are taking a quiz */
    private boolean quizMode = false;
    /** The frame a quiz question opened at */
    private int lockedFrame = -1;
    /** The final quiz results have been sent */
    //private boolean quizSent = false;

    /**
     * Creates a new instance of ControlPanel
     * @param visualizer the visualizer to be controlled.
     */
    public ControlPanel(Visualizer visualizer) {
        super(new BorderLayout());
        this.visualizer = visualizer;
        queue = new CommandQueue();
        
        if(icons == null) {
            loadGraphics();
        }
        
        if(isControllable()) {
            add(BorderLayout.SOUTH, buildControls());
        }
        
        JScrollPane scroll = new JScrollPane(visualizer.getRenderPane());
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(visualizer.getRenderPane().getPreferredSize());
        
        // Instantiate the code page if it's null
        if(codePage == null) {
            codePage = new JEditorPane();
            codePage.setEditable(false);
	    //Just added
        }

        // Instantiate the info page if it's null
        if(infoPage == null) {
            infoPage = new JEditorPane();
            infoPage.setEditable(false);
        }
	
        documentTabs = new JTabbedPane();
        JHAVETranslator.getGUIBuilder().insertTranslatableTab("pseudoCodeTab",
            new JScrollPane(codePage), documentTabs);
        JHAVETranslator.getGUIBuilder().insertTranslatableTab("infoTab", 
            new JScrollPane(infoPage), documentTabs);
//        documentTabs.add("Pseudo Code", new JScrollPane(codePage));
//        documentTabs.add("Info", new JScrollPane(infoPage));
        
        // Since there will only be one code and info page at one time we make
        // them static, but since the contents might not change at loading
        // we zero out the page being shown.
        codePage.setText("");
        infoPage.setText("");
        
        if(docHandler == null) {
            docHandler = new DocumentHandler();
        }
        visualizer.addDocumentationListener(docHandler);
        
        if(audioTextHandler == null) {
            audioTextHandler = new AudioTextHandler();
        }
        visualizer.addAudioTextListener(audioTextHandler);
        
        /*
         * William Clements
         * Aug 28, 2010
         * jvmbytecodes project
         * Get the name of the current running algorithm
         */
        ClientNetworkController control = ClientNetworkController.getInstance();
        algoName = control.getLastAlgorithm().GetAlgoName();
        if(algoName.compareTo("jvmbytecodes")==0) {
            /*
             * William Clements
             * Aug 28, 2010
             * A bytecode tab and a source code tab are made for the jvmbytecodes project
             */
            byteCodeTab = new JTabbedPane();

            JHAVETranslator.getGUIBuilder().insertTranslatableTab("byteCodeTab",
                new JScrollPane(codePage), byteCodeTab);
            sourceCodeTab = new JTabbedPane();
            JHAVETranslator.getGUIBuilder().insertTranslatableTab("sourceCodeTab", 
            new JScrollPane(infoPage), sourceCodeTab);

	        /*
             * William Clements
             * Aug 28, 2010
	         * This is where the jvmbytecodes project splits the code pane
	         */
	        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, byteCodeTab, sourceCodeTab);
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, scroll, splitPane);
            sourceCodeTab.setMinimumSize(new Dimension(100, 0));
            byteCodeTab.setMinimumSize(new Dimension(100, 0));
            scroll.setMinimumSize(new Dimension(0, 0));

        }
        else { //normal JHAVE visualization
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, scroll, documentTabs);
            documentTabs.setMinimumSize(new Dimension(200, 0));
            scroll.setMinimumSize(new Dimension(0, 0));
            splitPane.setDividerLocation(9000);
        }
        
        //setPreferredSize(visualizer.getRenderPane().getPreferredSize());
        add(BorderLayout.CENTER, splitPane);
        setState(STATE_INITIAL);
    }
    
    /**
     * Load the graphics required for this panel.
     */
    private void loadGraphics() {
        icons = new HashMap(7);
        
        ClassLoader loader = getClass().getClassLoader();
        Image image = Toolkit.getDefaultToolkit().getImage(loader.getResource(ICON_PLAY));
        icons.put("play", image);
        
        image = Toolkit.getDefaultToolkit().getImage(loader.getResource(ICON_STOP));
        icons.put("stop", image);
        
        image = Toolkit.getDefaultToolkit().getImage(loader.getResource(ICON_PAUSE));
        icons.put("pause", image);
        
        image = Toolkit.getDefaultToolkit().getImage(loader.getResource(ICON_STEP_BACKWARD));
        icons.put("step_backward", image);
        
        image = Toolkit.getDefaultToolkit().getImage(loader.getResource(ICON_STEP_FORWARD));
        icons.put("step_forward", image);
        
        image = Toolkit.getDefaultToolkit().getImage(loader.getResource(ICON_ZOOM_IN));
        icons.put("zoom_in", image);
        
        image = Toolkit.getDefaultToolkit().getImage(loader.getResource(ICON_ZOOM_OUT));
        icons.put("zoom_out", image);
    }
    
    /**
     *
     */
    private JPanel buildControls() {
        int count = 0;
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(layout);
        ActionListener listener = new ControlPanel.ButtonActionListener();
        
        if(isStepBackwardSupported()) {
            stepBackward = new JButton(new ImageIcon((Image)icons.get("step_backward")));
            stepBackward.setActionCommand("step_backward");
            stepBackward.addActionListener(listener);
            panel.add(stepBackward);
            count++;
        }
        
        if(isPlaySupported()) {
            playButton = new JButton(new ImageIcon((Image)icons.get("play")));
            playButton.setActionCommand("play");
            playButton.addActionListener(listener);
            panel.add(playButton);
            count++;
            
            //panel.add(new JLabel(new ImageIcon((Image)icons.get("pause"))));
            if(isStopSupported()) {
                stopButton = new JButton(new ImageIcon((Image)icons.get("stop")));
                stopButton.setActionCommand("stop");
                stopButton.addActionListener(listener);
                panel.add(stopButton);
                count++;
            }
        }
        
        if(isStepForwardSupported()) {
            stepForward = new JButton(new ImageIcon((Image)icons.get("step_forward")));
            stepForward.setActionCommand("step_forward");
            stepForward.addActionListener(listener);
            panel.add(stepForward);
            count++;
        }
        
        if (isModelAnswerSupported()) {
        	toggleModelAnswer = new JButton("toggle modelanswer");
        	toggleModelAnswer.setActionCommand("toggle_model");
        	toggleModelAnswer.addActionListener(listener);
        	// if in quiz mode, should set the button disabled until finished
        	// how to check if we are in quizmode?? -ville
        	if (quizMode) toggleModelAnswer.setEnabled(false);
        	panel.add(toggleModelAnswer);
        	count++;
        }
        
        if (isFinishSupported()) {
        	finishButton = new JButton("finish");
        	finishButton.setActionCommand("finish");
        	finishButton.addActionListener(listener);
        	panel.add(finishButton);
        	count++;
        }
        
        if(isZoomSupported()) {
            zoomIn = new JButton(new ImageIcon((Image)icons.get("zoom_in")));
            zoomIn.setActionCommand("zoom_in");
            zoomIn.addActionListener(listener);
            panel.add(zoomIn);
            
            zoomOut = new JButton(new ImageIcon((Image)icons.get("zoom_out")));
            zoomOut.setActionCommand("zoom_out");
            zoomOut.addActionListener(listener);
            panel.add(zoomOut);
            count += 2;
        }
        
        if(isGotoFrameSupported()) {
            gotoSlider = new JSlider(0, visualizer.getFrameCount() - 1, 0);
            gotoSlider.setMinorTickSpacing(1);
            gotoSlider.setPaintTicks(true);
            gotoSlider.setSnapToTicks(true);
            
            gotoSlider.addChangeListener(new SliderChangeListener());
            new Thread(new FrameChangeChecker()).start();
            
            panel.add(gotoSlider);
            count++;
        }
        
        frameNumber = new JLabel(Integer.toString(visualizer.getCurrentFrame() + 1));
        panel.add(frameNumber);
        //panel.setLayout(new GridLayout(1, count));
        return panel;
    }

    /* 
     * William Clements
     * Aug 28, 2010
     * jvmbytecodes project - returns the name of the currently running visualization
     */
    String getCurrentAlgorithm() {
        return algoName;
    }

	/**
     *  Give the control panel a reference to the quiz info.
     *  @param quizInfo a reference to the QuizInfo instance
     */
    public void setQuizInfo(QuizInfo quizInfo) {
    	this.quizInfo = quizInfo;
    	quizMode = true;
    	if (isModelAnswerSupported() && toggleModelAnswer != null) 
    		toggleModelAnswer.setEnabled(false);
    	QuestionView.addQuizListener(this);
    }

    /**
     *  Lock / Unlock the quiz-related controls
     *  @param lockState whether to lock controls for quizmode
     */
    public void setQuizLock(boolean lock) {
	if(!lock) {
	    lockedFrame = -1;
	    setState(state);
	} else {
	    lockedFrame = visualizer.getCurrentFrame();
	}
    }

    /**
     *  On QuizEvent, unlock quiz-related controls if the even denotes a closed quiz question,
     *  or lock them if it denotes an opened quiz question.
     *  @param e the QuizEvent that was caught.
     */
    public void handleQuizEvent(QuizEvent e) {

	setQuizLock(e.open());
    }
    
    /**
     *
     */
    private void setState(final int newState) {
    	if(!isControllable()) {
    		return;
    	}
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			boolean backward = true, forward = true, stop = true, slider = true, play = true;

    			if(newState == STATE_INITIAL) {
    				stop = false;
    			} else if(newState == STATE_PLAY) {
    				if(!isPauseSupported()) {
    					play = false;
    				}
    				backward = false;
    				forward = false;
    				slider = false;
    			} else if(newState == STATE_PAUSED && visualizer.getCurrentFrame() == lockedFrame) {
    				forward = false;
    				slider = false;
    			} else if(newState == STATE_STEP) {
    				play = false;
    				stop = false;
    				slider = false;
    			}

    			if(visualizer.getCurrentFrame() == 0) {
    				backward = false;

    				if(newState != STATE_PLAY) {
    					stop = false;
    				}
    			} else if(visualizer.getCurrentFrame() == visualizer.getFrameCount() - 1 && !isFinishSupported() /*&& !quizSent*/) {
    				// if finish supported we report quiz results when that button is clicked
    				forward = false;
    				play = false;
    				//quizSent = true;
    				if(quizMode) {
    					quizInfo.sendResults();
    				}
    			}


    			if(stepBackward != null) {
    				stepBackward.setEnabled(backward);
    			}

    			if(playButton != null) {
    				if(isPauseSupported()) {
    					if(newState != STATE_PLAY) {
    						playButton.setIcon(new ImageIcon((Image)icons.get("play")));
    						playButton.setActionCommand("play");
    					} else {
    						playButton.setIcon(new ImageIcon((Image)icons.get("pause")));
    						playButton.setActionCommand("pause");
    					}
    				}
    				playButton.setEnabled(play);
    			}
    			if(stopButton != null) {
    				stopButton.setEnabled(stop);
    			}
    			if(stepForward != null) {
    				stepForward.setEnabled(forward);
    			}
    			if(gotoSlider != null) {
    				gotoSlider.setEnabled(slider);
    				gotoSlider.setValue(visualizer.getCurrentFrame());
    			}
    		}  // end run()
    	});
    	state = newState;
    }
    
    public void paintComponent(Graphics g) {
        if(isFirstPaint) {
            splitPane.setDividerLocation(.75);
            isFirstPaint = false;
        }
        super.paintComponent(g);
    }
    private class ButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("play")) {
                queue.enqueue(new PlayCommand(visualizer));
            } else if(e.getActionCommand().equals("pause")) {
                visualizer.pause();
                setState(STATE_PAUSED);
            } else if(e.getActionCommand().equals("stop")) {
                visualizer.stop();
                setState(STATE_STOPPED);
            } else if (e.getActionCommand().equals("toggle_model")) {
            	visualizer.toggleModelAnswerVisible();
            } else if (e.getActionCommand().equals("finish")) {
            	int[] grade = visualizer.finish();
            	JOptionPane.showMessageDialog(null, "Grade " + grade[0] + " of " + grade[1] + " steps were correct");
            	toggleModelAnswer.setEnabled(true);
            	// here, the data should be submitted to jhave/trakla2 db
				if(quizMode) {
					quizInfo.sendResults(grade[0], grade[1]);
				}
            } else if(e.getActionCommand().equals("step_backward")) {
                queue.enqueue(new StepBackwardCommand(visualizer));
            } else if(e.getActionCommand().equals("step_forward")) {
                queue.enqueue(new StepForwardCommand(visualizer));
            } else if(e.getActionCommand().equals("zoom_in")) {
                visualizer.zoom(visualizer.getZoom() + .10);
            } else if(e.getActionCommand().equals("zoom_out")) {
                visualizer.zoom(visualizer.getZoom() - .10);
            }
        }
    }
    
    private class SliderChangeListener implements javax.swing.event.ChangeListener {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
            JSlider source = (JSlider)e.getSource();
            
            if(source.getValueIsAdjusting()) {
                visualizer.gotoFrame(source.getValue());
            }

	    int frame = visualizer.getCurrentFrame();

            frameNumber.setText(Integer.toString(frame + 1));

	    // Properly set the buttons when the slider is used.
	    if(frame == 0){
		stepForward.setEnabled(true);
		stepBackward.setEnabled(false);
	    }else if(frame == (visualizer.getFrameCount() - 1)){
		stepForward.setEnabled(false);
		stepBackward.setEnabled(true);
	    }else{
		stepForward.setEnabled(true);
		stepBackward.setEnabled(true);
	    }
        }
    }
    
    private class FrameChangeChecker implements Runnable {
        int lastFrame = -1;
        boolean stop = false;
        public void run() {
            while(!stop) {
                int frame = visualizer.getCurrentFrame();
                if (gotoSlider != null) {
                	gotoSlider.setMaximum(visualizer.getFrameCount() - 1);
                }
                if(lastFrame != frame & gotoSlider != null) {
                    gotoSlider.setValue(frame);
                    lastFrame = frame;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                	e.printStackTrace();
                    break;
                }
            }
        }
    }
    
    private class DocumentHandler implements DocumentationListener {
		public void showDocument(jhave.event.DocumentEvent e) {
			URI uri = e.getPage();
			JEditorPane page;

			switch (e.getType()) {
			case jhave.event.DocumentEvent.TYPE_INFORMATION_PAGE:
			default:
				page = infoPage;
				documentTabs.setSelectedIndex(TAB_INFO);
				break;
			case jhave.event.DocumentEvent.TYPE_PSEUDOCODE_PAGE:
				page = codePage;
				documentTabs.setSelectedIndex(TAB_CODE);
				break;
			}

			try {
				if (uri.getScheme() == null
						|| uri.getScheme().equalsIgnoreCase("rel")) {
					// URL url = new URL(BASE_ROOT + "doc/" + "script_name/" +
					// uri.getSchemeSpecificPart());
					// page.setPage(url);
					ClientNetworkController control = ClientNetworkController
							.getInstance();

					String webroot = control.getClientProperties().getWebroot()
							.toString();
					// If webroot goes beyond html_root, then we must have a
					// path to doc for local script debug mode
					boolean webroot_for_local_file = !(webroot
							.endsWith("html_root") || webroot
							.endsWith("html_root/"));

					if (Client.getDebug() && control != null
							&& webroot_for_local_file) {

						if (webroot.charAt(webroot.length() - 1) == '/') {
							webroot += uri.getSchemeSpecificPart();
						} else {
							webroot += "/" + uri.getSchemeSpecificPart();
						}

						try {
							URL url = new URL(convertPHPurl(webroot));
							page.setPage(url);
						} catch (MalformedURLException exc) {
							page.setText(JHAVETranslator.translateMessage(
									"invalidURL", webroot));
							// page.setText(webroot + " is not a valid URL");
						} catch (IOException exc) {
							page.setText(JHAVETranslator.translateMessage(
									"cannotLoad", webroot));
							// page.setText("Could not load " + webroot);
						}
					}

					else if (control != null) {
						String algoName = control.getLastAlgorithm()
								.GetAlgoName();
						// String webroot =
						// control.getClientProperties().getWebroot().toString();

						if (webroot.charAt(webroot.length() - 1) == '/') {
							webroot += "doc/"
									+ Algorithm.GetAlgoNameMinusIngen(algoName)
									+ "/" + uri.getSchemeSpecificPart();
						} else {
							webroot += "/doc/"
									+ Algorithm.GetAlgoNameMinusIngen(algoName)
									+ "/" + uri.getSchemeSpecificPart();
						}

						try {
							URL url = new URL(convertPHPurl(webroot));
							page.setPage(url);
						} catch (MalformedURLException exc) {
							page.setText(webroot + " is not a valid URL");
						} catch (IOException exc) {
							page.setText("Could not load " + webroot);
						}
					}

				} else if (uri.getScheme().equalsIgnoreCase("res")) {
					ClassLoader loader = getClass().getClassLoader();
					URL url = loader.getResource(convertPHPurl(uri
							.getSchemeSpecificPart()));
					page.setPage(url);
				} else if(uri.getScheme().equalsIgnoreCase("str")) {
					page.setContentType("text/html");
					page.setText(uri.getSchemeSpecificPart());
				} else {
					// Turn it directly into a URL
					page
							.setPage(new URL(convertPHPurl(uri.toURL()
									.toString())));
				}
			} catch (MalformedURLException exception) {
				page.setText(JHAVETranslator.translateMessage("malformedRes",
						uri.toString()));
				// page.setText("Malformed Resource: " + uri.toString());
			} catch (IOException exception) {
				page.setText(JHAVETranslator.translateMessage("errorLoading",
						uri.toString()));
				// page.setText("Error loading page: " + uri.toString());
			}
		}

	private String convertPHPurl(String webroot){
	    return webroot.replace(';', '&');
	}
    }


    /****   AudioTextEvent handling -- TLN 6/8/07 ***********/

    private class AudioTextHandler implements AudioTextListener {
        public void speakAudioText(jhave.event.AudioTextEvent e) {

	    if (Client.getAudioActive() && e.is_text_to_speech()) {
		// Create a synthesizer for US English
		try {
		    Synthesizer talker = null;

		    SynthesizerModeDesc desc = 
			new SynthesizerModeDesc(null, 
						"general",
						Locale.US,
						null,
						null);

		    FreeTTSEngineCentral central = new FreeTTSEngineCentral();
		    EngineList list = central.createEngineList(desc); 
            
		    if (list.size() > 0) { 
			EngineCreate creator = (EngineCreate) list.get(0); 
			talker = (Synthesizer) creator.createEngine(); 
		    } 

		    if (talker == null) {
			System.err.println("Cannot create synthesizer");
		    }
		    // Get it ready to speak
		    talker.allocate();
		    talker.resume();

		    /* Choose the voice.
		     */
		    desc = (SynthesizerModeDesc) talker.getEngineModeDesc();
		    Voice[] voices = desc.getVoices();
		    Voice voice = null;
		    for (int i = 0; i < voices.length; i++) {
			System.out.println(voices[i].getName());
			if (voices[i].getName().equals("kevin16")) {
			    voice = voices[i];
			    break;
			}
		    }
		    if (voice == null) {
			System.err.println(
					   "Synthesizer does not have a voice named kevin16.");
		    }

		    talker.getSynthesizerProperties().setVoice(voice);

		    //	    talker.speakPlainText("Java is Wicked Cool, tell all your friends! 45", null);
		    talker.speakPlainText( ((AudioTextEvent)e).getText(), null);

		    // Wait till speaking is done
		    talker.waitEngineState(Synthesizer.QUEUE_EMPTY);

		    // Clean up
		    talker.deallocate();

		} catch (IllegalArgumentException exc) {
		    exc.printStackTrace();
		} catch (EngineException exc) {
		    exc.printStackTrace();
		} catch (AudioException exc) {
		    exc.printStackTrace();
		} catch (EngineStateError exc) {
		    exc.printStackTrace();
		} catch (InterruptedException exc) {
		    exc.printStackTrace();
		} catch (JSMLException exc) {
		    exc.printStackTrace();
		} catch (NullPointerException exc) {
		    System.err.println("Cannot create synthesizer");
		    exc.printStackTrace();
		} catch (Exception exc) {
		    exc.printStackTrace();
		}
	    }
	    else if ( Client.getAudioActive() ) { // it must be an audio file, so need to stream it

		URI uri = e.getAudioResource();
		try {
		    if(uri.getScheme() == null || uri.getScheme().equalsIgnoreCase("rel")) {
			// URL url = new URL(BASE_ROOT + "doc/" + "script_name/" + uri.getSchemeSpecificPart());
			// page.setPage(url);
			ClientNetworkController control = 
			    ClientNetworkController.getInstance();

			String webroot = control.getClientProperties().getWebroot().toString();
			// If webroot goes beyond html_root, then we must have a path to doc for local script debug mode
			boolean webroot_for_local_file = !(webroot.endsWith("html_root") || webroot.endsWith("html_root/"));

			if(Client.getDebug() && control != null && webroot_for_local_file) {
                        
			    if(webroot.charAt(webroot.length() - 1) == '/') {
				webroot +=  
				    uri.getSchemeSpecificPart();
			    } else {
				webroot += "/" + 
				    uri.getSchemeSpecificPart();
			    }
      
			    try {
				URL url = new URL(webroot);
				playAudioFile(url);
				// Play the file here
			    } catch (MalformedURLException exc) {
				System.err.println(webroot + " is not a valid URL");
			    } catch (Exception exc) {
				System.err.println("Could not load " + webroot);
			    }
			}

			else if (control != null) {
			    String algoName = 
				control.getLastAlgorithm().GetAlgoName();
			    //                        String webroot = control.getClientProperties().getWebroot().toString();
                        
			    if(webroot.charAt(webroot.length() - 1) == '/') {
				webroot += "doc/" + Algorithm.GetAlgoNameMinusIngen(algoName) + "/" + 
				    uri.getSchemeSpecificPart();
			    } else {
				webroot += "/doc/" + Algorithm.GetAlgoNameMinusIngen(algoName) + "/" + 
				    uri.getSchemeSpecificPart();
			    }
      
			    try {
				URL url = new URL(webroot);
				playAudioFile(url);
				// Play the file here
			    } catch (MalformedURLException exc) {
				System.err.println(webroot + " is not a valid URL");
			    } catch (Exception exc) {
				System.err.println("Could not load " + webroot);
			    }
			}

		    } else if(uri.getScheme().equalsIgnoreCase("res")) {
			ClassLoader loader = getClass().getClassLoader();
			URL url = loader.getResource(uri.getSchemeSpecificPart());
			playAudioFile(url);
			// play it here -- page.setPage(url);
		    } else {
			// Turn it directly into a URL
			URL url = new URL(uri.toURL().toString());
			playAudioFile(url);
			// play it here -- page.setPage(new URL(convertPHPurl(uri.toURL().toString())));
		    }
		} catch (MalformedURLException exception) {
		    System.err.println("Malformed Resource: " + uri.toString());
		} catch (Exception exception) {
		    System.err.println("Error loading page: " + uri.toString());
		} // end try-catch block


	    } // end else-if

        } // end speakAudioText

    }


    /** Read sampled audio data from the specified URL and play it streamified*/
    public static void playAudioFile(URL url)
    {
        AudioInputStream ain = null;  // We read audio data from here
        SourceDataLine line = null;   // And write it here.

        try {
            // Get an audio input stream from the URL
            ain=AudioSystem.getAudioInputStream(url);

            // Get information about the format of the stream
            AudioFormat format = ain.getFormat( );
            DataLine.Info info=new DataLine.Info(SourceDataLine.class,format);

            // If the format is not supported directly (i.e. if it is not PCM
            // encoded), then try to transcode it to PCM.
            if (!AudioSystem.isLineSupported(info)) {
                // This is the PCM format we want to transcode to.
                // The parameters here are audio format details that you
                // shouldn't need to understand for casual use.
                AudioFormat pcm =
                    new AudioFormat(format.getSampleRate( ), 16,
                                    format.getChannels( ), true, false);

                // Get a wrapper stream around the input stream that does the
                // transcoding for us.
                ain = AudioSystem.getAudioInputStream(pcm, ain);

                // Update the format and info variables for the transcoded data
                format = ain.getFormat( ); 
                info = new DataLine.Info(SourceDataLine.class, format);
            }

            // Open the line through which we'll play the streaming audio.
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);  

            // Allocate a buffer for reading from the input stream and writing
            // to the line.  Make it large enough to hold 4k audio frames.
            // Note that the SourceDataLine also has its own internal buffer.
            int framesize = format.getFrameSize( );
            byte[  ] buffer = new byte[4 * 1024 * framesize]; // the buffer
            int numbytes = 0;                               // how many bytes

            // We haven't started the line yet.
            boolean started = false;

            for(;;) {  // We'll exit the loop when we reach the end of stream
                // First, read some bytes from the input stream.
                int bytesread=ain.read(buffer,numbytes,buffer.length-numbytes);
                // If there were no more bytes to read, we're done.
                if (bytesread == -1) break;
                numbytes += bytesread;
                
                // Now that we've got some audio data to write to the line,
                // start the line, so it will play that data as we write it.
                if (!started) {
                    line.start( );
                    started = true;
                }
                
                // We must write bytes to the line in an integer multiple of
                // the framesize.  So figure out how many bytes we'll write.
                int bytestowrite = (numbytes/framesize)*framesize;
                
                // Now write the bytes. The line will buffer them and play
                // them. This call will block until all bytes are written.
                line.write(buffer, 0, bytestowrite);
                
                // If we didn't have an integer multiple of the frame size, 
                // then copy the remaining bytes to the start of the buffer.
                int remaining = numbytes - bytestowrite;
                if (remaining > 0)
                    System.arraycopy(buffer,bytestowrite,buffer,0,remaining);
                numbytes = remaining;
            }

            // Now block until all buffered sound finishes playing.
            line.drain( );
        }
	catch (IOException e) {
	    System.out.println("Bad audio" + e.toString());
	}
	catch (UnsupportedAudioFileException e) {
	    System.out.println("Bad audio" + e.toString());
	}
	catch ( LineUnavailableException e) {
	    System.out.println("Bad audio" + e.toString());
	}
        finally { // Always relinquish the resources we use
	    try {
            if (line != null) line.close( );
            if (ain != null) ain.close( );
	    }
	    catch (IOException e) {
		System.out.println("Bad audio" + e.toString());
	    }
        }
    }


//     private void playAudioFile ( URL the_file ) {
// 	try {
//  	    AudioClip ac = Applet.newAudioClip(the_file);
// 	    System.out.println("trying to play " + the_file.toString());
//  	    ac.play();
// 	    System.out.println("played it");
//  	    ac.stop();
// 	} catch (Exception e) {
// 	    System.out.println(e);
// 	}
//     }


    /****   End AudioTextEvent handling -- TLN 6/8/07 ***********/




    /**
     *
     */
    private final boolean isControllable() {
        return (visualizer.getCapabilities() & Visualizer.CAP_CONTROLLABLE) == Visualizer.CAP_CONTROLLABLE;
    }
    
    /**
     *
     */
    private final boolean isPlaySupported() {
        return (visualizer.getCapabilities() & Visualizer.CAP_PLAY) == Visualizer.CAP_PLAY;
    }
    
    /**
     *
     */
    private final boolean isStopSupported() {
        return (visualizer.getCapabilities() & Visualizer.CAP_STOP) == Visualizer.CAP_STOP;
    }
    
    /**
     *
     */
    private final boolean isPauseSupported() {
        return (visualizer.getCapabilities() & Visualizer.CAP_PAUSE) == Visualizer.CAP_PAUSE;
    }
    
    /**
     *
     */
    private final boolean isStepBackwardSupported() {
        return (visualizer.getCapabilities() & Visualizer.CAP_STEP_BACKWARD) == Visualizer.CAP_STEP_BACKWARD;
    }
    
    /**
     *
     */
    private final boolean isStepForwardSupported() {
        return (visualizer.getCapabilities() & Visualizer.CAP_STEP_FORWARD) == Visualizer.CAP_STEP_FORWARD;
    }
    
    /**
     *
     */
    private final boolean isZoomSupported() {
        return (visualizer.getCapabilities() & Visualizer.CAP_ZOOM) == Visualizer.CAP_ZOOM;
    }
    
    /**
     *
     */
    private final boolean isGotoFrameSupported() {
        return (visualizer.getCapabilities() & Visualizer.CAP_GOTO_FRAME) == Visualizer.CAP_GOTO_FRAME;
    }
    

    private boolean isModelAnswerSupported() {
        return (visualizer.getCapabilities() & Visualizer.CAP_MODEL) == Visualizer.CAP_MODEL;
	}

    private boolean isFinishSupported() {
        return (visualizer.getCapabilities() & Visualizer.CAP_FINISH) == Visualizer.CAP_FINISH;
	}

    /**
     *
     */
    private class PlayCommand implements Runnable {
        private Visualizer visualizer;
        public PlayCommand(Visualizer vis) {
            visualizer = vis;
        }
        public void run() {
            setState(STATE_PLAY);
            visualizer.play();
            if(state == STATE_PLAY) {
                setState(STATE_PAUSED);
            }
        }
    }
    
    /**
     *
     */
    private class StepBackwardCommand implements Runnable {
        private Visualizer visualizer;
        public StepBackwardCommand(Visualizer vis) {
            visualizer = vis;
        }
        public void run() {
            setState(STATE_STEP);
            visualizer.stepBackward();
            if(state == STATE_STEP) {
                setState(STATE_PAUSED);
            }
        }
    }
    
    /**
     *
     */
    private class StepForwardCommand implements Runnable {
        private Visualizer visualizer;
        public StepForwardCommand(Visualizer vis) {
            visualizer = vis;
        }
        public void run() {
            setState(STATE_STEP);
            visualizer.stepForward();
            if(state == STATE_STEP) {
                setState(STATE_PAUSED);
            }
        }
    }
    
    /**
     *
     */
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        class FakeVisualizer extends Visualizer {
            Thread sleepThread = null;
            int frame = 0;
            JComponent render = null;
            public FakeVisualizer(java.io.InputStream s) throws java.io.IOException {
                super(s);
                
                int cap = CAP_CONTROLLABLE + Visualizer.CAP_GOTO_FRAME + Visualizer.CAP_PAUSE +
                Visualizer.CAP_PLAY + Visualizer.CAP_STEP_BACKWARD + Visualizer.CAP_STEP_FORWARD +
                Visualizer.CAP_STOP + Visualizer.CAP_ZOOM;
                
                setCapabilities(cap);
                
                render = getRenderPane();
            }
            public int getCurrentFrame() {
                return frame;
            }
            public int getFrameCount() {
                return 25;
            }
            public JComponent getRenderPane() {
                if(render == null) {
                    JPanel p = new JPanel() {
                        public void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            
                            Font f = g.getFont().deriveFont(72f);
                            g.setFont(f);
                            g.drawString(Integer.toString(frame + 1), 0, 100);
                        }
                    };
                    p.setPreferredSize(new Dimension(400, 400));
                    render = p;
                }
                return render;
            }
            public void play() {
                for(int i = getCurrentFrame(); i < getFrameCount() - 1; i++) {
                    try {
                        sleepThread = Thread.currentThread();
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        break;
                    } finally {
                        sleepThread = null;
                    }
                    frame++;
                    render.repaint();
                }
            }
            public void pause() {
                if(sleepThread != null) {
                    sleepThread.interrupt();
                }
            }
            public void stepBackward() {
                frame--;
                render.repaint();
                try {
                    Thread.sleep(1000);
                } catch (Exception e){}
            }
            public void stepForward() {
                frame++;
                render.repaint();
                try {
                    Thread.sleep(1000);
                } catch (Exception e){}
            }
            public void stop() {
                frame = 0;
                render.repaint();
            }
        }
        
        ControlPanel p = new ControlPanel(new FakeVisualizer(null));
        JFrame f = new JFrame("Test");
        f.setContentPane(p);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
