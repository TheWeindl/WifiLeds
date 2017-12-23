package ledServer;

// This class represents one led strip with its three color values in RGB
public class LedStrip {

	private int mRed;
	private int mBlue;
	private int mGreen;
	private int mStatus;

	public LedStrip() {
		mRed = 255;
		mGreen = 255;
		mBlue = 255;
		mStatus = 0;

	}

	// Sets the given values for the led strip if valid
	void setStripColor(int red, int green, int blue) {

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

	int getRed() {
		return mRed;
	}

	int getGreen() {
		return mGreen;
	}

	int getBlue() {
		return mBlue;
	}

	int getStatus() {
		return mStatus;
	}

	void setStatus(int status) {
		mStatus = status;
	}

}
