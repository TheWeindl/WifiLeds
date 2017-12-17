package servers;

import java.util.ArrayList;

public class Strips {

	private ArrayList<LedStrip> mStrips;

	public Strips() {
		mStrips = new ArrayList<LedStrip>();
		for (int i = 0; i < 3; i++) {
			mStrips.add(new LedStrip());
		}
	}

	void setStripColor(int red, int green, int blue, int strip) {

		mStrips.trimToSize();
		if (mStrips.size() >= strip) {
				mStrips.get(strip).setStripColor(red, green, blue);
		}
	}

	int getRed(int strip) {
		return mStrips.get(strip).getRed();
	}

	int getGreen(int strip) {
		return mStrips.get(strip).getGreen();
	}

	int getBlue(int strip) {
		return mStrips.get(strip).getBlue();
	}

	int getStatus(int strip) {
		return mStrips.get(strip).getStatus();
	}

	void setStatus(int status, int strip) {
		mStrips.get(strip).setStatus(status);
	}
	
	int getLength()
	{
		mStrips.trimToSize();
		return mStrips.size();
	}

}
