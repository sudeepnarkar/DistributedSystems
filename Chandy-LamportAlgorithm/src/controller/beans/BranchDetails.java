package controller.beans;

import java.util.ArrayList;
import java.util.List;

import snapshotService.BranchID;

public class BranchDetails {

	private int initialAmount;

	private List<BranchID> branchInfoList = null;

	public BranchDetails() {
		branchInfoList = new ArrayList<BranchID>();
	}

	public int getInitialAmount() {
		return initialAmount;
	}

	public void setInitialAmount(int initialAmount) {
		this.initialAmount = initialAmount;
	}

	public List<BranchID> getBranchInfoList() {
		return branchInfoList;
	}

	public void setBranchInfoList(List<BranchID> branchInfoList) {
		this.branchInfoList = branchInfoList;
	}

	@Override
	public String toString() {
		return "BranchDetails [initialAmount=" + initialAmount + ", branchInfoList=" + branchInfoList + "]";
	}
}
