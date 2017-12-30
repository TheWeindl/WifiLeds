package ledServer;

// This class represents one led strip with its three color values in RGB
public class LedStrip {

	private int mRed;
	private int mBlue;
	private int mGreen;
	private int mStatus;

	public LedStrip() {
		mRed = 255;
		mGreen = 0;
		mBlue = 0;
		mStatus = 0;

	}

	// Sets the given values for the led strip if valid
	synchronized void setStripColor(int red, int green, int blue) {

		if (red >= 0 && red < 255) {
			mRed = red;
		}
		if (blue >= 0 && blue < 255) {
			mBlue = blue;
		}
		if (green >= 0 && green < 255) {
			mGreen = green;
		}
	}

	synchronized int getRed() {
		return mRed;
	}

	synchronized int getGreen() {
		return mGreen;
	}

	synchronized int getBlue() {
		return mBlue;
	}

	synchronized int getStatus() {
		return mStatus;
	}

	synchronized void setStatus(int status) {
		mStatus = status;
	}

}
