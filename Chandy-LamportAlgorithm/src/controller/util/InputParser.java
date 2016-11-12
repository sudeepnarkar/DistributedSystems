package controller.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import controller.FileReaderI;
import controller.beans.BranchDetails;
import snapshotService.BranchID;

public class InputParser {

	FileReaderI fileProcessor = null;

	private static final String BRANCH_DETAIL_PATTERN = "(.+) (.+) (.+)";

	public InputParser(FileReaderI fileProcessor) {
		super();
		this.fileProcessor = fileProcessor;
	}

	public BranchDetails parseInput(String[] args) {
		try {
			BranchDetails branchDetails = new BranchDetails();
			int initialAmount = Integer.parseInt(args[0]);
			branchDetails.setInitialAmount(initialAmount);
			BranchID branchID = null;
			String line = null;
			while ((line = fileProcessor.read()) != null) {
				Pattern branchDetailsPattern = Pattern.compile(BRANCH_DETAIL_PATTERN);
				Matcher matcher = branchDetailsPattern.matcher(line);
				matcher.find();
				int portNo = Integer.parseInt(matcher.group(3));
				branchID = new BranchID(matcher.group(1), matcher.group(2), portNo);
				branchDetails.getBranchInfoList().add(branchID);
			}
			return branchDetails;
		} catch (IllegalStateException exception) {
			System.out.println("Error occured while reading an input file. Please provide a correct input file.");
			exception.printStackTrace();
			System.exit(1);
		}
		return null;
	}
}
