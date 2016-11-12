package controller.snapshot.taskSchedular;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import shared.logger.LogLevel;
import shared.logger.Logger;
import snapshotService.Branch;
import snapshotService.BranchID;
import snapshotService.LocalSnapshot;

/**
 * This class contains thread implementation to retrieve snapshot details at
 * regular intervals.
 * 
 * @author anandkulkarni
 *
 */
public class SnapshotRetriever extends Thread {

	private int snapshotId = 1;
	private List<BranchID> branchList = null;

	public SnapshotRetriever(List<BranchID> branchListIn) {
		branchList = branchListIn;
	}

	@Override
	public void run() {
		while (true) {
			try {
				// sleep for 35 seconds before retrieving a next snapshot.
				Thread.sleep(35000);
				String snapShotDetails = "SnapShot ID: " + snapshotId;
				String branchDetails = "";
				for (BranchID branch : branchList) {
					TTransport transport = null;
					TProtocol protocol = null;
					Branch.Client client = null;
					try (TSocket socket = new TSocket(branch.getIp(), branch.getPort())) {
						transport = socket;
						transport.open();
						protocol = new TBinaryProtocol(transport);
						client = new Branch.Client(protocol);
						Logger.writeMessage(
								"Calling remote procedure 'retrieveSnapshot()' with arguments: [snapshotId: "
										+ snapshotId + "]",
								LogLevel.INFO);
						LocalSnapshot localSnapshot = client.retrieveSnapshot(snapshotId);
						String branchName = "branch";
						branchDetails = System.lineSeparator() + branch.getName() + ": " + "balance: "
								+ localSnapshot.getBalance() + " | ";
						String channelDetails = "";
						for (int count = 1; count <= branchList.size(); count++) {
							if (!(branchName + count).equals(branch.getName())) {
								String channelState = branchName + count + "-" + branch.getName() + " = { "
										+ ((localSnapshot.getMessages().get(count) == 0) ? ""
												: localSnapshot.getMessages().get(count))
										+ " }, ";
								channelDetails = channelDetails + channelState;
							}
						}
						branchDetails = branchDetails + channelDetails.substring(0, channelDetails.length() - 2);
						snapShotDetails = snapShotDetails + branchDetails;
					} catch (TTransportException exception) {
						Logger.writeMessage("Error occured while retrieving branches.", LogLevel.DEBUG);
						exception.printStackTrace();
						System.exit(1);
					} catch (TException exception) {
						Logger.writeMessage("Error occured while retrieving branches.", LogLevel.DEBUG);
						exception.printStackTrace();
					}
				}
				Logger.writeMessage(snapShotDetails, LogLevel.OUTPUT);
				snapshotId += 1;
			} catch (InterruptedException exception) {
				Logger.writeMessage("Error occured while initializing a snapshot.", LogLevel.ERROR);
				exception.printStackTrace();
				System.exit(1);
			}
		}
	}

	public int getSnapshotId() {
		return snapshotId;
	}
}
