package org.mylearning.notification.handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolTest {

	static StringBuffer sb = new StringBuffer();

	public static void main(String[] args) {

		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println(cores);
		// ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 1, 3L, TimeUnit.SECONDS,
		// new ArrayBlockingQueue<>(1));

		ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 1, 3L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1),
				new ThreadFactory() {
					private AtomicInteger count = new AtomicInteger();

					@Override
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						thread.setName("NotificationEventHandler - " + count.incrementAndGet());
						thread.setDaemon(true);
						return thread;
					}
				});

		// tpe.setRejectedExecutionHandler(new CustomerRejectionHandler());
		tpe.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

		for (int i = 10000; i > 0; i--)
			tpe.submit(new Task(i));

		 Runtime.getRuntime().addShutdownHook(new Thread() 
		    { 
		      public void run() 
		      { 
		        System.out.println("Shutdown Hook is running !"); 
		        tpe.shutdown();
		      } 
		    }); 
		
		try (FileWriter fw = new FileWriter(new File("logFile.out"))) {
			fw.write(sb.toString());
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Application Terminating ..."); 
	}
}

class CustomerRejectionHandler implements RejectedExecutionHandler {

	@Override
	public void rejectedExecution(Runnable arg0, ThreadPoolExecutor arg1) {
		System.out.println("Nothing gets rejected");
		FutureTask<Boolean> task = (FutureTask<Boolean>) arg0;
		// Object data = task.get(3L, TimeUnit.SECONDS);
		// System.out.println("Printing the rejected one: " + task.data);
	}

}

class Task implements Callable<Boolean> {

	int data;

	public Task(int i) {
		this.data = i;
	}

	@Override
	public Boolean call() throws Exception {
		String str = Thread.currentThread().getName() + " HELLO WORLD: " + data;
		ThreadPoolTest.sb.append("\n").append(str);
		// System.out.println("HELLO WORLD: " + data);
		return true;
	}

}