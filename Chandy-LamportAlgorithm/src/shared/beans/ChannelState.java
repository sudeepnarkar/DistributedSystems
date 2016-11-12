package shared.beans;

/**
 * This class is used to store information about channel.
 * 
 * @author anandkulkarni
 *
 */
public class ChannelState {
	private boolean isMarkedEmtpy = false;
	private int lastSeenMessageId = -1;
	private int amount = 0;

	public ChannelState(int lastSeenMessageIdIn) {
		lastSeenMessageId = lastSeenMessageIdIn;
		isMarkedEmtpy = false;
	}

	public ChannelState(int lastSeenMessageIdIn, boolean isMarkedEmptyIn) {
		lastSeenMessageId = lastSeenMessageIdIn;
		isMarkedEmtpy = isMarkedEmptyIn;
	}

	public ChannelState() {
		isMarkedEmtpy = false;
	}

	public int getLastSeenMessageId() {
		return lastSeenMessageId;
	}

	public void setLastSeenMessageId(int lastSeenMessageId) {
		this.lastSeenMessageId = lastSeenMessageId;
	}

	public boolean isMarkedEmtpy() {
		return isMarkedEmtpy;
	}

	public void setMarkedEmtpy(boolean isMarkedEmtpy) {
		this.isMarkedEmtpy = isMarkedEmtpy;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "ChannelState [isMarkedEmtpy=" + isMarkedEmtpy + ", lastSeenMessageId=" + lastSeenMessageId + "]";
	}
}
