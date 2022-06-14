package info.ballroomdancemusic.tail4e.parts;

import org.eclipse.swt.widgets.Event;

import java.awt.TextArea;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class Tail4eView {
	private Label myLabelInView;

	@Inject
	private UISynchronize sync;

	@PostConstruct
	public void createPartControl(Composite parent) {
		System.out.println("Enter in SampleE4View postConstruct");

		myLabelInView = new Label(parent, SWT.BORDER);
		myLabelInView.setText("This is a sample E4 view");
		CircularFifoQueue<String> fifo = new CircularFifoQueue<>(100);
		startTail(fifo);
		myLabelInView.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event event) {

				myLabelInView.setText(fifo.toString());
			}
		});
	}

	@Focus
	public void setFocus() {
		myLabelInView.setFocus();

	}

	/**
	 * This method manages the selection of your current object. In this example we
	 * listen to a single Object (even the ISelection already captured in E3 mode).
	 * <br/>
	 * You should change the parameter type of your received Object to manage your
	 * specific selection
	 * 
	 * @param o : the current object received
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object o) {

		// Test if label exists (inject methods are called before PostConstruct)
		if (myLabelInView != null)
			myLabelInView.setText("Current single selection class is : " + o.getClass());
	}

	/**
	 * This method manages the multiple selection of your current objects. <br/>
	 * You should change the parameter type of your array of Objects to manage your
	 * specific selection
	 * 
	 * @param o : the current array of objects received in case of multiple
	 *          selection
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object[] selectedObjects) {

		// Test if label exists (inject methods are called before PostConstruct)
		if (myLabelInView != null)
			myLabelInView.setText("This is a multiple selection of " + selectedObjects.length + " objects");
	}

	private void startTail(CircularFifoQueue<String> fifo) {
		Job job = Job.create("Monitor log file", (ICoreRunnable) monitor -> {
			TailerListener tailer = new TailerListener() {

				@Override
				public void init(Tailer tailer) {
				}

				@Override
				public void fileNotFound() {
					System.err.println("Did not find file");
				}

				@Override
				public void fileRotated() {
					// sync.asyncExec(() -> styledText.setText(""));
					fifo.clear();
					System.out.println("Rotated the file");
				}

				@Override
				public void handle(String line) {
					fifo.add(line + "\n");
					System.out.println("added " + line);
					sync.asyncExec(() -> {myLabelInView.notifyListeners(SWT.Verify, null);});
				}

				@Override
				public void handle(Exception ex) {
					ex.printStackTrace();

				}
			};
			Tailer tail = Tailer.create(new File("e:/logs/http4e"), tailer);
		});
		job.schedule();
	}

}
