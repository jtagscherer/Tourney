package usspg31.tourney.controller.util;

/**
 * Helper class for fuzzy searching
 * 
 * @author Jan Tagscherer
 */
public class SearchUtilities {
	public static boolean fuzzyMatches(String input, String filter) {
		input = input.toLowerCase();
		filter = filter.toLowerCase();
		
		if(input.indexOf(filter) != -1) {
			return true;
		} else if(SearchUtilities.calculateDistance(input, filter) < 3) {
			return true;
		}
		
		return false;
	}
	/**
	 * Calculate the edit distance between two strings
	 * 
	 * @param firstString
	 *            First given string
	 * @param secondString
	 *            Second given string
	 * @return The edit distance between the two strings, the higher it is the
	 *         more do they differ
	 */
	private static int calculateDistance(String firstString, String secondString) {
		firstString = firstString.toLowerCase();
		secondString = secondString.toLowerCase();

		int[] costs = new int[secondString.length() + 1];
		for (int j = 0; j < costs.length; j++)
			costs[j] = j;
		for (int i = 1; i <= firstString.length(); i++) {
			costs[0] = i;
			int nw = i - 1;
			for (int j = 1; j <= secondString.length(); j++) {
				int cj = Math
						.min(1 + Math.min(costs[j], costs[j - 1]),
								firstString.charAt(i - 1) == secondString
										.charAt(j - 1) ? nw : nw + 1);
				nw = costs[j];
				costs[j] = cj;
			}
		}
		return costs[secondString.length()];
	}
}
