import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    public double p_graph;
    public double p_malicious;
    public double p_tXDistribution;
    public int numRounds;

    public boolean[] followees;
    public Set<Transaction> pendingTransactions;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_tXDistribution = p_txDistribution;
        this.numRounds = numRounds;
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    public Set<Transaction> sendToFollowers() {
    	return this.pendingTransactions;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
         for(Candidate candidate : candidates) {
        	 this.pendingTransactions.add(candidate.tx);
         }
    }
}