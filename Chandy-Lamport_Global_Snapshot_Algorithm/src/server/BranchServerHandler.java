package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import shared.beans.ChannelState;
import shared.beans.SnapshotDetails;
import shared.logger.LogLevel;
import shared.logger.Logger;
import snapshotService.Branch;
import snapshotService.BranchID;
import snapshotService.LocalSnapshot;
import snapshotService.SystemException;
import snapshotService.TransferMessage;

/**
 * This class contains implementation of all operations related each branch.
 * 
 * @author anandkulkarni
 *
 */
public class BranchServerHandler implements Branch.Iface {

	private static int totalBranches = -1;
	private static BranchID localBranchInfo = null;
	private static int currentBalance = 0;
	private volatile static List<BranchID> otherBranchInfo = null;
	private volatile static Map<BranchID, Integer> messageIDSequence;
	private static Map<BranchID, Integer> lastSeenMessageId;
	private static final Lock messageSequencerLock = new ReentrantLock();
	private static final Lock balanceLock = new ReentrantLock();
	private static final Lock messageProcessorLock = new ReentrantLock();
	private static Map<Integer, SnapshotDetails> snapshotsMap = null;
	private static final Condition messageOutOfSeq = messageProcessorLock.newCondition();
	private volatile static boolean sendingMarkers = false;

	public BranchServerHandler(String ipAddrIn, int portIn, String branchNameIn) {
		localBranchInfo = new BranchID(branchNameIn, ipAddrIn, portIn);
		otherBranchInfo = new ArrayList<BranchID>();
		messageIDSequence = new HashMap<BranchID, Integer>();
		lastSeenMessageId = new HashMap<BranchID, Integer>();
		snapshotsMap = new HashMap<Integer, SnapshotDetails>();

	}

	/**
	 * This method is used to initialize all information related to a local
	 * branch.
	 * 
	 * @param balance
	 * @param allBranches
	 */
	private void setupBranch(int balance, List<BranchID> allBranches) {
		updateBalance(balance);
		totalBranches = allBranches.size();
		allBranches.remove(localBranchInfo);
		otherBranchInfo.addAll(allBranches);
		otherBranchInfo.stream().forEach(branch -> {
			lastSeenMessageId.put(branch, -1);
			messageIDSequence.put(branch, 0);
		});
	}

	/**
	 * This method is used to initialize branch information and to start a new
	 * thread to transfer money to other branches at regular intervals.
	 */
	@Override
	public void initBranch(int balance, List<BranchID> allBranches) throws SystemException, TException {
		Logger.writeMessage("Executing 'initBranch()' with parameters: [balance: " + balance + ", allBranches: "
				+ allBranches + "]", LogLevel.DEBUG);
		int initialBalance = balance;
		setupBranch(initialBalance, allBranches);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Random randomGenerator = new Random();
				int randomValue = 0;
				int amountToBeTransferred = 0;
				while (true) {
					try {
						BranchID destBranchInfo = otherBranchInfo.get(randomGenerator.nextInt(otherBranchInfo.size()));
						randomValue = randomGenerator.nextInt(5) + 1;
						amountToBeTransferred = (int) (((float) initialBalance * randomValue) / 100);
						Thread.sleep(randomValue * 1000);
						TTransport transport;
						transport = new TSocket(destBranchInfo.getIp(), destBranchInfo.getPort());
						transport.open();
						TProtocol protocol = new TBinaryProtocol(transport);
						Branch.Client client = new Branch.Client(protocol);
						TransferMessage transferMessage = new TransferMessage(localBranchInfo, amountToBeTransferred);
						if ((!sendingMarkers) && (updateBalance((-1) * amountToBeTransferred))) {
							Logger.writeMessage("Calling remote procedure Marker() with parameters: [message:"
									+ transferMessage + "]", LogLevel.INFO);
							client.transferMoney(transferMessage, getAndUpdateSequence(destBranchInfo));
							transport.close();
						}
					} catch (InterruptedException exception) {
						Logger.writeMessage("Error occured while scheduling a transaction.", LogLevel.ERROR);
						exception.printStackTrace();
						System.exit(1);
					} catch (TTransportException exception) {
						Logger.writeMessage("Error occured while scheduling a transaction.", LogLevel.ERROR);
						exception.printStackTrace();
						System.exit(1);
					} catch (SystemException exception) {
						Logger.writeMessage("Error occured while scheduling a transaction.", LogLevel.ERROR);
						exception.printStackTrace();
						System.exit(1);
					} catch (TException exception) {
						Logger.writeMessage("Error occured while scheduling a transaction.", LogLevel.ERROR);
						exception.printStackTrace();
						System.exit(1);
					}
				}
			}
		}).start();
	}

	/**
	 * This method updates the balance of branch only if updated value remaining
	 * >0. Otherwise it returns false indicating update call is not desired.
	 * 
	 * @param amount
	 *            amount to be added/removed from the balance.
	 * @return whether the update is desired.
	 */
	private boolean updateBalance(int amount) {
		balanceLock.lock();
		try {
			if ((currentBalance + amount) >= 0) {
				currentBalance += amount;
				Logger.writeMessage("Balance updated. Current balance: " + currentBalance, LogLevel.INFO);
				return true;
			}
		} catch (Exception exception) {
			Logger.writeMessage("Error occured while updating the balance.", LogLevel.ERROR);
			exception.printStackTrace();
			System.exit(1);
		} finally {
			balanceLock.unlock();
		}
		return false;
	}

	/**
	 * This method is used to generate next number in the sequence.
	 * 
	 * @param branch
	 *            branch for which next number is to be generated.
	 * @return next number in sequence.
	 */
	private int getAndUpdateSequence(BranchID branch) {
		messageSequencerLock.lock();
		try {
			int nextSequenceID = messageIDSequence.get(branch);
			messageIDSequence.put(branch, nextSequenceID + 1);
			return nextSequenceID;
		} catch (Exception exception) {
			Logger.writeMessage("Error occured while calculating sequence number.", LogLevel.ERROR);
			exception.printStackTrace();
			System.exit(1);
		} finally {
			messageSequencerLock.unlock();
		}
		return -1;
	}

	/**
	 * This method contains the logic to process money received from other
	 * branches.
	 */
	@Override
	public void transferMoney(TransferMessage message, int messageId) throws SystemException, TException {
		messageProcessorLock.lock();
		Logger.writeMessage("Executing 'transferMoney()' with parameters: [message: " + message + "]", LogLevel.DEBUG);
		while (messageId - lastSeenMessageId.get(message.getOrig_branchId()) > 1) {
			try {
				Logger.writeMessage("Waiting!!! [messageId: " + messageId + " lastseenmessageId:"
						+ lastSeenMessageId.get(message.getOrig_branchId()) + "]", LogLevel.DEBUG);
				messageOutOfSeq.await();
			} catch (InterruptedException exception) {
				Logger.writeMessage("A thread waiting in the 'transferMoney' process is interrupted.", LogLevel.DEBUG);
				exception.printStackTrace();
				System.exit(1);
			}
		}
		snapshotsMap.forEach((snapshotId, snapshotDetails) -> {
			if (snapshotDetails.getChannelStatusMap().get(message.getOrig_branchId().getName()).isMarkedEmtpy() == false
					&& snapshotDetails.getChannelStatusMap().get(message.getOrig_branchId().getName())
							.getLastSeenMessageId() < messageId) {
				int channelAmount = snapshotDetails.getChannelStatusMap().get(message.getOrig_branchId().getName())
						.getAmount();
				snapshotDetails.getChannelStatusMap().get(message.getOrig_branchId().getName())
						.setAmount(channelAmount + message.getAmount());
				snapshotsMap.put(snapshotId, snapshotDetails);
			}
		});
		updateBalance(message.getAmount());
		lastSeenMessageId.put(message.getOrig_branchId(), messageId);
		messageOutOfSeq.signalAll();
		messageProcessorLock.unlock();
	}

	/**
	 * This method contains the logic to initialize a snapshot by sending marker
	 * messages to other branches.
	 */
	@Override
	public void initSnapshot(int snapshotId) throws SystemException, TException {
		Logger.writeMessage("Executing 'initSnapshot()' with parameters: [snapshotId: " + snapshotId + "]",
				LogLevel.DEBUG);
		// checking for a duplicate snapshot ID
		if (snapshotsMap.get(snapshotId) == null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendingMarkers = true;
					LocalSnapshot localSnapshot = new LocalSnapshot(snapshotId, currentBalance,
							new ArrayList<Integer>());
					SnapshotDetails snapShotDetails = new SnapshotDetails(localBranchInfo, localSnapshot,
							otherBranchInfo, lastSeenMessageId);
					snapshotsMap.put(snapshotId, snapShotDetails);
					// send marker messages to all other branches.
					otherBranchInfo.stream().forEach(branch -> {
						TTransport transport = null;
						try {
							transport = new TSocket(branch.getIp(), branch.getPort());
							transport.open();
							TProtocol protocol = new TBinaryProtocol(transport);
							Branch.Client client = new Branch.Client(protocol);
							Logger.writeMessage("Calling remote procedure Marker() with parameters: [branch:"
									+ localBranchInfo + ", snapshotId: " + snapshotId + "]", LogLevel.INFO);
							client.Marker(localBranchInfo, snapshotId, getAndUpdateSequence(branch));
							transport.close();
						} catch (TException exception) {
							Logger.writeMessage("Error occured while sending a marker message.", LogLevel.ERROR);
							exception.printStackTrace();
							System.exit(1);
						}
					});
					sendingMarkers = false;
				}
			}).start();
		} else {
			// duplicate snapshot ID received.
			Logger.writeMessage("Duplicate snapshot ID [snapshotdId : " + snapshotId + " encountered.", LogLevel.ERROR);
		}
	}

	/**
	 * This method contains the logic to process marker messages received from
	 * other branches.
	 */
	@Override
	public void Marker(BranchID branchId, int snapshotId, int messageId) throws SystemException, TException {
		messageProcessorLock.lock();
		Logger.writeMessage("Executing 'Marker()' with parameters: [branchId: " + branchId + ", snapshotId: "
				+ snapshotId + ", messageId: " + messageId + "]", LogLevel.DEBUG);
		try {
			Thread.sleep(400);
			while (messageId - lastSeenMessageId.get(branchId) > 1) {
				try {
					Logger.writeMessage("Waiting!!! [messageId: " + messageId + " lastseenmessageId:"
							+ lastSeenMessageId.get(branchId) + "]", LogLevel.DEBUG);
					messageOutOfSeq.await();
				} catch (InterruptedException exception) {
					exception.printStackTrace();
				}
			}
			// checking whether marker message is received for the first time.
			if (snapshotsMap.get(snapshotId) == null) {
				// marker message received for the time.
				LocalSnapshot localSnapshot = new LocalSnapshot(snapshotId, currentBalance, new ArrayList<Integer>());
				SnapshotDetails snapshotDetails = new SnapshotDetails(localBranchInfo, localSnapshot, otherBranchInfo,
						lastSeenMessageId);
				snapshotDetails.getChannelStatusMap().get(branchId.getName()).setMarkedEmtpy(true);
				snapshotsMap.put(snapshotId, snapshotDetails);
				new Thread(new Runnable() {
					@Override
					public void run() {
						sendingMarkers = true;
						otherBranchInfo.stream().forEach(branch -> {
							TTransport transport = null;
							try {
								transport = new TSocket(branch.getIp(), branch.getPort());
								transport.open();
								TProtocol protocol = new TBinaryProtocol(transport);
								Branch.Client client = new Branch.Client(protocol);
								Logger.writeMessage("Calling remote procedure Marker() with parameters: [branch:"
										+ localBranchInfo + ", snapshotId: " + snapshotId + "]", LogLevel.INFO);
								client.Marker(localBranchInfo, snapshotId, getAndUpdateSequence(branch));
								transport.close();
							} catch (TException exception) {
								Logger.writeMessage("Error occured while sending a marker message.", LogLevel.ERROR);
								exception.printStackTrace();
								System.exit(1);
							}
						});
						sendingMarkers = false;
					}
				}).start();
			} else {
				// not the first time a marker message is received.
				Logger.writeMessage("Channel from branch : " + branchId + " is marked empty.", LogLevel.DEBUG);
				SnapshotDetails snapshotDetails = snapshotsMap.get(snapshotId);
				snapshotDetails.getChannelStatusMap().get(branchId.getName()).setMarkedEmtpy(true);
				snapshotsMap.put(snapshotId, snapshotDetails);
			}
			lastSeenMessageId.put(branchId, messageId);
			messageOutOfSeq.signalAll();
		} catch (InterruptedException exception) {
			Logger.writeMessage("A waiting thread is interrupted.", LogLevel.ERROR);
			exception.printStackTrace();
			System.exit(1);
		} finally {
			messageProcessorLock.unlock();
		}
	}

	/**
	 * This method contains the logic to retrieve a snapshot.
	 */
	@Override
	public LocalSnapshot retrieveSnapshot(int snapshotId) throws SystemException, TException {
		Logger.writeMessage("Executing 'retrieveSnapshot()' with parameters: [snapshotId: " + snapshotId + "]",
				LogLevel.DEBUG);
		SnapshotDetails snapshotDetails = snapshotsMap.get(snapshotId);
		LocalSnapshot localSnapshot = snapshotDetails.getLocalSnapshot();
		localSnapshot.getMessages().add(0);
		Map<String, ChannelState> channelStatusMap = snapshotDetails.getChannelStatusMap();
		String branchName = "branch";
		// populate messages in a channel.
		for (int count = 1; count <= totalBranches; count++) {
			ChannelState channelState = channelStatusMap.get(branchName + count);
			if (channelState == null) {
				localSnapshot.getMessages().add(0);
			} else {
				localSnapshot.getMessages().add(channelState.getAmount());
			}
		}
		snapshotsMap.remove(snapshotId);
		return localSnapshot;
	}
}
