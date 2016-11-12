package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import controller.beans.BranchDetails;
import controller.io.SimpleFileReader;
import controller.snapshot.taskSchedular.SnapshotInitiator;
import controller.snapshot.taskSchedular.SnapshotRetriever;
import controller.util.InputParser;
import shared.logger.LogLevel;
import shared.logger.Logger;
import snapshotService.Branch;

/**
 * This class contains the logic to initialize all branches and then take
 * snapshots at regular intervals.
 * 
 * @author anandkulkarni
 *
 */
public class Controller {

	public static void main(String[] args) {
		Controller controller = new Controller();
		controller.validateInputArgs(args);
		FileReaderI fileProcessor = new SimpleFileReader(args[1]);
		InputParser inputParser = new InputParser(fileProcessor);
		BranchDetails branchDetails = inputParser.parseInput(args);
		controller.initializeBranches(branchDetails);
		try {
			SnapshotRetriever snapshotRetriever = new SnapshotRetriever(branchDetails.getBranchInfoList());
			SnapshotInitiator snapshotInitiator = new SnapshotInitiator(branchDetails.getBranchInfoList());
			snapshotRetriever.start();
			snapshotInitiator.start();
			snapshotInitiator.join();
			snapshotRetriever.join();
		} catch (InterruptedException exception) {
			Logger.writeMessage("Error occured while scheduling a snapshots.", LogLevel.ERROR);
			exception.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * This method make remote calls to 'initBranch()' to each of the branches
	 * read from the remote file.
	 * 
	 * @param branchDetails
	 *            details about all branches.
	 */
	private void initializeBranches(BranchDetails branchDetails) {
		int balance = branchDetails.getInitialAmount() / branchDetails.getBranchInfoList().size();
		branchDetails.getBranchInfoList().stream().forEach(branch -> {
			TTransport transport = null;
			TProtocol protocol = null;
			Branch.Client client = null;
			try (TSocket socket = new TSocket(branch.getIp(), branch.getPort())) {
				transport = socket;
				transport.open();
				protocol = new TBinaryProtocol(transport);
				client = new Branch.Client(protocol);
				Logger.writeMessage("Calling remote procedure 'initBranch()' with arguments: [balance: " + balance
						+ ", allBranches: " + branchDetails.getBranchInfoList() + "]", LogLevel.INFO);
				client.initBranch(balance, branchDetails.getBranchInfoList());
				transport.close();
			} catch (TTransportException exception) {
				Logger.writeMessage("Error occured while initializing branches.", LogLevel.ERROR);
				exception.printStackTrace();
				System.exit(1);
			} catch (TException exception) {
				Logger.writeMessage("Error occured while initializing branches.", LogLevel.ERROR);
				exception.printStackTrace();
				System.exit(1);
			}
		});
	}

	/**
	 * This method validates the input arguments.
	 * 
	 * @param args
	 *            input arguments read from the command line.
	 */
	private void validateInputArgs(String[] args) {
		try {
			if (args.length != 2) {
				Logger.writeMessage("Please enter valid number of arguments.", LogLevel.ERROR);
				System.exit(1);
			}
			int initialAmount = Integer.parseInt(args[0]);
			if (initialAmount <= 0) {
				throw new NumberFormatException(" For input value: " + initialAmount);
			}
			File file = new File(args[1]);
			if (file.isFile() && file.canRead()) {
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
				} catch (IOException exception) {
					Logger.writeMessage("Invalid input file. Please provide a valid input file.", LogLevel.ERROR);
					System.exit(1);
				}
			} else {
				Logger.writeMessage("Please provide a valid file.", LogLevel.ERROR);
				System.exit(1);
			}
		} catch (NumberFormatException exception) {
			Logger.writeMessage("Initial Amount must be a positive integer value. Please enter a valid input.",
					LogLevel.ERROR);
			System.exit(1);
		}
	}
}