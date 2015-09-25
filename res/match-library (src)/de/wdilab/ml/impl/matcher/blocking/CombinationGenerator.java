package de.wdilab.ml.impl.matcher.blocking;

/**
 * see also package de.uni_leipzig.dbs.blocking.qgram_indexing
 * author: Lars Kolb
 */
import java.math.BigInteger;


public class CombinationGenerator
{

	private int[] a;
	private int n;
	private int r;
	private BigInteger numLeft;
	private BigInteger total;


	public CombinationGenerator (int n, int r)
	{
		if(r>n)
			throw new IllegalArgumentException("n>=r required");

		if(n<1)
			throw new IllegalArgumentException("n>=1 required");

		this.n= n;
		this.r= r;
		a= new int[r];

		total= getBinomial(n, r);
		reset();
	}

	public void reset()
	{
		for(int i=0; i<a.length; i++)
			a[i]= i;

		numLeft= total;
	}


	public BigInteger getNumLeft()
	{
		return numLeft;
	}

	public boolean hasMore()
	{
		return numLeft.compareTo(BigInteger.ZERO)==1;
	}

	public BigInteger getTotal()
	{
		return total;
	}

	public int[] getNext()
	{
		if(numLeft==total)
		{
			numLeft= numLeft.subtract (BigInteger.ONE);
			return a;
		}

		int i= r-1;
		while(a[i]==n-r+i)
			i--;

		a[i]= a[i]+1;

		for(int j= i+1; j<r; j++)
			a[j]= a[i]+j-i;

		numLeft= numLeft.subtract(BigInteger.ONE);
		return a;
	}

	private BigInteger getBinomial(int n, int k)
	{
		if(k==0)
			return BigInteger.ONE;

		if(2*k > n)
			return getBinomial(n, n-k);

		BigInteger result= new BigInteger(String.valueOf(n));
		for(int i=2; i<=k; i++)
		{
			result= result.multiply(new BigInteger(String.valueOf(n-i+1)));
			result= result.divide(new BigInteger(String.valueOf(i)));
		}

		return result;
	}
}