import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);

		int nProcesses, nResources;
		int[][] allocation, max;
		int[] resourcesCount;

		System.out.println("Enter number of processes");
		nProcesses = in.nextInt();

		System.out.println("Enter number of resources");
		nResources = in.nextInt();
		resourcesCount = new int[nResources];

		for (int i = 0; i < nResources; i++) {
			System.out.println("Enter total number of instances of resource #" + i);
			resourcesCount[i] = in.nextInt();
		}

		allocation = new int[nProcesses][nResources];
		max = new int[nProcesses][nResources];
		for (int i = 0; i < nProcesses; i++) {
			System.out.println("Enter number of allocated resources for process #" + i);
			for (int j = 0; j < nResources; j++) {
				allocation[i][j] = in.nextInt();
			}
		}
		for (int i = 0; i < nProcesses; i++) {
			System.out.println("Enter maximum number of resources needed by process #" + i);
			for (int j = 0; j < nResources; j++) {
				max[i][j] = in.nextInt();
			}
		}

		System.out.println("Select an operation");
		System.out.println("1- Calculate safety");
		System.out.println("2- Request");
		int respone = in.nextInt();
		if (respone == 1) {
			boolean safe = calculateSafety(nProcesses, nResources, resourcesCount, allocation, max);
			if (safe)
				System.out.println("System is safe");
			else
				System.out.println("System is NOT safe");
		} else if (respone == 2) {
			int[] request = new int[nResources];
			System.out.println("Enter the number of the process that makes the request");
			int requestingProcess = in.nextInt();
			System.out.println("Enter maximum number of resources requested by process #" + requestingProcess);
			for (int i = 0; i < request.length; i++)
				request[i] = in.nextInt();
			request(nProcesses, nResources, resourcesCount, allocation, max, request, requestingProcess);
		}
	}

	private static boolean calculateSafety(int nProcesses, int nResources, int[] resourcesCount, int[][] allocation,
			int[][] max) {
		int[][] need = subtract(max, allocation);
		int[] available = new int[nResources];
		int[] work = new int[nResources];
		boolean[] finish = new boolean[nProcesses];

		// Calculate available
		for (int j = 0; j < nResources; j++) {
			int allocated = 0;
			for (int i = 0; i < nProcesses; i++) 
				allocated += allocation[i][j];
			available[j] = resourcesCount[j] - allocated;
			work[j] = available[j];
		}

		boolean change = true;
		while (change) {
			change = false;
			for (int i = 0; i < nProcesses; i++) {
				if (finish[i] == false && lessThanOrEqual(need[i], work)) {
					System.out.println("Process #" + i);
					finish[i] = true;
					work = add(work, allocation[i]);
					change = true;
				}
			}
		}
		boolean safe = true;
		for (int i = 0; i < finish.length; i++) {
			if (finish[i] == false) {
				safe = false;
				break;
			}
		}

		return safe;
	}

	private static void request(int nProcesses, int nResources, int[] resourcesCount, int[][] allocation, int[][] max,
			int[] request, int requestingProcess) {

		int[][] need = subtract(max, allocation);
		int[] available = new int[nResources];

		for (int j = 0; j < nResources; j++) {
			int allocated = 0;
			for (int i = 0; i < nProcesses; i++)
				allocated += allocation[i][j];
			available[j] = resourcesCount[j] - allocated;
		}

		if (!lessThanOrEqual(request, need[requestingProcess])) {
			System.out.println("Error: process requests more than it needs");
			return;
		}
		if (!lessThanOrEqual(request, available)) {
			System.out.println("Error: process requests more resources than arailable");
			return;
		}

		int[] tmpAvailable = getCopy(available);
		int[][] tmpAllocation = getCopy(allocation);
		int[][] tmpNeed = getCopy(need);

		tmpAvailable = subtract(tmpAvailable, request);
		tmpAllocation[requestingProcess] = add(tmpAllocation[requestingProcess], request);
		tmpNeed[requestingProcess] = subtract(tmpNeed[requestingProcess], request);

		if (calculateSafety(nProcesses, nResources, resourcesCount, tmpAllocation, max))
			System.out.println("Accept request");
		else
			System.out.println("Wait");
	}

	private static int[] getCopy(int[] source) {
		int[] destination = new int[source.length];
		for (int i = 0; i < source.length; i++) {
			System.arraycopy(source, 0, destination, 0, source.length);
		}
		return destination;
	}

	private static int[][] getCopy(int[][] source) {
		int[][] destination = new int[source.length][source[0].length];
		for (int i = 0; i < source.length; i++) {
			System.arraycopy(source[i], 0, destination[i], 0, source[0].length);
		}
		return destination;
	}

	private static boolean lessThanOrEqual(int[] arr1, int[] arr2) {
		for (int i = 0; i < arr1.length; i++)
			if (!(arr1[i] <= arr2[i]))
				return false;
		return true;
	}

	private static boolean lessThanOrEqual(int[][] arr1, int[][] arr2) {
		for (int i = 0; i < arr1.length; i++)
			if (lessThanOrEqual(arr1[i], arr2[i]) == false)
				return false;
		return true;
	}

	private static int[] add(int[] arr1, int[] arr2) {
		int[] ret = new int[arr1.length];
		for (int i = 0; i < arr1.length; i++)
			ret[i] = arr1[i] + arr2[i];
		return ret;
	}

	private static int[][] add(int[][] arr1, int[][] arr2) {
		int[][] ret = new int[arr1.length][arr1[0].length];
		for (int i = 0; i < arr1.length; i++)
			ret[i] = add(arr1[i], arr2[i]);
		return ret;
	}

	private static int[] subtract(int[] arr1, int[] arr2) {
		int[] ret = new int[arr1.length];
		for (int i = 0; i < arr1.length; i++)
			ret[i] = arr1[i] - arr2[i];
		return ret;
	}

	private static int[][] subtract(int[][] arr1, int[][] arr2) {
		int[][] ret = new int[arr1.length][arr1[0].length];
		for (int i = 0; i < arr1.length; i++)
			ret[i] = subtract(arr1[i], arr2[i]);
		return ret;
	}
}
