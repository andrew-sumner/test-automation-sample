package nz.govt.msd.utils.data;

import java.util.ArrayList;
import java.util.List;

public class DataCleanupHelper {
	private List<DataCleanup> toClean = new ArrayList<DataCleanup>();

	public void register(DataCleanup toclean) {
		this.toClean.add(toclean);
	}

	public void cleanup() {
		for (DataCleanup data : toClean) {
			data.cleanup();
		}

		toClean.clear();
	}

	public boolean hasCleanupItems() {
		return toClean.size() > 0;
	}
}
