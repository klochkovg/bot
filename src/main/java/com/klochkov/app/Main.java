package com.klochkov.app;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.klochkov.app.config.Config;
import com.klochkov.app.queing.CompetingReceiver;


import com.klochkov.app.queing.Sender;

import com.klochkov.app.imaging.Resizer;
import com.klochkov.app.upload.Uploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main{
	private final static Logger LOGGER = LoggerFactory.getLogger(Sender.class);
	public static final String RESIZE_QUEUE = "resize";
	public static final String UPLOAD_QUEUE = "upload";
	public static final String DONE_QUEUE = "done";
	public static final String FAILED_QUEUE = "failed";


	public void sendFailed(String data){
		sender.send(FAILED_QUEUE,data);

	}

	public void sendDone(String data){
		sender.send(DONE_QUEUE,data);
	}


	public void sendUpload(String data){
		sender.send(UPLOAD_QUEUE,data);
	}


	public void sendResize(String data){
		sender.send(RESIZE_QUEUE,data);
	}

	private void printHelp(){
		System.out.println("Uploader Bot");
		System.out.println("Usage:");
		System.out.println("  command [arguments]");
		System.out.println("Available commands:");
		System.out.println("  schedule Addfilenames to resize queue");
		System.out.println("  resize   Resize next images from the queue");
		System.out.println("  status   Output current status in format %queue%:%number_of_images%");
		System.out.println("  upload   Upload next images to remove storage ");
	}
	Sender sender = null;

	public void run(String[] args){
		File configFile = new File("config.json");
		if(configFile.exists()){
			Config.parse(configFile.getAbsolutePath());
		}

		sender = new Sender();
		sender.initialize();
		if(commandNames.size() == 0){
			printHelp();
		}else if(commandNames.get(0).equals("status")) {
			status();
		}else if(commandNames.get(0).equals("schedule")){
			schedule();
		}else if(commandNames.get(0).equals("resize")){
			resize(Config.getResizeDirectoryName());
		}else if(commandNames.get(0).equals("upload")){
			upload();
		}
		sender.destroy();
	}


	private void schedule(){
		if(commandNames.size() < 2) {
			LOGGER.error("Directory name wasn't provided.");
			return;
		}
		String pathToDirectoy = commandNames.get(1);
		File dir = new File(pathToDirectoy);
		if(!(dir.exists() || dir.isDirectory())){
			LOGGER.error("Images directory doesn't exist");
			return;
		}
		File[] files = dir.listFiles();
		if(files != null) {
			for (File file : files) {
				if (!file.getName().startsWith(".")) {
					sendResize(file.getAbsolutePath());
				}
			}
		}
	}

	private void resize(String destination){
		CompetingReceiver receiver1 = new CompetingReceiver(RESIZE_QUEUE);
		receiver1.initialize();
		File destinationDir = new File(destination);
		if(!destinationDir.exists()){
			destinationDir.mkdir();
		}
		if(destinationDir.exists() && destinationDir.isDirectory()) {
			int leftMessages = sender.getQueueStatus(RESIZE_QUEUE);
			int packageSize = leftMessages>count?count:leftMessages;
			for (int i = 0; i < packageSize; i++) {
				String element = receiver1.receive();
				File ef = new File(element);
				if(ef.exists()) {
					String destFileName = destinationDir + File.separator + ef.getName();
					Resizer.process(element, destFileName, 640, 640);
					sendUpload((new File(destFileName)).getAbsolutePath());
					ef.delete();
				}else{
					sendFailed(element);
				}
			}
		}else{
			System.out.println("Couldn't get access to the destination directory");
		}
		receiver1.destroy();
	}

	private void upload(){

		final CompetingReceiver receiver1 = new CompetingReceiver(UPLOAD_QUEUE);
		receiver1.initialize();
		int leftMessages = sender.getQueueStatus(UPLOAD_QUEUE);
		int packageSize = leftMessages>count?count:leftMessages;
		Uploader.initialize();
		for (int i = 0; i < packageSize; i++) {
			String element = receiver1.receive();
			File file = new File(element);
			if(file.exists()) {
				Uploader.uploadTest(element, file.getName());
				file.delete();
				sendDone(element);
			}else{
				sendFailed(element);
			}

		}
		receiver1.destroy();
	}

	private void status(){
		int resizeMessage = sender.getQueueStatus(RESIZE_QUEUE);
		int uploadMessage = sender.getQueueStatus(UPLOAD_QUEUE);
		int doneMessage = sender.getQueueStatus(DONE_QUEUE);
		int failedMessage = sender.getQueueStatus(FAILED_QUEUE);

		System.out.println("Images Processor Bot");
		System.out.println("Queue   Count");
		System.out.println("resize  " + resizeMessage);
		System.out.println("upload  " + uploadMessage);
		System.out.println("done    " + doneMessage);
		System.out.println("failed  " + failedMessage);

	}


	@Parameter(description = "Command")
	private List<String> commandNames = new ArrayList<String>();

	@Parameter(names={"--number","-n"})
	private int count = 20;

	@Parameter(names={"--help"})
	private boolean showHelp = false;


	public static void main(String[] args){
		Main main = new Main();
		JCommander jCommander = new JCommander(main,args);
		main.run(args);

	}



}
