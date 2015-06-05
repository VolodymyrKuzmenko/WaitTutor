import java.lang.invoke.VolatileCallSite;

import org.junit.Test;

/**
 *  ак сделать так, чтобы потоки вызывались по очереди?
 * 
 * „асто необходимо упор€дочить потоки, т.к. результат одного потока
 * понадобитс€ другому, и нужно дождатьс€, когда первый поток сделает свою работу.
 * 
 * «адача: добавьте еще один поток, который будет выводить в лог сообщени€ о 
 * 	значени€х счетчика, кратных 10, например 10, 20, 30...
 * ѕри этом такие сообщени€ должны выводитьс€ после того, как все потоки преодолели
 * кратность 10, но до того, как какой-либо поток двинулс€ дальше.
 */
public class WaitTutor {
	Thread t1, t2, t3;
	Object monitor = new Object();
	int runningThreadNumber = 1;
	volatile int t1Counter = 0, t2Counter = 0, t3Counter=0;
	//int maxCounter = 0;
 
	class TestThread implements Runnable {
		String threadName;
		int n;
		
		public TestThread(String threadName, int n) {
			this.threadName = threadName;
			this.n = n;
		}
		
		@Override
		public void run() {
			for (int i=1;i<100;i++) {
				System.out.println(threadName+":"+i);
				synchronized(monitor) {
					if (n==1) t1Counter = i;
					if (n==2) t2Counter = i;
					if (n==3) t3Counter = i;
					monitor.notify();
					Thread.yield();
					try {
						if (n==1) {
							if (i%10==0){	
								monitor.wait();
							}
							
							if (i>t3Counter) {
								monitor.wait();
							}
							
						}
						if (n==2) {
							if (i%10==0){
								monitor.wait();
							}
							
							if (i>t3Counter) {
								monitor.wait();
							}
							
						}
						
						if(n==3){
							if (i>t1Counter | i> t2Counter) {
								monitor.wait();
							}
							
							if (i%10==0){
							
								System.out.println("t3 ==== "+ t3Counter);
								monitor.notifyAll();
							}
							
						}
						
						if(n!=3){
							if(t1Counter%10 !=0){
								
							}
						}
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Thread.yield();
			}
		}
	}
	
	@Test
	public void testThread() {
		t1 = new Thread(new TestThread("t1", 1));
		t2 = new Thread(new TestThread("t2", 2));
		t3 = new Thread(new TestThread("t3", 3));
	    System.out.println("Starting threads");
		t1.start();
		t2.start();
		t3.start();
 
		System.out.println("Waiting for threads");
	    try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}