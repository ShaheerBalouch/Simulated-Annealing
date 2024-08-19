import java.util.*;
import java.io.*;
import java.lang.Math;
public class SimulatedAnnealing
{
	//Initializing arrays and variables
	static int kemenyScore = 0;
	static int bestKemenyScore = 10000;

	static int[][] disagreementScores = new int[35][35];	
	static String[] currentRankingList = new String[35];
	static String[] bestRankingList = new String[35];

	//Simulated Annealing Parameters
	static double temperature = 80;
	static int temperatureLength = 10;
	static double coolingRatio = 0.99;
	static int num_non_improve = 400000;

	public SimulatedAnnealing()
	{
	}

	public static void ReadInput(String fileName) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();

		//Read in participants' names
		for(int i = 0; i < 35; i++)
		{
			line = reader.readLine();
			String[] names = line.split(",");
			currentRankingList[i] = names[1];
		}

		reader.readLine();
		line = reader.readLine();

		//Read in match results to get the initial kemeny score and also adding the scores to the 2d array
		while(line != null)
		{
			int[] results = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();

			if(results[1] > results[2])
			{
				kemenyScore+=results[0];
			}
			
			disagreementScores[results[1]-1][results[2]-1] = results[0];
			disagreementScores[results[2]-1][results[1]-1] = -results[0];

			line = reader.readLine();		
		}
		reader.close();
	}

	/*------------------------------------------------------------
	This method performs the actual steps of the SA algorithm
	--------------------------------------------------------------*/
	public static void SimulateAnnealing()
	{
		int count = 0;
		Random random = new Random();

		//Outer Loop begins
		while(count <= num_non_improve)
		{
			//Inner Loop begins
			for(int i = 0; i < temperatureLength; i++)
			{
				//Get neighbouring solution
				int firstDriver = random.nextInt(34);
				int secondDriver = firstDriver+1;

				//Calculate Cost difference for new neighbour
				int costDiff = disagreementScores[firstDriver][secondDriver];

				//Determine whether to move to this solution or not
				if(costDiff <= 0)
				{
					kemenyScore = kemenyScore + costDiff;

					//Accept new solution
					SwapAdjacentRankings(firstDriver, secondDriver);
				}
				else
				{
					//Generate probability to move to higher cost solution
					double probability = random.nextDouble();

					if(probability < Math.exp(-costDiff/temperature))
					{
						kemenyScore = kemenyScore + costDiff;

						//Accept new solution
						SwapAdjacentRankings(firstDriver, secondDriver);
					}
				}

				//Update best ranking list and kemeny score if the solution found has a lower cost
				if(kemenyScore < bestKemenyScore)
				{
					bestKemenyScore = kemenyScore;
					System.arraycopy(currentRankingList, 0, bestRankingList, 0, currentRankingList.length);
				}
				//Or else increment the counter for the stopping criterion
				else
				{
					count+=1;
				}
			}

			//Cool temperature down after inner loop iterations
			temperature = temperature * coolingRatio;

			//Increase the temperature length as the temperature cools down
			temperatureLength+=3;
		}
	}

	/*------------------------------------------------------------------------
	This method performs the step to accept the new state in the SA algorithm
	--------------------------------------------------------------------------*/
	public static void SwapAdjacentRankings(int firstDriver, int secondDriver)
	{
		//Swap the drivers' name
		String ranking = currentRankingList[firstDriver];

		currentRankingList[firstDriver] = currentRankingList[secondDriver];
		currentRankingList[secondDriver] = ranking;

		//Swap Rows in scores table
		int tmpRow[] = disagreementScores[firstDriver];
		disagreementScores[firstDriver] = disagreementScores[secondDriver];
		disagreementScores[secondDriver] = tmpRow;

		//Swap Columns in scores table
		for(int k = 0; k < 35; k++)
		{
			int tempScore = disagreementScores[k][firstDriver];
			disagreementScores[k][firstDriver] = disagreementScores[k][secondDriver];
			disagreementScores[k][secondDriver] = tempScore;
		}
	}

	public static void PrintBestRankingList()
	{
		for(int i = 0; i < 35; i++)
		{
			int rankNumber = i+1;
			System.out.println(rankNumber+": "+bestRankingList[i]);
		}
		System.out.println("Best Kemeny Score: "+bestKemenyScore);
		//System.out.println("Final Temperature: "+temperature);
	}
}