import org.junit.Test;

/**
 *  ак сделать так, чтобы потоки вызывались по очереди?
 * 
 * „асто необходимо упор€дочить потоки, т.к. результат одного потока понадобитс€
 * другому, и нужно дождатьс€, когда первый поток сделает свою работу.
 * 
 * «адача: добавьте еще один поток, который будет выводить в лог сообщени€ о
 * значени€х счетчика, кратных 10, например 10, 20, 30... ѕри этом такие
 * сообщени€ должны выводитьс€ после того, как все потоки преодолели кратность
 * 10, но до того, как какой-либо поток двинулс€ дальше.
 */
public class WaitTutorVer2 {
	Thread t1, t2, t3;
	Object monitor = new Object();
	
	int t1Counter = 0, t2Counter = 0, t3Counter = 0;

	int flag = 0;

	void barrier(int counter, int tId) {
		synchronized (monitor) {
			try {
				switch (tId) {
				case 1:
					t1Counter = counter;
					break;
				case 2:
					t2Counter = counter;
					break;
				case 3:
					t3Counter = counter;
					break;
				}

				if (flag < 2) {
					flag++;
					monitor.wait();
				} else {
					flag = 0;
					System.out.println("t3: ====== " + t3Counter);
					monitor.notifyAll();
					Thread.yield();
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	class TestThread implements Runnable {
		String threadName;
		int tId;

		public TestThread(String threadName, int n) {
			this.threadName = threadName;
			this.tId = n;
		}

		@Override
		public void run() {
			for (int counter = 1; counter < 100; counter++) {
				System.out.println(threadName + ":" + counter);
				Thread.yield();
				if (counter % 10 == 0) {
					barrier(counter, tId);

				}

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