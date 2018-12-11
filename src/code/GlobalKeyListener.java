package code;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {
	public int lastKey = 0;

	public void nativeKeyPressed(NativeKeyEvent e) {  }
	public void nativeKeyReleased(NativeKeyEvent e) { lastKey = e.getKeyCode(); }

	public void nativeKeyTyped(NativeKeyEvent e) {  }

	public static GlobalKeyListener startup() {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// Change the level for all handlers attached to the default logger.
		Handler[] handlers = Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(Level.OFF);
		}
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("Could not start key listener");
			System.err.println(ex.getMessage());

			System.exit(1);
		}
		GlobalKeyListener gkl = new GlobalKeyListener();
		GlobalScreen.addNativeKeyListener(gkl);
		return gkl;
	}

	public static void stop() {
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}