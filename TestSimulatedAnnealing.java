import java.util.*;
import java.io.*;
public class TestSimulatedAnnealing
{
	public static void main(String[] args) throws IOException
	{
		long start = System.currentTimeMillis();

		SimulatedAnnealing.ReadInput(args[0]);
		SimulatedAnnealing.SimulateAnnealing();

		SimulatedAnnealing.PrintBestRankingList();

		long end = System.currentTimeMillis();


		System.out.println("Execution Time: "+(end-start)+ " ms");
	}
}