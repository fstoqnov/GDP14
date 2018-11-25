package code;

//Class temporarily storing basic metrics from a test.
public class CheckResults {
	public Boolean overallPass;
	public Integer totalPassed;
	public Integer totalFailed;
	
	public CheckResults() {
		this.overallPass = true;
		this.totalPassed = 0;
		this.totalFailed = 0;
	}
	
	public void insertResult(boolean newTest) {
		this.overallPass = this.overallPass && newTest;
		if (newTest) {
			this.totalPassed++;
		}
		else {
			this.totalFailed++;
		}
	}
}
