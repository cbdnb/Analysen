package baumann.skurriles.jhr2025;

import java.io.IOException;

public class Controller {

	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {

		new ThreadAU().start();
		new ThreadSW().start();

	}

	public static class ThreadSW extends Thread {
		@Override
		public void run() {
			try {
				NewComerSW.main(null);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static class ThreadAU extends Thread {
		@Override
		public void run() {
			try {
				NewComerAutor.main(null);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
