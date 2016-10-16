package com.klochkov.app;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.klochkov.app.queing.CompetingReceiver;
import com.klochkov.app.queing.FanoutExchangeSenderDemo;
import com.klochkov.app.queing.PublishSubscribeReceiverDemo;
import com.klochkov.app.queing.Sender;

import com.klochkov.app.imaging.Resizer;
import com.rabbitmq.client.AMQP;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class Main{

	public static final String RESIZE_QUEUE = "resize";
	public static final String UPLOAD_QUEUE = "upload";
	public static final String DONE_QUEUE = "done";
	public static final String FAILED_QUEUE = "failed";


	public static void sendData(String data){
		System.out.println("Sending data to event_queue");
		Sender sender = new Sender();
		sender.initialize();
		sender.send(RESIZE_QUEUE,data);
		sender.destroy();
	}

	public static void receive(){
		try {
			final CompetingReceiver receiver1 = new CompetingReceiver();
			receiver1.initialize();
			final CompetingReceiver receiver2 = new CompetingReceiver();
			receiver2.initialize();

			Thread t1 = new Thread(new Runnable() {
				public void run() {
					receiver1.receive();
				}
			});
			Thread t2 = new Thread(new Runnable() {
				public void run() {
					receiver2.receive();
				}
			});
			t1.start();
			t2.start();
			t1.join();
			t2.join();
			receiver1.destroy();
			receiver2.destroy();
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}
	}

	public void run(String[] args){
		if(commandNames.size() == 0){
			System.out.println("Uploader Bot");
			System.out.println("Usage:");
			System.out.println("  command [arguments]");
			System.out.println("Available commands:");
			System.out.println("  schedule Addfilenames to resize queue");
			System.out.println("  resize   Resize next images from the queue");
			System.out.println("  status   Output current status in format %queue%:%number_of_images%");
			System.out.println("  upload   Upload next images to remove storage ");
		}else if(commandNames.get(0).equals("schedule")) {
			System.out.println("Scheduler called");
		}else if(commandNames.get(0).equals("status")){
			status();
		}else if(commandNames.equals("send")){
			if(args.length>=2){
				sendData(args[1]);
			}else {
				sendData("Data to send");
			}
		}else if(commandNames.get(0).equals("receive")){
			receive();
		}else if(commandNames.get(0).equals("fanout") ){
			FanoutExchangeSenderDemo.execute();
		}else if(commandNames.get(0).equals("fanin")){
			PublishSubscribeReceiverDemo.execute();
		}else if(commandNames.get(0).equals("imager")){
			Resizer.process("tmp.jpg",640,640);
		}





	}


	private void schedule(){
		ArrayList<String> filesList = new ArrayList<String>();
		for(int i = 1; i < commandNames.size(); i++){
			filesList.add(commandNames.get(i));
		}
		//Initially I'm trying to use a point to point way
		Sender sender = new Sender();
		sender.initialize();
		for(String element: filesList){
			//TODO I have to add content of actual files, not some content like now
			sender.send(RESIZE_QUEUE,element);
		}
		sender.destroy();
	}

	private void resize(){
		//TODO I have to process number of iterations and receive all existing and exit if number of iterations greater than in queue
		try {
			final CompetingReceiver receiver1 = new CompetingReceiver("receive");
			receiver1.initialize();

			Thread t1 = new Thread(new Runnable() {
				public void run() {
					for(int i = 0; i < count; i++) {
						receiver1.receive();
					}
				}
			});
			t1.start();
			t1.join();
			receiver1.destroy();
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}
	}



	private void status(){
		Sender sender = new Sender();
		sender.initialize();

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

		sender.destroy();


	}


	@Parameter(description = "Command")
	private List<String> commandNames = new ArrayList<String>();

	@Parameter(names={"--number","-n"})
	private int count;

	@Parameter(names={"--help"})
	private boolean showHelp = false;


	public static void main(String[] args){
		Main main = new Main();
		JCommander jCommander = new JCommander(main,args);
		main.run(args);

	}



}
