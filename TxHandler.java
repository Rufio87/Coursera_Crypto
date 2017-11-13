import java.util.ArrayList;

public class TxHandler {
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
    	this.utxoPool = new UTXOPool(utxoPool);
    }
    
    private UTXOPool utxoPool;

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        ArrayList<Transaction.Input> inputs = tx.getInputs();
        ArrayList<Transaction.Output> outputs = tx.getOutputs();
        
        UTXO utxo;

        //(1)
        for(Transaction.Input in : inputs) {
            utxo = new UTXO(in.prevTxHash, in.outputIndex);
            if(!utxoPool.contains(utxo)) {
            	return false;
            }
        }
        
        //(2)
        int i=0;
        for(Transaction.Input in : inputs) {
        	utxo = new UTXO(in.prevTxHash, in.outputIndex);
        	if(!Crypto.verifySignature(utxoPool.getTxOutput(utxo).address, tx.getRawDataToSign(i), in.signature)) {
        		return false;
        	}
        	i++;
        }
        
        //(3)
        UTXOPool uniqueUtxo = new UTXOPool();   
        for(Transaction.Input in : inputs) {
        	utxo = new UTXO(in.prevTxHash, in.outputIndex);
        	if(uniqueUtxo.contains(utxo)) {
        		return false;
        	}
        	uniqueUtxo.addUTXO(utxo, utxoPool.getTxOutput(utxo));
        }
        
        //(4)
        for(Transaction.Output out : outputs) {
        	if(out.value < 0.0) {
        		return false;
        	}
    	}
        
        //(5)
        double outValue = 0.0;
        double inValue = 0.0;
        
        for(Transaction.Input in : inputs) {
        	utxo = new UTXO(in.prevTxHash, in.outputIndex);
        	
        	inValue = inValue + this.utxoPool.getTxOutput(utxo).value;
        }
        
        for(Transaction.Output out : outputs) {
        	outValue = outValue + out.value;
        }
        
        if(inValue < outValue) {
        	return false;
        }
        
        return true;
        
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
    	ArrayList<Transaction> validTxs = new ArrayList<Transaction>();
    	UTXO utxo;
    	
    	for(Transaction tx : possibleTxs) {
    		if(isValidTx(tx)) {
    			validTxs.add(tx);
    			for(Transaction.Input in : tx.getInputs()) {
    				utxo = new UTXO(in.prevTxHash, in.outputIndex);
    				this.utxoPool.removeUTXO(utxo);
    			}
    			int i = 0;
    			for(Transaction.Output out : tx.getOutputs()) {
    				utxo = new UTXO(tx.getHash(), i);
    				this.utxoPool.addUTXO(utxo, out);
    				i++;
    			}
    		}
    	}
    	
        Transaction[] validTxArray = new Transaction[validTxs.size()];
    	return validTxs.toArray(validTxArray);
    }

}
