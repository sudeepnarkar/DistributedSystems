package controller.snapshot.taskSchedular;

import java.util.List;
import java.util.Random;

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

/**
 * This class contains thread implementation to initiate a new snapshot at
 * regular intervals.
 * 
 * @author anandkulkarni
 *
 */
public class SnapshotInitiator extends Thread {

	private int snapshotId = 1;
	private List<BranchID> branchList = null;

	public SnapshotInitiator(List<BranchID> branchListIn) {
		branchList = branchListIn;
	}

	@Override
	public void run() {
		while (true) {
			try {
				// sleep 15 seconds before initializing another snapshot.
				Thread.sleep(15000);
				BranchID branchToInitiate = branchList.get(new Random().nextInt(branchList.size()));
				TTransport transport = new TSocket(branchToInitiate.getIp(), branchToInitiate.getPort());
				transport.open();
				TProtocol protocol = new TBinaryProtocol(transport);
				Branch.Client client = new Branch.Client(protocol);
				Logger.writeMessage(
						"Calling remote procedure 'initSnapshot()' with arguments: [snapshotId: " + snapshotId + "]",
						LogLevel.INFO);
				client.initSnapshot(snapshotId);
				snapshotId += 1;
				transport.close();
			} catch (TTransportException exception) {
				Logger.writeMessage("Error occured while initiating  a snapshot.", LogLevel.ERROR);
				exception.printStackTrace();
				System.exit(1);
			} catch (TException exception) {
				Logger.writeMessage("Error occured while initiating  a snapshot.", LogLevel.ERROR);
				exception.printStackTrace();
				System.exit(1);
			} catch (InterruptedException exception) {
				Logger.writeMessage("Error occured while initiating  a snapshot.", LogLevel.ERROR);
				exception.printStackTrace();
				System.exit(1);
			}
		}
	}

	public int getSnapshotId() {
		return snapshotId;
	}
}
