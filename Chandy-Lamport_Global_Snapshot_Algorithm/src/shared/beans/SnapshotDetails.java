package shared.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import snapshotService.BranchID;
import snapshotService.LocalSnapshot;

/**
 * This class is used to store information about a snapshot.
 * 
 * @author anandkulkarni
 *
 */
public class SnapshotDetails {
	private BranchID localBranchInfo = null;
	private Map<String, ChannelState> channelStatusMap = null;
	private LocalSnapshot localSnapshot = null;

	public SnapshotDetails(BranchID currentBranchInfoIn, LocalSnapshot latestLocalSnapshotIn,
			List<BranchID> otherBranchInfoIn, Map<BranchID, Integer> lastSeenMessageIdIn) {
		super();
		localBranchInfo = currentBranchInfoIn;
		localSnapshot = latestLocalSnapshotIn;
		channelStatusMap = new HashMap<String, ChannelState>();
		initChannelState(otherBranchInfoIn, lastSeenMessageIdIn);
	}

	private void initChannelState(List<BranchID> otherBranchInfoIn, Map<BranchID, Integer> lastSeenMessageIdIn) {
		otherBranchInfoIn.stream().forEach(branch -> {
			channelStatusMap.put(branch.getName(), new ChannelState(lastSeenMessageIdIn.get(branch)));
		});
	}

	public BranchID getCurrentBranchInfo() {
		return localBranchInfo;
	}

	public void setCurrentBranchInfo(BranchID currentBranchInfo) {
		this.localBranchInfo = currentBranchInfo;
	}

	public BranchID getLocalBranchInfo() {
		return localBranchInfo;
	}

	public void setLocalBranchInfo(BranchID localBranchInfo) {
		this.localBranchInfo = localBranchInfo;
	}

	public Map<String, ChannelState> getChannelStatusMap() {
		return channelStatusMap;
	}

	public void setChannelStatusMap(Map<String, ChannelState> channelStatusMap) {
		this.channelStatusMap = channelStatusMap;
	}

	public LocalSnapshot getLocalSnapshot() {
		return localSnapshot;
	}

	public void setLocalSnapshot(LocalSnapshot localSnapshot) {
		this.localSnapshot = localSnapshot;
	}

	@Override
	public String toString() {
		return "SnapshotDetails [localBranchInfo=" + localBranchInfo + ", channelStatusMap=" + channelStatusMap
				+ ", localSnapshot=" + localSnapshot + "]";
	}
}